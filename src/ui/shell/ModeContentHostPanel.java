package ui.shell;

import common.state.RechnerModus;
import ui.animation.AnimationSupport;
import ui.theme.AppTheme;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

public class ModeContentHostPanel extends JPanel
{
    private final CardLayout cardLayout = new CardLayout();
    private final Map<RechnerModus, JPanel> modePanels = new EnumMap<>(RechnerModus.class);

    private double fadeOverlayAlpha;
    private Color fadeOverlayColor = Color.BLACK;

    public ModeContentHostPanel()
    {
        setLayout(cardLayout);
        setOpaque(false);
    }

    public void registerMode(RechnerModus modus, JPanel panel)
    {
        modePanels.put(modus, panel);
        add(panel, modus.name());
    }

    public void showMode(RechnerModus modus)
    {
        cardLayout.show(this, modus.name());
    }

    public void showMode(RechnerModus modus, AppTheme theme)
    {
        cardLayout.show(this, modus.name());
        animateModeChange(theme);
    }

    public void pulse(AppTheme theme)
    {
        animateModeChange(theme);
    }

    public JPanel getModePanel(RechnerModus modus)
    {
        return modePanels.get(modus);
    }

    double fadeOverlayAlphaForTest()
    {
        return fadeOverlayAlpha;
    }

    private void animateModeChange(AppTheme theme)
    {
        setOpaque(false);
        fadeOverlayColor = theme == null ? Color.BLACK : theme.softAccentBackground();
        fadeOverlayAlpha = 0.72;
        repaint();

        AnimationSupport.animate(220,
                progress -> {
                    fadeOverlayAlpha = 0.72 * (1.0 - progress);
                    repaint();
                },
                () -> {
                    fadeOverlayAlpha = 0.0;
                    repaint();
                });
    }

    @Override
    protected void paintChildren(Graphics graphics)
    {
        super.paintChildren(graphics);
        if (fadeOverlayAlpha <= 0.0)
        {
            return;
        }

        Graphics2D g = (Graphics2D) graphics.create();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) fadeOverlayAlpha));
        g.setColor(fadeOverlayColor);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g.dispose();
    }
}
