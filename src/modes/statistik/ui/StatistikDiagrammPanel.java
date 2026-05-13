package modes.statistik.ui;

import modes.statistik.formatting.StatistikFormatter;
import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikDiagrammTyp;
import modes.statistik.model.StatistikErgebnis;
import modes.statistik.model.StatistikKlasse;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

class StatistikDiagrammPanel extends JPanel
{
    private final StatistikFormatter formatter = new StatistikFormatter();
    private StatistikErgebnis ergebnis;
    private StatistikDiagrammTyp diagrammTyp = StatistikDiagrammTyp.HISTOGRAMM;
    private AppTheme theme = new DarkTheme();

    StatistikDiagrammPanel()
    {
        setPreferredSize(new Dimension(420, 260));
        setMinimumSize(new Dimension(320, 220));
    }

    void setErgebnis(StatistikErgebnis ergebnis)
    {
        this.ergebnis = ergebnis;
        repaint();
    }

    void setDiagrammTyp(StatistikDiagrammTyp diagrammTyp)
    {
        this.diagrammTyp = diagrammTyp == null ? StatistikDiagrammTyp.HISTOGRAMM : diagrammTyp;
        repaint();
    }

    void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.displayBackground());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(theme.displayForeground());

        if (ergebnis == null || ergebnis.getDatenpunkte().isEmpty())
        {
            drawCentered(g2, "Keine Statistikdaten");
            g2.dispose();
            return;
        }

        switch (diagrammTyp)
        {
            case HISTOGRAMM -> drawHistogramm(g2);
            case BOXPLOT -> drawBoxplot(g2);
            case STREUDIAGRAMM -> drawStreudiagramm(g2);
        }

        g2.dispose();
    }

    private void drawHistogramm(Graphics2D g2)
    {
        List<StatistikKlasse> klassen = ergebnis.getHistogramm();
        int left = 42;
        int top = 26;
        int width = getWidth() - 64;
        int height = getHeight() - 64;
        int max = klassen.stream().mapToInt(StatistikKlasse::anzahl).max().orElse(1);

        drawAxes(g2, left, top, width, height);

        int barWidth = Math.max(1, width / Math.max(1, klassen.size()));
        for (int i = 0; i < klassen.size(); i++)
        {
            StatistikKlasse klasse = klassen.get(i);
            int barHeight = (int) Math.round((klasse.anzahl() / (double) max) * (height - 8));
            int x = left + i * barWidth + 3;
            int y = top + height - barHeight;
            g2.setColor(theme.operatorButtonBackground());
            g2.fillRect(x, y, Math.max(1, barWidth - 6), barHeight);
            g2.setColor(theme.secondaryDisplayForeground());
            g2.drawString(Integer.toString(klasse.anzahl()), x, y - 4);
        }

        g2.setColor(theme.secondaryDisplayForeground());
        g2.drawString("Histogramm", left, 18);
    }

    private void drawBoxplot(Graphics2D g2)
    {
        int left = 48;
        int top = 38;
        int width = getWidth() - 96;
        int centerY = getHeight() / 2;
        int boxHeight = 58;

        double min = ergebnis.getMinimum();
        double max = ergebnis.getMaximum();
        double span = max - min == 0.0 ? 1.0 : max - min;

        int minX = left;
        int maxX = left + width;
        int q1X = left + (int) Math.round((ergebnis.getQ1() - min) / span * width);
        int medianX = left + (int) Math.round((ergebnis.getMedian() - min) / span * width);
        int q3X = left + (int) Math.round((ergebnis.getQ3() - min) / span * width);

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(theme.secondaryDisplayForeground());
        g2.drawLine(minX, centerY, maxX, centerY);
        g2.drawLine(minX, centerY - 20, minX, centerY + 20);
        g2.drawLine(maxX, centerY - 20, maxX, centerY + 20);

        g2.setColor(theme.functionButtonBackground());
        g2.fillRect(q1X, centerY - boxHeight / 2, Math.max(1, q3X - q1X), boxHeight);
        g2.setColor(theme.displayForeground());
        g2.drawRect(q1X, centerY - boxHeight / 2, Math.max(1, q3X - q1X), boxHeight);
        g2.setColor(theme.operatorButtonBackground());
        g2.drawLine(medianX, centerY - boxHeight / 2, medianX, centerY + boxHeight / 2);

        g2.setFont(g2.getFont().deriveFont(12f));
        drawLabel(g2, "min " + formatter.formatiereZahl(min), minX, top);
        drawLabel(g2, "Q1 " + formatter.formatiereZahl(ergebnis.getQ1()), q1X, top + 20);
        drawLabel(g2, "Median " + formatter.formatiereZahl(ergebnis.getMedian()), medianX, top + 40);
        drawLabel(g2, "Q3 " + formatter.formatiereZahl(ergebnis.getQ3()), q3X, top + 60);
        drawLabel(g2, "max " + formatter.formatiereZahl(max), maxX, top);
    }

    private void drawStreudiagramm(Graphics2D g2)
    {
        List<StatistikDatenpunkt> daten = ergebnis.getDatenpunkte();
        int left = 42;
        int top = 26;
        int width = getWidth() - 64;
        int height = getHeight() - 64;

        double minX = daten.stream().mapToDouble(StatistikDatenpunkt::x).min().orElse(0.0);
        double maxX = daten.stream().mapToDouble(StatistikDatenpunkt::x).max().orElse(1.0);
        double minY = daten.stream().mapToDouble(StatistikDatenpunkt::y).min().orElse(0.0);
        double maxY = daten.stream().mapToDouble(StatistikDatenpunkt::y).max().orElse(1.0);
        double spanX = maxX - minX == 0.0 ? 1.0 : maxX - minX;
        double spanY = maxY - minY == 0.0 ? 1.0 : maxY - minY;

        drawAxes(g2, left, top, width, height);
        g2.setColor(theme.operatorButtonBackground());
        for (StatistikDatenpunkt punkt : daten)
        {
            int x = left + (int) Math.round((punkt.x() - minX) / spanX * width);
            int y = top + height - (int) Math.round((punkt.y() - minY) / spanY * height);
            g2.fillOval(x - 4, y - 4, 8, 8);
        }

        g2.setColor(theme.secondaryDisplayForeground());
        g2.drawString("Streudiagramm", left, 18);
    }

    private void drawAxes(Graphics2D g2, int left, int top, int width, int height)
    {
        g2.setColor(theme.secondaryDisplayForeground());
        g2.drawLine(left, top + height, left + width, top + height);
        g2.drawLine(left, top, left, top + height);
    }

    private void drawCentered(Graphics2D g2, String text)
    {
        FontMetrics metrics = g2.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = (getHeight() + metrics.getAscent()) / 2;
        g2.drawString(text, x, y);
    }

    private void drawLabel(Graphics2D g2, String text, int x, int y)
    {
        g2.setColor(theme.secondaryDisplayForeground());
        g2.drawString(text, Math.max(6, Math.min(x, getWidth() - 110)), y);
    }
}
