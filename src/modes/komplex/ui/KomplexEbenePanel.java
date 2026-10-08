package modes.komplex.ui;

import modes.komplex.model.KomplexeZahl;
import ui.theme.AppFonts;
import ui.theme.AppTheme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Gaußsche Zahlenebene: z1, z2 und das Ergebnis als Pfeile vom Ursprung aus. */
public class KomplexEbenePanel extends JComponent
{
    private static final int RAND = 28;
    private static final double SPITZE_LAENGE = 11;
    private static final double SPITZE_WINKEL = Math.toRadians(25);

    private KomplexeZahl z1 = new KomplexeZahl(0, 0);
    private KomplexeZahl z2 = new KomplexeZahl(0, 0);
    private KomplexeZahl ergebnis = new KomplexeZahl(0, 0);

    // Standardfarben, falls noch kein Theme gesetzt wurde
    private Color hintergrund = Color.WHITE;
    private Color achsenFarbe = Color.GRAY;
    private Color schriftFarbe = Color.DARK_GRAY;
    private Color farbeZ1 = new Color(70, 190, 255);
    private Color farbeZ2 = new Color(255, 190, 60);
    private Color farbeErgebnis = new Color(30, 190, 120);

    public KomplexEbenePanel()
    {
        setPreferredSize(new Dimension(320, 260));
        setOpaque(true);
    }

    public void setZahlen(KomplexeZahl z1, KomplexeZahl z2, KomplexeZahl ergebnis)
    {
        this.z1 = z1;
        this.z2 = z2;
        this.ergebnis = ergebnis;
        repaint();
    }

    public void applyTheme(AppTheme theme)
    {
        hintergrund = theme.canvasBackground();
        achsenFarbe = theme.gridColor();
        schriftFarbe = theme.secondaryDisplayForeground();
        farbeZ1 = theme.graphYAchseColor();
        farbeZ2 = theme.graphExtremumColor();
        farbeErgebnis = theme.graphNullstelleColor();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics)
    {
        Graphics2D g = (Graphics2D) graphics.create();
        try
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(hintergrund);
            g.fillRect(0, 0, getWidth(), getHeight());

            int mitteX = getWidth() / 2;
            int mitteY = getHeight() / 2;
            zeichneAchsen(g, mitteX, mitteY);

            double pixelProEinheit = (Math.min(getWidth(), getHeight()) / 2.0 - RAND) / groessterBetrag();
            zeichnePfeil(g, z1, "z1", farbeZ1, mitteX, mitteY, pixelProEinheit);
            zeichnePfeil(g, z2, "z2", farbeZ2, mitteX, mitteY, pixelProEinheit);
            zeichnePfeil(g, ergebnis, "Ergebnis", farbeErgebnis, mitteX, mitteY, pixelProEinheit);
        }
        finally
        {
            g.dispose();
        }
    }

    private void zeichneAchsen(Graphics2D g, int mitteX, int mitteY)
    {
        g.setColor(achsenFarbe);
        g.setStroke(new BasicStroke(1f));
        g.drawLine(0, mitteY, getWidth(), mitteY);
        g.drawLine(mitteX, 0, mitteX, getHeight());

        g.setColor(schriftFarbe);
        g.setFont(AppFonts.normal(12));
        g.drawString("Re", getWidth() - 22, mitteY - 6);
        g.drawString("Im", mitteX + 6, 14);
    }

    private double groessterBetrag()
    {
        double max = 0;
        for (KomplexeZahl zahl : new KomplexeZahl[] {z1, z2, ergebnis})
        {
            double betrag = zahl.betrag();
            if (Double.isFinite(betrag))
            {
                max = Math.max(max, betrag);
            }
        }
        return max == 0 ? 1 : max;
    }

    private void zeichnePfeil(Graphics2D g, KomplexeZahl zahl, String name, Color farbe, int mitteX, int mitteY, double pixelProEinheit)
    {
        if (!Double.isFinite(zahl.betrag()))
        {
            return;
        }

        double zielX = mitteX + zahl.getReal() * pixelProEinheit;
        double zielY = mitteY - zahl.getImaginaer() * pixelProEinheit;
        g.setColor(farbe);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (zahl.betrag() == 0)
        {
            g.fillOval(mitteX - 3, mitteY - 3, 6, 6);
        }
        else
        {
            g.drawLine(mitteX, mitteY, (int) Math.round(zielX), (int) Math.round(zielY));
            double winkel = Math.atan2(zielY - mitteY, zielX - mitteX);
            zeichneSpitzenLinie(g, zielX, zielY, winkel + Math.PI - SPITZE_WINKEL);
            zeichneSpitzenLinie(g, zielX, zielY, winkel + Math.PI + SPITZE_WINKEL);
        }

        g.setFont(AppFonts.fett(12));
        g.drawString(name, (int) Math.round(zielX) + 6, (int) Math.round(zielY) - 6);
    }

    private void zeichneSpitzenLinie(Graphics2D g, double x, double y, double winkel)
    {
        g.drawLine((int) Math.round(x), (int) Math.round(y),
                (int) Math.round(x + SPITZE_LAENGE * Math.cos(winkel)),
                (int) Math.round(y + SPITZE_LAENGE * Math.sin(winkel)));
    }
}
