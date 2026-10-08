package modes.netzwerk.logic;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Kürzt IPv6-Adressen nach RFC 5952 und schreibt sie wieder voll aus.
 * Bewusst eigener Parser: InetAddress würde ggf. DNS fragen und ist bei Eingaben zu nachsichtig.
 */
public final class Ipv6Rechner
{
    private Ipv6Rechner()
    {
    }

    public static String kuerze(String text)
    {
        Eingabe eingabe = lese(text);
        int[] gruppen = eingabe.gruppen();

        // Längste Nullfolge ab 2 Gruppen; bei Gleichstand gewinnt die erste.
        int besterStart = -1;
        int besteLaenge = 1;
        for (int i = 0; i < 8; i++)
        {
            int laenge = 0;
            while (i + laenge < 8 && gruppen[i + laenge] == 0)
            {
                laenge++;
            }
            if (laenge > besteLaenge)
            {
                besterStart = i;
                besteLaenge = laenge;
            }
        }

        if (besterStart < 0)
        {
            return hex(gruppen, 0, 8) + eingabe.suffix();
        }
        return hex(gruppen, 0, besterStart) + "::" + hex(gruppen, besterStart + besteLaenge, 8) + eingabe.suffix();
    }

    public static String schreibeAus(String text)
    {
        Eingabe eingabe = lese(text);
        return Arrays.stream(eingabe.gruppen())
                .mapToObj(gruppe -> String.format("%04x", gruppe))
                .collect(Collectors.joining(":")) + eingabe.suffix();
    }

    private static String hex(int[] gruppen, int von, int bis)
    {
        return Arrays.stream(gruppen, von, bis)
                .mapToObj(Integer::toHexString)
                .collect(Collectors.joining(":"));
    }

    private record Eingabe(int[] gruppen, String suffix)
    {
    }

    private static Eingabe lese(String text)
    {
        String original = text == null ? "" : text.strip();
        String eingabe = original.toLowerCase(Locale.ROOT);
        if (eingabe.isEmpty())
        {
            throw new IllegalArgumentException("Bitte eine IPv6-Adresse eingeben.");
        }

        String suffix = "";
        int slash = eingabe.indexOf('/');
        if (slash >= 0)
        {
            String praefix = eingabe.substring(slash + 1);
            if (!praefix.matches("\\d{1,3}") || Integer.parseInt(praefix) > 128)
            {
                throw new IllegalArgumentException("Präfix „/" + praefix + "“ ist ungültig, erlaubt ist /0 bis /128.");
            }
            suffix = "/" + Integer.parseInt(praefix);
            eingabe = eingabe.substring(0, slash);
        }

        String fehler = "„" + original + "“ ist keine gültige IPv6-Adresse.";
        int doppel = eingabe.indexOf("::");
        if (doppel >= 0 && eingabe.indexOf("::", doppel + 1) >= 0)
        {
            throw new IllegalArgumentException(fehler + " „::“ darf nur einmal vorkommen.");
        }

        String[] links = teile(doppel >= 0 ? eingabe.substring(0, doppel) : eingabe, fehler);
        String[] rechts = doppel >= 0 ? teile(eingabe.substring(doppel + 2), fehler) : new String[0];
        int vorhanden = links.length + rechts.length;
        if (doppel < 0 && vorhanden != 8)
        {
            throw new IllegalArgumentException(fehler + " Es müssen 8 Gruppen sein.");
        }
        if (doppel >= 0 && vorhanden > 7)
        {
            throw new IllegalArgumentException(fehler + " Zu viele Gruppen für „::“.");
        }

        int[] gruppen = new int[8];
        for (int i = 0; i < links.length; i++)
        {
            gruppen[i] = Integer.parseInt(links[i], 16);
        }
        for (int i = 0; i < rechts.length; i++)
        {
            gruppen[8 - rechts.length + i] = Integer.parseInt(rechts[i], 16);
        }
        return new Eingabe(gruppen, suffix);
    }

    private static String[] teile(String teil, String fehler)
    {
        if (teil.isEmpty())
        {
            return new String[0];
        }
        String[] gruppen = teil.split(":", -1);
        for (String gruppe : gruppen)
        {
            if (!gruppe.matches("[0-9a-f]{1,4}"))
            {
                throw new IllegalArgumentException(fehler + " Jede Gruppe hat 1 bis 4 Hex-Ziffern.");
            }
        }
        return gruppen;
    }
}
