package modes.graph.ui;

import common.state.WinkelModus;
import modes.graph.logic.GraphEvaluator;
import modes.graph.model.FunktionsDefinition;
import modes.graph.model.GraphState;
import ui.theme.AppTheme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;

public class GraphCanvasPanel extends JPanel
{
    private final GraphEvaluator evaluator;
    private GraphState state;
    private AppTheme theme;
    private WinkelModus winkelModus = WinkelModus.DEG;
    private Point letzterDragPunkt;
    private Runnable viewportChangedListener = () -> {};

    public GraphCanvasPanel(GraphState state, GraphEvaluator evaluator)
    {
        this.state = state;
        this.evaluator = evaluator;
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setupMouseInteraction();
    }

    public void setState(GraphState state)
    {
        this.state = state;
        repaint();
    }

    public void setWinkelModus(WinkelModus winkelModus)
    {
        this.winkelModus = winkelModus;
        repaint();
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.displayBackground());
        repaint();
    }

    public void setViewportChangedListener(Runnable viewportChangedListener)
    {
        this.viewportChangedListener = viewportChangedListener == null ? () -> {} : viewportChangedListener;
    }

    @Override
    protected void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AppTheme activeTheme = theme;
        Color background = activeTheme == null ? new Color(18, 18, 18) : activeTheme.displayBackground();
        Color foreground = activeTheme == null ? Color.WHITE : activeTheme.displayForeground();
        Color secondary = activeTheme == null ? new Color(150, 150, 150) : activeTheme.secondaryDisplayForeground();
        Color grid = blend(background, foreground, 0.18);

        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        zeichneRaster(g, grid, secondary);
        zeichneAchsen(g, foreground);
        zeichneFunktionen(g);
        zeichneBereich(g, secondary);

        g.dispose();
    }

    private void zeichneRaster(Graphics2D g, Color grid, Color labels)
    {
        g.setStroke(new BasicStroke(1f));
        g.setColor(grid);

        double xStep = ermittleSchrittweite(state.getXMax() - state.getXMin());
        double yStep = ermittleSchrittweite(state.getYMax() - state.getYMin());

        for (double x = Math.ceil(state.getXMin() / xStep) * xStep; x <= state.getXMax(); x += xStep)
        {
            int px = worldToScreenX(x);
            g.drawLine(px, 0, px, getHeight());
            zeichneLabel(g, labels, format(x), px + 4, Math.min(getHeight() - 6, worldToScreenY(0) + 16));
        }

        for (double y = Math.ceil(state.getYMin() / yStep) * yStep; y <= state.getYMax(); y += yStep)
        {
            int py = worldToScreenY(y);
            g.drawLine(0, py, getWidth(), py);
            if (Math.abs(y) > 1e-9)
            {
                zeichneLabel(g, labels, format(y), Math.max(4, worldToScreenX(0) + 6), py - 4);
            }
        }
    }

    private void zeichneAchsen(Graphics2D g, Color foreground)
    {
        g.setColor(foreground);
        g.setStroke(new BasicStroke(2f));

        if (state.getYMin() <= 0.0 && state.getYMax() >= 0.0)
        {
            int y = worldToScreenY(0.0);
            g.drawLine(0, y, getWidth(), y);
        }

        if (state.getXMin() <= 0.0 && state.getXMax() >= 0.0)
        {
            int x = worldToScreenX(0.0);
            g.drawLine(x, 0, x, getHeight());
        }
    }

    private void zeichneFunktionen(Graphics2D g)
    {
        for (FunktionsDefinition funktion : state.getFunktionen())
        {
            if (!funktion.isSichtbar())
            {
                continue;
            }

            Path2D path = new Path2D.Double();
            boolean pathGestartet = false;
            int letzterY = 0;

            for (int px = 0; px < getWidth(); px++)
            {
                double x = screenToWorldX(px);

                try
                {
                    double y = evaluator.auswerten(funktion.getAusdruck(), x, winkelModus);
                    if (!Double.isFinite(y) || y < state.getYMin() - 1_000 || y > state.getYMax() + 1_000)
                    {
                        pathGestartet = false;
                        continue;
                    }

                    int py = worldToScreenY(y);
                    if (pathGestartet && Math.abs(py - letzterY) > getHeight() * 2)
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
                catch (RuntimeException e)
                {
                    pathGestartet = false;
                }
            }

            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(funktion.getFarbe());
            g.draw(path);
        }
    }

    private void zeichneBereich(Graphics2D g, Color secondary)
    {
        String text = String.format("x %.1f .. %.1f | y %.1f .. %.1f", state.getXMin(), state.getXMax(), state.getYMin(), state.getYMax());
        g.setColor(secondary);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, getWidth() - metrics.stringWidth(text) - 12, getHeight() - 12);
    }

    private void zeichneLabel(Graphics2D g, Color color, String text, int x, int y)
    {
        g.setColor(color);
        g.drawString(text, x, y);
    }

    private int worldToScreenX(double x)
    {
        return (int) Math.round((x - state.getXMin()) / (state.getXMax() - state.getXMin()) * getWidth());
    }

    private int worldToScreenY(double y)
    {
        return (int) Math.round((state.getYMax() - y) / (state.getYMax() - state.getYMin()) * getHeight());
    }

    private double screenToWorldX(int x)
    {
        return state.getXMin() + (x / Math.max(1.0, getWidth() - 1.0)) * (state.getXMax() - state.getXMin());
    }

    private double screenToWorldY(int y)
    {
        return state.getYMax() - (y / Math.max(1.0, getHeight() - 1.0)) * (state.getYMax() - state.getYMin());
    }

    private void setupMouseInteraction()
    {
        MouseAdapter mouseAdapter = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                letzterDragPunkt = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                letzterDragPunkt = null;
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                if (letzterDragPunkt == null)
                {
                    return;
                }

                double vorherX = screenToWorldX(letzterDragPunkt.x);
                double vorherY = screenToWorldY(letzterDragPunkt.y);
                double jetztX = screenToWorldX(e.getX());
                double jetztY = screenToWorldY(e.getY());

                state.verschiebe(vorherX - jetztX, vorherY - jetztY);
                letzterDragPunkt = e.getPoint();
                viewportChangedListener.run();
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                state.zoom(e.getPreciseWheelRotation() < 0 ? 0.85 : 1.15);
                viewportChangedListener.run();
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    state.resetAnsicht();
                    viewportChangedListener.run();
                    repaint();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    private double ermittleSchrittweite(double span)
    {
        double rough = span / 10.0;
        double power = Math.pow(10, Math.floor(Math.log10(rough)));
        double normalized = rough / power;

        if (normalized < 2.0) return power;
        if (normalized < 5.0) return 2.0 * power;
        return 5.0 * power;
    }

    private String format(double value)
    {
        if (Math.abs(value) < 1e-9)
        {
            return "0";
        }
        if (Math.abs(value - Math.rint(value)) < 1e-9)
        {
            return Long.toString(Math.round(value));
        }
        return String.format("%.1f", value);
    }

    private Color blend(Color a, Color b, double amount)
    {
        double inverse = 1.0 - amount;
        return new Color(
                (int) (a.getRed() * inverse + b.getRed() * amount),
                (int) (a.getGreen() * inverse + b.getGreen() * amount),
                (int) (a.getBlue() * inverse + b.getBlue() * amount)
        );
    }
}
