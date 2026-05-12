package ui.settings;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import ui.theme.ThemeType;
import ui.theme.themes.DarkTheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SettingsDialogTest
{
    @Test
    void settingsDialog_ShouldPublishOnlyWhenApplyOrSaveIsClicked()
    {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());

        List<AppSettings> published = new ArrayList<>();
        SettingsDialog dialog = new SettingsDialog(null, new DarkTheme(), new AppSettings(), published::add, null, null);

        comboBox(dialog, 0).setSelectedItem(ThemeType.LIGHT);
        assertTrue(published.isEmpty());

        button(dialog, "Anwenden").doClick();
        assertEquals(1, published.size());
        assertEquals(ThemeType.LIGHT, published.getFirst().getThemeType());

        dialog.dispose();
    }

    @Test
    void settingsDialog_ShouldCloseWithoutPublishing_WhenCancelIsClicked()
    {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());

        List<AppSettings> published = new ArrayList<>();
        SettingsDialog dialog = new SettingsDialog(null, new DarkTheme(), new AppSettings(), published::add, null, null);

        comboBox(dialog, 0).setSelectedItem(ThemeType.LIGHT);
        button(dialog, "Abbrechen").doClick();

        assertTrue(published.isEmpty());
        assertFalse(dialog.isDisplayable());
    }

    @Test
    void settingsDialog_ShouldResetToAppliedValues_WhenResetIsClicked()
    {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());

        List<AppSettings> published = new ArrayList<>();
        SettingsDialog dialog = new SettingsDialog(null, new DarkTheme(), new AppSettings(), published::add, null, null);

        comboBox(dialog, 0).setSelectedItem(ThemeType.LIGHT);
        button(dialog, "Zurücksetzen").doClick();
        button(dialog, "Anwenden").doClick();

        assertEquals(1, published.size());
        assertEquals(ThemeType.DARK, published.getFirst().getThemeType());

        dialog.dispose();
    }

    private JComboBox<?> comboBox(Container container, int index)
    {
        List<JComboBox<?>> comboBoxes = new ArrayList<>();
        collectComboBoxes(container, comboBoxes);
        assertTrue(comboBoxes.size() > index);
        return comboBoxes.get(index);
    }

    private void collectComboBoxes(Container container, List<JComboBox<?>> comboBoxes)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JComboBox<?> comboBox)
            {
                comboBoxes.add(comboBox);
            }

            if (component instanceof Container child)
            {
                collectComboBoxes(child, comboBoxes);
            }
        }
    }

    private JButton button(Container container, String text)
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
                    return button(child, text);
                }
                catch (AssertionError ignored)
                {
                }
            }
        }

        fail("Button not found: " + text);
        return null;
    }
}
