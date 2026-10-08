package modes.vektor.logic;

import modes.vektor.model.Vektor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VektorRechnerTest
{
    private static final double DELTA = 1e-9;

    @Test
    void addiereUndSubtrahiere_ShouldWorkIn2DAnd3D()
    {
        // Arrange
        Vektor a2 = Vektor.zweiD(1, 2);
        Vektor b2 = Vektor.zweiD(3, -4);
        Vektor a3 = Vektor.dreiD(1, 2, 3);
        Vektor b3 = Vektor.dreiD(4, 5, 6);

        // Act & Assert
        assertEquals(Vektor.zweiD(4, -2), VektorRechner.addiere(a2, b2));
        assertEquals(Vektor.zweiD(-2, 6), VektorRechner.subtrahiere(a2, b2));
        assertEquals(Vektor.dreiD(5, 7, 9), VektorRechner.addiere(a3, b3));
        assertEquals(Vektor.dreiD(-3, -3, -3), VektorRechner.subtrahiere(a3, b3));
    }

    @Test
    void skaliere_ShouldMultiplyEveryComponentAndKeepDimension()
    {
        // Act
        Vektor ergebnis = VektorRechner.skaliere(Vektor.dreiD(1, -2, 0.5), 2);

        // Assert
        assertEquals(Vektor.dreiD(2, -4, 1), ergebnis);
        assertEquals(2, VektorRechner.skaliere(Vektor.zweiD(1, 1), 3).dimension());
    }

    @Test
    void operationen_ShouldRejectDifferentDimensions()
    {
        // Arrange
        Vektor a = Vektor.zweiD(1, 2);
        Vektor b = Vektor.dreiD(1, 2, 3);

        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> VektorRechner.addiere(a, b));

        // Assert
        assertEquals("Beide Vektoren brauchen die gleiche Dimension (2D oder 3D).", fehler.getMessage());
        assertThrows(IllegalArgumentException.class, () -> VektorRechner.skalarprodukt(a, b));
        assertThrows(IllegalArgumentException.class, () -> VektorRechner.abstand(a, b));
    }

    @Test
    void skalarproduktUndBetrag_ShouldMatchKnownValues()
    {
        // Act & Assert
        assertEquals(32, VektorRechner.skalarprodukt(Vektor.dreiD(1, 2, 3), Vektor.dreiD(4, 5, 6)), DELTA);
        assertEquals(0, VektorRechner.skalarprodukt(Vektor.zweiD(1, 0), Vektor.zweiD(0, 5)), DELTA);
        assertEquals(5, VektorRechner.betrag(Vektor.zweiD(3, 4)), DELTA);
        assertEquals(3, VektorRechner.betrag(Vektor.dreiD(1, 2, 2)), DELTA);
        assertEquals(0, VektorRechner.betrag(Vektor.dreiD(0, 0, 0)), DELTA);
    }

    @Test
    void winkelInGrad_ShouldReturnDegrees()
    {
        // Act & Assert
        assertEquals(90, VektorRechner.winkelInGrad(Vektor.zweiD(1, 0), Vektor.zweiD(0, 2)), DELTA);
        assertEquals(45, VektorRechner.winkelInGrad(Vektor.zweiD(1, 0), Vektor.zweiD(1, 1)), DELTA);
        assertEquals(180, VektorRechner.winkelInGrad(Vektor.dreiD(1, 2, 3), Vektor.dreiD(-2, -4, -6)), DELTA);
    }

    @Test
    void winkelInGrad_ShouldReturnZeroForParallelVectorsDespiteRounding()
    {
        // Act
        double winkel = VektorRechner.winkelInGrad(Vektor.dreiD(0.1, 0.2, 0.3), Vektor.dreiD(0.3, 0.6, 0.9));

        // Assert
        assertFalse(Double.isNaN(winkel));
        assertEquals(0, winkel, 1e-6);
    }

    @Test
    void winkelInGrad_ShouldRejectNullVector()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> VektorRechner.winkelInGrad(Vektor.zweiD(0, 0), Vektor.zweiD(1, 1)));

        // Assert
        assertEquals("Mit dem Nullvektor gibt es keinen Winkel.", fehler.getMessage());
    }

    @Test
    void kreuzprodukt_ShouldWorkFor3DAndRejectIn2D()
    {
        // Act
        Vektor ergebnis = VektorRechner.kreuzprodukt(Vektor.dreiD(1, 0, 0), Vektor.dreiD(0, 1, 0));
        Vektor allgemein = VektorRechner.kreuzprodukt(Vektor.dreiD(1, 2, 3), Vektor.dreiD(4, 5, 6));
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> VektorRechner.kreuzprodukt(Vektor.zweiD(1, 0), Vektor.zweiD(0, 1)));

        // Assert
        assertEquals(Vektor.dreiD(0, 0, 1), ergebnis);
        assertEquals(Vektor.dreiD(-3, 6, -3), allgemein);
        assertEquals("Das Kreuzprodukt gibt es nur für 3D-Vektoren.", fehler.getMessage());
    }

    @Test
    void abstandUndMittelpunkt_ShouldWorkIn2DAnd3D()
    {
        // Act & Assert
        assertEquals(5, VektorRechner.abstand(Vektor.zweiD(1, 1), Vektor.zweiD(4, 5)), DELTA);
        assertEquals(3, VektorRechner.abstand(Vektor.dreiD(1, 1, 1), Vektor.dreiD(2, 3, 3)), DELTA);
        assertEquals(Vektor.zweiD(2.5, 3), VektorRechner.mittelpunkt(Vektor.zweiD(1, 1), Vektor.zweiD(4, 5)));
        assertEquals(Vektor.dreiD(1, 0, -1), VektorRechner.mittelpunkt(Vektor.dreiD(0, 0, 0), Vektor.dreiD(2, 0, -2)));
    }

    @Test
    void steigung_ShouldComputeSlopeIn2D()
    {
        // Act & Assert
        assertEquals(2, VektorRechner.steigung(Vektor.zweiD(0, 1), Vektor.zweiD(2, 5)), DELTA);
        assertEquals(0, VektorRechner.steigung(Vektor.zweiD(-1, 3), Vektor.zweiD(4, 3)), DELTA);
    }

    @Test
    void steigung_ShouldRejectVerticalLineAnd3D()
    {
        // Act
        IllegalArgumentException senkrecht = assertThrows(IllegalArgumentException.class,
                () -> VektorRechner.steigung(Vektor.zweiD(2, 1), Vektor.zweiD(2, 7)));
        IllegalArgumentException dreiD = assertThrows(IllegalArgumentException.class,
                () -> VektorRechner.steigung(Vektor.dreiD(0, 0, 0), Vektor.dreiD(1, 1, 1)));

        // Assert
        assertEquals("Steigung nicht definiert (senkrechte Gerade).", senkrecht.getMessage());
        assertEquals("Die Steigung gibt es nur für 2D-Punkte.", dreiD.getMessage());
    }
}
