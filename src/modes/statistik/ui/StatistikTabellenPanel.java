package modes.statistik.ui;

import common.formatting.ZahlenEingabe;
import modes.statistik.formatting.StatistikFormatter;
import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikErgebnis;
import ui.theme.AppTheme;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/** Eingabetabelle (x, y, Gewicht) mit farbig markierten Ausreißern. */
public class StatistikTabellenPanel extends JPanel
{
    private static final String[] TABLE_COLUMNS = {"x", "y", "Gewicht"};
    private static final int MIN_ZEILEN = 18;

    private final StatistikFormatter formatter = new StatistikFormatter();
    private final DefaultTableModel tableModel = new DefaultTableModel(TABLE_COLUMNS, MIN_ZEILEN);
    private final JTable dataTable = new JTable(tableModel);

    private AppTheme theme;
    private StatistikErgebnis ergebnis;

    public StatistikTabellenPanel()
    {
        super(new BorderLayout());
        setOpaque(false);

        dataTable.setFillsViewportHeight(true);
        dataTable.setRowHeight(24);
        dataTable.setDefaultRenderer(Object.class, new AusreisserRenderer());
        add(new JScrollPane(dataTable), BorderLayout.CENTER);
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        dataTable.setBackground(theme.inputBackground());
        dataTable.setForeground(theme.displayForeground());
        dataTable.setGridColor(theme.modeBorder());
        dataTable.getTableHeader().setBackground(theme.toggleButtonBackground());
        dataTable.getTableHeader().setForeground(theme.toggleButtonForeground());
    }

    /** Merkt sich das Ergebnis, damit die Ausreißer-Zeilen eingefärbt werden; null hebt die Markierung auf. */
    public void markiereAusreisser(StatistikErgebnis ergebnis)
    {
        this.ergebnis = ergebnis;
        dataTable.repaint();
    }

    public List<StatistikDatenpunkt> leseDaten()
    {
        List<StatistikDatenpunkt> daten = new ArrayList<>();
        int fallbackX = 1;

        for (int row = 0; row < tableModel.getRowCount(); row++)
        {
            String xText = cell(row, 0);
            String yText = cell(row, 1);
            String gewichtText = cell(row, 2);

            if (xText.isBlank() && yText.isBlank() && gewichtText.isBlank())
            {
                continue;
            }

            double x;
            double y;
            if (yText.isBlank())
            {
                x = fallbackX;
                y = ZahlenEingabe.lese(xText);
            }
            else
            {
                x = xText.isBlank() ? fallbackX : ZahlenEingabe.lese(xText);
                y = ZahlenEingabe.lese(yText);
            }

            double gewicht = gewichtText.isBlank() ? 1.0 : ZahlenEingabe.lese(gewichtText);
            daten.add(new StatistikDatenpunkt(x, y, gewicht));
            fallbackX++;
        }

        return daten;
    }

    public void zeigeDaten(List<StatistikDatenpunkt> daten)
    {
        leeren(Math.max(MIN_ZEILEN, daten.size()));
        for (int i = 0; i < daten.size(); i++)
        {
            StatistikDatenpunkt punkt = daten.get(i);
            tableModel.setValueAt(formatter.formatiereZahl(punkt.x()), i, 0);
            tableModel.setValueAt(formatter.formatiereZahl(punkt.y()), i, 1);
            tableModel.setValueAt(formatter.formatiereZahl(punkt.gewicht()), i, 2);
        }
    }

    public void fuelleBeispiel()
    {
        leeren();
        double[][] beispiel = {
                {1, 2, 1},
                {2, 3, 1},
                {3, 5, 1},
                {4, 8, 1},
                {5, 13, 1}
        };

        for (int i = 0; i < beispiel.length; i++)
        {
            tableModel.setValueAt(beispiel[i][0], i, 0);
            tableModel.setValueAt(beispiel[i][1], i, 1);
            tableModel.setValueAt(beispiel[i][2], i, 2);
        }
    }

    public void leeren()
    {
        leeren(MIN_ZEILEN);
    }

    private void leeren(int rows)
    {
        tableModel.setRowCount(rows);
        for (int row = 0; row < tableModel.getRowCount(); row++)
        {
            for (int column = 0; column < tableModel.getColumnCount(); column++)
            {
                tableModel.setValueAt(null, row, column);
            }
        }
    }

    private String cell(int row, int column)
    {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private boolean istAusreisserZeile(int row)
    {
        if (ergebnis == null)
        {
            return false;
        }

        // Gleiche Regel wie in leseDaten: steht nur ein Wert in x, ist er der y-Wert.
        String wertText = cell(row, 1).isBlank() ? cell(row, 0) : cell(row, 1);
        try
        {
            return !wertText.isBlank() && ergebnis.istAusreisser(ZahlenEingabe.lese(wertText));
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    private class AusreisserRenderer extends DefaultTableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column)
        {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected)
            {
                boolean markiert = theme != null && istAusreisserZeile(row);
                component.setBackground(markiert ? theme.errorPulseColor() : table.getBackground());
                component.setForeground(markiert ? theme.contrastForeground(theme.errorPulseColor()) : table.getForeground());
            }
            return component;
        }
    }
}
