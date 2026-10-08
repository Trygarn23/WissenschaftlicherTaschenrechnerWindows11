package modes.graph.formatting;

import modes.graph.model.FunktionsDefinition;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Funktionsliste als Text, eine Funktion pro Zeile: {@code name;ausdruck;farbeHex;sichtbar}. */
public final class FunktionslistenFormat
{
    private FunktionslistenFormat()
    {
    }

    public static String schreibe(List<FunktionsDefinition> funktionen)
    {
        StringBuilder text = new StringBuilder();
        for (FunktionsDefinition funktion : funktionen)
        {
            Color farbe = funktion.getFarbe();
            text.append(funktion.getName()).append(';')
                    .append(funktion.getAusdruck()).append(';')
                    .append(String.format("#%02X%02X%02X", farbe.getRed(), farbe.getGreen(), farbe.getBlue())).append(';')
                    .append(funktion.isSichtbar()).append('\n');
        }
        return text.toString();
    }

    public static List<FunktionsDefinition> lese(String text)
    {
        List<FunktionsDefinition> funktionen = new ArrayList<>();
        Set<String> namen = new HashSet<>();
        String[] zeilen = text.split("\\R");
        for (int i = 0; i < zeilen.length; i++)
        {
            if (zeilen[i].isBlank())
            {
                continue;
            }
            FunktionsDefinition funktion = leseZeile(zeilen[i].trim(), i + 1);
            if (!namen.add(funktion.getName().toLowerCase(Locale.ROOT)))
            {
                throw new IllegalArgumentException("Zeile " + (i + 1) + ": Name „" + funktion.getName() + "“ kommt doppelt vor");
            }
            funktionen.add(funktion);
        }

        if (funktionen.isEmpty())
        {
            throw new IllegalArgumentException("Die Datei enthält keine Funktionen");
        }
        return funktionen;
    }

    private static FunktionsDefinition leseZeile(String zeile, int nummer)
    {
        String[] teile = zeile.split(";", -1);
        if (teile.length != 4)
        {
            throw new IllegalArgumentException("Zeile " + nummer + " ist ungültig, erwartet: name;ausdruck;farbe;sichtbar");
        }

        String name = teile[0].trim();
        String ausdruck = teile[1].trim();
        String farbeText = teile[2].trim();
        String sichtbarText = teile[3].trim().toLowerCase(Locale.ROOT);

        if (!name.matches("[a-zA-Z][a-zA-Z0-9]*") || name.equalsIgnoreCase("x"))
        {
            throw new IllegalArgumentException("Zeile " + nummer + ": ungültiger Funktionsname „" + name + "“");
        }
        if (!farbeText.matches("#[0-9a-fA-F]{6}"))
        {
            throw new IllegalArgumentException("Zeile " + nummer + ": ungültige Farbe „" + farbeText + "“");
        }
        if (!sichtbarText.equals("true") && !sichtbarText.equals("false"))
        {
            throw new IllegalArgumentException("Zeile " + nummer + ": sichtbar muss true oder false sein");
        }

        FunktionsDefinition funktion = new FunktionsDefinition(name, ausdruck, Color.decode(farbeText));
        funktion.setSichtbar(Boolean.parseBoolean(sichtbarText));
        return funktion;
    }
}
