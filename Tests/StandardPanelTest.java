import modes.standard.ui.StandardPanel;
import org.junit.jupiter.api.Test;

import java.awt.GridLayout;

import static org.junit.jupiter.api.Assertions.*;

public class StandardPanelTest
{
    @Test
    void standardPanel_ShouldUseStableFourColumnGrid()
    {
        // Arrange
        StandardPanel panel = new StandardPanel();

        // Act
        GridLayout layout = (GridLayout) panel.getLayout();

        // Assert
        assertEquals(4, layout.getColumns());
        assertEquals(24, panel.getComponentCount());
        assertEquals(6, layout.getHgap());
        assertEquals(6, layout.getVgap());
    }
}
