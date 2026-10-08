package common.konstanten;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EigeneKonstantenTest
{
    @TempDir
    Path ordner;

    @Test
    void fuegeHinzu_ShouldPersistConstantInCategoryEigene()
    {
        // Arrange
        Path datei = ordner.resolve("eigene.txt");
        EigeneKonstanten eigene = new EigeneKonstanten(datei);

        // Act
        Konstante neu = eigene.fuegeHinzu("  Mehrwertsteuer ", "MwSt", "0,19", "");
        EigeneKonstanten geladen = new EigeneKonstanten(datei);

        // Assert
        assertEquals("Mehrwertsteuer", neu.name());
        assertEquals(KonstantenKategorie.EIGENE, neu.kategorie());
        assertEquals(List.of(neu), geladen.alle());
    }

    @Test
    void fuegeHinzu_ShouldRejectInvalidInputWithGermanMessage()
    {
        // Arrange
        EigeneKonstanten eigene = new EigeneKonstanten(ordner.resolve("eigene.txt"));
        eigene.fuegeHinzu("Meine", "m", "1", "");

        // Act & Assert
        assertEquals("Bitte einen Namen eingeben.", fehler(eigene, " ", "x", "1"));
        assertEquals("Name, Symbol und Einheit dürfen kein „;“ enthalten.", fehler(eigene, "a;b", "x", "1"));
        assertEquals("Der Wert muss eine endliche Zahl sein.", fehler(eigene, "Unendlich", "x", "Infinity"));
        assertEquals("Der Wert muss eine endliche Zahl sein.", fehler(eigene, "Keine Zahl", "x", "NaN"));
        assertEquals("„abc“ ist keine gültige Zahl.", fehler(eigene, "Text", "x", "abc"));
        assertEquals("Eine Konstante „lichtgeschwindigkeit“ gibt es schon.", fehler(eigene, "lichtgeschwindigkeit", "c", "3"));
        assertEquals("Eine Konstante „MEINE“ gibt es schon.", fehler(eigene, "MEINE", "x", "2"));
        assertEquals(1, eigene.alle().size());
    }

    @Test
    void konstruktor_ShouldSkipBrokenLinesAndKeepValidOnes() throws IOException
    {
        // Arrange
        Path datei = ordner.resolve("eigene.txt");
        Files.writeString(datei, """
                Gut;g;1.5;kg
                zu;wenig;Teile
                ;leer;1;
                Kaputt;k;abc;
                Unendlich;u;Infinity;
                Gut;doppelt;2;
                Planck-Konstante;h;1;
                """);

        // Act
        EigeneKonstanten eigene = new EigeneKonstanten(datei);

        // Assert
        assertEquals(List.of(new Konstante("Gut", "g", 1.5, "kg", KonstantenKategorie.EIGENE, "Eigene Konstante")), eigene.alle());
    }

    @Test
    void konstruktor_ShouldStartEmpty_WhenPathIsDirectory() throws IOException
    {
        // Arrange
        Path ordnerStattDatei = Files.createDirectory(ordner.resolve("eigene.txt"));

        // Act & Assert
        assertTrue(new EigeneKonstanten(ordnerStattDatei).alle().isEmpty());
    }

    @Test
    void entferne_ShouldRemoveConstantPersistently()
    {
        // Arrange
        Path datei = ordner.resolve("eigene.txt");
        EigeneKonstanten eigene = new EigeneKonstanten(datei);
        eigene.fuegeHinzu("Eins", "", "1", "");
        eigene.fuegeHinzu("Zwei", "", "2", "");

        // Act
        eigene.entferne("Eins");

        // Assert
        assertEquals(List.of("Zwei"), new EigeneKonstanten(datei).alle().stream().map(Konstante::name).toList());
    }

    private static String fehler(EigeneKonstanten eigene, String name, String symbol, String wert)
    {
        return assertThrows(IllegalArgumentException.class, () -> eigene.fuegeHinzu(name, symbol, wert, "")).getMessage();
    }
}
