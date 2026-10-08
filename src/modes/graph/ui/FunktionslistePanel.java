package modes.graph.ui;

import modes.graph.model.GraphState;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/** Überschrift „Funktionen“ mit einer Zeile pro Funktion: Farbknopf, Ausdruck, Sichtbar-Haken, Entfernen. */
final class FunktionslistePanel extends JPanel
{
    private final GraphState state;
    private final Runnable zeichnen;
    private final IntConsumer auswaehlen;
    private final IntConsumer entfernen;

    private final JPanel expressionRows = new JPanel(new GridLayout(0, 1, 0, 8));
    private final JScrollPane functionScrollPane = new JScrollPane(expressionRows);
    private final List<JTextField> expressionFields = new ArrayList<>();
    private final List<JCheckBox> visibleChecks = new ArrayList<>();
    private final List<JButton> functionButtons = new ArrayList<>();
    private final List<JButton> removeButtons = new ArrayList<>();
    private AppTheme theme;

    FunktionslistePanel(GraphState state, Runnable hinzufuegen, Runnable zeichnen, IntConsumer auswaehlen, IntConsumer entfernen)
    {
        super(new BorderLayout(0, 6));
        this.state = state;
        this.zeichnen = zeichnen;
        this.auswaehlen = auswaehlen;
        this.entfernen = entfernen;
        setOpaque(false);

        JLabel title = new JLabel("Funktionen");
        title.setFont(AppFonts.fett(19));

        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setOpaque(false);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(GraphSteuerleiste.kompakterButton("+ Funktion", "Noch eine Funktion hinzufügen", hinzufuegen), BorderLayout.EAST);

        expressionRows.setOpaque(false);
        baueNeu();

        functionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        functionScrollPane.setOpaque(false);
        functionScrollPane.getViewport().setOpaque(false);
        functionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        functionScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        functionScrollPane.setPreferredSize(new Dimension(300, 126));

        add(titleRow, BorderLayout.NORTH);
        add(functionScrollPane, BorderLayout.CENTER);
    }

    void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        for (JTextField expressionField : expressionFields)
        {
            expressionField.setFont(AppFonts.normal(16));
            ModernButtonStyler.styleInput(expressionField, theme);
            expressionField.setCaretColor(theme.displayForeground());
        }
        functionScrollPane.setBorder(BorderFactory.createLineBorder(theme.cardBorder()));
        functionScrollPane.getViewport().setBackground(theme.panelBackground());
    }

    void baueNeu()
    {
        expressionFields.clear();
        visibleChecks.clear();
        functionButtons.clear();
        removeButtons.clear();
        expressionRows.removeAll();

        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            expressionRows.add(buildFunctionRow(index));
        }

        expressionRows.revalidate();
        expressionRows.repaint();
        aktualisiereAuswahl();
    }

    /** Überträgt Texte und Haken aus den Eingabezeilen in den {@link GraphState}. */
    void uebernehmeInState()
    {
        for (int i = 0; i < expressionFields.size(); i++)
        {
            state.getFunktion(i).setAusdruck(expressionFields.get(i).getText().trim());
            state.getFunktion(i).setSichtbar(visibleChecks.get(i).isSelected());
        }
    }

    void fokussiereAktive()
    {
        expressionFields.get(state.getAktiveFunktionIndex()).requestFocusInWindow();
    }

    void aktualisiereAuswahl()
    {
        for (int index = 0; index < functionButtons.size(); index++)
        {
            JButton button = functionButtons.get(index);
            Color farbe = state.getFunktion(index).getFarbe();
            boolean aktiv = index == state.getAktiveFunktionIndex();
            Color rahmen = theme == null
                    ? (aktiv ? Color.WHITE : farbe.darker())
                    : (aktiv ? theme.focusBorder() : theme.cardBorder());
            button.setBackground(farbe);
            button.setForeground(theme == null ? Color.WHITE : theme.contrastForeground(farbe));
            button.setBorder(BorderFactory.createLineBorder(rahmen, aktiv ? 3 : 1));
        }

        boolean kannEntfernen = state.getFunktionen().size() > 1;
        for (JButton removeButton : removeButtons)
        {
            removeButton.setEnabled(kannEntfernen);
        }
    }

    private JPanel buildFunctionRow(int index)
    {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JButton swatch = new JButton(state.getFunktion(index).getName());
        swatch.setHorizontalAlignment(JLabel.CENTER);
        swatch.setOpaque(true);
        swatch.setPreferredSize(new Dimension(28, 36));
        swatch.setBackground(state.getFunktion(index).getFarbe());
        swatch.setForeground(Color.WHITE);
        swatch.putClientProperty("graphSwatch", Boolean.TRUE);
        swatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        swatch.setToolTipText("Kurvendiskussion für diese Funktion anzeigen");
        swatch.addActionListener(e -> auswaehlen.accept(index));
        functionButtons.add(swatch);

        JTextField field = new JTextField(state.getFunktion(index).getAusdruck());
        field.setToolTipText("Andere Funktionen gehen als f(x) oder kurz als f, Parameter a, b, c, d, k bekommen einen Regler");
        field.addActionListener(e -> zeichnen.run());
        field.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                auswaehlen.accept(index);
            }
        });
        expressionFields.add(field);

        JCheckBox visible = new JCheckBox();
        visible.setSelected(state.getFunktion(index).isSichtbar());
        visible.setOpaque(false);
        visible.addActionListener(e -> {
            state.getFunktion(index).setSichtbar(visible.isSelected());
            zeichnen.run();
        });
        visibleChecks.add(visible);

        JButton remove = new JButton("×");
        remove.setFocusable(false);
        remove.putClientProperty("compactGraphControl", Boolean.TRUE);
        remove.setMargin(new java.awt.Insets(2, 6, 2, 6));
        remove.setToolTipText("Funktion entfernen");
        remove.addActionListener(e -> entfernen.accept(index));
        removeButtons.add(remove);

        JPanel rowActions = new JPanel(new BorderLayout(4, 0));
        rowActions.setOpaque(false);
        rowActions.add(visible, BorderLayout.WEST);
        rowActions.add(remove, BorderLayout.EAST);

        row.add(swatch, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.add(rowActions, BorderLayout.EAST);
        return row;
    }
}
