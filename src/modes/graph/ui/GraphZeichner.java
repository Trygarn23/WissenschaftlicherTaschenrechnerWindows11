package modes.graph.ui;

import common.state.WinkelModus;
import modes.graph.formatting.GraphFormatter;
import modes.graph.logic.GraphEvaluator;
import modes.graph.model.FunktionsDefinition;
import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import modes.graph.model.KurvendiskussionResult;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Path2D;

/** Zeichnet Raster, Achsen, Kurven, Analysepunkte und den Hover-Hinweis. Kennt keine Maus und keinen Zustand. */
final class GraphZeichner
{
    private static final AppTheme FALLBACK_THEME = new DarkTheme();

    /** Alles, was für ein Bild gebraucht wird. {@code hoverPunkt} ist {@code null}, wenn nichts angezeigt werden soll. */
    record Szene(
            GraphKoordinaten koordinaten,
            AppTheme theme,
            WinkelModus winkelModus,
            KurvendiskussionResult analyse,
            Point hoverPunkt,
            double refreshPulse)
    {
    }

    private final GraphEvaluator evaluator;
    private final GraphFormatter formatter;

    GraphZeichner(GraphEvaluator evaluator, GraphFormatter formatter)
    {
        this.evaluator = evaluator;
        this.formatter = formatter;
    }

    void zeichne(Graphics2D g, Szene szene)
    {
        AppTheme theme = szene.theme() != null ? szene.theme() : FALLBACK_THEME;
        GraphKoordinaten k = szene.koordinaten();

        g.setColor(theme.canvasBackground());
        g.fillRect(0, 0, k.breite(), k.hoehe());
        zeichneRefreshPulse(g, k, theme, szene.refreshPulse());

        zeichneRaster(g, k, theme.gridColor(), theme.secondaryDisplayForeground());
        zeichneAchsen(g, k, theme.displayForeground());
        zeichneFunktionen(g, k, szene.winkelModus());
        zeichneAnalysePunkte(g, k, theme, szene.analyse());
        zeichneBereich(g, k, theme.secondaryDisplayForeground());
        zeichneHoverKoordinaten(g, k, theme, szene.hoverPunkt());
    }

    private void zeichneRefreshPulse(Graphics2D g, GraphKoordinaten k, AppTheme theme, double refreshPulse)
    {
        if (refreshPulse <= 0.0)
        {
            return;
        }

        Graphics2D pulseGraphics = (Graphics2D) g.create();
        pulseGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.16 * refreshPulse)));
        pulseGraphics.setColor(theme.successPulseColor());
        pulseGraphics.fillRoundRect(0, 0, k.breite(), k.hoehe(), 18, 18);
        pulseGraphics.dispose();
    }

    private void zeichneRaster(Graphics2D g, GraphKoordinaten k, Color grid, Color labels)
    {
        GraphState state = k.state();
        g.setStroke(new BasicStroke(1f));

        double xStep = ermittleSchrittweite(state.getXMax() - state.getXMin());
        double yStep = ermittleSchrittweite(state.getYMax() - state.getYMin());

        for (double x = Math.ceil(state.getXMin() / xStep) * xStep; x <= state.getXMax(); x += xStep)
        {
            int px = k.zuBildschirmX(x);
            g.setColor(grid);
            g.drawLine(px, 0, px, k.hoehe());
            zeichneLabel(g, labels, formatter.formatiereAchsenwert(x), px + 4, Math.min(k.hoehe() - 6, k.zuBildschirmY(0) + 16));
        }

        for (double y = Math.ceil(state.getYMin() / yStep) * yStep; y <= state.getYMax(); y += yStep)
        {
            int py = k.zuBildschirmY(y);
            g.setColor(grid);
            g.drawLine(0, py, k.breite(), py);
            if (Math.abs(y) > 1e-9)
            {
                zeichneLabel(g, labels, formatter.formatiereAchsenwert(y), Math.max(4, k.zuBildschirmX(0) + 6), py - 4);
            }
        }
    }

    private void zeichneAchsen(Graphics2D g, GraphKoordinaten k, Color foreground)
    {
        GraphState state = k.state();
        g.setColor(foreground);
        g.setStroke(new BasicStroke(2f));

        if (state.getYMin() <= 0.0 && state.getYMax() >= 0.0)
        {
            int y = k.zuBildschirmY(0.0);
            g.drawLine(0, y, k.breite(), y);
        }

        if (state.getXMin() <= 0.0 && state.getXMax() >= 0.0)
        {
            int x = k.zuBildschirmX(0.0);
            g.drawLine(x, 0, x, k.hoehe());
        }
    }

    private void zeichneFunktionen(Graphics2D g, GraphKoordinaten k, WinkelModus winkelModus)
    {
        GraphState state = k.state();
        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            FunktionsDefinition funktion = state.getFunktion(index);
            if (!funktion.isSichtbar())
            {
                continue;
            }

            Path2D path = new Path2D.Double();
            boolean pathGestartet = false;
            int letzterY = 0;

            for (int px = 0; px < k.breite(); px++)
            {
                double y = evaluator.wertOderNaN(funktion.getAusdruck(), k.zuWeltX(px), winkelModus);
                if (Double.isNaN(y) || y < state.getYMin() - 1_000 || y > state.getYMax() + 1_000)
                {
                    pathGestartet = false;
                    continue;
                }

                int py = k.zuBildschirmY(y);
                // Riesiger Sprung zwischen zwei Pixeln (z. B. bei tan) → Linie nicht durchziehen.
                if (pathGestartet && Math.abs(py - letzterY) > k.hoehe() * 2)
                {
                    pathGestartet = false;
                }

                if (!pathGestartet)
                {
                    path.moveTo(px, py);
                    pathGestartet = true;
                }
                else
                {
                    path.lineTo(px, py);
                }
                letzterY = py;
            }

            float breite = index == state.getAktiveFunktionIndex() ? 3.5f : 2.5f;
            g.setStroke(new BasicStroke(breite, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(funktion.getFarbe());
            g.draw(path);
        }
    }

    private void zeichneAnalysePunkte(Graphics2D g, GraphKoordinaten k, AppTheme theme, KurvendiskussionResult analyse)
    {
        if (analyse == null)
        {
            return;
        }

        Color hintergrund = theme.canvasBackground();
        zeichneMarker(g, k, analyse.getYAchsenSchnittpunkt(), theme.graphYAchseColor(), hintergrund, "Y");

        for (GraphPunkt punkt : analyse.getNullstellen())
        {
            zeichneMarker(g, k, punkt, theme.graphNullstelleColor(), hintergrund, "N");
        }

        for (GraphPunkt punkt : analyse.getExtremstellen())
        {
            zeichneMarker(g, k, punkt, theme.graphExtremumColor(), hintergrund, "E");
        }

        for (GraphPunkt punkt : analyse.getWendestellen())
        {
            zeichneMarker(g, k, punkt, theme.graphWendestelleColor(), hintergrund, "W");
        }
    }

    private void zeichneMarker(Graphics2D g, GraphKoordinaten k, GraphPunkt punkt, Color color, Color background, String label)
    {
        if (punkt == null || !k.istSichtbar(punkt))
        {
            return;
        }

        int x = k.zuBildschirmX(punkt.getX());
        int y = k.zuBildschirmY(punkt.getY());

        g.setColor(background);
        g.fillOval(x - 7, y - 7, 14, 14);
        g.setColor(color);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x - 7, y - 7, 14, 14);
        g.fillOval(x - 3, y - 3, 6, 6);
        g.drawString(label, x + 8, y - 8);
    }

    private void zeichneBereich(Graphics2D g, GraphKoordinaten k, Color secondary)
    {
        GraphState state = k.state();
        String text = String.format("x %.1f .. %.1f | y %.1f .. %.1f", state.getXMin(), state.getXMax(), state.getYMin(), state.getYMax());
        g.setColor(secondary);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, k.breite() - metrics.stringWidth(text) - 12, k.hoehe() - 12);
    }

    private void zeichneHoverKoordinaten(Graphics2D g, GraphKoordinaten k, AppTheme theme, Point hoverPunkt)
    {
        if (hoverPunkt == null)
        {
            return;
        }

        String text = "x = " + formatter.formatiereZahl(k.zuWeltX(hoverPunkt.x))
                + "   y = " + formatter.formatiereZahl(k.zuWeltY(hoverPunkt.y));
        FontMetrics metrics = g.getFontMetrics();
        int breite = metrics.stringWidth(text) + 20;
        int hoehe = metrics.getHeight() + 10;
        int x = Math.min(hoverPunkt.x + 14, Math.max(6, k.breite() - breite - 6));
        int y = hoverPunkt.y - hoehe - 12;
        if (y < 6)
        {
            y = Math.min(k.hoehe() - hoehe - 6, hoverPunkt.y + 16);
        }

        g.setColor(theme.popupBackground());
        g.fillRoundRect(x, y, breite, hoehe, 10, 10);
        g.setColor(theme.cardBorder());
        g.drawRoundRect(x, y, breite, hoehe, 10, 10);
        g.setColor(theme.popupForeground());
        g.drawString(text, x + 10, y + metrics.getAscent() + 5);
    }

    private void zeichneLabel(Graphics2D g, Color color, String text, int x, int y)
    {
        g.setColor(color);
        g.drawString(text, x, y);
    }

    /** Rasterabstand auf „schöne“ Werte runden: 1, 2 oder 5 mal Zehnerpotenz. */
    private double ermittleSchrittweite(double span)
    {
        double rough = span / 10.0;
        double power = Math.pow(10, Math.floor(Math.log10(rough)));
        double normalized = rough / power;

        if (normalized < 2.0) return power;
        if (normalized < 5.0) return 2.0 * power;
        return 5.0 * power;
    }
}
