package modes.graph.ui;

import modes.graph.logic.GraphEvaluator;
import modes.graph.model.Flaeche;
import modes.graph.model.GraphState;
import modes.graph.model.Tangente;
import modes.graph.ui.GraphCanvasPanel;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void speichereAlsPng_ShouldWriteReadableImage() throws Exception
    {
        // Arrange
        GraphCanvasPanel canvas = new GraphCanvasPanel(new GraphState(), new GraphEvaluator());
        canvas.setSize(320, 240);
        canvas.setTangente(new Tangente(1, 1.0, -3.0, 2.0));
        canvas.setFlaeche(new Flaeche(1, -1.0, 2.0, -9.0, 9.0));
        Path datei = Files.createTempFile("graph-test", ".png");

        try
        {
            // Act
            canvas.speichereAlsPng(datei.toFile());

            // Assert
            assertTrue(Files.size(datei) > 0);
            BufferedImage bild = ImageIO.read(datei.toFile());
            assertNotNull(bild);
            assertEquals(320, bild.getWidth());
            assertEquals(240, bild.getHeight());
        }
        finally
        {
            Files.deleteIfExists(datei);
        }
    }

    @Test
    void speichereAlsPng_ShouldReject_WhenCanvasHasNoSize()
    {
        // Arrange
        GraphCanvasPanel canvas = new GraphCanvasPanel(new GraphState(), new GraphEvaluator());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> canvas.speichereAlsPng(new java.io.File("nie-geschrieben.png")));
    }
}
