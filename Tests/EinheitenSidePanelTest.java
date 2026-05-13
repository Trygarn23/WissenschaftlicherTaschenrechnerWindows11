import org.junit.jupiter.api.Test;
import ui.theme.themes.LightTheme;
import ui.units.EinheitenSidePanel;
import ui.units.EinheitenSidePanelHost;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EinheitenSidePanelTest
{
    @Test
    void sidePanel_ShouldExposeConverterControls()
    {
        EinheitenSidePanel panel = new EinheitenSidePanel();

        assertEquals(3, countComponents(panel, JComboBox.class));
        assertEquals(1, countComponents(panel, JTextField.class));
        assertNotNull(findButton(panel, "Tauschen"));
        assertNotNull(findButton(panel, "x"));
    }

    @Test
    void sidePanel_ShouldConvertLive()
    {
        EinheitenSidePanel panel = new EinheitenSidePanel();
        JTextField field = findComponent(panel, JTextField.class);

        field.setText("1000");

        assertNotNull(findLabelContaining(panel, "100 cm"));
    }

    @Test
    void sidePanel_ShouldApplyThemeToControls()
    {
        EinheitenSidePanel panel = new EinheitenSidePanel();
        LightTheme theme = new LightTheme();

        panel.applyTheme(theme);

        assertEquals(theme.toggleButtonBackground(), findButton(panel, "Tauschen").getBackground());
        assertEquals(theme.panelBackground(), panel.getBackground());
    }

    @Test
    void sidePanelHost_ShouldToggleOpenState()
    {
        EinheitenSidePanelHost host = new EinheitenSidePanelHost();

        host.toggle();

        assertTrue(host.isGeoeffnet());
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

    private JLabel findLabelContaining(Container container, String text)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JLabel label && label.getText().contains(text))
            {
                return label;
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findLabelContaining(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Label not found: " + text);
        return null;
    }

    private <T> T findComponent(Container container, Class<T> type)
    {
        for (Component component : container.getComponents())
        {
            if (type.isInstance(component))
            {
                return type.cast(component);
            }

            if (component instanceof Container child)
            {
                try
                {
                    return findComponent(child, type);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Component not found: " + type.getSimpleName());
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
