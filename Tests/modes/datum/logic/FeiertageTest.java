package modes.datum.logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FeiertageTest
{
    @Test
    void ostersonntag_ShouldMatchKnownDates()
    {
        // Act & Assert
        assertEquals(LocalDate.of(2000, 4, 23), Feiertage.ostersonntag(2000));
        assertEquals(LocalDate.of(2024, 3, 31), Feiertage.ostersonntag(2024));
        assertEquals(LocalDate.of(2025, 4, 20), Feiertage.ostersonntag(2025));
        assertEquals(LocalDate.of(2026, 4, 5), Feiertage.ostersonntag(2026));
        assertEquals(LocalDate.of(1818, 3, 22), Feiertage.ostersonntag(1818));
        assertEquals(LocalDate.of(2038, 4, 25), Feiertage.ostersonntag(2038));
    }

    @Test
    void istFeiertag_ShouldKnowAllNationwideHolidaysOf2026()
    {
        // Arrange
        LocalDate[] feiertage = {
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 3),
                LocalDate.of(2026, 4, 6),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 14),
                LocalDate.of(2026, 5, 25),
                LocalDate.of(2026, 10, 3),
                LocalDate.of(2026, 12, 25),
                LocalDate.of(2026, 12, 26)
        };

        // Act & Assert
        for (LocalDate tag : feiertage)
        {
            assertTrue(Feiertage.istFeiertag(tag), tag.toString());
        }
    }

    @Test
    void istFeiertag_ShouldIgnoreRegionalAndNormalDays()
    {
        // Act & Assert
        assertFalse(Feiertage.istFeiertag(LocalDate.of(2026, 4, 5)));
        assertFalse(Feiertage.istFeiertag(LocalDate.of(2026, 1, 6)));
        assertFalse(Feiertage.istFeiertag(LocalDate.of(2026, 12, 24)));
        assertFalse(Feiertage.istFeiertag(LocalDate.of(2026, 10, 8)));
    }
}
