package modes.finanz.ui;

import common.state.RechnerModus;
import modes.finanz.logic.FinanzRechnung;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FinanzPanelTest
{
    @Test
    void finanzPanel_ShouldShowDefaultCalculationOnStart()
    {
        // Arrange
        FinanzPanel panel = new FinanzPanel();

        // Assert
        assertEquals(RechnerModus.FINANZ, panel.getRechnerModus());
        assertNotNull(SwingSuche.button(panel, "Berechnen"));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, label -> "47,50 €".equals(label.getText())));
    }

    @Test
    @SuppressWarnings("unchecked")
    void berechnen_ShouldShowResultForSelectedCalculation()
    {
        // Arrange
        FinanzPanel panel = new FinanzPanel();
        JComboBox<FinanzRechnung> box = SwingSuche.finde(panel, JComboBox.class);
        box.setSelectedItem(FinanzRechnung.NETTO_ZU_BRUTTO);
        List<JTextField> felder = SwingSuche.alle(panel, JTextField.class);
        felder.get(0).setText("200");
        felder.get(1).setText("7");

        // Act
        SwingSuche.button(panel, "Berechnen").doClick();

        // Assert
        assertFalse(felder.get(2).isVisible());
        assertNotNull(SwingSuche.finde(panel, JLabel.class, label -> "214,00 €".equals(label.getText())));
    }

    @Test
    void berechnen_ShouldShowGermanErrorForInvalidInput()
    {
        // Arrange
        FinanzPanel panel = new FinanzPanel();
        SwingSuche.alle(panel, JTextField.class).get(0).setText("abc");

        // Act
        SwingSuche.button(panel, "Berechnen").doClick();

        // Assert
        assertNotNull(SwingSuche.finde(panel, JLabel.class, label -> "„abc“ ist keine gültige Zahl.".equals(label.getText())));
    }
}
