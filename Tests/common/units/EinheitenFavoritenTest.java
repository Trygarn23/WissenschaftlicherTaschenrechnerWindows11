package common.units;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EinheitenFavoritenTest
{
    @TempDir
    Path ordner;

    @Test
    void umschalten_ShouldPersistConversionsAcrossInstances()
    {
        // Arrange
        Path datei = ordner.resolve("unterordner/einheiten.txt");
        EinheitenFavoriten favoriten = new EinheitenFavoriten(datei);
        EinheitenFavoriten.Umrechnung meterZuKm = new EinheitenFavoriten.Umrechnung(EinheitKategorie.LAENGE, "Meter", "Kilometer");
        EinheitenFavoriten.Umrechnung celsiusZuF = new EinheitenFavoriten.Umrechnung(EinheitKategorie.TEMPERATUR, "Celsius", "Fahrenheit");

        // Act
        favoriten.umschalten(meterZuKm);
        favoriten.umschalten(celsiusZuF);
        favoriten.umschalten(meterZuKm);
        EinheitenFavoriten geladen = new EinheitenFavoriten(datei);

        // Assert
        assertEquals(List.of(celsiusZuF), geladen.alle());
        assertTrue(geladen.istFavorit(celsiusZuF));
        assertFalse(geladen.istFavorit(meterZuKm));
        assertEquals("Temperatur: Celsius → Fahrenheit", celsiusZuF.toString());
    }

    @Test
    void konstruktor_ShouldSkipBrokenAndDuplicateLines() throws IOException
    {
        // Arrange
        Path datei = ordner.resolve("einheiten.txt");
        Files.writeString(datei, "LAENGE;Meter;Kilometer\nGIBTSNICHT;a;b\nkaputt\nLAENGE;;Meter\nLAENGE;Meter;Kilometer\n");

        // Act
        EinheitenFavoriten favoriten = new EinheitenFavoriten(datei);

        // Assert
        assertEquals(List.of(new EinheitenFavoriten.Umrechnung(EinheitKategorie.LAENGE, "Meter", "Kilometer")), favoriten.alle());
    }
}
