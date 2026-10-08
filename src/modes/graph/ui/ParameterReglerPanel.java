package modes.graph.ui;

import modes.graph.formatting.GraphFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Ein Schieberegler pro Parameter (a, b, c, d, k) von −10 bis 10 in Schritten von 0,1. */
final class ParameterReglerPanel extends JPanel
{
    private static final int SKALA = 10;
    private static final double STARTWERT = 1.0;

    private final GraphFormatter formatter = new GraphFormatter();
    private final Runnable geaendert;
    // Werte bleiben erhalten, auch wenn ein Parameter kurz aus dem Ausdruck verschwindet.
    private final Map<String, Double> alleWerte = new LinkedHashMap<>();
    private Set<String> angezeigt = Set.of();

    ParameterReglerPanel(Runnable geaendert)
    {
        super(new GridLayout(0, 1, 0, 2));
        this.geaendert = geaendert;
        setOpaque(false);
    }

    /** Baut die Regler nur neu, wenn sich die Parameter geändert haben; liefert {@code true} in dem Fall. */
    boolean zeige(Set<String> parameter)
    {
        if (parameter.equals(angezeigt))
        {
            return false;
        }

        angezeigt = Set.copyOf(parameter);
        removeAll();
        for (String name : new TreeSet<>(parameter))
        {
            alleWerte.putIfAbsent(name, STARTWERT);
            add(baueZeile(name));
        }
        revalidate();
        repaint();
        return true;
    }

    /** Aktuelle Werte der angezeigten Parameter. */
    Map<String, Double> werte()
    {
        Map<String, Double> werte = new LinkedHashMap<>();
        for (String name : angezeigt)
        {
            werte.put(name, alleWerte.get(name));
        }
        return werte;
    }

    private JPanel baueZeile(String name)
    {
        JSlider slider = new JSlider(-10 * SKALA, 10 * SKALA, (int) Math.round(alleWerte.get(name) * SKALA));
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setName("parameter-" + name);
        slider.setToolTipText("Parameter " + name + " verändern");

        JLabel wertLabel = new JLabel();
        wertLabel.setPreferredSize(new Dimension(70, 20));
        Runnable zeigeWert = () -> wertLabel.setText(name + " = " + formatter.formatiereZahl(alleWerte.get(name)));
        zeigeWert.run();

        slider.addChangeListener(e -> {
            alleWerte.put(name, slider.getValue() / (double) SKALA);
            zeigeWert.run();
            geaendert.run();
        });

        JPanel zeile = new JPanel(new BorderLayout(6, 0));
        zeile.setOpaque(false);
        zeile.add(wertLabel, BorderLayout.WEST);
        zeile.add(slider, BorderLayout.CENTER);
        return zeile;
    }
}
