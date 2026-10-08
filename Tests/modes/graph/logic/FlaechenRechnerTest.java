package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.Flaeche;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlaechenRechnerTest
{
    private final FlaechenRechner rechner = new FlaechenRechner(new GraphEvaluator());

    @Test
    void berechne_ShouldIntegratePolynomial()
    {
        // Act
        Flaeche flaeche = rechner.berechne(0, "x^2", 0.0, 3.0, WinkelModus.RAD);

        // Assert
        assertEquals(9.0, flaeche.integral(), 1e-9);
        assertEquals(9.0, flaeche.flaecheninhalt(), 1e-9);
    }

    @Test
    void berechne_ShouldCountAreaBelowAxisNegative_AndGiveAbsoluteArea()
    {
        // Act
        Flaeche flaeche = rechner.berechne(0, "x", -1.0, 1.0, WinkelModus.RAD);
        Flaeche unten = rechner.berechne(0, "-(x^2)", 0.0, 3.0, WinkelModus.RAD);

        // Assert
        assertEquals(0.0, flaeche.integral(), 1e-9);
        assertEquals(1.0, flaeche.flaecheninhalt(), 1e-6);
        assertEquals(-9.0, unten.integral(), 1e-9);
        assertEquals(9.0, unten.flaecheninhalt(), 1e-9);
    }

    @Test
    void berechne_ShouldFlipSign_WhenBoundsAreSwapped()
    {
        // Act
        Flaeche flaeche = rechner.berechne(0, "x^2", 3.0, 0.0, WinkelModus.RAD);

        // Assert
        assertEquals(-9.0, flaeche.integral(), 1e-9);
        assertEquals(9.0, flaeche.flaecheninhalt(), 1e-9);
        assertEquals(3.0, flaeche.a());
        assertEquals(0.0, flaeche.b());
    }

    @Test
    void berechne_ShouldRespectAngleMode()
    {
        // Act
        Flaeche flaeche = rechner.berechne(0, "sin(x)", 0.0, Math.PI, WinkelModus.RAD);

        // Assert
        assertEquals(2.0, flaeche.integral(), 1e-9);
    }

    @Test
    void berechne_ShouldHandleRootSingularityInDerivative()
    {
        // Act
        Flaeche flaeche = rechner.berechne(0, "sqrt(x)", 0.0, 1.0, WinkelModus.RAD);

        // Assert
        assertEquals(2.0 / 3.0, flaeche.integral(), 1e-4);
    }

    @Test
    void berechne_ShouldReject_WhenPoleLiesInInterval()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rechner.berechne(0, "1/x", -1.0, 1.0, WinkelModus.RAD));
        assertThrows(IllegalArgumentException.class, () -> rechner.berechne(0, "1/x", -1.0, 1.001, WinkelModus.RAD));
        assertThrows(IllegalArgumentException.class, () -> rechner.berechne(0, "1/x^2", -1.0, 1.0003, WinkelModus.RAD));
        assertThrows(IllegalArgumentException.class, () -> rechner.berechne(0, "tan(x)", 0.0, 3.0, WinkelModus.RAD));
    }

    @Test
    void berechne_ShouldReject_WhenFunctionIsUndefinedInInterval()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> rechner.berechne(0, "sqrt(x)", -1.0, 1.0, WinkelModus.RAD));

        // Assert
        assertTrue(fehler.getMessage().contains("nicht überall definiert"));
    }

    @Test
    void berechne_ShouldReject_WhenBoundsAreEqualOrInvalid()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rechner.berechne(0, "x", 2.0, 2.0, WinkelModus.RAD));
        assertThrows(IllegalArgumentException.class, () -> rechner.berechne(0, "x", Double.NaN, 2.0, WinkelModus.RAD));
    }
}
