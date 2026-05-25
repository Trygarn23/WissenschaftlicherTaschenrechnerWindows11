import common.history.VerlaufEintrag;
import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class VerlaufEintragTest
{
    @Test
    void verlaufEintrag_ShouldStoreStructuredHistoryData_WhenCreated()
    {
        // Arrange
        LocalDateTime zeitpunkt = LocalDateTime.of(2026, 5, 8, 12, 0);

        // Act
        VerlaufEintrag eintrag = new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, zeitpunkt, true);

        // Assert
        assertEquals("2+3", eintrag.getAusdruck());
        assertEquals("5", eintrag.getErgebnis());
        assertEquals(RechnerModus.STANDARD, eintrag.getModus());
        assertEquals(zeitpunkt, eintrag.getZeitpunkt());
        assertTrue(eintrag.isFavorit());
        assertEquals("2+3 = 5", eintrag.toLegacyText());
        assertEquals("[Standard] 08.05.2026 12:00 · 2+3 = 5", eintrag.toDisplayText());
        assertTrue(eintrag.matchesSuchtext("standard"));
        assertTrue(eintrag.matchesSuchtext("2+3"));
        assertTrue(eintrag.matchesSuchtext("5"));
    }

    @Test
    void verlaufEintrag_ShouldCompareByValue_WhenFieldsAreEqual()
    {
        // Arrange
        LocalDateTime zeitpunkt = LocalDateTime.of(2026, 5, 8, 12, 0);

        // Act
        VerlaufEintrag erster = new VerlaufEintrag("x^2", "4", RechnerModus.GRAPH, zeitpunkt, false);
        VerlaufEintrag zweiter = new VerlaufEintrag("x^2", "4", RechnerModus.GRAPH, zeitpunkt, false);

        // Assert
        assertEquals(erster, zweiter);
        assertEquals(erster.hashCode(), zweiter.hashCode());
    }

    @Test
    void verlaufEintrag_ShouldCreateFavoriteCopies_WhenFavoriteChanges()
    {
        // Arrange
        LocalDateTime zeitpunkt = LocalDateTime.of(2026, 5, 8, 12, 0);
        VerlaufEintrag eintrag = new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, zeitpunkt, false);

        // Act
        VerlaufEintrag favorit = eintrag.withFavorit(true);
        VerlaufEintrag normal = favorit.toggleFavorit();

        // Assert
        assertFalse(eintrag.isFavorit());
        assertTrue(favorit.isFavorit());
        assertFalse(normal.isFavorit());
        assertEquals(eintrag.getAusdruck(), favorit.getAusdruck());
        assertEquals(eintrag.getErgebnis(), favorit.getErgebnis());
        assertEquals(eintrag.getZeitpunkt(), favorit.getZeitpunkt());
    }
}
