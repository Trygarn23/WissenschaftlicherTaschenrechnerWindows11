package modes.gleichung.ui;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JLabel;
import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.*;

public class GleichungPanelTest
{
    @Test
    void gleichungPanel_ShouldSolveDefaultCoefficientsOnClick()
    {
        // Arrange
        GleichungPanel panel = new GleichungPanel();

        // Act
        SwingSuche.button(panel, "Lösen").doClick();

        // Assert
        assertEquals(RechnerModus.GLEICHUNG, panel.getRechnerModus());
        assertEquals(4, SwingSuche.alle(panel, JTextField.class).size());
        assertNotNull(SwingSuche.button(panel, "Gleichung lösen"));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "x₁ = -2,  x₂ = 2".equals(l.getText())));
    }
}
