package ui.units;

import common.formatting.ZahlenAnzeige;
import common.formatting.ZahlenEingabe;
import common.persistence.AppDateien;
import common.units.Einheit;
import common.units.EinheitKategorie;
import common.units.EinheitenFavoriten;
import common.units.EinheitenService;
import ui.history.StarIcon;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EinheitenSidePanel extends JPanel
{
    private static final String KEIN_FAVORIT = "Favorit wählen…";

    private final EinheitenService service = new EinheitenService();
    private final EinheitenFavoriten favoriten;

    private final JLabel titleLabel = new JLabel("Einheiten");
    private final JButton closeButton = new JButton("x");
    private final JComboBox<Object> favoritesBox = new JComboBox<>();
    private final JButton favoriteButton = new JButton("Favorit");
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
        this(new EinheitenFavoriten(AppDateien.einheitenFavoriten()));
    }

    public EinheitenSidePanel(EinheitenFavoriten favoriten)
    {
        this.favoriten = favoriten;
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setOpaque(true);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        fuelleKategorien();
        fuelleFavoriten();
        wireEvents();
        aktualisiereEinheiten();
        rechneLive();
    }

    public void setCloseListener(Runnable closeListener)
    {
        this.closeListener = closeListener;
    }

    /** Übernimmt z. B. ein Rechenergebnis als Eingabewert. */
    public void setWert(double wert)
    {
        valueField.setText(BigDecimal.valueOf(wert).stripTrailingZeros().toPlainString().replace('.', ','));
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.panelBackground());
        titleLabel.setForeground(theme.displayForeground());
        titleLabel.setFont(AppFonts.fett(20));
        resultLabel.setForeground(theme.displayForeground());
        resultLabel.setBackground(theme.displayBackground());
        resultLabel.setOpaque(true);
        resultLabel.setFont(theme.displayFont().deriveFont(28f));
        resultLabel.setBorder(ModernButtonStyler.cardBorder(theme));
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setForeground(theme.secondaryDisplayForeground());
        for (JLabel label : fieldLabels)
        {
            label.setForeground(theme.secondaryDisplayForeground());
        }

        styleCombo(favoritesBox);
        styleCombo(categoryBox);
        styleCombo(fromBox);
        styleCombo(toBox);
        styleField(valueField);
        styleButton(swapButton);
        styleButton(favoriteButton);
        styleButton(closeButton);
    }

    private JPanel buildHeader()
    {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        closeButton.setToolTipText("Einheiten schließen");
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

        body.add(label("Favoriten"), gbc);
        gbc.gridy++;
        body.add(favoritesBox, gbc);
        gbc.gridy++;
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
        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setOpaque(false);
        buttons.add(swapButton);
        buttons.add(favoriteButton);
        body.add(buttons, gbc);
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
        favoriteButton.setToolTipText("Diese Umrechnung als Favorit merken");
        favoriteButton.addActionListener(e -> {
            aktuelleUmrechnung().ifPresent(favoriten::umschalten);
            fuelleFavoriten();
            aktualisiereFavoritStern();
        });
        favoritesBox.addActionListener(e -> {
            if (favoritesBox.getSelectedItem() instanceof EinheitenFavoriten.Umrechnung umrechnung)
            {
                waehle(umrechnung);
            }
        });
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

    private void fuelleFavoriten()
    {
        favoritesBox.removeAllItems();
        favoritesBox.addItem(KEIN_FAVORIT);
        favoriten.alle().forEach(favoritesBox::addItem);
    }

    private Optional<EinheitenFavoriten.Umrechnung> aktuelleUmrechnung()
    {
        if (!(fromBox.getSelectedItem() instanceof Einheit von) || !(toBox.getSelectedItem() instanceof Einheit nach))
        {
            return Optional.empty();
        }
        return Optional.of(new EinheitenFavoriten.Umrechnung(von.kategorie(), von.name(), nach.name()));
    }

    private void waehle(EinheitenFavoriten.Umrechnung umrechnung)
    {
        categoryBox.setSelectedItem(umrechnung.kategorie());
        for (Einheit einheit : service.einheitenFuer(umrechnung.kategorie()))
        {
            if (einheit.name().equals(umrechnung.von()))
            {
                fromBox.setSelectedItem(einheit);
            }
            if (einheit.name().equals(umrechnung.nach()))
            {
                toBox.setSelectedItem(einheit);
            }
        }
        favoritesBox.setSelectedIndex(0);
    }

    private void aktualisiereFavoritStern()
    {
        boolean favorit = aktuelleUmrechnung().map(favoriten::istFavorit).orElse(false);
        favoriteButton.setIcon(new StarIcon(favorit, StarIcon.BUTTON_GROESSE));
        favoriteButton.setText(favorit ? "Gemerkt" : "Favorit");
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

        aktualisiereFavoritStern();
        try
        {
            Einheit von = (Einheit) fromBox.getSelectedItem();
            Einheit nach = (Einheit) toBox.getSelectedItem();
            double wert = parseZahl(valueField.getText());
            double ergebnis = service.rechneUm(wert, von, nach);

            resultLabel.setText(ZahlenAnzeige.formatiere(ergebnis) + " " + nach.symbol());
            detailLabel.setText(von + " -> " + nach);
            statusLabel.setText("Live umgerechnet");
        }
        catch (IllegalArgumentException e)
        {
            resultLabel.setText("Fehler");
            detailLabel.setText("Keine gültige Umrechnung");
            statusLabel.setText(e.getMessage() == null ? "Ungültiger Wert" : e.getMessage());
        }
    }

    private double parseZahl(String text)
    {
        return ZahlenEingabe.lese(text);
    }

    private void styleCombo(JComboBox<?> comboBox)
    {
        comboBox.setFont(AppFonts.normal(13));
        comboBox.setBackground(theme.inputBackground());
        comboBox.setForeground(theme.displayForeground());
        comboBox.setFocusable(false);
    }

    private void styleField(JTextField field)
    {
        field.setFont(AppFonts.normal(16));
        ModernButtonStyler.styleInput(field, theme);
        field.setCaretColor(theme.displayForeground());
    }

    private void styleButton(JButton button)
    {
        ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
    }
}
