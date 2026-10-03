package modes.graph.ui;

import common.state.WinkelModus;
import modes.graph.formatting.GraphFormatter;
import modes.graph.logic.GraphEvaluator;
import modes.graph.model.FunktionsDefinition;
import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import modes.graph.model.KurvendiskussionResult;
import ui.animation.AnimationSupport;
import ui.theme.AppTheme;

import java.awt.AlphaComposite;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class GraphCanvasPanel extends JPanel
{
    private static final int HOVER_VERZOEGERUNG_MS = 500;
    /** Maximaler Abstand in Pixeln, bis zu dem ein Klick noch eine Funktionskurve trifft. */
    private static final double KLICK_TOLERANZ_KURVE_PX = 11.0;
    /** Maximaler Abstand in Pixeln, bis zu dem ein Rechtsklick noch einen Analysepunkt trifft. */
    private static final double KLICK_TOLERANZ_PUNKT_PX = 15.0;
    private static final double ZOOM_FAKTOR_REIN = 0.85;
    private static final double ZOOM_FAKTOR_RAUS = 1.15;

    private final GraphEvaluator evaluator;
    private final GraphFormatter formatter = new GraphFormatter();
    private GraphState state;
    private AppTheme theme;
    private WinkelModus winkelModus = WinkelModus.DEG;
    private KurvendiskussionResult kurvendiskussionResult;
    private Point letzterDragPunkt;
    private Point hoverPunkt;
    private boolean hoverSichtbar;
    private double refreshPulse;
    private Runnable viewportChangedListener = () -> {};
    private IntConsumer functionSelectionListener = index -> {};
    private Consumer<GraphPunkt> pointSelectionListener = punkt -> {};
    private final Timer hoverTimer;

    public GraphCanvasPanel(GraphState state, GraphEvaluator evaluator)
    {
        this.state = state;
        this.evaluator = evaluator;
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        hoverTimer = new Timer(HOVER_VERZOEGERUNG_MS, e -> {
            hoverSichtbar = hoverPunkt != null;
            repaint();
        });
        hoverTimer.setRepeats(false);
        registriereMaussteuerung();
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

    public void setFunctionSelectionListener(IntConsumer functionSelectionListener)
    {
        this.functionSelectionListener = functionSelectionListener == null ? index -> {} : functionSelectionListener;
    }

    public void setPointSelectionListener(Consumer<GraphPunkt> pointSelectionListener)
    {
        this.pointSelectionListener = pointSelectionListener == null ? punkt -> {} : pointSelectionListener;
    }

    public void setKurvendiskussionResult(KurvendiskussionResult kurvendiskussionResult)
    {
        this.kurvendiskussionResult = kurvendiskussionResult;
        repaint();
    }

    public void pulseRefresh()
    {
        AnimationSupport.animate(220,
                progress -> {
                    refreshPulse = Math.sin(progress * Math.PI);
                    repaint();
                },
                () -> {
                    refreshPulse = 0.0;
                    repaint();
                });
    }

    @Override
    protected void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AppTheme activeTheme = theme;
        Color background = activeTheme == null ? new Color(18, 18, 18) : activeTheme.canvasBackground();
        Color foreground = activeTheme == null ? Color.WHITE : activeTheme.displayForeground();
        Color secondary = activeTheme == null ? new Color(150, 150, 150) : activeTheme.secondaryDisplayForeground();
        Color grid = activeTheme == null ? mische(background, foreground, 0.18) : activeTheme.gridColor();

        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());
        zeichneRefreshPulse(g, activeTheme);

        zeichneRaster(g, grid, secondary);
        zeichneAchsen(g, foreground);
        zeichneFunktionen(g);
        zeichneAnalysePunkte(g, background, foreground);
        zeichneBereich(g, secondary);
        zeichneHoverKoordinaten(g);

        g.dispose();
    }

    private void zeichneRefreshPulse(Graphics2D g, AppTheme activeTheme)
    {
        if (activeTheme == null || refreshPulse <= 0.0)
        {
            return;
        }

        Graphics2D pulseGraphics = (Graphics2D) g.create();
        pulseGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.16 * refreshPulse)));
        pulseGraphics.setColor(activeTheme.successPulseColor());
        pulseGraphics.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        pulseGraphics.dispose();
    }

    private void zeichneRaster(Graphics2D g, Color grid, Color labels)
    {
        g.setStroke(new BasicStroke(1f));
        g.setColor(grid);

        double xStep = ermittleSchrittweite(state.getXMax() - state.getXMin());
        double yStep = ermittleSchrittweite(state.getYMax() - state.getYMin());

        for (double x = Math.ceil(state.getXMin() / xStep) * xStep; x <= state.getXMax(); x += xStep)
        {
            int px = zuBildschirmX(x);
            g.drawLine(px, 0, px, getHeight());
            zeichneLabel(g, labels, formatter.formatiereAchsenwert(x), px + 4, Math.min(getHeight() - 6, zuBildschirmY(0) + 16));
        }

        for (double y = Math.ceil(state.getYMin() / yStep) * yStep; y <= state.getYMax(); y += yStep)
        {
            int py = zuBildschirmY(y);
            g.drawLine(0, py, getWidth(), py);
            if (Math.abs(y) > 1e-9)
            {
                zeichneLabel(g, labels, formatter.formatiereAchsenwert(y), Math.max(4, zuBildschirmX(0) + 6), py - 4);
            }
        }
    }

    private void zeichneAchsen(Graphics2D g, Color foreground)
    {
        g.setColor(foreground);
        g.setStroke(new BasicStroke(2f));

        if (state.getYMin() <= 0.0 && state.getYMax() >= 0.0)
        {
            int y = zuBildschirmY(0.0);
            g.drawLine(0, y, getWidth(), y);
        }

        if (state.getXMin() <= 0.0 && state.getXMax() >= 0.0)
        {
            int x = zuBildschirmX(0.0);
            g.drawLine(x, 0, x, getHeight());
        }
    }

    private void zeichneFunktionen(Graphics2D g)
    {
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

            for (int px = 0; px < getWidth(); px++)
            {
                double x = zuWeltX(px);

                try
                {
                    double y = evaluator.auswerten(funktion.getAusdruck(), x, winkelModus);
                    if (!Double.isFinite(y) || y < state.getYMin() - 1_000 || y > state.getYMax() + 1_000)
                    {
                        pathGestartet = false;
                        continue;
                    }

                    int py = zuBildschirmY(y);
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

            float breite = index == state.getAktiveFunktionIndex() ? 3.5f : 2.5f;
            g.setStroke(new BasicStroke(breite, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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

    private int zuBildschirmX(double x)
    {
        return (int) Math.round((x - state.getXMin()) / (state.getXMax() - state.getXMin()) * getWidth());
    }

    private int zuBildschirmY(double y)
    {
        return (int) Math.round((state.getYMax() - y) / (state.getYMax() - state.getYMin()) * getHeight());
    }

    private double zuWeltX(int x)
    {
        return state.getXMin() + (x / Math.max(1.0, getWidth() - 1.0)) * (state.getXMax() - state.getXMin());
    }

    private void zeichneAnalysePunkte(Graphics2D g, Color background, Color foreground)
    {
        if (kurvendiskussionResult == null)
        {
            return;
        }

        AppTheme activeTheme = theme;
        Color nullstelle = activeTheme == null ? new Color(30, 190, 120) : activeTheme.graphNullstelleColor();
        Color extremum = activeTheme == null ? new Color(255, 190, 60) : activeTheme.graphExtremumColor();
        Color wendestelle = activeTheme == null ? new Color(190, 120, 255) : activeTheme.graphWendestelleColor();
        Color yAchse = activeTheme == null ? new Color(70, 190, 255) : activeTheme.graphYAchseColor();

        zeichneMarker(g, kurvendiskussionResult.getYAchsenSchnittpunkt(), yAchse, background, "Y");

        for (GraphPunkt punkt : kurvendiskussionResult.getNullstellen())
        {
            zeichneMarker(g, punkt, nullstelle, background, "N");
        }

        for (GraphPunkt punkt : kurvendiskussionResult.getExtremstellen())
        {
            zeichneMarker(g, punkt, extremum, background, "E");
        }

        for (GraphPunkt punkt : kurvendiskussionResult.getWendestellen())
        {
            zeichneMarker(g, punkt, wendestelle, background, "W");
        }
    }

    private void zeichneMarker(Graphics2D g, GraphPunkt punkt, Color color, Color background, String label)
    {
        if (punkt == null || !istSichtbar(punkt))
        {
            return;
        }

        int x = zuBildschirmX(punkt.getX());
        int y = zuBildschirmY(punkt.getY());

        g.setColor(background);
        g.fillOval(x - 7, y - 7, 14, 14);
        g.setColor(color);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x - 7, y - 7, 14, 14);
        g.fillOval(x - 3, y - 3, 6, 6);
        g.drawString(label, x + 8, y - 8);
    }

    private boolean istSichtbar(GraphPunkt punkt)
    {
        return punkt.getX() >= state.getXMin()
                && punkt.getX() <= state.getXMax()
                && punkt.getY() >= state.getYMin()
                && punkt.getY() <= state.getYMax();
    }

    private double zuWeltY(int y)
    {
        return state.getYMax() - (y / Math.max(1.0, getHeight() - 1.0)) * (state.getYMax() - state.getYMin());
    }

    private void zeichneHoverKoordinaten(Graphics2D g)
    {
        if (!hoverSichtbar || hoverPunkt == null)
        {
            return;
        }

        String text = "x = " + formatter.formatiereZahl(zuWeltX(hoverPunkt.x))
                + "   y = " + formatter.formatiereZahl(zuWeltY(hoverPunkt.y));
        FontMetrics metrics = g.getFontMetrics();
        int breite = metrics.stringWidth(text) + 20;
        int hoehe = metrics.getHeight() + 10;
        int x = Math.min(hoverPunkt.x + 14, Math.max(6, getWidth() - breite - 6));
        int y = hoverPunkt.y - hoehe - 12;
        if (y < 6)
        {
            y = Math.min(getHeight() - hoehe - 6, hoverPunkt.y + 16);
        }

        AppTheme activeTheme = theme;
        Color background = activeTheme == null ? new Color(35, 35, 35) : activeTheme.popupBackground();
        Color foreground = activeTheme == null ? Color.WHITE : activeTheme.popupForeground();
        Color border = activeTheme == null ? new Color(100, 100, 100) : activeTheme.cardBorder();

        g.setColor(background);
        g.fillRoundRect(x, y, breite, hoehe, 10, 10);
        g.setColor(border);
        g.drawRoundRect(x, y, breite, hoehe, 10, 10);
        g.setColor(foreground);
        g.drawString(text, x + 10, y + metrics.getAscent() + 5);
    }

    private void registriereMaussteuerung()
    {
        Maussteuerung maussteuerung = new Maussteuerung();
        addMouseListener(maussteuerung);
        addMouseMotionListener(maussteuerung);
        addMouseWheelListener(maussteuerung);
    }

    private void ansichtGeaendert()
    {
        viewportChangedListener.run();
        repaint();
    }

    private void verbergeHover()
    {
        hoverTimer.stop();
        hoverSichtbar = false;
        hoverPunkt = null;
        repaint();
    }

    private int findeFunktion(Point punkt)
    {
        double x = zuWeltX(punkt.x);
        int besterIndex = -1;
        double besterAbstand = KLICK_TOLERANZ_KURVE_PX;

        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            FunktionsDefinition funktion = state.getFunktion(index);
            if (!funktion.isSichtbar() || funktion.getAusdruck().isBlank())
            {
                continue;
            }

            try
            {
                double y = evaluator.auswerten(funktion.getAusdruck(), x, winkelModus);
                if (!Double.isFinite(y))
                {
                    continue;
                }

                double abstand = Math.abs(zuBildschirmY(y) - punkt.y);
                if (abstand < besterAbstand)
                {
                    besterAbstand = abstand;
                    besterIndex = index;
                }
            }
            catch (RuntimeException ignored)
            {
                // Eine ungültige Funktion ist an dieser Stelle einfach nicht anklickbar.
            }
        }
        return besterIndex;
    }

    private void zeigePunktMenu(MouseEvent event)
    {
        GraphPunkt punkt = findeAnalysePunkt(event.getPoint());
        if (punkt == null)
        {
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        JMenuItem uebernehmen = new JMenuItem("In Wertetabelle übernehmen");
        uebernehmen.addActionListener(e -> pointSelectionListener.accept(punkt));
        JMenuItem kopieren = new JMenuItem("Punkt kopieren");
        kopieren.addActionListener(e -> kopierePunkt(punkt));
        gestaltePopup(menu, uebernehmen, kopieren);
        menu.add(uebernehmen);
        menu.add(kopieren);
        menu.show(this, event.getX(), event.getY());
    }

    private GraphPunkt findeAnalysePunkt(Point mausPunkt)
    {
        GraphPunkt besterPunkt = null;
        double besterAbstand = KLICK_TOLERANZ_PUNKT_PX;
        for (GraphPunkt punkt : analysePunkte())
        {
            if (punkt == null || !istSichtbar(punkt))
            {
                continue;
            }

            double deltaX = zuBildschirmX(punkt.getX()) - mausPunkt.x;
            double deltaY = zuBildschirmY(punkt.getY()) - mausPunkt.y;
            double abstand = Math.hypot(deltaX, deltaY);
            if (abstand < besterAbstand)
            {
                besterAbstand = abstand;
                besterPunkt = punkt;
            }
        }
        return besterPunkt;
    }

    private List<GraphPunkt> analysePunkte()
    {
        if (kurvendiskussionResult == null)
        {
            return List.of();
        }

        List<GraphPunkt> punkte = new ArrayList<>();
        punkte.add(kurvendiskussionResult.getYAchsenSchnittpunkt());
        punkte.addAll(kurvendiskussionResult.getNullstellen());
        punkte.addAll(kurvendiskussionResult.getExtremstellen());
        punkte.addAll(kurvendiskussionResult.getWendestellen());
        return punkte;
    }

    private void gestaltePopup(JPopupMenu menu, JMenuItem... items)
    {
        if (theme == null)
        {
            return;
        }

        menu.setBackground(theme.popupBackground());
        menu.setBorder(javax.swing.BorderFactory.createLineBorder(theme.cardBorder()));
        for (JMenuItem item : items)
        {
            item.setBackground(theme.popupOptionBackground());
            item.setForeground(theme.popupOptionForeground());
        }
    }

    private void kopierePunkt(GraphPunkt punkt)
    {
        String text = formatter.formatierePunkt(punkt);
        try
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        }
        catch (RuntimeException e)
        {
            Toolkit.getDefaultToolkit().beep();
        }
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

    private Color mische(Color a, Color b, double amount)
    {
        double inverse = 1.0 - amount;
        return new Color(
                (int) (a.getRed() * inverse + b.getRed() * amount),
                (int) (a.getGreen() * inverse + b.getGreen() * amount),
                (int) (a.getBlue() * inverse + b.getBlue() * amount)
        );
    }

    /** Verschieben, Zoomen, Hover und Klicks auf der Zeichenfläche. */
    private class Maussteuerung extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
            verbergeHover();
            if (e.isPopupTrigger())
            {
                zeigePunktMenu(e);
                return;
            }
            if (SwingUtilities.isRightMouseButton(e))
            {
                return;
            }
            letzterDragPunkt = e.getPoint();
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            letzterDragPunkt = null;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            if (e.isPopupTrigger())
            {
                zeigePunktMenu(e);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            verbergeHover();
            if (letzterDragPunkt == null)
            {
                return;
            }

            double vorherX = zuWeltX(letzterDragPunkt.x);
            double vorherY = zuWeltY(letzterDragPunkt.y);
            double jetztX = zuWeltX(e.getX());
            double jetztY = zuWeltY(e.getY());

            state.verschiebe(vorherX - jetztX, vorherY - jetztY);
            letzterDragPunkt = e.getPoint();
            ansichtGeaendert();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            verbergeHover();
            state.zoom(e.getPreciseWheelRotation() < 0 ? ZOOM_FAKTOR_REIN : ZOOM_FAKTOR_RAUS);
            ansichtGeaendert();
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            hoverPunkt = e.getPoint();
            hoverSichtbar = false;
            hoverTimer.restart();
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            verbergeHover();
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (!SwingUtilities.isLeftMouseButton(e))
            {
                return;
            }

            if (e.getClickCount() == 2)
            {
                state.resetAnsicht();
                ansichtGeaendert();
                return;
            }

            int funktionIndex = findeFunktion(e.getPoint());
            if (funktionIndex >= 0)
            {
                functionSelectionListener.accept(funktionIndex);
            }
        }
    }
}
