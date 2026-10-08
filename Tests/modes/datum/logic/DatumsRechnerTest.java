package modes.datum.logic;

import modes.datum.logic.DatumsRechner.Einheit;
import modes.datum.logic.DatumsRechner.Verschiebung;
import modes.datum.logic.DatumsRechner.Zeitraum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

public class DatumsRechnerTest
{
    @Test
    void lese_ShouldAcceptGermanShortAndIsoFormats()
    {
        // Act & Assert
        assertEquals(LocalDate.of(2024, 2, 1), DatumsRechner.lese("01.02.2024"));
        assertEquals(LocalDate.of(2024, 2, 1), DatumsRechner.lese(" 1.2.2024 "));
        assertEquals(LocalDate.of(2024, 2, 1), DatumsRechner.lese("2024-02-01"));
    }

    @Test
    void lese_ShouldExplainInvalidDatesInGerman()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class, () -> DatumsRechner.lese(" "));
        IllegalArgumentException text = assertThrows(IllegalArgumentException.class, () -> DatumsRechner.lese("abc"));
        IllegalArgumentException kalender = assertThrows(IllegalArgumentException.class, () -> DatumsRechner.lese("29.02.2023"));

        // Assert
        assertEquals("Bitte ein Datum eingeben.", leer.getMessage());
        assertEquals("„abc“ ist kein gültiges Datum (TT.MM.JJJJ).", text.getMessage());
        assertEquals("„29.02.2023“ gibt es im Kalender nicht.", kalender.getMessage());
    }

    @Test
    void lese_ShouldAcceptLeapDayInLeapYear()
    {
        // Act & Assert
        assertEquals(LocalDate.of(2024, 2, 29), DatumsRechner.lese("29.02.2024"));
        assertEquals(LocalDate.of(2000, 2, 29), DatumsRechner.lese("29.02.2000"));
        assertThrows(IllegalArgumentException.class, () -> DatumsRechner.lese("29.02.1900"));
    }

    @Test
    void zeitraum_ShouldCountLeapDay()
    {
        // Act
        Zeitraum schaltjahr = DatumsRechner.zeitraum(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 1));
        Zeitraum normal = DatumsRechner.zeitraum(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 3, 1));

        // Assert
        assertEquals(60, schaltjahr.tage());
        assertEquals(59, normal.tage());
        assertEquals(8, schaltjahr.wochen());
        assertEquals(4, schaltjahr.restTage());
    }

    @Test
    void zeitraum_ShouldSpanYearChangeAndGiveYearsMonthsDays()
    {
        // Act
        Zeitraum jahreswechsel = DatumsRechner.zeitraum(LocalDate.of(2025, 12, 28), LocalDate.of(2026, 1, 3));
        Zeitraum lang = DatumsRechner.zeitraum(LocalDate.of(2020, 3, 15), LocalDate.of(2026, 5, 10));

        // Assert
        assertEquals(6, jahreswechsel.tage());
        assertEquals(Period.of(6, 1, 25), lang.periode());
    }

    @Test
    void zeitraum_ShouldKeepSignButPositivePeriodWhenBackwards()
    {
        // Act
        Zeitraum rueckwaerts = DatumsRechner.zeitraum(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 1));

        // Assert
        assertEquals(-9, rueckwaerts.tage());
        assertEquals(Period.ofDays(9), rueckwaerts.periode());
        assertEquals(1, rueckwaerts.wochen());
        assertEquals(2, rueckwaerts.restTage());
    }

    @Test
    void verschiebe_ShouldClampToMonthEndAndReportIt()
    {
        // Act
        Verschiebung schaltjahr = DatumsRechner.verschiebe(LocalDate.of(2024, 1, 31), 1, Einheit.MONATE);
        Verschiebung normal = DatumsRechner.verschiebe(LocalDate.of(2025, 1, 31), 1, Einheit.MONATE);
        Verschiebung rueckwaerts = DatumsRechner.verschiebe(LocalDate.of(2026, 3, 31), -1, Einheit.MONATE);
        Verschiebung schalttag = DatumsRechner.verschiebe(LocalDate.of(2024, 2, 29), 12, Einheit.MONATE);
        Verschiebung ohneKappen = DatumsRechner.verschiebe(LocalDate.of(2026, 1, 15), 1, Einheit.MONATE);

        // Assert
        assertEquals(new Verschiebung(LocalDate.of(2024, 2, 29), true), schaltjahr);
        assertEquals(new Verschiebung(LocalDate.of(2025, 2, 28), true), normal);
        assertEquals(new Verschiebung(LocalDate.of(2026, 2, 28), true), rueckwaerts);
        assertEquals(new Verschiebung(LocalDate.of(2025, 2, 28), true), schalttag);
        assertEquals(new Verschiebung(LocalDate.of(2026, 2, 15), false), ohneKappen);
    }

    @Test
    void verschiebe_ShouldCrossYearWithDaysAndWeeks()
    {
        // Act & Assert
        assertEquals(LocalDate.of(2026, 1, 1), DatumsRechner.verschiebe(LocalDate.of(2025, 12, 31), 1, Einheit.TAGE).datum());
        assertEquals(LocalDate.of(2026, 1, 4), DatumsRechner.verschiebe(LocalDate.of(2025, 12, 28), 1, Einheit.WOCHEN).datum());
        assertEquals(LocalDate.of(2025, 12, 31), DatumsRechner.verschiebe(LocalDate.of(2026, 1, 1), -1, Einheit.TAGE).datum());
        assertFalse(DatumsRechner.verschiebe(LocalDate.of(2025, 12, 31), 1, Einheit.TAGE).gekappt());
    }

    @Test
    void wochentag_ShouldBeGermanWithIsoWeek()
    {
        // Act & Assert
        assertEquals("Donnerstag", DatumsRechner.wochentag(LocalDate.of(2026, 10, 8)));
        assertEquals("Montag", DatumsRechner.wochentag(LocalDate.of(2025, 12, 29)));
        assertEquals("KW 1/2026", DatumsRechner.kalenderwoche(LocalDate.of(2025, 12, 29)));
        assertEquals("KW 53/2026", DatumsRechner.kalenderwoche(LocalDate.of(2027, 1, 1)));
        assertEquals("KW 41/2026", DatumsRechner.kalenderwoche(LocalDate.of(2026, 10, 8)));
    }

    @Test
    void arbeitstage_ShouldSkipWeekendsAndOptionallyHolidays()
    {
        // Arrange
        LocalDate dezemberStart = LocalDate.of(2025, 12, 1);
        LocalDate dezemberEnde = LocalDate.of(2025, 12, 31);
        LocalDate aprilStart = LocalDate.of(2026, 4, 1);
        LocalDate aprilEnde = LocalDate.of(2026, 4, 30);

        // Act & Assert
        assertEquals(23, DatumsRechner.arbeitstage(dezemberStart, dezemberEnde, false));
        assertEquals(21, DatumsRechner.arbeitstage(dezemberStart, dezemberEnde, true));
        assertEquals(22, DatumsRechner.arbeitstage(aprilStart, aprilEnde, false));
        assertEquals(20, DatumsRechner.arbeitstage(aprilStart, aprilEnde, true));
        assertEquals(20, DatumsRechner.arbeitstage(aprilEnde, aprilStart, true));
    }

    @Test
    void arbeitstage_ShouldCountSingleDayAndWeekend()
    {
        // Act & Assert
        assertEquals(1, DatumsRechner.arbeitstage(LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 8), false));
        assertEquals(0, DatumsRechner.arbeitstage(LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 11), false));
        assertEquals(0, DatumsRechner.arbeitstage(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 1), true));
    }
}
