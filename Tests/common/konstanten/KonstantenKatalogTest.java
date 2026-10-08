package common.konstanten;

import common.parser.AusdruckParser;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KonstantenKatalogTest
{
    @Test
    void standard_ShouldHaveUniqueNamesFiniteValuesAndEveryBuiltInCategory()
    {
        // Arrange
        Set<String> namen = new HashSet<>();

        // Act & Assert
        assertTrue(KonstantenKatalog.STANDARD.size() >= 40);
        for (Konstante k : KonstantenKatalog.STANDARD)
        {
            assertTrue(namen.add(k.name().toLowerCase()), "Doppelter Name: " + k.name());
            assertTrue(Double.isFinite(k.wert()), k.name());
            assertNotEquals(KonstantenKategorie.EIGENE, k.kategorie());
        }
        for (KonstantenKategorie kategorie : List.of(KonstantenKategorie.MATHEMATIK, KonstantenKategorie.PHYSIK,
                KonstantenKategorie.CHEMIE, KonstantenKategorie.INFORMATIK))
        {
            assertFalse(KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "", kategorie).isEmpty(), kategorie.name());
        }
    }

    @Test
    void standard_ShouldContainExactCodata2018Values()
    {
        // Act & Assert
        assertEquals(299792458.0, wert("Lichtgeschwindigkeit"));
        assertEquals(6.62607015e-34, wert("Planck-Konstante"));
        assertEquals(1.602176634e-19, wert("Elementarladung"));
        assertEquals(1.380649e-23, wert("Boltzmann-Konstante"));
        assertEquals(6.02214076e23, wert("Avogadro-Konstante"));
        assertEquals(9.80665, wert("Normfallbeschleunigung"));
        assertEquals(6.67430e-11, wert("Gravitationskonstante"));
        assertEquals(1.054571817e-34, wert("Reduzierte Planck-Konstante"), 1e-43);
        assertEquals(8.314462618, wert("Universelle Gaskonstante"), 1e-9);
        assertEquals(96485.33212, wert("Faraday-Konstante"), 1e-5);
        assertEquals(1.618033988749895, wert("Goldener Schnitt"), 1e-15);
        assertEquals(4294967296.0, wert("Zwei hoch 32"));
        assertEquals(1073741824.0, wert("Bytes pro Gibibyte"));
    }

    @Test
    void alsEingabe_ShouldBeParsedBackToTheSameValue_ForEveryConstant()
    {
        for (Konstante k : KonstantenKatalog.STANDARD)
        {
            // Act
            double gelesen = AusdruckParser.auswerten(k.alsEingabe(), 0.0, WinkelModus.DEG);

            // Assert
            assertEquals(k.wert(), gelesen, Math.abs(k.wert()) * 1e-15, k.name() + " → " + k.alsEingabe());
        }
    }

    @Test
    void alsEingabe_ShouldUseDecimalCommaAndLowercaseExponent()
    {
        // Act & Assert
        assertEquals("6,62607015e-34", konstante("Planck-Konstante").alsEingabe());
        assertEquals("299792458", konstante("Lichtgeschwindigkeit").alsEingabe());
        assertEquals("6,02214076e23", konstante("Avogadro-Konstante").alsEingabe());
        assertEquals("3,141592653589793", konstante("Kreiszahl Pi").alsEingabe());
        assertEquals("(-1,5)", new Konstante("x", "", -1.5, "", KonstantenKategorie.EIGENE, "").alsEingabe());
        assertEquals("0", new Konstante("x", "", 0.0, "", KonstantenKategorie.EIGENE, "").alsEingabe());
    }

    @Test
    void suche_ShouldIgnoreCaseAndMatchNameSymbolOrDescription()
    {
        // Act
        List<Konstante> boltzmann = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "boltzmann", null);
        List<Konstante> symbol = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "N_A", null);
        List<Konstante> beschreibung = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "WIRKUNGSQUANTUM", null);
        List<Konstante> ohneAkzent = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "apery", null);
        List<Konstante> mehrereWoerter = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "planck reduzierte", null);

        // Assert
        assertTrue(boltzmann.stream().anyMatch(k -> k.name().equals("Boltzmann-Konstante")));
        assertTrue(boltzmann.stream().anyMatch(k -> k.name().equals("Stefan-Boltzmann-Konstante")));
        assertTrue(symbol.stream().anyMatch(k -> k.name().equals("Avogadro-Konstante")));
        assertEquals("Planck-Konstante", beschreibung.getFirst().name());
        assertEquals("Apéry-Konstante", ohneAkzent.getFirst().name());
        assertEquals(List.of("Reduzierte Planck-Konstante"), mehrereWoerter.stream().map(Konstante::name).toList());
    }

    @Test
    void suche_ShouldFilterByCategoryAndReturnEmptyListWithoutMatch()
    {
        // Act
        List<Konstante> chemie = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "konstante", KonstantenKategorie.CHEMIE);
        List<Konstante> nichts = KonstantenKatalog.suche(KonstantenKatalog.STANDARD, "gibtsnicht", null);

        // Assert
        assertFalse(chemie.isEmpty());
        assertTrue(chemie.stream().allMatch(k -> k.kategorie() == KonstantenKategorie.CHEMIE));
        assertTrue(nichts.isEmpty());
    }

    private static Konstante konstante(String name)
    {
        return KonstantenKatalog.STANDARD.stream().filter(k -> k.name().equals(name)).findFirst().orElseThrow();
    }

    private static double wert(String name)
    {
        return konstante(name).wert();
    }
}
