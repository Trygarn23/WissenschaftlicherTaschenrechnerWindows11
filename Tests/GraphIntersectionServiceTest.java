import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import modes.graph.logic.GraphIntersectionService;
import modes.graph.model.GraphPunkt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphIntersectionServiceTest
{
    @Test
    void findeSchnittpunkte_ShouldFindIntersectionBetweenLineAndParabola()
    {
        // Arrange
        GraphIntersectionService service = new GraphIntersectionService(new GraphEvaluator());

        // Act
        List<GraphPunkt> punkte = service.findeSchnittpunkte("x^2", "x+2", -4, 4, WinkelModus.DEG);

        // Assert
        assertTrue(punkte.stream().anyMatch(p -> Math.abs(p.getX() + 1.0) < 0.02 && Math.abs(p.getY() - 1.0) < 0.05));
        assertTrue(punkte.stream().anyMatch(p -> Math.abs(p.getX() - 2.0) < 0.02 && Math.abs(p.getY() - 4.0) < 0.05));
    }
}
