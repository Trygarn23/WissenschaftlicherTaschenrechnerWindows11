package ui.units;

import common.units.Einheit;
import common.units.EinheitKategorie;
import common.units.EinheitenService;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class EinheitenSidePanel extends JPanel
{
    private final EinheitenService service = new EinheitenService();
    private final EinheitenFormatter formatter = new EinheitenFormatter();

    private final JLabel titleLabel = new JLabel("Einheiten");
    private final JButton closeButton = new JButton("x");
    private final JComboBox<EinheitKategorie> categoryBox = new JComboBox<>();
    private final JComboBox<Einheit> fromBox = new JComboBox<>();
    private final JComboBox<Einheit> toBox = new JComboBox<>();
    private final JTextField valueField = new JTextField("1");
    private final JButton swapButton = new JButton("Tauschen");
    private final JLabel resultLabel = new JLabel("1");
    private final JLabel detailLabel = new JLabel("Meter (m) -> Meter (m)");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final List<JLabel> fieldLabels = new ArrayList<>();

    private AppTheme theme = new DarkTheme();
    private Runnable closeListener;
    private boolean updatingUnits;

    public EinheitenSidePanel()
    {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setOpaque(true);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        fuelleKategorien();
        wireEvents();
        aktualisiereEinheiten();
        rechneLive();
    }

    public void setCloseListener(Runnable closeListener)
    {
        this.closeListener = closeListener;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.panelBackground());
        titleLabel.setForeground(theme.displayForeground());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultLabel.setForeground(theme.displayForeground());
        resultLabel.setBackground(theme.displayBackground());
        resultLabel.setOpaque(true);
        resultLabel.setFont(theme.displayFont().deriveFont(28f));
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setForeground(theme.secondaryDisplayForeground());
        for (JLabel label : fieldLabels)
        {
            label.setForeground(theme.secondaryDisplayForeground());
        }

        styleCombo(categoryBox);
        styleCombo(fromBox);
        styleCombo(toBox);
        styleField(valueField);
        styleButton(swapButton);
        styleButton(closeButton);
    }

    private JPanel buildHeader()
    {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        closeButton.setToolTipText("Einheiten schliessen");
        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody()
    {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);

        body.add(label("Kategorie"), gbc);
        gbc.gridy++;
        body.add(categoryBox, gbc);
        gbc.gridy++;
        body.add(label("Wert"), gbc);
        gbc.gridy++;
        body.add(valueField, gbc);
        gbc.gridy++;
        body.add(label("Von"), gbc);
        gbc.gridy++;
        body.add(fromBox, gbc);
        gbc.gridy++;
        body.add(label("Nach"), gbc);
        gbc.gridy++;
        body.add(toBox, gbc);
        gbc.gridy++;
        body.add(swapButton, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 4, 0);
        body.add(resultLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(detailLabel, gbc);
        gbc.gridy++;
        gbc.weighty = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        body.add(spacer, gbc);
        return body;
    }

    private JLabel label(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(theme.secondaryDisplayForeground());
        fieldLabels.add(label);
        return label;
    }

    private void fuelleKategorien()
    {
        for (EinheitKategorie kategorie : service.kategorien())
        {
            categoryBox.addItem(kategorie);
        }
    }

    private void wireEvents()
    {
        closeButton.addActionListener(e -> {
            if (closeListener != null)
            {
                closeListener.run();
            }
        });

        categoryBox.addActionListener(e -> aktualisiereEinheiten());
        fromBox.addActionListener(e -> rechneLive());
        toBox.addActionListener(e -> rechneLive());
        swapButton.addActionListener(e -> tauscheEinheiten());
        valueField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                rechneLive();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                rechneLive();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                rechneLive();
            }
        });
    }

    private void aktualisiereEinheiten()
    {
        EinheitKategorie kategorie = (EinheitKategorie) categoryBox.getSelectedItem();
        List<Einheit> einheiten = service.einheitenFuer(kategorie);

        updatingUnits = true;
        fromBox.removeAllItems();
        toBox.removeAllItems();
        for (Einheit einheit : einheiten)
        {
            fromBox.addItem(einheit);
            toBox.addItem(einheit);
        }

        if (toBox.getItemCount() > 1)
        {
            toBox.setSelectedIndex(1);
        }
        updatingUnits = false;
        rechneLive();
    }

    private void tauscheEinheiten()
    {
        Object von = fromBox.getSelectedItem();
        fromBox.setSelectedItem(toBox.getSelectedItem());
        toBox.setSelectedItem(von);
        rechneLive();
    }

    private void rechneLive()
    {
        if (updatingUnits)
        {
            return;
        }

        try
        {
            Einheit von = (Einheit) fromBox.getSelectedItem();
            Einheit nach = (Einheit) toBox.getSelectedItem();
            double wert = parseZahl(valueField.getText());
            double ergebnis = service.rechneUm(wert, von, nach);

            resultLabel.setText(formatter.formatiere(ergebnis) + " " + nach.symbol());
            detailLabel.setText(von + " -> " + nach);
            statusLabel.setText("Live umgerechnet");
        }
        catch (Exception e)
        {
            resultLabel.setText("Fehler");
            detailLabel.setText("Keine gueltige Umrechnung");
            statusLabel.setText(e.getMessage() == null ? "Ungueltiger Wert" : e.getMessage());
        }
    }

    private double parseZahl(String text)
    {
        return Double.parseDouble(text.trim().replace(',', '.'));
    }

    private void styleCombo(JComboBox<?> comboBox)
    {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(theme.historySearchBackground());
        comboBox.setForeground(theme.displayForeground());
        comboBox.setFocusable(false);
    }

    private void styleField(JTextField field)
    {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(theme.historySearchBackground());
        field.setForeground(theme.displayForeground());
        field.setCaretColor(theme.displayForeground());
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void styleButton(JButton button)
    {
        button.setFont(theme.buttonFont());
        button.setBackground(theme.toggleButtonBackground());
        button.setForeground(theme.toggleButtonForeground());
        button.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
