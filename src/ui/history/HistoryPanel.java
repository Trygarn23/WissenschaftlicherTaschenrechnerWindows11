package ui.history;

import common.history.VerlaufEintrag;
import common.history.VerlaufExport;
import common.history.VerlaufTextMapper;
import common.state.RechnerModus;
import ui.animation.AnimationSupport;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class HistoryPanel extends JPanel
{
    private static final String SEARCH_PLACEHOLDER = "Suche...";

    /** Eintrag in der Filterauswahl: alle, nur Favoriten oder ein bestimmter Modus. */
    private record FilterOption(String label, RechnerModus mode, boolean favoritesOnly)
    {
        @Override
        public String toString()
        {
            return label;
        }
    }

    private final DefaultListModel<VerlaufEintrag> allHistoryModel = new DefaultListModel<>();
    private final DefaultListModel<VerlaufEintrag> filteredHistoryModel = new DefaultListModel<>();

    private final JTextField historySearchField = new JTextField();
    private final JComboBox<FilterOption> filterBox = new JComboBox<>();
    private final JButton favoriteButton = new JButton(new StarIcon(false, 14));
    private final JButton moreButton = new JButton("Mehr");
    private final JPopupMenu moreMenu = new JPopupMenu();
    private final JMenuItem undoItem = new JMenuItem("Löschen rückgängig");
    private final JList<VerlaufEintrag> historyList = new JList<>(filteredHistoryModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);
    private final JLabel emptyLabel = new JLabel("Noch nix gerechnet. Mutig.");

    private final Predicate<String> deleteConfirmation;

    /** Stand vor dem letzten Löschen. Gilt nur, bis sich der Verlauf wieder anders ändert. */
    private List<VerlaufEintrag> undoSnapshot;

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

        setLayout(new BorderLayout(6, 6));
        setOpaque(false);
        setPreferredSize(new Dimension(220, 0));

        buildUi();
        setupSearchFiltering();
        setupInteractions();
    }

    private void buildUi()
    {
        historyList.setFont(AppFonts.normal(14));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFocusable(false);
        historyList.setFixedCellHeight(58);
        historyList.setCellRenderer(new HistoryEntryRenderer(
                () -> historySearchField.getText(),
                () -> currentTheme,
                SEARCH_PLACEHOLDER
        ));

        historyScroll.setBorder(null);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        configureSearchField();
        configureFilterBox();
        buildMoreMenu();

        favoriteButton.setFocusable(false);
        favoriteButton.setToolTipText("Favorit umschalten");
        favoriteButton.addActionListener(e -> toggleSelectedFavorite());

        moreButton.setFocusable(false);
        moreButton.setToolTipText("Löschen und Exportieren");
        moreButton.setComponentPopupMenu(moreMenu);
        moreButton.addActionListener(e -> moreMenu.show(moreButton, 0, moreButton.getHeight()));

        JPanel historyActions = new JPanel(new GridLayout(1, 2, 6, 0));
        historyActions.setOpaque(false);
        historyActions.add(favoriteButton);
        historyActions.add(moreButton);

        // Suchfeld bekommt eine eigene Zeile, neben den Buttons blieb nur Platz für „Such“.
        JPanel historyTop = new JPanel(new BorderLayout(6, 6));
        historyTop.setOpaque(false);
        historyTop.add(historySearchField, BorderLayout.NORTH);
        historyTop.add(filterBox, BorderLayout.CENTER);
        historyTop.add(historyActions, BorderLayout.SOUTH);

        add(historyTop, BorderLayout.NORTH);
        add(historyScroll, BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);
    }

    private void configureFilterBox()
    {
        filterBox.addItem(new FilterOption("Alle Einträge", null, false));
        filterBox.addItem(new FilterOption("Nur Favoriten", null, true));
        for (RechnerModus mode : RechnerModus.values())
        {
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
        addMenuItem("Als TXT exportieren…", e -> export("txt", VerlaufExport::alsText));
        addMenuItem("Als CSV exportieren…", e -> export("csv", VerlaufExport::alsCsv));
    }

    private void addMenuItem(String text, ActionListener action)
    {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        moreMenu.add(item);
    }

    private void configureSearchField()
    {
        historySearchField.setFont(AppFonts.normal(14));
        historySearchField.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        historySearchField.setOpaque(true);
        showPlaceholder();

        historySearchField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (SEARCH_PLACEHOLDER.equals(historySearchField.getText()))
                {
                    historySearchField.setText("");
                    historySearchField.setForeground(getHistoryForeground());
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (historySearchField.getText().isBlank())
                {
                    showPlaceholder();
                }
            }
        });
    }

    private void setupSearchFiltering()
    {
        historySearchField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                applyFilter();
            }
        });
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

    /** Wird aufgerufen, wenn Einträge gelöscht oder wiederhergestellt wurden. */
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

    public boolean hasSearchSelection()
    {
        return historySearchField.getSelectionStart() != historySearchField.getSelectionEnd();
    }

    public int getSearchCaretPosition()
    {
        return historySearchField.getCaretPosition();
    }

    public void clearSearch()
    {
        if (historySearchField.isFocusOwner())
        {
            historySearchField.setText("");
            historySearchField.setForeground(getHistoryForeground());
        }
        else
        {
            showPlaceholder();
        }
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
        setUndoSnapshot(null);
        allHistoryModel.clear();

        for (VerlaufEintrag entry : entries)
        {
            if (entry != null && !entry.toLegacyText().isBlank())
            {
                allHistoryModel.addElement(entry);
            }
        }

        applyFilter();
    }

    public List<String> getAllEntries()
    {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            result.add(allHistoryModel.getElementAt(i).toLegacyText());
        }
        return result;
    }

    public List<VerlaufEintrag> getAllStructuredEntries()
    {
        return modelEntries(allHistoryModel);
    }

    public void addEntry(String entry)
    {
        if (entry == null || entry.isBlank())
        {
            return;
        }

        addStructuredEntry(VerlaufTextMapper.ausLegacyText(entry, RechnerModus.STANDARD));
    }

    public void addStructuredEntry(VerlaufEintrag entry)
    {
        if (entry == null || entry.toLegacyText().isBlank())
        {
            return;
        }

        setUndoSnapshot(null);
        allHistoryModel.addElement(entry);
        applyFilter();
        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(historyList, currentTheme.softAccentBackground(), 180);
        }
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

        filterBox.setBackground(theme.toggleButtonBackground());
        filterBox.setForeground(theme.toggleButtonForeground());

        moreMenu.setBackground(theme.popupBackground());
        moreMenu.setBorder(BorderFactory.createLineBorder(theme.modeBorder(), 1));
        for (Component item : moreMenu.getComponents())
        {
            item.setBackground(theme.popupBackground());
            item.setForeground(theme.popupForeground());
        }

        ModernButtonStyler.styleInput(historySearchField, theme);
        historySearchField.setCaretColor(theme.historyForeground());
        emptyLabel.setForeground(theme.placeholderForeground());
        emptyLabel.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));

        if (SEARCH_PLACEHOLDER.equals(historySearchField.getText()))
        {
            historySearchField.setForeground(theme.placeholderForeground());
        }
        else
        {
            historySearchField.setForeground(theme.historyForeground());
        }

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
        List<VerlaufEintrag> visible = modelEntries(filteredHistoryModel);
        if (!visible.isEmpty() && deleteConfirmation.test(visible.size() + " angezeigte Einträge löschen?"))
        {
            removeEntries(visible);
        }
    }

    private void deleteAllEntries()
    {
        if (!allHistoryModel.isEmpty() && deleteConfirmation.test("Den kompletten Verlauf löschen?"))
        {
            removeEntries(getAllStructuredEntries());
        }
    }

    private void removeEntries(List<VerlaufEintrag> entries)
    {
        List<VerlaufEintrag> before = getAllStructuredEntries();
        for (VerlaufEintrag entry : entries)
        {
            allHistoryModel.removeElement(entry);
        }
        setUndoSnapshot(before);
        applyFilter();
        fireEntriesChanged();
    }

    private void undoDelete()
    {
        if (undoSnapshot == null)
        {
            return;
        }

        List<VerlaufEintrag> restored = undoSnapshot;
        setUndoSnapshot(null);
        allHistoryModel.clear();
        restored.forEach(allHistoryModel::addElement);
        applyFilter();
        fireEntriesChanged();
    }

    private void setUndoSnapshot(List<VerlaufEintrag> snapshot)
    {
        undoSnapshot = snapshot;
        undoItem.setEnabled(snapshot != null);
    }

    private void fireEntriesChanged()
    {
        if (entriesChangedListener != null)
        {
            entriesChangedListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "entriesChanged"));
        }
    }

    /** Exportiert die gerade angezeigten Einträge, also inklusive Suche und Filter. */
    private void export(String extension, Function<List<VerlaufEintrag>, String> format)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Verlauf exportieren");
        chooser.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + "-Datei", extension));
        chooser.setSelectedFile(new File("verlauf." + extension));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        Path file = chooser.getSelectedFile().toPath();
        if (!file.getFileName().toString().toLowerCase().endsWith("." + extension))
        {
            file = file.resolveSibling(file.getFileName() + "." + extension);
        }

        // BOM, damit Excel die Umlaute in der CSV richtig erkennt.
        String content = (extension.equals("csv") ? "﻿" : "") + format.apply(modelEntries(filteredHistoryModel));
        try
        {
            Files.writeString(file, content, StandardCharsets.UTF_8);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Datei konnte nicht gespeichert werden:\n" + ex.getMessage(),
                    "Export fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
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
        int modelIndex = allHistoryModel.indexOf(selected);
        if (selected == null || modelIndex < 0)
        {
            return;
        }

        VerlaufEintrag updated = selected.toggleFavorit();
        allHistoryModel.set(modelIndex, updated);
        applyFilter();
        selectUpdatedEntry(updated);

        if (favoriteChangedListener != null)
        {
            favoriteChangedListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "favoriteChanged"));
        }

        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(favoriteButton, currentTheme.successPulseColor(), 180);
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
                SEARCH_PLACEHOLDER,
                option == null ? null : option.mode(),
                option != null && option.favoritesOnly()
        );

        filteredHistoryModel.clear();
        visible.forEach(filteredHistoryModel::addElement);

        int last = filteredHistoryModel.size() - 1;
        emptyLabel.setVisible(allHistoryModel.isEmpty());
        if (last >= 0)
        {
            historyList.ensureIndexIsVisible(last);
        }
    }

    private static List<VerlaufEintrag> modelEntries(DefaultListModel<VerlaufEintrag> model)
    {
        List<VerlaufEintrag> result = new ArrayList<>();
        for (int i = 0; i < model.size(); i++)
        {
            result.add(model.getElementAt(i));
        }
        return result;
    }

    private void showPlaceholder()
    {
        historySearchField.setText(SEARCH_PLACEHOLDER);
        historySearchField.setForeground(getPlaceholderForeground());
    }

    private Color getHistoryForeground()
    {
        return currentTheme != null ? currentTheme.historyForeground() : Color.WHITE;
    }

    private Color getPlaceholderForeground()
    {
        return currentTheme != null ? currentTheme.placeholderForeground() : Color.GRAY;
    }

}
