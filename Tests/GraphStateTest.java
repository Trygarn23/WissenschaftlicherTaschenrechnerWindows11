import modes.graph.model.GraphState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphStateTest
{
    @Test
    void graphState_ShouldStartWithDefaultFunctionAndViewport()
    {
        // Arrange
        GraphState state = new GraphState();

        // Assert
        assertEquals("sin(x)", state.getHauptfunktion().getAusdruck());
        assertEquals(-10.0, state.getXMin());
        assertEquals(10.0, state.getXMax());
        assertEquals(-10.0, state.getYMin());
        assertEquals(10.0, state.getYMax());
    }

    @Test
    void zoom_ShouldShrinkAndResetViewport()
    {
        // Arrange
        GraphState state = new GraphState();

        // Act
        state.zoom(0.5);

        // Assert
        assertEquals(-5.0, state.getXMin());
        assertEquals(5.0, state.getXMax());

        // Act
        state.resetAnsicht();

        // Assert
        assertEquals(-10.0, state.getXMin());
        assertEquals(10.0, state.getXMax());
    }

    @Test
    void verschiebe_ShouldMoveViewportWithoutChangingSpan()
    {
        // Arrange
        GraphState state = new GraphState();

        // Act
        state.verschiebe(3.0, -2.0);

        // Assert
        assertEquals(-7.0, state.getXMin());
        assertEquals(13.0, state.getXMax());
        assertEquals(-12.0, state.getYMin());
        assertEquals(8.0, state.getYMax());
    }

    @Test
    void funktionen_ShouldBeAddableSelectableAndRemovable()
    {
        // Arrange
        GraphState state = new GraphState();

        // Act
        state.fuegeFunktionHinzu("x^3");

        // Assert
        assertEquals(3, state.getFunktionen().size());
        assertEquals("h", state.getAktiveFunktion().getName());
        assertEquals("x^3", state.getAktiveFunktion().getAusdruck());

        // Act
        state.setAktiveFunktion(1);
        boolean entfernt = state.entferneFunktion(0);

        // Assert
        assertTrue(entfernt);
        assertEquals("g", state.getAktiveFunktion().getName());
        assertEquals(0, state.getAktiveFunktionIndex());
    }

    @Test
    void entferneFunktion_ShouldKeepAtLeastOneFunction()
    {
        // Arrange
        GraphState state = new GraphState();

        // Act
        assertTrue(state.entferneFunktion(1));
        boolean entfernt = state.entferneFunktion(0);

        // Assert
        assertFalse(entfernt);
        assertEquals(1, state.getFunktionen().size());
        assertEquals("f", state.getAktiveFunktion().getName());
    }
}
