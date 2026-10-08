package ui.befehle;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BefehlsSucheTest
{
    private static final List<Befehl> ALLE = List.of(
            befehl("Standard", "Modus"),
            befehl("Wissenschaftlich", "Modus"),
            befehl("Dunkles Theme", "Theme"),
            befehl("Sinus", "Funktion", "sin"),
            befehl("Lichtgeschwindigkeit", "Konstante", "c"),
            befehl("Verlauf löschen", "Aktion"),
            befehl("Über das Programm", "Aktion")
    );

    @Test
    void filtere_ShouldReturnAllInOriginalOrder_WhenTextIsEmpty()
    {
        // Act
        List<Befehl> treffer = BefehlsSuche.filtere(ALLE, "  ", 3);

        // Assert
        assertEquals(List.of("Standard", "Wissenschaftlich", "Dunkles Theme"), titel(treffer));
    }

    @Test
    void filtere_ShouldRankTitleStartBeforeWordStartBeforeContainsBeforeFuzzy()
    {
        // Arrange
        List<Befehl> befehle = List.of(
                befehl("Xtaxa", "Aktion"),
                befehl("Neues Tab", "Aktion"),
                befehl("Ta-Rechner", "Modus"),
                befehl("Tabelle", "Aktion"),
                befehl("Torwart", "Aktion")
        );

        // Act
        List<Befehl> treffer = BefehlsSuche.filtere(befehle, "ta", 10);

        // Assert
        assertEquals(List.of("Ta-Rechner", "Tabelle", "Neues Tab", "Xtaxa", "Torwart"), titel(treffer));
    }

    @Test
    void filtere_ShouldIgnoreCase()
    {
        // Act
        List<Befehl> treffer = BefehlsSuche.filtere(ALLE, "WISSEN", 10);

        // Assert
        assertEquals("Wissenschaftlich", treffer.getFirst().titel());
    }

    @Test
    void filtere_ShouldFindFuzzyLetters()
    {
        // Act
        List<Befehl> treffer = BefehlsSuche.filtere(ALLE, "wsn", 10);

        // Assert
        assertEquals(List.of("Wissenschaftlich"), titel(treffer));
    }

    @Test
    void filtere_ShouldTreatUmlautAndAeAlike()
    {
        // Act
        List<Befehl> mitAe = BefehlsSuche.filtere(ALLE, "ueber", 10);
        List<Befehl> mitUmlaut = BefehlsSuche.filtere(ALLE, "lösch", 10);

        // Assert
        assertEquals("Über das Programm", mitAe.getFirst().titel());
        assertEquals("Verlauf löschen", mitUmlaut.getFirst().titel());
    }

    @Test
    void filtere_ShouldMatchCategoryAndSearchTerms()
    {
        // Act
        List<Befehl> perKategorie = BefehlsSuche.filtere(ALLE, "konstante", 10);
        List<Befehl> perSuchbegriff = BefehlsSuche.filtere(ALLE, "sin", 10);

        // Assert
        assertEquals(List.of("Lichtgeschwindigkeit"), titel(perKategorie));
        assertEquals("Sinus", perSuchbegriff.getFirst().titel());
    }

    @Test
    void filtere_ShouldReturnEmpty_WhenNothingMatches()
    {
        // Act
        List<Befehl> treffer = BefehlsSuche.filtere(ALLE, "qqq", 10);

        // Assert
        assertTrue(treffer.isEmpty());
    }

    @Test
    void filtere_ShouldRespectMax()
    {
        // Act
        List<Befehl> treffer = BefehlsSuche.filtere(ALLE, "e", 2);

        // Assert
        assertEquals(2, treffer.size());
    }

    private static Befehl befehl(String titel, String kategorie, String... suchbegriffe)
    {
        return new Befehl(titel, kategorie, List.of(suchbegriffe), () -> { });
    }

    private static List<String> titel(List<Befehl> befehle)
    {
        return befehle.stream().map(Befehl::titel).toList();
    }
}
