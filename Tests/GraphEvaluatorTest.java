import common.parser.AusdruckParser;
import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GraphEvaluatorTest
{
    @Test
    void parser_ShouldEvaluateExpressionWithVariableX()
    {
        // Act
        double actual = AusdruckParser.auswerten("x^2+2x+1", 0.0, WinkelModus.DEG, Map.of("x", 3.0));

        // Assert
        assertEquals(16.0, actual, 1e-10);
    }

    @Test
    void parser_ShouldKeepUnknownIdentifierInvalid_WhenVariableIsMissing()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> AusdruckParser.auswerten("x+1", 0.0, WinkelModus.DEG));
    }

    @Test
    void graphEvaluator_ShouldRespectAngleMode()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();

        // Act
        double deg = evaluator.auswerten("sin(x)", 90.0, WinkelModus.DEG);
        double rad = evaluator.auswerten("sin(x)", Math.PI / 2.0, WinkelModus.RAD);

        // Assert
        assertEquals(1.0, deg, 1e-10);
        assertEquals(1.0, rad, 1e-10);
    }
}
