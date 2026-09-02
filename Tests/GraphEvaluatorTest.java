import common.parser.AusdruckParser;
import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import modes.graph.model.FunktionsDefinition;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;
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

    @Test
    void graphEvaluator_ShouldApproximateFirstAndSecondDerivative()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();

        // Act
        double first = evaluator.ersteAbleitung("x^3", 2.0, WinkelModus.DEG);
        double second = evaluator.zweiteAbleitung("x^3", 2.0, WinkelModus.DEG);

        // Assert
        assertEquals(12.0, first, 1e-5);
        assertEquals(12.0, second, 1e-3);
    }

    @Test
    void graphEvaluator_ShouldUseOtherGraphFunctions()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();
        FunktionsDefinition f = new FunktionsDefinition("f", "2x", Color.BLUE);
        FunktionsDefinition g = new FunktionsDefinition("g", "x^2+f(x)", Color.RED);
        evaluator.setFunktionen(List.of(f, g));

        // Act
        double result = evaluator.auswerten(g.getAusdruck(), 3.0, WinkelModus.DEG);

        // Assert
        assertEquals(15.0, result, 1e-10);
    }

    @Test
    void graphEvaluator_ShouldSupportNestedArgumentsAndShortNames()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();
        FunktionsDefinition f = new FunktionsDefinition("f", "2x", Color.BLUE);
        FunktionsDefinition g = new FunktionsDefinition("g", "f(x+1)+f", Color.RED);
        evaluator.setFunktionen(List.of(f, g));

        // Act
        double result = evaluator.auswerten(g.getAusdruck(), 3.0, WinkelModus.DEG);

        // Assert
        assertEquals(14.0, result, 1e-10);
    }

    @Test
    void graphEvaluator_ShouldRejectCircularFunctionReferences()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();
        FunktionsDefinition f = new FunktionsDefinition("f", "g(x)+1", Color.BLUE);
        FunktionsDefinition g = new FunktionsDefinition("g", "f(x)-1", Color.RED);
        evaluator.setFunktionen(List.of(f, g));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> evaluator.auswerten(f.getAusdruck(), 2.0, WinkelModus.DEG));
        assertTrue(exception.getMessage().contains("Funktionskreis erkannt"));
    }
}
