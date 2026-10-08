package modes.datum.logic;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;

/** Bundesweite gesetzliche Feiertage in Deutschland (keine länderspezifischen). */
public final class Feiertage
{
    private static final Set<MonthDay> FESTE_FEIERTAGE = Set.of(
            MonthDay.of(1, 1),
            MonthDay.of(5, 1),
            MonthDay.of(10, 3),
            MonthDay.of(12, 25),
            MonthDay.of(12, 26));

    private Feiertage()
    {
    }

    public static boolean istFeiertag(LocalDate datum)
    {
        if (FESTE_FEIERTAGE.contains(MonthDay.from(datum)))
        {
            return true;
        }
        long abstandZuOstern = datum.toEpochDay() - ostersonntag(datum.getYear()).toEpochDay();
        // Karfreitag, Ostermontag, Christi Himmelfahrt, Pfingstmontag
        return abstandZuOstern == -2 || abstandZuOstern == 1 || abstandZuOstern == 39 || abstandZuOstern == 50;
    }

    /** Anonymous-Gregorian-Algorithmus (Meeus/Jones/Butcher). */
    public static LocalDate ostersonntag(int jahr)
    {
        int a = jahr % 19;
        int b = jahr / 100;
        int c = jahr % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int monat = (h + l - 7 * m + 114) / 31;
        int tag = (h + l - 7 * m + 114) % 31 + 1;
        return LocalDate.of(jahr, monat, tag);
    }
}
