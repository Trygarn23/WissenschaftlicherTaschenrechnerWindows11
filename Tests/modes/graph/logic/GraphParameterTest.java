package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.FunktionsDefinition;
import modes.graph.model.Tangente;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GraphParameterTest
{
    @Test
    void findeParameter_ShouldFindSingleLetterParameters()
    {
        // Arrange
        List<FunktionsDefinition> funktionen = List.of(
                new FunktionsDefinition("f", "a*x^2+b", Color.BLUE),
                new FunktionsDefinition("g", "2k*sin(x)", Color.RED));

        // Act
        Set<String> parameter = GraphEvaluator.findeParameter(funktionen);

        // Assert
        assertEquals(Set.of("a", "b", "k"), parameter);
    }

    @Test
    void findeParameter_ShouldIgnoreFunctionNamesConstantsAndX()
    {
        // Arrange
        List<FunktionsDefinition> funktionen = List.of(
                new FunktionsDefinition("f", "abs(x)+acos(x)+tan(x)+sec(x)+cbrt(x)+e+pi+π+x", Color.BLUE));

        // Act
        Set<String> parameter = GraphEvaluator.findeParameter(funktionen);

        // Assert
        assertTrue(parameter.isEmpty(), "gefunden: " + parameter);
    }

    @Test
    void findeParameter_ShouldIgnoreLetterThatIsAFunctionName()
    {
        // Arrange
        List<FunktionsDefinition> funktionen = List.of(
                new FunktionsDefinition("k", "x+1", Color.BLUE),
                new FunktionsDefinition("g", "k(x)*c", Color.RED));

        // Act
        Set<String> parameter = GraphEvaluator.findeParameter(funktionen);

        // Assert
        assertEquals(Set.of("c"), parameter);
    }

    @Test
    void findeParameter_ShouldTreatWhitespaceLikeParser()
    {
        // Arrange – der Parser entfernt Leerzeichen, "a x" wird also zu "ax" (kein Parameter)
        List<FunktionsDefinition> funktionen = List.of(new FunktionsDefinition("f", "a x", Color.BLUE));

        // Act & Assert
        assertTrue(GraphEvaluator.findeParameter(funktionen).isEmpty());
    }

    @Test
    void auswerten_ShouldUseParameterValues()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();
        evaluator.setParameter(Map.of("a", 0.5, "b", -1.0));

        // Act
        double y = evaluator.auswerten("a*x^2+b", 4.0, WinkelModus.DEG);

        // Assert
        assertEquals(7.0, y, 1e-10);
    }

    @Test
    void auswerten_ShouldKeepFunctionsWorking_WhenParametersAreSet()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();
        evaluator.setParameter(Map.of("a", 2.0, "c", 3.0));

        // Act & Assert
        assertEquals(5.0, evaluator.auswerten("abs(x)", -5.0, WinkelModus.DEG), 1e-10);
        assertEquals(0.0, evaluator.auswerten("acos(x)", 1.0, WinkelModus.DEG), 1e-10);
        assertEquals(1.0, evaluator.auswerten("tan(x)", 45.0, WinkelModus.DEG), 1e-10);
        assertEquals(10.0, evaluator.auswerten("2a+c+x", 3.0, WinkelModus.DEG), 1e-10);
    }

    @Test
    void auswerten_ShouldPassParametersIntoReferencedFunctions()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();
        FunktionsDefinition f = new FunktionsDefinition("f", "a*x", Color.BLUE);
        FunktionsDefinition g = new FunktionsDefinition("g", "f(x)+1", Color.RED);
        evaluator.setFunktionen(List.of(f, g));
        evaluator.setParameter(Map.of("a", 3.0));

        // Act
        double y = evaluator.auswerten(g.getAusdruck(), 2.0, WinkelModus.DEG);

        // Assert
        assertEquals(7.0, y, 1e-10);
    }

    @Test
    void auswerten_ShouldReject_WhenParameterHasNoValue()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.auswerten("a*x", 1.0, WinkelModus.DEG));
    }

    @Test
    void tangente_ShouldUseNumericDerivative()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();

        // Act
        Tangente tangente = evaluator.tangente(0, "x^2", 1.0, WinkelModus.DEG);

        // Assert
        assertEquals(1.0, tangente.y0(), 1e-10);
        assertEquals(2.0, tangente.steigung(), 1e-6);
        assertEquals(-1.0, tangente.achsenabschnitt(), 1e-6);
        assertEquals(5.0, tangente.wert(3.0), 1e-6);
    }

    @Test
    void tangente_ShouldBeNull_WhenFunctionIsUndefined()
    {
        // Arrange
        GraphEvaluator evaluator = new GraphEvaluator();

        // Act & Assert
        assertNull(evaluator.tangente(0, "sqrt(x)", -1.0, WinkelModus.DEG));
        assertNull(evaluator.tangente(0, "1/x", 0.0, WinkelModus.DEG));
    }
}
