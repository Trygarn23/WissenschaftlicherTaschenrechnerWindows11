package ui.shell;

import common.state.RechnerModus;
import modes.graph.ui.GraphPanel;
import modes.komplex.ui.KomplexPanel;
import modes.matrix.ui.MatrixPanel;
import modes.programmierer.ui.ProgrammiererPanel;
import modes.standard.ui.StandardPanel;
import modes.statistik.ui.StatistikPanel;
import modes.wissenschaftlich.ui.WissenschaftlichPanel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModePanelTest
{
    @Test
    void alleHauptpanelsMeldenIhrenModus()
    {
        List<ModePanel> panels = List.of(
                new StandardPanel(),
                new WissenschaftlichPanel(),
                new ProgrammiererPanel(),
                new GraphPanel(),
                new KomplexPanel(),
                new MatrixPanel(),
                new StatistikPanel()
        );

        assertEquals(
                List.of(
                        RechnerModus.STANDARD,
                        RechnerModus.WISSENSCHAFTLICH,
                        RechnerModus.PROGRAMMIERER,
                        RechnerModus.GRAPH,
                        RechnerModus.KOMPLEX,
                        RechnerModus.MATRIX,
                        RechnerModus.STATISTIK
                ),
                panels.stream().map(ModePanel::getRechnerModus).toList()
        );
    }

    @Test
    void sichtbarkeitUndShortcutsBleibenTeilDesVertrags()
    {
        ModePanel standard = new StandardPanel();
        ModePanel graph = new GraphPanel();

        assertTrue(standard.zeigtGlobalesDisplay());
        assertTrue(standard.zeigtHistory());
        assertTrue(standard.nutztStandardShortcuts());

        assertFalse(graph.zeigtGlobalesDisplay());
        assertFalse(graph.zeigtHistory());
        assertFalse(graph.nutztStandardShortcuts());
    }
}
