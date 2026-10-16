package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.FunktionsDefinition;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GraphFunktionsResolverTest
{
    private static FunktionsDefinition f(String name, String ausdruck)
    {
        return new FunktionsDefinition(name, ausdruck, Color.BLUE);
    }

    @Test
    void auswerten_ShouldInsertValueOfReferencedFunction()
    {
        // Arrange
        GraphFunktionsResolver resolver = new GraphFunktionsResolver();
        List<FunktionsDefinition> funktionen = List.of(f("f", "x^2"), f("g", "f(x)+1"));

        // Act
        double wert = resolver.auswerten("g(3)", 0, WinkelModus.RAD, funktionen);

        // Assert
        assertEquals(10.0, wert, 1e-12);
    }

    @Test
    void auswerten_ShouldUseXAsArgument_WhenNoParenthesesFollow()
    {
        // Arrange
        GraphFunktionsResolver resolver = new GraphFunktionsResolver();
        List<FunktionsDefinition> funktionen = List.of(f("f", "2*x"));

        // Act
        double wert = resolver.auswerten("f+1", 4, WinkelModus.RAD, funktionen);

        // Assert
        assertEquals(9.0, wert, 1e-12);
    }

    @Test
    void auswerten_ShouldUseParameters()
    {
        // Arrange
        GraphFunktionsResolver resolver = new GraphFunktionsResolver();
        resolver.setParameter(Map.of("a", 5.0));
        List<FunktionsDefinition> funktionen = List.of(f("f", "a*x"));

        // Act
        double wert = resolver.auswerten("f(2)", 0, WinkelModus.RAD, funktionen);

        // Assert
        assertEquals(10.0, wert, 1e-12);
    }

    @Test
    void auswerten_ShouldThrow_WhenFunctionsReferenceEachOther()
    {
        // Arrange
        GraphFunktionsResolver resolver = new GraphFunktionsResolver();
        List<FunktionsDefinition> funktionen = List.of(f("f", "g(x)"), f("g", "f(x)"));

        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> resolver.auswerten("f(1)", 0, WinkelModus.RAD, funktionen));

        // Assert
        assertTrue(e.getMessage().startsWith("Funktionskreis erkannt"), e.getMessage());
    }

    @Test
    void auswerten_ShouldThrow_WhenClosingParenthesisIsMissing()
    {
        // Arrange
        GraphFunktionsResolver resolver = new GraphFunktionsResolver();
        List<FunktionsDefinition> funktionen = List.of(f("f", "x"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> resolver.auswerten("f(1", 0, WinkelModus.RAD, funktionen));
    }

    @Test
    void auswerten_ShouldIgnoreCaseOfFunctionName()
    {
        // Arrange
        GraphFunktionsResolver resolver = new GraphFunktionsResolver();
        List<FunktionsDefinition> funktionen = List.of(f("F", "x+1"));

        // Act
        double wert = resolver.auswerten("f(1)", 0, WinkelModus.RAD, funktionen);

        // Assert
        assertEquals(2.0, wert, 1e-12);
    }
}
