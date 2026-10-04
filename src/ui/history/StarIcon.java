package ui.history;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Selbst gemalter Stern, weil Segoe UI die Zeichen ☆/★ nicht kennt und nur ein Kästchen zeigt.
 * Die Farbe kommt von der Komponente, damit der Stern in jedem Theme passt.
 */
final class StarIcon implements Icon
{
    private final boolean filled;
    private final int size;

    StarIcon(boolean filled, int size)
    {
        this.filled = filled;
        this.size = size;
    }

    boolean isFilled()
    {
        return filled;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getForeground());

        Path2D star = new Path2D.Double();
        double center = size / 2.0;
        for (int i = 0; i < 10; i++)
        {
            double radius = i % 2 == 0 ? center : center * 0.42;
            double angle = Math.PI / 5 * i - Math.PI / 2;
            double px = x + center + radius * Math.cos(angle);
            double py = y + center + radius * Math.sin(angle);
            if (i == 0)
            {
                star.moveTo(px, py);
            }
            else
            {
                star.lineTo(px, py);
            }
        }
        star.closePath();

        if (filled)
        {
            g2.fill(star);
        }
        else
        {
            g2.setStroke(new BasicStroke(1.3f));
            g2.draw(star);
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth()
    {
        return size;
    }

    @Override
    public int getIconHeight()
    {
        return size;
    }
}
