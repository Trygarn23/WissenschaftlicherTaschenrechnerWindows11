package modes.graph.ui;

import modes.graph.logic.GraphEvaluator;
import modes.graph.model.GraphPunkt;
import modes.graph.model.KurvendiskussionResult;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AnalyseAusgabePanelTest
{
    @Test
    void analysis_ShouldGroupPointsAndKeepCategoryExpandedAfterUpdate() throws Exception
    {
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            AnalyseAusgabePanel panel = new AnalyseAusgabePanel(new GraphEvaluator(), (a, b) -> {}, () -> {});
            var result = new KurvendiskussionResult(new GraphPunkt(0, -1),
                    List.of(new GraphPunkt(-1, 0), new GraphPunkt(1, 0)), List.of(), List.of());
            panel.zeigeAnalyse(result, List.of(), "");
            JTree tree = SwingSuche.finde(panel, JTree.class);
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            DefaultMutableTreeNode zeros = (DefaultMutableTreeNode) root.getChildAt(1);
            assertEquals("Nullstellen (2)", zeros.toString());
            assertEquals(2, zeros.getChildCount());
            assertEquals("(-1 | 0)", zeros.getChildAt(0).toString());
            assertFalse(tree.isExpanded(new TreePath(zeros.getPath())));
            tree.expandPath(new TreePath(zeros.getPath()));
            panel.zeigeAnalyse(result, List.of(new GraphPunkt(2, 3)), "Integral = 4");
            root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            zeros = (DefaultMutableTreeNode) root.getChildAt(1);
            assertTrue(tree.isExpanded(new TreePath(zeros.getPath())));
            assertEquals("Schnitt mit anderen (1)", root.getChildAt(4).toString());
            assertEquals("Integral = 4", root.getLastChild().toString());
            panel.zeigeText("Kurvendiskussion nicht möglich.");
            assertEquals(1, tree.getModel().getChildCount(tree.getModel().getRoot()));
        });
    }
}
