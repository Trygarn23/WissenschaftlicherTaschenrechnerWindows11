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

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
    private final GraphZeichner zeichner;
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
        this.zeichner = new GraphZeichner(evaluator, formatter);
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
        zeichner.zeichne(g, new GraphZeichner.Szene(
                koordinaten(),
                theme,
                winkelModus,
                kurvendiskussionResult,
                hoverSichtbar ? hoverPunkt : null,
                refreshPulse));
        g.dispose();
    }

    private GraphKoordinaten koordinaten()
    {
        return new GraphKoordinaten(state, getWidth(), getHeight());
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
        double x = koordinaten().zuWeltX(punkt.x);
        int besterIndex = -1;
        double besterAbstand = KLICK_TOLERANZ_KURVE_PX;

        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            FunktionsDefinition funktion = state.getFunktion(index);
            if (!funktion.isSichtbar() || funktion.getAusdruck().isBlank())
            {
                continue;
            }

            // Eine ungültige Funktion liefert NaN und ist an dieser Stelle einfach nicht anklickbar.
            double y = evaluator.wertOderNaN(funktion.getAusdruck(), x, winkelModus);
            if (Double.isNaN(y))
            {
                continue;
            }

            double abstand = Math.abs(koordinaten().zuBildschirmY(y) - punkt.y);
            if (abstand < besterAbstand)
            {
                besterAbstand = abstand;
                besterIndex = index;
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
            if (punkt == null || !koordinaten().istSichtbar(punkt))
            {
                continue;
            }

            double deltaX = koordinaten().zuBildschirmX(punkt.getX()) - mausPunkt.x;
            double deltaY = koordinaten().zuBildschirmY(punkt.getY()) - mausPunkt.y;
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
        catch (IllegalStateException e)
        {
            // Zwischenablage gerade von einem anderen Programm belegt.
            Toolkit.getDefaultToolkit().beep();
        }
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

            double vorherX = koordinaten().zuWeltX(letzterDragPunkt.x);
            double vorherY = koordinaten().zuWeltY(letzterDragPunkt.y);
            double jetztX = koordinaten().zuWeltX(e.getX());
            double jetztY = koordinaten().zuWeltY(e.getY());

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
