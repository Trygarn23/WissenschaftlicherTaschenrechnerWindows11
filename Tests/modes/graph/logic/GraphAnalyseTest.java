package modes.graph.logic;

import common.state.WinkelModus;
import modes.graph.model.FunktionsDefinition;
import modes.graph.model.GraphState;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphAnalyseTest
{
    private final GraphEvaluator evaluator = new GraphEvaluator();
    private final GraphAnalyse analyse = new GraphAnalyse(evaluator);
    private final GraphState state = new GraphState();

    private void setzeFunktionen(String... ausdruecke)
    {
        List<FunktionsDefinition> funktionen = new ArrayList<>();
        for (int i = 0; i < ausdruecke.length; i++)
        {
            funktionen.add(new FunktionsDefinition("f" + i, ausdruecke[i], Color.BLACK));
        }
        state.ersetzeFunktionen(funktionen);
        evaluator.setFunktionen(state.getFunktionen());
    }

    @Test
    void aktualisiere_ShouldRecomputeTangentAfterExpressionChange()
    {
        // Arrange
        setzeFunktionen("x^2");
        analyse.setzeTangente(state, 0, 1, WinkelModus.RAD);
        state.getFunktion(0).setAusdruck("x^3");

        // Act
        String fehler = analyse.aktualisiere(state, WinkelModus.RAD);

        // Assert
        assertNull(fehler);
        assertEquals(3, analyse.getTangente().steigung(), 1e-4);
    }

    @Test
    void berechneFlaeche_ShouldKeepAreaAndVerwerfeAllesShouldClearBoth()
    {
        // Arrange
        setzeFunktionen("x");
        analyse.setzeTangente(state, 0, 0, WinkelModus.RAD);

        // Act
        analyse.berechneFlaeche(state, 0, 2, WinkelModus.RAD);

        // Assert
        assertEquals(2, analyse.getFlaeche().integral(), 1e-6);
        analyse.verwerfeAlles();
        assertNull(analyse.getFlaeche());
        assertNull(analyse.getTangente());
    }

    @Test
    void schnittpunkte_ShouldIgnoreHiddenFunctions()
    {
        // Arrange
        setzeFunktionen("x^2", "x+2");

        // Act + Assert
        assertTrue(analyse.schnittpunkte(state, WinkelModus.RAD).stream().anyMatch(p -> Math.abs(p.getX() - 2) < 0.02));
        state.getFunktion(1).setSichtbar(false);
        assertTrue(analyse.schnittpunkte(state, WinkelModus.RAD).isEmpty());
    }
}
