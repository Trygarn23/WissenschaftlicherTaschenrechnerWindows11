import modes.graph.logic.GraphEvaluator;
import modes.graph.model.GraphState;
import modes.graph.ui.GraphCanvasPanel;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphCanvasPanelTest
{
    @Test
    void clickOnCurve_ShouldSelectItsFunction() throws Exception
    {
        AtomicInteger selectedIndex = new AtomicInteger(-1);

        SwingUtilities.invokeAndWait(() -> {
            GraphState state = new GraphState();
            GraphCanvasPanel canvas = new GraphCanvasPanel(state, new GraphEvaluator());
            canvas.setSize(400, 400);
            canvas.setFunctionSelectionListener(selectedIndex::set);

            canvas.dispatchEvent(new MouseEvent(
                    canvas,
                    MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(),
                    0,
                    200,
                    280,
                    1,
                    false,
                    MouseEvent.BUTTON1
            ));
        });

        assertEquals(1, selectedIndex.get());
    }
}
