package ui.theme;

import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * Tabs in Theme-Farben. Das Standard-Look-and-Feel malt den aktiven Tab und den Inhaltsbereich
 * mit festen Farben (hellblau) und ignoriert {@code setBackground}, im Dark Theme sah das übel aus.
 */
public final class ThemeTabbedPaneUI extends BasicTabbedPaneUI
{
    private final AppTheme theme;

    private ThemeTabbedPaneUI(AppTheme theme)
    {
        this.theme = theme;
    }

    public static void apply(JTabbedPane tabs, AppTheme theme)
    {
        tabs.setUI(new ThemeTabbedPaneUI(theme));
        tabs.setBackground(theme.panelBackground());
        tabs.setForeground(theme.displayForeground());
        tabs.setFont(AppFonts.fett(12));
    }

    @Override
    protected void installDefaults()
    {
        super.installDefaults();
        Color rand = theme.modeBorder();
        highlight = rand;
        lightHighlight = rand;
        shadow = rand;
        darkShadow = rand;
        focus = theme.panelBackground();
        tabInsets = new Insets(5, 12, 5, 12);
        contentBorderInsets = new Insets(1, 1, 1, 1);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected)
    {
        g.setColor(isSelected ? theme.modeButtonActiveBackground() : theme.modeButtonInactiveBackground());
        g.fillRect(x, y, w, h);
    }

    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex,
                             String title, Rectangle textRect, boolean isSelected)
    {
        g.setFont(font);
        g.setColor(theme.modeButtonForeground(isSelected));
        g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex)
    {
        Insets insets = tabPane.getInsets();
        int tabAreaHoehe = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int x = insets.left;
        int y = insets.top + tabAreaHoehe;
        int w = tabPane.getWidth() - insets.left - insets.right;
        int h = tabPane.getHeight() - y - insets.bottom;

        g.setColor(theme.panelBackground());
        g.fillRect(x, y, w, h);
        g.setColor(theme.modeBorder());
        g.drawRect(x, y, w - 1, h - 1);
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
                                       Rectangle iconRect, Rectangle textRect, boolean isSelected)
    {
        // Tabs sind nicht fokussierbar, ein Fokusrahmen wäre nur ein Strich mitten im Text.
    }
}
