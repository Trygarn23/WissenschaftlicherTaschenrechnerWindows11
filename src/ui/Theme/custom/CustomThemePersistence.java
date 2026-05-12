package ui.theme.custom;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class CustomThemePersistence
{
    private static final Path STANDARD_DATEI =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_custom_theme.properties");

    private final Path datei;

    public CustomThemePersistence()
    {
        this(STANDARD_DATEI);
    }

    public CustomThemePersistence(Path datei)
    {
        this.datei = datei;
    }

    public CustomThemeColors lade()
    {
        if (!Files.exists(datei))
        {
            return CustomThemeColors.DEFAULT;
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(datei, StandardCharsets.UTF_8))
        {
            properties.load(reader);
        }
        catch (IOException ignored)
        {
            return CustomThemeColors.DEFAULT;
        }

        CustomThemeColors defaults = CustomThemeColors.DEFAULT;
        return new CustomThemeColors(
                readColor(properties, "panelBackground", defaults.panelBackground()),
                readColor(properties, "displayBackground", defaults.displayBackground()),
                readColor(properties, "displayForeground", defaults.displayForeground()),
                readColor(properties, "numberButtonBackground", defaults.numberButtonBackground()),
                readColor(properties, "operatorButtonBackground", defaults.operatorButtonBackground()),
                readColor(properties, "functionButtonBackground", defaults.functionButtonBackground()),
                readColor(properties, "accentBackground", defaults.accentBackground())
        );
    }

    public void speichere(CustomThemeColors colors)
    {
        CustomThemeColors value = colors == null ? CustomThemeColors.DEFAULT : colors;
        Properties properties = new Properties();
        properties.setProperty("panelBackground", writeColor(value.panelBackground()));
        properties.setProperty("displayBackground", writeColor(value.displayBackground()));
        properties.setProperty("displayForeground", writeColor(value.displayForeground()));
        properties.setProperty("numberButtonBackground", writeColor(value.numberButtonBackground()));
        properties.setProperty("operatorButtonBackground", writeColor(value.operatorButtonBackground()));
        properties.setProperty("functionButtonBackground", writeColor(value.functionButtonBackground()));
        properties.setProperty("accentBackground", writeColor(value.accentBackground()));

        try
        {
            Path parent = datei.getParent();
            if (parent != null)
            {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(datei, StandardCharsets.UTF_8))
            {
                properties.store(writer, "Wissenschaftlicher Taschenrechner Custom Theme");
            }
        }
        catch (IOException ignored)
        {
        }
    }

    private Color readColor(Properties properties, String key, Color fallback)
    {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank())
        {
            return fallback;
        }

        String normalized = value.trim();
        if (normalized.startsWith("#"))
        {
            normalized = normalized.substring(1);
        }

        if (!normalized.matches("[0-9a-fA-F]{6}"))
        {
            return fallback;
        }

        try
        {
            return new Color(Integer.parseInt(normalized, 16));
        }
        catch (NumberFormatException ignored)
        {
            return fallback;
        }
    }

    private String writeColor(Color color)
    {
        Color value = color == null ? Color.BLACK : color;
        return String.format("#%02X%02X%02X", value.getRed(), value.getGreen(), value.getBlue());
    }
}
