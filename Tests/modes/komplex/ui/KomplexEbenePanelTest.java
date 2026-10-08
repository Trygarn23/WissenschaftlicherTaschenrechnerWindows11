package modes.komplex.ui;

import modes.komplex.model.KomplexeZahl;
import org.junit.jupiter.api.Test;
import ui.theme.themes.DarkTheme;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class KomplexEbenePanelTest
{
    @Test
    void paint_ShouldDrawArrowsIntoImageWithoutErrors()
    {
        // Arrange
        KomplexEbenePanel ebene = new KomplexEbenePanel();
        ebene.setSize(300, 240);
        ebene.applyTheme(new DarkTheme());
        ebene.setZahlen(new KomplexeZahl(3, 4), new KomplexeZahl(-1, 2), new KomplexeZahl(2, 6));
        BufferedImage bild = new BufferedImage(300, 240, BufferedImage.TYPE_INT_ARGB);

        // Act
        Graphics2D g = bild.createGraphics();
        assertDoesNotThrow(() -> ebene.paint(g));
        g.dispose();

        // Assert – der Ergebnis-Pfeil zeigt nach rechts oben, also liegt dort Ergebnisfarbe
        int ergebnisFarbe = new DarkTheme().graphNullstelleColor().getRGB();
        boolean gefunden = false;
        for (int x = 150; x < 300 && !gefunden; x++)
        {
            for (int y = 0; y < 120 && !gefunden; y++)
            {
                gefunden = bild.getRGB(x, y) == ergebnisFarbe;
            }
        }
        assertTrue(gefunden);
    }

    @Test
    void paint_ShouldSurviveZeroAndInfiniteNumbers()
    {
        // Arrange
        KomplexEbenePanel ebene = new KomplexEbenePanel();
        ebene.setSize(200, 200);
        ebene.setZahlen(new KomplexeZahl(0, 0), new KomplexeZahl(Double.POSITIVE_INFINITY, 1), new KomplexeZahl(Double.NaN, 0));
        BufferedImage bild = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);

        // Act & Assert
        Graphics2D g = bild.createGraphics();
        assertDoesNotThrow(() -> ebene.paint(g));
        g.dispose();
    }
}
