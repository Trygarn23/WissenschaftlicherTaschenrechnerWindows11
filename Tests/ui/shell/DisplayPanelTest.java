package ui.shell;

import org.junit.jupiter.api.Test;
import ui.shell.DisplayPanel;
import ui.theme.themes.DarkTheme;

import static org.junit.jupiter.api.Assertions.*;

public class DisplayPanelTest
{
    @Test
    void displayKeys_ShouldReachCalculator_WhenDisplayHasFocus() throws Exception
    {
        org.junit.jupiter.api.Assumptions.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            try
            {
                DisplayPanel display = new DisplayPanel();
                frame.add(display);
                common.logic.RechnerService rechner = new common.logic.RechnerService();
                java.util.concurrent.atomic.AtomicInteger bestaetigt = new java.util.concurrent.atomic.AtomicInteger();
                ShellActionRegistry actions = new ShellActionRegistry(rechner,
                        new modes.wissenschaftlich.logic.WissenschaftlichOperationen(rechner.getAusdruckEditor()),
                        () -> {}, text -> {}, bestaetigt::incrementAndGet);
                new KeyboardShortcutBinder(frame.getRootPane(), new ui.history.HistoryPanel(), actions, () -> true).setupKeyboard();
                frame.setSize(400, 200);
                frame.setVisible(true);
                for (javax.swing.JTextPane pane : testhilfen.SwingSuche.alle(display, javax.swing.JTextPane.class))
                {
                    rechner.setAusdruckText("123");
                    for (int key : new int[]{java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.KeyEvent.VK_ENTER})
                    {
                        java.awt.event.KeyEvent event = new java.awt.event.KeyEvent(pane,
                                java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key, java.awt.event.KeyEvent.CHAR_UNDEFINED);
                        assertTrue(javax.swing.SwingUtilities.processKeyBindings(event));
                    }
                    assertEquals("12", rechner.getAusdruckText());
                }
                assertEquals(2, bestaetigt.get());
            }
            finally { frame.dispose(); }
        });
    }

    @Test
    void displayPanel_ShouldExposeDisplayTexts_WhenTextsAreSet()
    {
        // Arrange
        DisplayPanel panel = new DisplayPanel();

        // Act
        panel.setMainText("42");
        panel.setSecondaryText("6*7 = 42");
        panel.setStatusText("Modus: Standard | Winkel: DEG | Speicher leer");

        // Assert
        assertEquals("42", panel.getMainText());
        assertEquals("6*7 = 42", panel.getSecondaryText());
        assertEquals("Modus: Standard | Winkel: DEG | Speicher leer", panel.getStatusText());
    }

    @Test
    void displayPanel_ShouldDescribeClipboardShortcutsInTooltip()
    {
        // Arrange
        DisplayPanel panel = new DisplayPanel();

        // Act
        String tooltip = panel.getToolTipText();

        // Assert
        assertTrue(tooltip.contains("Strg+C"));
        assertTrue(tooltip.contains("Strg+V"));
    }

    @Test
    void displayPanel_ShouldReduceMainFontSize_WhenExpressionIsLong()
    {
        // Arrange
        DisplayPanel panel = new DisplayPanel();
        panel.applyTheme(new DarkTheme());
        int normalSize = panel.getMainFontSize();

        // Act
        panel.setMainText("1234567890+1234567890+1234567890+1234567890");

        // Assert
        assertTrue(panel.getMainFontSize() < normalSize);
    }
}
