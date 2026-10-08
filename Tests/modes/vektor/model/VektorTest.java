package modes.vektor.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VektorTest
{
    @Test
    void zweiD_ShouldIgnoreZAndShowTwoComponents()
    {
        // Arrange
        Vektor vektor = new Vektor(1, 2, 99, 2);

        // Act
        String text = vektor.alsText();

        // Assert
        assertEquals(0, vektor.z());
        assertEquals("(1 | 2)", text);
    }

    @Test
    void alsText_ShouldUseGermanNotationAndRoundTinyErrors()
    {
        // Arrange
        Vektor vektor = Vektor.dreiD(0.1 + 0.2, -1.5, 1e-15);

        // Act
        String text = vektor.alsText();

        // Assert
        assertEquals("(0,3 | -1,5 | 0)", text);
    }

    @Test
    void konstruktor_ShouldRejectInvalidDimension()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> new Vektor(1, 2, 3, 4));

        // Assert
        assertEquals("Ein Vektor muss 2 oder 3 Komponenten haben.", fehler.getMessage());
    }
}
