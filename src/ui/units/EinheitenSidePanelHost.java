package ui.units;

import ui.theme.AppTheme;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class EinheitenSidePanelHost extends JPanel
{
    private static final int OPEN_WIDTH = 330;
    private static final int STEP = 42;
    private static final int TIMER_DELAY_MS = 12;

    private final EinheitenSidePanel sidePanel = new EinheitenSidePanel();
    private final Timer animationTimer;

    private int currentWidth;
    private int targetWidth;
    private boolean geoeffnet;

    public EinheitenSidePanelHost()
    {
        setLayout(new BorderLayout());
        setOpaque(false);
        add(sidePanel, BorderLayout.CENTER);
        sidePanel.setCloseListener(() -> setGeoeffnet(false));
        setPreferredSize(new Dimension(0, 1));
        setMinimumSize(new Dimension(0, 1));

        animationTimer = new Timer(TIMER_DELAY_MS, e -> animateStep());
        animationTimer.setRepeats(true);
    }

    public void toggle()
    {
        setGeoeffnet(!geoeffnet);
    }

    public void setGeoeffnet(boolean geoeffnet)
    {
        this.geoeffnet = geoeffnet;
        targetWidth = geoeffnet ? OPEN_WIDTH : 0;
        if (!animationTimer.isRunning())
        {
            animationTimer.start();
        }
    }

    public boolean isGeoeffnet()
    {
        return geoeffnet;
    }

    public int getAktuelleBreite()
    {
        return currentWidth;
    }

    public void applyTheme(AppTheme theme)
    {
        sidePanel.applyTheme(theme);
    }

    private void animateStep()
    {
        if (currentWidth == targetWidth)
        {
            animationTimer.stop();
            return;
        }

        if (currentWidth < targetWidth)
        {
            currentWidth = Math.min(targetWidth, currentWidth + STEP);
        }
        else
        {
            currentWidth = Math.max(targetWidth, currentWidth - STEP);
        }

        Dimension size = new Dimension(currentWidth, 1);
        setPreferredSize(size);
        setMinimumSize(size);
        revalidate();
        repaint();
    }
}
