package ui.shell;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import ui.theme.themes.AzubiModernTheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ModeContentHostPanelTest
{
    @Test
    void showMode_WithTheme_ShouldPaintFadeOverlay()
    {
        // Arrange
        ModeContentHostPanel host = new ModeContentHostPanel();
        JPanel standard = new JPanel();
        standard.setBackground(Color.WHITE);
        JPanel graph = new JPanel();
        graph.setBackground(Color.WHITE);
        host.registerMode(RechnerModus.STANDARD, standard);
        host.registerMode(RechnerModus.GRAPH, graph);
        host.setSize(40, 40);
        host.doLayout();
        int ohneOverlay = mittlererPixel(host);

        // Act
        host.showMode(RechnerModus.GRAPH, new AzubiModernTheme());

        // Assert: direkt nach dem Wechsel liegt eine halbtransparente Farbe über dem Modus
        assertNotEquals(ohneOverlay, mittlererPixel(host));
    }

    private static int mittlererPixel(ModeContentHostPanel host)
    {
        host.doLayout();
        BufferedImage bild = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bild.createGraphics();
        host.paint(g);
        g.dispose();
        return bild.getRGB(20, 20);
    }
}
