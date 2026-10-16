package ui.units;

import common.units.EinheitKategorie;
import common.units.EinheitenFavoriten;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testhilfen.SwingSuche;
import ui.theme.themes.LightTheme;
import ui.units.EinheitenSidePanel;
import ui.units.EinheitenSidePanelHost;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.Path;

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

        assertEquals(4, countComponents(panel, JComboBox.class));
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
    void favoriteButton_ShouldRememberConversionAndSelectItAgain(@TempDir Path ordner)
    {
        // Arrange
        EinheitenFavoriten favoriten = new EinheitenFavoriten(ordner.resolve("einheiten.txt"));
        EinheitenSidePanel panel = new EinheitenSidePanel(favoriten);
        findButton(panel, "Favorit").doClick();
        EinheitenFavoriten.Umrechnung gemerkt = favoriten.alle().getFirst();
        @SuppressWarnings("unchecked")
        JComboBox<Object> favoritenBox = SwingSuche.finde(panel, JComboBox.class,
                box -> box.getItemCount() > 0 && "Favorit wählen…".equals(box.getItemAt(0)));
        @SuppressWarnings("unchecked")
        JComboBox<Object> kategorieBox = SwingSuche.finde(panel, JComboBox.class,
                box -> box.getItemCount() > 0 && box.getItemAt(0) instanceof EinheitKategorie);
        kategorieBox.setSelectedItem(EinheitKategorie.TEMPERATUR);

        // Act
        favoritenBox.setSelectedItem(gemerkt);

        // Assert
        assertEquals(gemerkt.kategorie(), kategorieBox.getSelectedItem());
        assertNotNull(findButton(panel, "Gemerkt"));
        assertEquals(0, favoritenBox.getSelectedIndex());
    }

    @Test
    void sidePanelHost_ShouldOpenWithValue()
    {
        // Arrange
        EinheitenSidePanelHost host = new EinheitenSidePanelHost();

        // Act
        host.oeffneMitWert(2.5);

        // Assert
        assertTrue(host.isGeoeffnet());
        assertEquals("2,5", findComponent(host, JTextField.class).getText());
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
