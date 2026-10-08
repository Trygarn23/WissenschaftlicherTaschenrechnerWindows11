package modes.datum.ui;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatumPanelTest
{
    @Test
    void datumPanel_ShouldOfferThreeTabsAndMainButtons()
    {
        // Arrange
        DatumPanel panel = new DatumPanel();

        // Assert
        assertEquals(RechnerModus.DATUM, panel.getRechnerModus());
        assertEquals(3, SwingSuche.finde(panel, JTabbedPane.class).getTabCount());
        assertNotNull(SwingSuche.button(panel, "Tage zwischen"));
        assertNotNull(SwingSuche.button(panel, "Arbeitstage"));
        assertNotNull(SwingSuche.button(panel, "Wochentag"));
        assertNotNull(SwingSuche.button(panel, "Heute"));
        assertNotNull(SwingSuche.button(panel, "Summieren"));
    }

    @Test
    void tageZwischen_ShouldShowDaysInResultLabel()
    {
        // Arrange
        DatumPanel panel = new DatumPanel();
        List<JTextField> felder = SwingSuche.alle(panel, JTextField.class);
        felder.get(0).setText("01.01.2024");
        felder.get(1).setText("2024-03-01");

        // Act
        SwingSuche.button(panel, "Tage zwischen").doClick();

        // Assert
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "60 Tage".equals(l.getText())));
    }

    @Test
    void summieren_ShouldShowHoursInResultLabel()
    {
        // Arrange
        DatumPanel panel = new DatumPanel();
        SwingSuche.finde(panel, JTextArea.class).setText("7:30\n-0:30\n45min");

        // Act
        SwingSuche.button(panel, "Summieren").doClick();

        // Assert
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "07:45".equals(l.getText())));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "7,75 h".equals(l.getText())));
    }
}
