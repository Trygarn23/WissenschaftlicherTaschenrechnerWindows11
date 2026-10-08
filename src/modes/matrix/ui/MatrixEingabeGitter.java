package modes.matrix.ui;

import common.formatting.ZahlenAnzeige;
import common.formatting.ZahlenEingabe;
import modes.matrix.model.Matrix;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Titel, Zeilen-/Spaltenauswahl und das Textfeld-Gitter für eine Matrix. */
public class MatrixEingabeGitter extends JPanel
{
    private static final Integer[] GROESSEN = {1, 2, 3, 4, 5, 6};

    private final String prefix;
    private final JComboBox<Integer> zeilenBox = new JComboBox<>(GROESSEN);
    private final JComboBox<Integer> spaltenBox = new JComboBox<>(GROESSEN);
    private final JPanel host = new JPanel(new BorderLayout());
    private JTextField[][] felder = new JTextField[0][0];
    private AppTheme theme;

    public MatrixEingabeGitter(String titel, String prefix)
    {
        super(new BorderLayout(0, 10));
        this.prefix = prefix;
        setOpaque(false);

        JLabel label = new JLabel(titel);
        label.setFont(AppFonts.fett(18));

        zeilenBox.setSelectedItem(2);
        spaltenBox.setSelectedItem(2);

        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sizePanel.setOpaque(false);
        sizePanel.add(new JLabel("Zeilen"));
        sizePanel.add(zeilenBox);
        sizePanel.add(new JLabel("Spalten"));
        sizePanel.add(spaltenBox);

        zeilenBox.addActionListener(e -> baueGitter());
        spaltenBox.addActionListener(e -> baueGitter());

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(label, BorderLayout.NORTH);
        top.add(sizePanel, BorderLayout.CENTER);

        host.setOpaque(false);
        add(top, BorderLayout.NORTH);
        add(host, BorderLayout.CENTER);
        baueGitter();
    }

    public Matrix lese()
    {
        double[][] values = new double[felder.length][felder[0].length];
        for (int z = 0; z < felder.length; z++)
        {
            for (int s = 0; s < felder[z].length; s++)
            {
                values[z][s] = ZahlenEingabe.lese(felder[z][s].getText());
            }
        }
        return new Matrix(values);
    }

    public void setze(Matrix matrix)
    {
        int max = GROESSEN[GROESSEN.length - 1];
        if (matrix.getZeilen() > max || matrix.getSpalten() > max)
        {
            throw new IllegalArgumentException("Matrix ist zu groß, höchstens " + max + "×" + max + " möglich.");
        }

        zeilenBox.setSelectedItem(matrix.getZeilen());
        spaltenBox.setSelectedItem(matrix.getSpalten());
        for (int z = 0; z < felder.length; z++)
        {
            for (int s = 0; s < felder[z].length; s++)
            {
                felder[z][s].setText(ZahlenAnzeige.formatiere(matrix.get(z, s)).replace(".", ""));
            }
        }
    }

    public void leeren()
    {
        for (JTextField[] zeile : felder)
        {
            for (JTextField feld : zeile)
            {
                feld.setText("0");
            }
        }
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        for (JComboBox<Integer> box : List.of(zeilenBox, spaltenBox))
        {
            box.setFont(AppFonts.normal(13));
            box.setBackground(theme.inputBackground());
            box.setForeground(theme.displayForeground());
            box.setFocusable(false);
        }
        for (JTextField[] zeile : felder)
        {
            for (JTextField feld : zeile)
            {
                feld.setFont(AppFonts.normal(15));
                ModernButtonStyler.styleInput(feld, theme);
                feld.setCaretColor(theme.displayForeground());
            }
        }
    }

    private void baueGitter()
    {
        int zeilen = (Integer) zeilenBox.getSelectedItem();
        int spalten = (Integer) spaltenBox.getSelectedItem();
        host.removeAll();
        JPanel grid = new JPanel(new GridLayout(zeilen, spalten, 6, 6));
        grid.setOpaque(false);

        felder = new JTextField[zeilen][spalten];
        for (int z = 0; z < zeilen; z++)
        {
            for (int s = 0; s < spalten; s++)
            {
                JTextField field = new JTextField("0");
                field.setName(prefix + (z + 1) + (s + 1));
                felder[z][s] = field;
                grid.add(field);
            }
        }

        host.add(grid, BorderLayout.NORTH);
        if (theme != null)
        {
            applyTheme(theme);
        }
        revalidate();
        repaint();
    }
}
