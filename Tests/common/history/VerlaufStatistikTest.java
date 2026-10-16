package common.history;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VerlaufStatistikTest
{
    private static VerlaufEintrag eintrag(String ausdruck, RechnerModus modus)
    {
        return new VerlaufEintrag(ausdruck, "1", modus, LocalDateTime.of(2026, 1, 1, 12, 0), false);
    }

    @Test
    void aus_ShouldFindFavoriteFunctionAndMode_WhenHistoryHasFunctions()
    {
        // Arrange
        List<VerlaufEintrag> eintraege = List.of(
                eintrag("sin(30)+SIN(60)", RechnerModus.WISSENSCHAFTLICH),
                eintrag("√(4)+sqrt (9)+√16", RechnerModus.WISSENSCHAFTLICH),
                eintrag("nCr(5;2)", RechnerModus.STANDARD),
                eintrag("1+2", RechnerModus.WISSENSCHAFTLICH));

        // Act
        VerlaufStatistik statistik = VerlaufStatistik.aus(eintraege);

        // Assert
        assertEquals(4, statistik.anzahlRechnungen());
        assertEquals(Optional.of("sqrt"), statistik.lieblingsFunktion());
        assertEquals(3, statistik.funktionsAnzahl());
        assertEquals(Optional.of(RechnerModus.WISSENSCHAFTLICH), statistik.lieblingsModus());
        assertTrue(statistik.alsText().contains("Deine Lieblingsfunktion: sqrt (3×)"));
    }

    @Test
    void aus_ShouldPreferFirstFunction_WhenCountsAreEqual()
    {
        // Act
        VerlaufStatistik statistik = VerlaufStatistik.aus(List.of(eintrag("cos(1)*ln(2)", RechnerModus.STANDARD)));

        // Assert
        assertEquals(Optional.of("cos"), statistik.lieblingsFunktion());
        assertEquals(1, statistik.funktionsAnzahl());
    }

    @Test
    void aus_ShouldReportNothing_WhenHistoryIsEmpty()
    {
        // Act
        VerlaufStatistik statistik = VerlaufStatistik.aus(List.of());

        // Assert
        assertEquals(0, statistik.anzahlRechnungen());
        assertTrue(statistik.lieblingsFunktion().isEmpty());
        assertTrue(statistik.lieblingsModus().isEmpty());
        assertTrue(statistik.alsText().contains("Noch keine Funktion benutzt"));
    }
}
