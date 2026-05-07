import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import modes.graph.logic.KurvendiskussionService;
import modes.graph.model.KurvendiskussionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KurvendiskussionServiceTest
{
    @Test
    void analysiere_ShouldFindImportantPointsForQuadraticFunction()
    {
        // Arrange
        KurvendiskussionService service = new KurvendiskussionService(new GraphEvaluator());

        // Act
        KurvendiskussionResult result = service.analysiere("x^2-4", -5, 5, WinkelModus.DEG);

        // Assert
        assertEquals(-4.0, result.getYAchsenSchnittpunkt().getY(), 1e-6);
        assertTrue(result.getNullstellen().stream().anyMatch(p -> Math.abs(p.getX() + 2.0) < 0.02));
        assertTrue(result.getNullstellen().stream().anyMatch(p -> Math.abs(p.getX() - 2.0) < 0.02));
        assertTrue(result.getExtremstellen().stream().anyMatch(p -> Math.abs(p.getX()) < 0.02 && Math.abs(p.getY() + 4.0) < 0.02));
    }

    @Test
    void analysiere_ShouldFindInflectionPointForCubicFunction()
    {
        // Arrange
        KurvendiskussionService service = new KurvendiskussionService(new GraphEvaluator());

        // Act
        KurvendiskussionResult result = service.analysiere("x^3", -3, 3, WinkelModus.DEG);

        // Assert
        assertTrue(result.getWendestellen().stream().anyMatch(p -> Math.abs(p.getX()) < 0.03));
    }
}
