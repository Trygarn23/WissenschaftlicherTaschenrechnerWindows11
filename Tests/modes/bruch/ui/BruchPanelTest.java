package modes.bruch.ui;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class BruchPanelTest
{
    @Test
    void bruchPanel_ShouldRechnenUndErgebnisUebernehmen()
    {
        // Arrange
        BruchPanel panel = new BruchPanel();
        List<JTextField> felder = SwingSuche.alle(panel, JTextField.class);
        felder.get(0).setText("1/2");
        felder.get(1).setText("1/3");
        AtomicReference<String> uebernommen = new AtomicReference<>();
        panel.setErgebnisUebernehmenListener(uebernommen::set);

        // Act
        SwingSuche.button(panel, "+").doClick();
        SwingSuche.button(panel, "Ins Display übernehmen").doClick();

        // Assert
        assertEquals(RechnerModus.BRUCH, panel.getRechnerModus());
        assertEquals(2, felder.size());
        assertNotNull(SwingSuche.button(panel, "÷"));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "5/6".equals(l.getText())));
        assertEquals("0,8333333333", uebernommen.get());
    }

    @Test
    void kuerzenUmwandeln_ShouldErkennePeriodischeDezimalzahl()
    {
        // Arrange
        BruchPanel panel = new BruchPanel();
        SwingSuche.alle(panel, JTextField.class).get(0).setText("2,333333333333");

        // Act
        SwingSuche.button(panel, "Kürzen/Umwandeln").doClick();

        // Assert
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "7/3".equals(l.getText())));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "gemischt: 2 1/3".equals(l.getText())));
    }
}
