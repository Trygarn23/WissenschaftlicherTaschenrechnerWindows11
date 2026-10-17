package common.history;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerlaufListeTest
{
    private static VerlaufEintrag eintrag(String ausdruck, int minute)
    {
        return new VerlaufEintrag(ausdruck, "1", RechnerModus.STANDARD, LocalDateTime.of(2026, 1, 1, 12, minute), false);
    }

    @Test
    void ersetzeAlle_ShouldSkipBlankEntries()
    {
        // Arrange
        VerlaufListe liste = new VerlaufListe();
        VerlaufEintrag leer = new VerlaufEintrag("", "", RechnerModus.STANDARD, LocalDateTime.of(2026, 1, 1, 12, 0), false);

        // Act
        liste.ersetzeAlle(Arrays.asList(eintrag("1+1", 0), null, leer));

        // Assert
        assertEquals(List.of(eintrag("1+1", 0)), liste.alle());
    }

    @Test
    void macheLoeschenRueckgaengig_ShouldRestoreEntries_OnlyOnce()
    {
        // Arrange
        VerlaufListe liste = new VerlaufListe();
        liste.ersetzeAlle(List.of(eintrag("1+1", 0), eintrag("2+2", 1)));

        // Act
        liste.entferne(List.of(eintrag("1+1", 0)));
        boolean ersterVersuch = liste.macheLoeschenRueckgaengig();
        boolean zweiterVersuch = liste.macheLoeschenRueckgaengig();

        // Assert
        assertTrue(ersterVersuch);
        assertFalse(zweiterVersuch);
        assertEquals(2, liste.alle().size());
    }

    @Test
    void fuegeHinzu_ShouldForgetUndo_WhenNewEntryArrives()
    {
        // Arrange
        VerlaufListe liste = new VerlaufListe();
        liste.ersetzeAlle(List.of(eintrag("1+1", 0)));
        liste.entferne(liste.alle());

        // Act
        liste.fuegeHinzu(eintrag("3+3", 2));

        // Assert
        assertFalse(liste.kannRueckgaengig());
        assertEquals(List.of(eintrag("3+3", 2)), liste.alle());
    }

    @Test
    void importiere_ShouldMergeWithoutDuplicates_AndSortByTime()
    {
        // Arrange
        VerlaufListe liste = new VerlaufListe();
        liste.ersetzeAlle(List.of(eintrag("b", 5)));

        // Act
        liste.importiere(List.of(eintrag("b", 5), eintrag("a", 1)));

        // Assert
        assertEquals(List.of(eintrag("a", 1), eintrag("b", 5)), liste.alle());
    }

    @Test
    void schalteFavorit_ShouldReplaceEntry_AndIgnoreUnknownEntries()
    {
        // Arrange
        VerlaufListe liste = new VerlaufListe();
        liste.ersetzeAlle(List.of(eintrag("1+1", 0)));

        // Act
        var geaendert = liste.schalteFavorit(eintrag("1+1", 0));
        var unbekannt = liste.schalteFavorit(eintrag("9+9", 9));

        // Assert
        assertTrue(geaendert.orElseThrow().isFavorit());
        assertTrue(liste.alle().get(0).isFavorit());
        assertTrue(unbekannt.isEmpty());
    }
}
