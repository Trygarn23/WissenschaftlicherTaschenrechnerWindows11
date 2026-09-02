package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import ui.theme.themes.AzubiModernTheme;

import javax.swing.JPanel;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeContentHostPanelTest
{
    @Test
    void showMode_WithTheme_ShouldStartFadeOverlay()
    {
        ModeContentHostPanel host = new ModeContentHostPanel();
        host.registerMode(RechnerModus.STANDARD, new JPanel());
        host.registerMode(RechnerModus.GRAPH, new JPanel());

        host.showMode(RechnerModus.GRAPH, new AzubiModernTheme());

        assertTrue(host.fadeOverlayAlphaForTest() > 0.0);
    }
}
