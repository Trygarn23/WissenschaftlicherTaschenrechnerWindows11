package modes.komplex.ui;

import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.logic.KomplexRechnerService;
import modes.komplex.model.KomplexState;
import modes.komplex.model.KomplexeZahl;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KomplexPanelTest
{
    @Test
    void komplexPanel_ShouldExposeInputFieldsOperationsAndDisplayMode()
    {
        // Arrange
        KomplexPanel panel = new KomplexPanel();

        // Assert
        assertEquals(4, countComponents(panel, JTextField.class));
        assertNotNull(findButton(panel, "+"));
        assertNotNull(findButton(panel, "÷"));
        assertNotNull(findButton(panel, "conj z1"));
        assertEquals(1, countComponents(panel, JComboBox.class));
    }

    @Test
    void plusButton_ShouldShowSumInResultLabel()
    {
        // Arrange
        KomplexPanel panel = new KomplexPanel();
        List<JTextField> felder = SwingSuche.alle(panel, JTextField.class);
        felder.get(0).setText("1");
        felder.get(1).setText("2");
        felder.get(2).setText("3");
        felder.get(3).setText("4");

        // Act
        findButton(panel, "+").doClick();

        // Assert
        assertTrue(SwingSuche.alle(panel, JLabel.class).stream()
                .anyMatch(label -> "4 + 6i".equals(label.getText())));
        assertEquals(1, countComponents(panel, KomplexEbenePanel.class));
    }

    @Test
    void komplexPanel_ShouldShowInjectedState()
    {
        // Arrange
        KomplexState state = new KomplexState();
        state.setErgebnis(new KomplexeZahl(3, 4));

        // Act
        KomplexPanel panel = new KomplexPanel(state, new KomplexRechnerService(),
                new KomplexFormatter());

        // Assert
        assertTrue(SwingSuche.alle(panel, JLabel.class).stream()
                .anyMatch(label -> "3 + 4i".equals(label.getText())));
    }

    private JButton findButton(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button && text.equals(button.getText()))
            {
                return button;
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findButton(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Button not found: " + text);
        return null;
    }

    private int countComponents(Container container, Class<?> type)
    {
        int count = 0;
        for (Component component : container.getComponents())
        {
            if (type.isInstance(component))
            {
                count++;
            }

            if (component instanceof Container child)
            {
                count += countComponents(child, type);
            }
        }
        return count;
    }
}
