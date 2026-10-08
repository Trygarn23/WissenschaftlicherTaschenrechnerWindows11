package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import ui.befehle.Befehl;
import ui.befehle.BefehlsSuche;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ShellBefehleTest
{
    private final AtomicReference<RechnerModus> modus = new AtomicReference<>();
    private final List<String> eingefuegt = new ArrayList<>();

    private ShellBefehle.Aktionen aktionen()
    {
        Runnable nichts = () -> { };
        return new ShellBefehle.Aktionen(modus::set, theme -> { }, eingefuegt::add,
                nichts, nichts, nichts, nichts, nichts, nichts, nichts);
    }

    @Test
    void erstelle_ShouldFindModesFunctionsAndConstants()
    {
        // Arrange
        List<Befehl> befehle = ShellBefehle.erstelle(aktionen(), List.of(), false);

        // Act
        BefehlsSuche.filtere(befehle, "Matrix", 1).getFirst().aktion().run();
        BefehlsSuche.filtere(befehle, "sin(", 1).getFirst().aktion().run();
        BefehlsSuche.filtere(befehle, "Lichtgeschw", 1).getFirst().aktion().run();

        // Assert
        assertEquals(RechnerModus.MATRIX, modus.get());
        assertEquals("sin(", eingefuegt.get(0));
        assertEquals("299792458", eingefuegt.get(1));
    }

    @Test
    void erstelle_ShouldHideBlockedModesAndToolsInExamMode()
    {
        // Act
        List<Befehl> befehle = ShellBefehle.erstelle(aktionen(), List.of(), true);

        // Assert
        assertTrue(befehle.stream().noneMatch(b -> b.titel().equals("Graph")));
        assertTrue(befehle.stream().noneMatch(b -> b.kategorie().equals("Konstante")));
        assertTrue(befehle.stream().anyMatch(b -> b.titel().equals("Wissenschaftlich")));
    }
}
