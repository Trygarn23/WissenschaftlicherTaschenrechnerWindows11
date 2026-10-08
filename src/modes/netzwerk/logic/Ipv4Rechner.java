package modes.netzwerk.logic;

import modes.netzwerk.model.Ipv4Netz;
import modes.netzwerk.model.Teilung;

import java.util.ArrayList;
import java.util.List;

/** Liest, schreibt und teilt IPv4-Adressen und Masken. */
public final class Ipv4Rechner
{
    public static final int MAX_ANGEZEIGTE_TEILNETZE = 256;

    private Ipv4Rechner()
    {
    }

    public static int leseAdresse(String text)
    {
        String eingabe = text == null ? "" : text.strip();
        if (eingabe.isEmpty())
        {
            throw new IllegalArgumentException("Bitte eine IPv4-Adresse eingeben.");
        }

        String[] teile = eingabe.split("\\.", -1);
        if (teile.length != 4)
        {
            throw new IllegalArgumentException("„" + eingabe + "“ ist keine gültige IPv4-Adresse (4 Zahlen mit Punkt).");
        }

        int adresse = 0;
        for (String teil : teile)
        {
            if (!teil.matches("\\d{1,3}") || Integer.parseInt(teil) > 255)
            {
                throw new IllegalArgumentException("„" + eingabe + "“ ist keine gültige IPv4-Adresse (jedes Oktett 0 bis 255).");
            }
            adresse = (adresse << 8) | Integer.parseInt(teil);
        }
        return adresse;
    }

    /** Versteht „/26“, „26“ und „255.255.255.192“. */
    public static int lesePraefix(String text)
    {
        String eingabe = text == null ? "" : text.strip();
        if (eingabe.isEmpty())
        {
            throw new IllegalArgumentException("Bitte eine Maske eingeben (z. B. /24 oder 255.255.255.0).");
        }

        String ohneSlash = eingabe.startsWith("/") ? eingabe.substring(1).strip() : eingabe;
        if (ohneSlash.matches("\\d{1,2}"))
        {
            int praefix = Integer.parseInt(ohneSlash);
            if (praefix > 32)
            {
                throw new IllegalArgumentException("Präfix /" + praefix + " ist ungültig, erlaubt ist /0 bis /32.");
            }
            return praefix;
        }

        if (eingabe.startsWith("/"))
        {
            throw new IllegalArgumentException("„" + eingabe + "“ ist kein gültiges Präfix (/0 bis /32).");
        }

        int maske = leseAdresse(eingabe);
        int invertiert = ~maske;
        // Gültig nur, wenn alle Einsen links stehen: die invertierte Maske ist dann 0…01…1.
        if ((invertiert & (invertiert + 1)) != 0)
        {
            throw new IllegalArgumentException("„" + eingabe + "“ ist keine gültige Subnetzmaske (Einsen müssen lückenlos links stehen).");
        }
        return Integer.bitCount(maske);
    }

    /** Liest „192.168.1.10/26“ aus einem Feld oder Adresse und Maske getrennt. */
    public static Ipv4Netz leseNetz(String adressText, String maskenText)
    {
        String adresse = adressText == null ? "" : adressText.strip();
        String maske = maskenText == null ? "" : maskenText.strip();
        int slash = adresse.indexOf('/');

        if (slash >= 0)
        {
            if (!maske.isEmpty())
            {
                throw new IllegalArgumentException("Maske bitte nur einmal angeben – im Adressfeld oder im Maskenfeld.");
            }
            maske = adresse.substring(slash);
            adresse = adresse.substring(0, slash);
        }
        else if (maske.isEmpty())
        {
            throw new IllegalArgumentException("Bitte eine Maske angeben (z. B. 192.168.1.10/24).");
        }

        return new Ipv4Netz(leseAdresse(adresse), lesePraefix(maske));
    }

    public static String alsText(int adresse)
    {
        return (adresse >>> 24) + "." + ((adresse >>> 16) & 0xFF) + "." + ((adresse >>> 8) & 0xFF) + "." + (adresse & 0xFF);
    }

    /** Binär mit Punkten zwischen den Oktetten und „|“ zwischen Netz- und Hostteil. */
    public static String binaer(int adresse, int praefix)
    {
        StringBuilder text = new StringBuilder(36);
        for (int bit = 0; bit < 32; bit++)
        {
            if (bit == praefix)
            {
                text.append('|');
            }
            else if (bit > 0 && bit % 8 == 0)
            {
                text.append('.');
            }
            text.append((adresse >>> (31 - bit)) & 1);
        }
        if (praefix == 32)
        {
            text.append('|');
        }
        return text.toString();
    }

    /** Teilt das Netz in mindestens {@code gewuenscht} gleich große Teilnetze (aufgerundet auf eine Zweierpotenz). */
    public static Teilung teile(Ipv4Netz netz, int gewuenscht)
    {
        if (gewuenscht < 1)
        {
            throw new IllegalArgumentException("Bitte mindestens 1 Teilnetz angeben.");
        }

        int zusatzBits = 64 - Long.numberOfLeadingZeros(gewuenscht - 1L);
        int neuerPraefix = netz.praefix() + zusatzBits;
        if (neuerPraefix > 32)
        {
            throw new IllegalArgumentException("Ein /" + netz.praefix() + " lässt sich nicht in " + gewuenscht
                    + " Teilnetze aufteilen (höchstens " + netz.anzahlAdressen() + ").");
        }

        long anzahl = 1L << zusatzBits;
        long schritt = 1L << (32 - neuerPraefix);
        long start = Integer.toUnsignedLong(netz.netzadresse());
        List<Ipv4Netz> netze = new ArrayList<>();
        for (long i = 0; i < Math.min(anzahl, MAX_ANGEZEIGTE_TEILNETZE); i++)
        {
            netze.add(new Ipv4Netz((int) (start + i * schritt), neuerPraefix));
        }
        return new Teilung(netz.alsNetz(), gewuenscht, anzahl, neuerPraefix, netze);
    }
}
