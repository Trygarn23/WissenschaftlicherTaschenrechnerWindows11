package modes.vektor.ui;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VektorPanelTest
{
    @Test
    void vektorPanel_ShouldBuildWithInputsAndDisableCrossProductIn2D()
    {
        // Arrange
        VektorPanel panel = new VektorPanel();

        // Assert
        assertEquals(RechnerModus.VEKTOR, panel.getRechnerModus());
        assertEquals(7, SwingSuche.alle(panel, JTextField.class).size());
        assertNotNull(SwingSuche.button(panel, "a + b"));
        assertNotNull(SwingSuche.button(panel, "Steigung"));
        assertFalse(SwingSuche.button(panel, "a × b").isEnabled());
    }

    @Test
    void klickAufKreuzprodukt_ShouldShowResultIn3D()
    {
        // Arrange
        VektorPanel panel = new VektorPanel();
        SwingSuche.finde(panel, JComboBox.class).setSelectedItem("3D");
        List<JTextField> felder = SwingSuche.alle(panel, JTextField.class);
        felder.get(0).setText("1");
        felder.get(4).setText("1");

        // Act
        SwingSuche.button(panel, "a × b").doClick();

        // Assert
        assertTrue(SwingSuche.alle(panel, JLabel.class).stream().anyMatch(l -> "(0 | 0 | 1)".equals(l.getText())));
    }
}
