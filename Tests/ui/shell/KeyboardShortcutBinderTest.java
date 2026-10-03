package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import ui.history.HistoryPanel;
import ui.shortcuts.Tastenkuerzel;
import ui.shortcuts.TastenkuerzelDialog;

import javax.swing.JRootPane;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardShortcutBinderTest
{
    private final JRootPane rootPane = new JRootPane();
    private final AtomicReference<RechnerModus> gewaehlterModus = new AtomicReference<>();
    private final AtomicInteger einheitenUmgeschaltet = new AtomicInteger();
    private final AtomicInteger hilfeGeoeffnet = new AtomicInteger();

    KeyboardShortcutBinderTest()
    {
        KeyboardShortcutBinder binder = new KeyboardShortcutBinder(rootPane, new HistoryPanel(), null, () -> {}, () -> false);
        binder.setupGlobaleTasten(gewaehlterModus::set, einheitenUmgeschaltet::incrementAndGet, hilfeGeoeffnet::incrementAndGet);
    }

    @Test
    void strgZahl_ShouldSwitchModeInModeOrder_EvenWhenCalculatorShortcutsAreOff()
    {
        // Act
        druecke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK);

        // Assert
        assertEquals(RechnerModus.WISSENSCHAFTLICH, gewaehlterModus.get());

        // Act
        druecke(KeyEvent.VK_NUMPAD7, InputEvent.CTRL_DOWN_MASK);

        // Assert
        assertEquals(RechnerModus.STATISTIK, gewaehlterModus.get());
    }

    @Test
    void strg8AndF1_ShouldToggleUnitsAndOpenHelp()
    {
        // Act
        druecke(KeyEvent.VK_8, InputEvent.CTRL_DOWN_MASK);
        druecke(KeyEvent.VK_F1, 0);

        // Assert
        assertEquals(1, einheitenUmgeschaltet.get());
        assertEquals(1, hilfeGeoeffnet.get());
    }

    @Test
    void tastenkuerzelDialog_ShouldListEveryModeAndEveryCalculatorKey()
    {
        // Act
        Map<String, List<TastenkuerzelDialog.Eintrag>> abschnitte = TastenkuerzelDialog.abschnitte();
        List<TastenkuerzelDialog.Eintrag> alle = abschnitte.values().stream().flatMap(List::stream).toList();

        // Assert
        assertTrue(alle.contains(new TastenkuerzelDialog.Eintrag("Strg+1", "Standard")));
        assertTrue(alle.contains(new TastenkuerzelDialog.Eintrag("Strg+8", "Einheiten ein-/ausblenden")));
        for (Tastenkuerzel kuerzel : Tastenkuerzel.values())
        {
            assertTrue(alle.stream().anyMatch(e -> e.taste().equals(kuerzel.getAnzeigeText())), "Fehlt: " + kuerzel);
        }
        assertTrue(alle.stream().allMatch(e -> e.beschreibung() != null && !e.beschreibung().isBlank()));
    }

    private void druecke(int tastenCode, int modifier)
    {
        Object name = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(tastenCode, modifier));
        assertNotNull(name, "Taste nicht gebunden");
        rootPane.getActionMap().get(name).actionPerformed(null);
    }
}
