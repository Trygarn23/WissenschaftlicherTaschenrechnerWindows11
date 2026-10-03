package modes.graph.ui;

import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphKoordinatenTest
{
    @Test
    void koordinaten_ShouldMapOriginToCenterAndBack()
    {
        // Arrange: Standardansicht ist x und y von -10 bis 10
        GraphState state = new GraphState();
        GraphKoordinaten k = new GraphKoordinaten(state, 401, 401);

        // Act
        int mitteX = k.zuBildschirmX(0.0);
        int mitteY = k.zuBildschirmY(0.0);

        // Assert
        assertEquals(state.getXMin(), -state.getXMax(), 1e-9);
        assertEquals(201, mitteX, 1);
        assertEquals(201, mitteY, 1);
        assertEquals(0.0, k.zuWeltX(200), 0.1);
        assertEquals(0.0, k.zuWeltY(200), 0.1);
        assertTrue(k.istSichtbar(new GraphPunkt(0, 0)));
        assertFalse(k.istSichtbar(new GraphPunkt(state.getXMax() + 1, 0)));
    }
}
