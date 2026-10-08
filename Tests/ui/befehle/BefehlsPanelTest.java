package ui.befehle;

import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;
import ui.theme.themes.DarkTheme;
import ui.theme.themes.LightTheme;

import javax.swing.JList;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BefehlsPanelTest
{
    @Test
    void tippen_ShouldFilterListAndEnterShouldRunFirstHitAndClose()
    {
        // Arrange
        List<String> protokoll = new ArrayList<>();
        BefehlsPanel panel = new BefehlsPanel(new DarkTheme(), befehle(protokoll), () -> protokoll.add("zu"));
        JTextField suchfeld = SwingSuche.finde(panel, JTextField.class);
        JList<?> liste = SwingSuche.finde(panel, JList.class);

        // Act
        suchfeld.setText("wsn");
        druecke(suchfeld, "befehl.ausfuehren");

        // Assert
        assertEquals(1, liste.getModel().getSize());
        assertEquals(List.of("zu", "Wissenschaftlich"), protokoll);
    }

    @Test
    void pfeilRunter_ShouldMoveSelectionWhileSearchFieldHasFocus()
    {
        // Arrange
        List<String> protokoll = new ArrayList<>();
        BefehlsPanel panel = new BefehlsPanel(new LightTheme(), befehle(protokoll), () -> { });
        JTextField suchfeld = SwingSuche.finde(panel, JTextField.class);

        // Act
        druecke(suchfeld, "befehl.runter");
        panel.fuehreAusgewaehltenAus();

        // Assert
        assertEquals(List.of("Wissenschaftlich"), protokoll);
    }

    @Test
    void enter_ShouldDoNothing_WhenNoHit()
    {
        // Arrange
        List<String> protokoll = new ArrayList<>();
        BefehlsPanel panel = new BefehlsPanel(new DarkTheme(), befehle(protokoll), () -> protokoll.add("zu"));

        // Act
        SwingSuche.finde(panel, JTextField.class).setText("qqq");
        panel.fuehreAusgewaehltenAus();

        // Assert
        assertTrue(protokoll.isEmpty());
    }

    private static List<Befehl> befehle(List<String> protokoll)
    {
        return List.of(
                new Befehl("Standard", "Modus", () -> protokoll.add("Standard")),
                new Befehl("Wissenschaftlich", "Modus", () -> protokoll.add("Wissenschaftlich")),
                new Befehl("Pi", "Konstante", () -> protokoll.add("Pi"))
        );
    }

    private static void druecke(JTextField feld, String aktion)
    {
        feld.getActionMap().get(aktion).actionPerformed(new ActionEvent(feld, ActionEvent.ACTION_PERFORMED, aktion));
    }
}
