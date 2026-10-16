package common.units;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EinheitenKatalogTest
{
    @Test
    void einheitenFuer_ShouldProvideAtLeastTwoUnits_ForEveryCategory()
    {
        // Arrange
        EinheitenKatalog katalog = new EinheitenKatalog();

        // Act & Assert
        for (EinheitKategorie kategorie : katalog.kategorien())
        {
            assertTrue(katalog.einheitenFuer(kategorie).size() >= 2, kategorie.getLabel());
        }
    }

    @Test
    void einheitenFuer_ShouldHaveUniqueSymbolsAndMatchingCategory()
    {
        // Arrange
        EinheitenKatalog katalog = new EinheitenKatalog();

        // Act & Assert
        for (EinheitKategorie kategorie : katalog.kategorien())
        {
            Set<String> symbole = new HashSet<>();
            for (Einheit einheit : katalog.einheitenFuer(kategorie))
            {
                assertEquals(kategorie, einheit.kategorie());
                assertTrue(symbole.add(einheit.symbol()), "doppeltes Symbol " + einheit.symbol());
            }
        }
    }

    @Test
    void einheitenFuer_ShouldContainBaseUnitWithFactorOne_ForLength()
    {
        // Arrange
        EinheitenKatalog katalog = new EinheitenKatalog();

        // Act
        List<Einheit> laenge = katalog.einheitenFuer(EinheitKategorie.LAENGE);

        // Assert
        assertTrue(laenge.stream().anyMatch(e -> e.symbol().equals("m") && e.faktorZurBasiseinheit() == 1.0));
    }

    @Test
    void celsiusToFahrenheit_ShouldConvertViaBase_WhenUsingCatalogUnits()
    {
        // Arrange
        List<Einheit> temperatur = new EinheitenKatalog().einheitenFuer(EinheitKategorie.TEMPERATUR);
        Einheit celsius = temperatur.stream().filter(e -> e.symbol().equals("C")).findFirst().orElseThrow();
        Einheit fahrenheit = temperatur.stream().filter(e -> e.symbol().equals("F")).findFirst().orElseThrow();

        // Act
        double f = fahrenheit.ausBasis(celsius.nachBasis(100));

        // Assert
        assertEquals(212.0, f, 1e-9);
    }
}
