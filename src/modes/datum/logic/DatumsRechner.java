package modes.datum.logic;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Datumsrechnungen ohne Swing; „heute“ kommt immer von außen. */
public final class DatumsRechner
{
    private static final Pattern DEUTSCH = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})");
    private static final Pattern ISO = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
    private static final DateTimeFormatter AUSGABE = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    public enum Einheit
    {
        TAGE("Tage", ChronoUnit.DAYS),
        WOCHEN("Wochen", ChronoUnit.WEEKS),
        MONATE("Monate", ChronoUnit.MONTHS);

        private final String anzeige;
        private final ChronoUnit unit;

        Einheit(String anzeige, ChronoUnit unit)
        {
            this.anzeige = anzeige;
            this.unit = unit;
        }

        @Override
        public String toString()
        {
            return anzeige;
        }
    }

    /** {@code tage} ist vorzeichenbehaftet, {@code periode} immer positiv (vom früheren zum späteren Datum). */
    public record Zeitraum(long tage, Period periode)
    {
        public long wochen()
        {
            return Math.abs(tage) / 7;
        }

        public long restTage()
        {
            return Math.abs(tage) % 7;
        }
    }

    /** {@code gekappt}: Zieltag gab es im Zielmonat nicht, java.time hat auf das Monatsende gekürzt. */
    public record Verschiebung(LocalDate datum, boolean gekappt)
    {
    }

    private DatumsRechner()
    {
    }

    public static LocalDate lese(String text)
    {
        String eingabe = text == null ? "" : text.strip();
        if (eingabe.isEmpty())
        {
            throw new IllegalArgumentException("Bitte ein Datum eingeben.");
        }

        Matcher deutsch = DEUTSCH.matcher(eingabe);
        Matcher iso = ISO.matcher(eingabe);
        try
        {
            if (deutsch.matches())
            {
                return LocalDate.of(zahl(deutsch, 3), zahl(deutsch, 2), zahl(deutsch, 1));
            }
            if (iso.matches())
            {
                return LocalDate.of(zahl(iso, 1), zahl(iso, 2), zahl(iso, 3));
            }
        }
        catch (DateTimeException e)
        {
            throw new IllegalArgumentException("„" + eingabe + "“ gibt es im Kalender nicht.");
        }
        throw new IllegalArgumentException("„" + eingabe + "“ ist kein gültiges Datum (TT.MM.JJJJ).");
    }

    public static String formatiere(LocalDate datum)
    {
        return datum.format(AUSGABE);
    }

    public static Zeitraum zeitraum(LocalDate von, LocalDate bis)
    {
        long tage = ChronoUnit.DAYS.between(von, bis);
        Period periode = tage >= 0 ? Period.between(von, bis) : Period.between(bis, von);
        return new Zeitraum(tage, periode);
    }

    public static Verschiebung verschiebe(LocalDate datum, long anzahl, Einheit einheit)
    {
        LocalDate ergebnis = datum.plus(anzahl, einheit.unit);
        return new Verschiebung(ergebnis, einheit == Einheit.MONATE && ergebnis.getDayOfMonth() != datum.getDayOfMonth());
    }

    public static String wochentag(LocalDate datum)
    {
        return datum.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }

    /** ISO-Kalenderwoche, z. B. „KW 1/2026“ – das Jahr ist das wochenbasierte Jahr. */
    public static String kalenderwoche(LocalDate datum)
    {
        return "KW " + datum.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + "/" + datum.get(IsoFields.WEEK_BASED_YEAR);
    }

    /** Zählt Mo–Fr einschließlich beider Grenzen; die Reihenfolge der Daten ist egal. */
    public static long arbeitstage(LocalDate von, LocalDate bis, boolean feiertageAbziehen)
    {
        LocalDate start = von.isBefore(bis) ? von : bis;
        LocalDate ende = von.isBefore(bis) ? bis : von;
        // ponytail: Tag-für-Tag-Schleife, reicht für menschliche Zeiträume; bei Jahrtausenden auf Wochenformel umstellen
        return start.datesUntil(ende.plusDays(1))
                .filter(tag -> tag.getDayOfWeek() != DayOfWeek.SATURDAY && tag.getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(tag -> !feiertageAbziehen || !Feiertage.istFeiertag(tag))
                .count();
    }

    private static int zahl(Matcher matcher, int gruppe)
    {
        return Integer.parseInt(matcher.group(gruppe));
    }
}
