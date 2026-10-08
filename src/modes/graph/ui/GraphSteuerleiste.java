package modes.graph.ui;

import modes.graph.model.GraphState;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** Knöpfe unter der Funktionsliste: Zeichnen, Ansicht (Reset/Zoom) und das Datei-Menü. */
final class GraphSteuerleiste extends JPanel
{
    private final JPopupMenu dateiMenu = new JPopupMenu();

    GraphSteuerleiste(GraphState state, Runnable zeichnen, Runnable ansichtGeaendert,
                      Runnable oeffnen, Runnable speichern, Runnable alsPngSpeichern)
    {
        super(new GridLayout(0, 1, 0, 6));
        setOpaque(false);

        JPanel ansicht = zeile();
        ansicht.add(kompakterButton("Zeichnen", "Funktionen neu zeichnen", zeichnen));
        ansicht.add(kompakterButton("Reset", "Graphansicht zurücksetzen", () -> {
            state.resetAnsicht();
            ansichtGeaendert.run();
        }));
        ansicht.add(kompakterButton("+", "In den Graphen hineinzoomen", () -> {
            state.zoom(0.75);
            ansichtGeaendert.run();
        }));
        ansicht.add(kompakterButton("−", "Aus dem Graphen herauszoomen", () -> {
            state.zoom(1.35);
            ansichtGeaendert.run();
        }));

        dateiMenu.add(menuPunkt("Funktionen öffnen…", oeffnen));
        dateiMenu.add(menuPunkt("Funktionen speichern…", speichern));
        dateiMenu.add(menuPunkt("Als PNG speichern…", alsPngSpeichern));

        JPanel datei = zeile();
        JButton dateiButton = kompakterButton("Datei…", "Funktionsliste öffnen/speichern oder Graph als PNG sichern", () -> {});
        dateiButton.addActionListener(e -> dateiMenu.show(dateiButton, 0, dateiButton.getHeight()));
        datei.add(dateiButton);

        add(ansicht);
        add(datei);
    }

    void applyTheme(AppTheme theme)
    {
        dateiMenu.setBackground(theme.popupBackground());
        dateiMenu.setBorder(BorderFactory.createLineBorder(theme.cardBorder()));
        for (var element : dateiMenu.getComponents())
        {
            element.setBackground(theme.popupOptionBackground());
            element.setForeground(theme.popupOptionForeground());
        }
    }

    static JButton kompakterButton(String text, String tooltip, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        button.putClientProperty("compactGraphControl", Boolean.TRUE);
        button.setToolTipText(tooltip);
        button.setMargin(new java.awt.Insets(3, 8, 3, 8));
        return button;
    }

    private static JPanel zeile()
    {
        JPanel zeile = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        zeile.setOpaque(false);
        return zeile;
    }

    private static JMenuItem menuPunkt(String text, Runnable action)
    {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }
}
