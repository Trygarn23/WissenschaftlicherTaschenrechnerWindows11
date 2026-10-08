package modes.datum.logic;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Summiert Zeitangaben wie „7:30“, „7h 30min“, „45min“ oder „-0:30“ (Pause abziehen). */
public final class ZeitSummierer
{
    private static final Pattern TRENNER = Pattern.compile("[\\s,;]+");
    private static final Pattern ZEIT = Pattern.compile(
            "(-)?(?:(\\d{1,5}):(\\d{2})|(\\d{1,5})\\s*h(?:\\s*(\\d{1,5})\\s*min)?|(\\d{1,5})\\s*min)(?=[\\s,;]|$)",
            Pattern.CASE_INSENSITIVE);

    private ZeitSummierer()
    {
    }

    public static int summiereMinuten(String text)
    {
        String eingabe = text == null ? "" : text.strip();
        if (eingabe.isEmpty())
        {
            throw new IllegalArgumentException("Bitte mindestens eine Zeit eingeben.");
        }

        Matcher zeit = ZEIT.matcher(eingabe);
        Matcher trenner = TRENNER.matcher(eingabe);
        int summe = 0;
        int position = 0;
        while (position < eingabe.length())
        {
            zeit.region(position, eingabe.length());
            if (!zeit.lookingAt())
            {
                String rest = TRENNER.split(eingabe.substring(position), 2)[0];
                throw new IllegalArgumentException("„" + rest + "“ ist keine gültige Zeit.");
            }
            summe += minuten(zeit);
            position = zeit.end();

            trenner.region(position, eingabe.length());
            if (trenner.lookingAt())
            {
                position = trenner.end();
            }
        }
        return summe;
    }

    /** z. B. 450 → „07:30“, -30 → „-00:30“. */
    public static String formatiereStunden(int minuten)
    {
        int betrag = Math.abs(minuten);
        return String.format("%s%02d:%02d", minuten < 0 ? "-" : "", betrag / 60, betrag % 60);
    }

    /** z. B. 450 → „7,50 h“. */
    public static String formatiereDezimal(int minuten)
    {
        return String.format(Locale.GERMAN, "%.2f h", minuten / 60.0);
    }

    private static int minuten(Matcher zeit)
    {
        int minuten;
        if (zeit.group(2) != null)
        {
            int min = Integer.parseInt(zeit.group(3));
            if (min >= 60)
            {
                throw new IllegalArgumentException("„" + zeit.group() + "“: Minuten müssen unter 60 liegen.");
            }
            minuten = Integer.parseInt(zeit.group(2)) * 60 + min;
        }
        else if (zeit.group(4) != null)
        {
            minuten = Integer.parseInt(zeit.group(4)) * 60 + (zeit.group(5) == null ? 0 : Integer.parseInt(zeit.group(5)));
        }
        else
        {
            minuten = Integer.parseInt(zeit.group(6));
        }
        return zeit.group(1) != null ? -minuten : minuten;
    }
}
