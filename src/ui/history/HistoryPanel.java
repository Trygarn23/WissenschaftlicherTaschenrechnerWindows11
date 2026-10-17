package ui.history;

import common.history.VerlaufEintrag;
import common.history.VerlaufExport;
import common.history.VerlaufJson;
import common.history.VerlaufListe;
import common.history.VerlaufStatistik;
import common.history.VerlaufTextMapper;
import common.state.RechnerModus;
import ui.shell.ModeVisibilityPolicy;
import ui.animation.AnimationSupport;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HistoryPanel extends JPanel
{
    private static final int BREITE = 220;
    private static final int BREITE_EINGEKLAPPT = 44;
    private static final int ABSTAND = 6;
    private static final int ZEILEN_HOEHE = 58;
    private static final int PULS_DAUER_MS = 180;

    /** Eintrag in der Filterauswahl: alle, nur Favoriten oder ein bestimmter Modus. */
    private record FilterOption(String label, RechnerModus mode, boolean favoritesOnly)
    {
        @Override
        public String toString()
        {
            return label;
        }
    }

    private final VerlaufListe verlauf = new VerlaufListe();
    private final DefaultListModel<VerlaufEintrag> filteredHistoryModel = new DefaultListModel<>();

    private final HistorySearchField historySearchField = new HistorySearchField(this::applyFilter);
    private final JComboBox<FilterOption> filterBox = new JComboBox<>();
    private final JButton favoriteButton = new JButton(new StarIcon(false, StarIcon.BUTTON_GROESSE));
    private final JButton moreButton = new JButton("Mehr");
    private final JButton collapseButton = new JButton("›");
    private final JPopupMenu moreMenu = new JPopupMenu();
    private final JMenuItem undoItem = new JMenuItem("Löschen rückgängig");
    private final JCheckBoxMenuItem zusammenfassenItem = new JCheckBoxMenuItem("Gleiche Rechnungen zusammenfassen");
    private final JList<VerlaufEintrag> historyList = new JList<>(filteredHistoryModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);
    private final JLabel emptyLabel = new JLabel("Noch nix gerechnet. Mutig.");
    private final JPanel historyTop = new JPanel(new BorderLayout(ABSTAND, ABSTAND));

    private final Predicate<String> deleteConfirmation;

    private boolean eingeklappt;

    private ActionListener entriesChangedListener;
    private ActionListener favoriteChangedListener;
    private Consumer<String> entryDoubleClickListener;

    private AppTheme currentTheme;

    public HistoryPanel()
    {
        this(null);
    }

    /** @param deleteConfirmation fragt vor dem Löschen mehrerer Einträge nach; {@code null} = normaler Ja/Nein-Dialog */
    HistoryPanel(Predicate<String> deleteConfirmation)
    {
        this.deleteConfirmation = deleteConfirmation != null ? deleteConfirmation : this::askUser;

        setLayout(new BorderLayout(ABSTAND, ABSTAND));
        setOpaque(false);
        setPreferredSize(new Dimension(BREITE, 0));

        buildUi();
        setupInteractions();
    }

    private void buildUi()
    {
        historyList.setFont(AppFonts.normal(14));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFocusable(false);
        historyList.setFixedCellHeight(ZEILEN_HOEHE);
        historyList.setCellRenderer(new HistoryEntryRenderer(
                historySearchField::getText,
                () -> currentTheme,
                HistorySearchField.PLACEHOLDER
        ));

        historyScroll.setBorder(null);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        configureFilterBox();
        buildMoreMenu();

        favoriteButton.setFocusable(false);
        favoriteButton.setToolTipText("Favorit umschalten");
        favoriteButton.addActionListener(e -> toggleSelectedFavorite());

        moreButton.setFocusable(false);
        moreButton.setToolTipText("Löschen, Exportieren und Importieren");
        moreButton.setComponentPopupMenu(moreMenu);
        moreButton.addActionListener(e -> moreMenu.show(moreButton, 0, moreButton.getHeight()));

        collapseButton.setFocusable(false);
        collapseButton.setToolTipText("Verlauf einklappen");
        collapseButton.addActionListener(e -> setEingeklappt(!eingeklappt));

        JPanel historyActions = new JPanel(new GridLayout(1, 2, ABSTAND, 0));
        historyActions.setOpaque(false);
        historyActions.add(favoriteButton);
        historyActions.add(moreButton);

        JPanel searchRow = new JPanel(new BorderLayout(ABSTAND, 0));
        searchRow.setOpaque(false);
        searchRow.add(historySearchField, BorderLayout.CENTER);

        // Suchfeld bekommt eine eigene Zeile, neben den Buttons blieb nur Platz für „Such“.
        historyTop.setOpaque(false);
        historyTop.add(historySearchField, BorderLayout.NORTH);
        historyTop.add(filterBox, BorderLayout.CENTER);
        historyTop.add(historyActions, BorderLayout.SOUTH);

        JPanel header = new JPanel(new BorderLayout(ABSTAND, ABSTAND));
        header.setOpaque(false);
        header.add(collapseButton, BorderLayout.NORTH);
        header.add(historyTop, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(historyScroll, BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);
    }

    private void configureFilterBox()
    {
        filterBox.addItem(new FilterOption("Alle Einträge", null, false));
        filterBox.addItem(new FilterOption("Nur Favoriten", null, true));
        for (RechnerModus mode : RechnerModus.values())
        {
            // Nur Modi, die überhaupt in den Verlauf schreiben – sonst wird die Liste lang und bleibt leer.
            if (!ModeVisibilityPolicy.sollHistoryAnzeigen(mode)) continue;
            filterBox.addItem(new FilterOption("Nur " + mode.getLabel(), mode, false));
        }
        filterBox.setFont(AppFonts.normal(13));
        filterBox.setFocusable(false);
        filterBox.setToolTipText("Verlauf filtern");
        filterBox.addActionListener(e -> applyFilter());
    }

    private void buildMoreMenu()
    {
        addMenuItem("Ausgewählten Eintrag löschen", e -> deleteSelectedEntry());
        addMenuItem("Angezeigte Einträge löschen…", e -> deleteVisibleEntries());
        addMenuItem("Alle Einträge löschen…", e -> deleteAllEntries());
        undoItem.setEnabled(false);
        undoItem.addActionListener(e -> undoDelete());
        moreMenu.add(undoItem);
        moreMenu.addSeparator();
        zusammenfassenItem.setToolTipText("Zeigt jede Rechnung nur einmal, und zwar die neueste");
        zusammenfassenItem.addActionListener(e -> applyFilter());
        moreMenu.add(zusammenfassenItem);
        moreMenu.addSeparator();
        // Exportiert werden immer die gerade angezeigten Einträge, also inklusive Suche und Filter.
        addMenuItem("Als TXT exportieren…", e -> HistoryDateiDialoge.speichere(this, "txt", VerlaufExport.alsText(visibleEntries())));
        // BOM, damit Excel die Umlaute in der CSV richtig erkennt.
        addMenuItem("Als CSV exportieren…", e -> HistoryDateiDialoge.speichere(this, "csv", "﻿" + VerlaufExport.alsCsv(visibleEntries())));
        addMenuItem("Als JSON exportieren…", e -> HistoryDateiDialoge.speichere(this, "json", VerlaufJson.schreibe(visibleEntries())));
        addMenuItem("JSON importieren…", e -> HistoryDateiDialoge.ladeJson(this).ifPresent(this::importEntries));
        moreMenu.addSeparator();
        addMenuItem("Statistik…", e -> JOptionPane.showMessageDialog(this,
                VerlaufStatistik.aus(getAllStructuredEntries()).alsText(), "Statistik", JOptionPane.INFORMATION_MESSAGE));
    }

    private void addMenuItem(String text, ActionListener action)
    {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        moreMenu.add(item);
    }

    private void setupInteractions()
    {
        historyList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() != 2 || entryDoubleClickListener == null)
                {
                    return;
                }

                int idx = historyList.locationToIndex(e.getPoint());
                if (idx < 0)
                {
                    return;
                }

                Rectangle r = historyList.getCellBounds(idx, idx);
                if (r == null || !r.contains(e.getPoint()))
                {
                    return;
                }

                entryDoubleClickListener.accept(filteredHistoryModel.getElementAt(idx).toLegacyText());
            }
        });
    }

    /** Wird aufgerufen, wenn Einträge gelöscht, importiert oder wiederhergestellt wurden. */
    public void setEntriesChangedListener(ActionListener listener)
    {
        this.entriesChangedListener = listener;
    }

    public void setFavoriteChangedListener(ActionListener listener)
    {
        this.favoriteChangedListener = listener;
    }

    public void setEntryDoubleClickListener(Consumer<String> listener)
    {
        this.entryDoubleClickListener = listener;
    }

    public void addSearchFieldKeyListener(KeyListener listener)
    {
        historySearchField.addKeyListener(listener);
    }

    public boolean isSearchFocusOwner()
    {
        return historySearchField.isFocusOwner();
    }

    public boolean isEingeklappt()
    {
        return eingeklappt;
    }

    /** Eingeklappt bleibt nur ein schmaler Streifen mit dem Aufklapp-Knopf, dann haben die Tasten mehr Platz. */
    public void setEingeklappt(boolean eingeklappt)
    {
        this.eingeklappt = eingeklappt;
        historyTop.setVisible(!eingeklappt);
        historyScroll.setVisible(!eingeklappt);
        emptyLabel.setVisible(!eingeklappt && verlauf.istLeer());
        collapseButton.setText(eingeklappt ? "‹" : "›");
        collapseButton.setToolTipText(eingeklappt ? "Verlauf ausklappen" : "Verlauf einklappen");
        setPreferredSize(new Dimension(eingeklappt ? BREITE_EINGEKLAPPT : BREITE, 0));
        revalidate();
        repaint();
    }

    public void setAllEntries(List<String> entries)
    {
        List<VerlaufEintrag> strukturierteEintraege = new ArrayList<>();
        for (String entry : entries)
        {
            if (entry != null && !entry.isBlank())
            {
                strukturierteEintraege.add(VerlaufTextMapper.ausLegacyText(entry, RechnerModus.STANDARD));
            }
        }

        setAllStructuredEntries(strukturierteEintraege);
    }

    public void setAllStructuredEntries(List<VerlaufEintrag> entries)
    {
        verlauf.ersetzeAlle(entries);
        refreshEntries();
    }

    public List<String> getAllEntries()
    {
        return verlauf.alle().stream().map(VerlaufEintrag::toLegacyText).toList();
    }

    public List<VerlaufEintrag> getAllStructuredEntries()
    {
        return verlauf.alle();
    }

    public void addStructuredEntry(VerlaufEintrag entry)
    {
        if (!verlauf.fuegeHinzu(entry))
        {
            return;
        }

        refreshEntries();
        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(historyList, currentTheme.softAccentBackground(), PULS_DAUER_MS);
        }
    }

    /** Fügt importierte Einträge hinzu; was schon genauso im Verlauf steht, kommt nicht doppelt rein. */
    void importEntries(List<VerlaufEintrag> imported)
    {
        verlauf.importiere(imported);
        refreshEntries();
        fireEntriesChanged();
    }

    public void applyTheme(AppTheme theme)
    {
        this.currentTheme = theme;

        historyList.setBackground(theme.historyBackground());
        historyList.setForeground(theme.historyForeground());
        historyList.setSelectionBackground(theme.historySelectionBackground());
        historyList.setSelectionForeground(theme.historyForeground());

        historyScroll.getViewport().setBackground(theme.historyBackground());

        ModernButtonStyler.styleButton(favoriteButton, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        ModernButtonStyler.styleButton(moreButton, theme, theme.specialButtonBackground(), theme.specialButtonForeground());
        ModernButtonStyler.styleButton(collapseButton, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());

        filterBox.setBackground(theme.toggleButtonBackground());
        filterBox.setForeground(theme.toggleButtonForeground());

        moreMenu.setBackground(theme.popupBackground());
        moreMenu.setBorder(BorderFactory.createLineBorder(theme.modeBorder(), 1));
        for (Component item : moreMenu.getComponents())
        {
            item.setBackground(theme.popupBackground());
            item.setForeground(theme.popupForeground());
        }

        historySearchField.applyTheme(theme);
        emptyLabel.setForeground(theme.placeholderForeground());
        emptyLabel.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));

        repaint();
    }

    private void deleteSelectedEntry()
    {
        VerlaufEintrag selected = historyList.getSelectedValue();
        if (selected != null)
        {
            removeEntries(List.of(selected));
        }
    }

    private void deleteVisibleEntries()
    {
        List<VerlaufEintrag> visible = visibleEntries();
        if (!visible.isEmpty() && deleteConfirmation.test(visible.size() + " angezeigte Einträge löschen?"))
        {
            removeEntries(visible);
        }
    }

    private void deleteAllEntries()
    {
        if (!verlauf.istLeer() && deleteConfirmation.test("Den kompletten Verlauf löschen?"))
        {
            removeEntries(getAllStructuredEntries());
        }
    }

    private void removeEntries(List<VerlaufEintrag> entries)
    {
        verlauf.entferne(entries);
        refreshEntries();
        fireEntriesChanged();
    }

    private void undoDelete()
    {
        if (verlauf.macheLoeschenRueckgaengig())
        {
            refreshEntries();
            fireEntriesChanged();
        }
    }

    /** Nach jeder Änderung am Verlauf: neu filtern und Rückgängig passend freischalten. */
    private void refreshEntries()
    {
        undoItem.setEnabled(verlauf.kannRueckgaengig());
        applyFilter();
    }

    private void fireEntriesChanged()
    {
        if (entriesChangedListener != null)
        {
            entriesChangedListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "entriesChanged"));
        }
    }

    private boolean askUser(String question)
    {
        return JOptionPane.showConfirmDialog(this, question, "Verlauf löschen",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void toggleSelectedFavorite()
    {
        VerlaufEintrag selected = historyList.getSelectedValue();
        VerlaufEintrag updated = selected == null ? null : verlauf.schalteFavorit(selected).orElse(null);
        if (updated == null)
        {
            return;
        }

        applyFilter();
        selectUpdatedEntry(updated);

        if (favoriteChangedListener != null)
        {
            favoriteChangedListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "favoriteChanged"));
        }

        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(favoriteButton, currentTheme.successPulseColor(), PULS_DAUER_MS);
        }
    }

    private void selectUpdatedEntry(VerlaufEintrag updated)
    {
        int index = filteredHistoryModel.indexOf(updated);
        if (index >= 0)
        {
            historyList.setSelectedIndex(index);
            historyList.ensureIndexIsVisible(index);
        }
    }

    private void applyFilter()
    {
        FilterOption option = (FilterOption) filterBox.getSelectedItem();
        List<VerlaufEintrag> visible = HistoryFilter.filter(
                getAllStructuredEntries(),
                historySearchField.getText(),
                HistorySearchField.PLACEHOLDER,
                option == null ? null : option.mode(),
                option != null && option.favoritesOnly()
        );
        if (zusammenfassenItem.isSelected())
        {
            visible = HistoryFilter.zusammenfassen(visible);
        }

        filteredHistoryModel.clear();
        visible.forEach(filteredHistoryModel::addElement);

        int last = filteredHistoryModel.size() - 1;
        emptyLabel.setVisible(!eingeklappt && verlauf.istLeer());
        if (last >= 0)
        {
            historyList.ensureIndexIsVisible(last);
        }
    }

    private List<VerlaufEintrag> visibleEntries()
    {
        List<VerlaufEintrag> result = new ArrayList<>();
        for (int i = 0; i < filteredHistoryModel.size(); i++)
        {
            result.add(filteredHistoryModel.getElementAt(i));
        }
        return result;
    }
}
