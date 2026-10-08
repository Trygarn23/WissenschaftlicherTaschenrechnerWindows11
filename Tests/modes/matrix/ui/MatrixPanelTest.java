package modes.matrix.ui;

import modes.matrix.model.Matrix;
import modes.matrix.ui.MatrixPanel;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixPanelTest
{
    @Test
    void matrixPanel_ShouldExposeSizeControlsInputsOperationsAndResultArea()
    {
        MatrixPanel panel = new MatrixPanel();

        assertEquals(4, countComponents(panel, JComboBox.class));
        assertEquals(9, countComponents(panel, JTextField.class));
        assertEquals(1, countComponents(panel, JTextArea.class));
        assertNotNull(findButton(panel, "A + B"));
        assertNotNull(findButton(panel, "A × B"));
        assertNotNull(findButton(panel, "A^T"));
        assertNotNull(findButton(panel, "spur A"));
        assertNotNull(findButton(panel, "rang A"));
        assertNotNull(findButton(panel, "det A"));
    }

    @Test
    void inverseButton_ShouldShowInverseAndSteps_WhenCheckboxIsSelected()
    {
        // Arrange
        MatrixPanel panel = new MatrixPanel();
        setze(panel, "A", new String[][]{{"4", "7"}, {"2", "6"}});
        SwingSuche.finde(panel, JCheckBox.class, b -> "Rechenschritte zeigen".equals(b.getText())).setSelected(true);

        // Act
        SwingSuche.button(panel, "A⁻¹").doClick();

        // Assert
        String text = SwingSuche.finde(panel, JTextArea.class).getText();
        assertTrue(text.startsWith("[ 0,6  -0,7 ]"), text);
        assertTrue(text.contains("Rechenschritte:"), text);
        assertTrue(text.contains("Z1 ← Z1 : 4"), text);
    }

    @Test
    void inverseButton_ShouldShowSingularMessage_WhenMatrixIsSingular()
    {
        // Arrange
        MatrixPanel panel = new MatrixPanel();
        setze(panel, "A", new String[][]{{"1", "2"}, {"2", "4"}});

        // Act
        SwingSuche.button(panel, "A⁻¹").doClick();

        // Assert
        JLabel status = SwingSuche.finde(panel, JLabel.class, l -> l.getText() != null && l.getText().contains("singulär"));
        assertEquals("Die Matrix ist singulär und hat keine Inverse.", status.getText());
    }

    @Test
    void gleichungssystemButton_ShouldSolveWithLastColumnOfB()
    {
        // Arrange
        MatrixPanel panel = new MatrixPanel();
        setze(panel, "A", new String[][]{{"2", "0"}, {"0", "4"}});
        setze(panel, "B", new String[][]{{"0", "6"}, {"0", "8"}});

        // Act
        SwingSuche.button(panel, "Ax = b").doClick();

        // Assert
        assertEquals("x1 = 3" + System.lineSeparator() + "x2 = 2", SwingSuche.finde(panel, JTextArea.class).getText());
    }

    @Test
    void eingabeGitter_ShouldResizeAndFill_WhenMatrixIsSet()
    {
        // Arrange
        MatrixEingabeGitter gitter = new MatrixEingabeGitter("Matrix A", "A");
        Matrix matrix = new Matrix(new double[][]{{1, 2.5, 3}, {4, 5, 1234}, {7, 8, 9}, {0, 0, 1}});

        // Act
        gitter.setze(matrix);

        // Assert
        assertEquals(matrix, gitter.lese());
        assertEquals(12, SwingSuche.alle(gitter, JTextField.class).size());
    }

    private void setze(MatrixPanel panel, String prefix, String[][] werte)
    {
        for (int z = 0; z < werte.length; z++)
        {
            for (int s = 0; s < werte[z].length; s++)
            {
                String name = prefix + (z + 1) + (s + 1);
                SwingSuche.finde(panel, JTextField.class, f -> name.equals(f.getName())).setText(werte[z][s]);
            }
        }
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
