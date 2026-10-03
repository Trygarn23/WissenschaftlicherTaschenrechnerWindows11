package ui.theme.themes;

import ui.theme.AppTheme;

import java.awt.Color;
import java.awt.Font;

/**
 * Gemeinsame Basis für die festen Themes: Unterklassen setzen im Konstruktor nur noch ihre Farben.
 * Die Felder werden ausschließlich im Konstruktor der Unterklasse belegt.
 */
public abstract class BasisTheme implements AppTheme
{
    private final String displayName;
    private final Font buttonFont;
    private final Font displayFont;
    private final Font secondaryDisplayFont;

    protected Color windowBackground;
    protected Color panelBackground;

    protected Color displayBackground;
    protected Color displayForeground;
    protected Color secondaryDisplayForeground;

    protected Color historyBackground;
    protected Color historyForeground;
    protected Color historySelectionBackground;
    protected Color historySearchBackground;
    protected Color placeholderForeground;

    protected Color modeBarBackground;
    protected Color modeButtonActiveBackground;
    protected Color modeButtonInactiveBackground;
    protected Color modeBorder;

    protected Color numberButtonBackground;
    protected Color numberButtonForeground;
    protected Color operatorButtonBackground;
    protected Color operatorButtonForeground;
    protected Color functionButtonBackground;
    protected Color functionButtonForeground;
    protected Color specialButtonBackground;
    protected Color specialButtonForeground;
    protected Color toggleButtonBackground;
    protected Color toggleButtonForeground;

    protected BasisTheme(String displayName, String schriftart)
    {
        this(displayName,
                new Font(schriftart, Font.PLAIN, 18),
                new Font(schriftart, Font.PLAIN, 48),
                new Font(schriftart, Font.PLAIN, 22));
    }

    protected BasisTheme(String displayName, Font buttonFont, Font displayFont, Font secondaryDisplayFont)
    {
        this.displayName = displayName;
        this.buttonFont = buttonFont;
        this.displayFont = displayFont;
        this.secondaryDisplayFont = secondaryDisplayFont;
    }

    @Override public String getDisplayName() { return displayName; }

    @Override public Color windowBackground() { return windowBackground; }
    @Override public Color panelBackground() { return panelBackground; }

    @Override public Color displayBackground() { return displayBackground; }
    @Override public Color displayForeground() { return displayForeground; }
    @Override public Color secondaryDisplayForeground() { return secondaryDisplayForeground; }

    @Override public Color historyBackground() { return historyBackground; }
    @Override public Color historyForeground() { return historyForeground; }
    @Override public Color historySelectionBackground() { return historySelectionBackground; }
    @Override public Color historySearchBackground() { return historySearchBackground; }
    @Override public Color placeholderForeground() { return placeholderForeground; }

    @Override public Color modeBarBackground() { return modeBarBackground; }
    @Override public Color modeButtonActiveBackground() { return modeButtonActiveBackground; }
    @Override public Color modeButtonInactiveBackground() { return modeButtonInactiveBackground; }
    @Override public Color modeBorder() { return modeBorder; }

    @Override public Color numberButtonBackground() { return numberButtonBackground; }
    @Override public Color numberButtonForeground() { return numberButtonForeground; }
    @Override public Color operatorButtonBackground() { return operatorButtonBackground; }
    @Override public Color operatorButtonForeground() { return operatorButtonForeground; }
    @Override public Color functionButtonBackground() { return functionButtonBackground; }
    @Override public Color functionButtonForeground() { return functionButtonForeground; }
    @Override public Color specialButtonBackground() { return specialButtonBackground; }
    @Override public Color specialButtonForeground() { return specialButtonForeground; }
    @Override public Color toggleButtonBackground() { return toggleButtonBackground; }
    @Override public Color toggleButtonForeground() { return toggleButtonForeground; }

    @Override public Font buttonFont() { return buttonFont; }
    @Override public Font displayFont() { return displayFont; }
    @Override public Font secondaryDisplayFont() { return secondaryDisplayFont; }
}
