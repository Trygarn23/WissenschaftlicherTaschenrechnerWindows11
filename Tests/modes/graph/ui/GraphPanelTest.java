package modes.graph.ui;

import modes.graph.ui.GraphPanel;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GraphPanelTest
{
    @Test
    void funktionHinzufuegen_ShouldCreateAnotherEditableFunction()
    {
        GraphPanel panel = new GraphPanel();
        int fieldsBefore = countComponents(panel, JTextField.class);

        findButton(panel, "+ Funktion").doClick();

        assertEquals(fieldsBefore + 1, countComponents(panel, JTextField.class));
        assertNotNull(findButton(panel, "h"));
        assertNotNull(findLabel(panel, "Kurvendiskussion · h(x)"));
    }

    @Test
    void functionButton_ShouldSelectFunctionForAnalysis()
    {
        GraphPanel panel = new GraphPanel();

        findButton(panel, "g").doClick();

        assertNotNull(findLabel(panel, "Kurvendiskussion · g(x)"));
    }

    @Test
    void graphPanel_ShouldExposeCompactControlsWithHelpfulTooltips()
    {
        GraphPanel panel = new GraphPanel();

        assertEquals("Funktionen neu zeichnen", findButton(panel, "Zeichnen").getToolTipText());
        assertEquals("Graphansicht zurücksetzen", findButton(panel, "Reset").getToolTipText());
        assertEquals("In den Graphen hineinzoomen", findButton(panel, "+").getToolTipText());
        assertEquals("Aus dem Graphen herauszoomen", findButton(panel, "−").getToolTipText());
    }

    @Test
    void parameterInExpression_ShouldCreateSliderThatChangesTable()
    {
        // Arrange
        GraphPanel panel = new GraphPanel();
        JTextField feld = SwingSuche.finde(panel, JTextField.class, f -> "sin(x)".equals(f.getText()));
        feld.setText("a*x^2");

        // Act
        feld.postActionEvent();
        JSlider regler = SwingSuche.finde(panel, JSlider.class, s -> "parameter-a".equals(s.getName()));
        regler.setValue(30);

        // Assert
        assertNotNull(findLabel(panel, "a = 3"));
        assertNotNull(findLabel(panel, "12"), "f(2) = 3·2² sollte in der Wertetabelle stehen");
    }

    @Test
    void flaecheButton_ShouldShowIntegralInStatus()
    {
        // Arrange
        GraphPanel panel = new GraphPanel();
        findButton(panel, "g").doClick();

        // Act
        findButton(panel, "Fläche").doClick();

        // Assert – g(x) = x^2-4 von 0 bis 1: 1/3 - 4
        JLabel status = SwingSuche.finde(panel, JLabel.class, l -> l.getText() != null && l.getText().startsWith("Integral = "));
        assertTrue(status.getText().matches("Integral = -3[,.]667"), status.getText());
    }

    @Test
    void graphPanel_ShouldOfferFileMenu()
    {
        GraphPanel panel = new GraphPanel();

        assertNotNull(findButton(panel, "Datei…"));
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

    private JLabel findLabel(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JLabel label && text.equals(label.getText()))
            {
                return label;
            }
            if (component instanceof Container child)
            {
                try
                {
                    return findLabel(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }
        fail("Label not found: " + text);
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
