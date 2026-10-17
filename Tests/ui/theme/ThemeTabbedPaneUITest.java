package ui.theme;

import org.junit.jupiter.api.Test;
import ui.theme.themes.DarkTheme;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThemeTabbedPaneUITest
{
    @Test
    void apply_ShouldPaintContentAreaInPanelColor_InsteadOfDefaultLightBlue()
    {
        // Arrange
        DarkTheme theme = new DarkTheme();
        JTabbedPane tabs = new JTabbedPane();
        JPanel inhalt = new JPanel();
        inhalt.setOpaque(false);
        tabs.addTab("Daten", inhalt);
        tabs.addTab("Mehr", new JPanel());
        tabs.setSize(300, 200);

        // Act
        ThemeTabbedPaneUI.apply(tabs, theme);
        tabs.doLayout();
        BufferedImage bild = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bild.createGraphics();
        tabs.paint(g);
        g.dispose();

        // Assert
        assertEquals(theme.panelBackground().getRGB(), bild.getRGB(150, 150));
    }
}
