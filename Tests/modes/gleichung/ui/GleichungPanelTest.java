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
    void equationInput_ShouldStayVisibleAndSingleLineAtWindowHeight() throws Exception
    {
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            GleichungPanel panel = new GleichungPanel();
            panel.setSize(980, 700);
            layout(panel);
            for (JTextField field : SwingSuche.alle(panel, JTextField.class))
            {
                assertTrue(field.getWidth() > 0 && field.getHeight() >= 18 && field.getHeight() < 80,
                        field.getText() + ": " + field.getSize());
            }
        });
    }

    private static void layout(java.awt.Container container)
    {
        container.doLayout();
        for (java.awt.Component child : container.getComponents())
            if (child instanceof java.awt.Container nested) layout(nested);
    }

    @Test
    void gleichungPanel_ShouldShowExampleAndSolveSystemsOnEnter() throws Exception
    {
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            GleichungPanel panel = new GleichungPanel();
            assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "x₁ = -2,  x₂ = 2".equals(l.getText())));
            javax.swing.JComboBox<?> auswahl = SwingSuche.alle(panel, javax.swing.JComboBox.class).getFirst();
            auswahl.setSelectedItem(2);
            java.util.List<JTextField> felder = SwingSuche.alle(panel, JTextField.class).stream()
                    .filter(f -> f.getAccessibleContext().getAccessibleName() != null
                            && f.getAccessibleContext().getAccessibleName().startsWith("Zeile ")).toList();
            String[] werte = {"1", "1", "5", "1", "-1", "1"};
            for (int i = 0; i < werte.length; i++) felder.get(i).setText(werte[i]);
            felder.getLast().postActionEvent();
            assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> l.getText().contains("x1 = 3<br>x2 = 2")));
            felder.get(3).setText("1");
            felder.get(4).setText("1");
            felder.getLast().postActionEvent();
            assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "Keine Lösung".equals(l.getText())));
            felder.getLast().setText("5");
            felder.getLast().postActionEvent();
            assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "Unendlich viele Lösungen".equals(l.getText())));
            auswahl.setSelectedItem(10);
            assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> l.getText().contains("x10 = 10")));
            auswahl.setSelectedItem(1);
            JTextField text = SwingSuche.alle(panel, JTextField.class).stream()
                    .filter(f -> f.getText().equals("x^2 - 4 = 0")).findFirst().orElseThrow();
            text.setText("2x + 3 = 7");
            text.postActionEvent();
            assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "x = 2".equals(l.getText())));
        });
    }

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
