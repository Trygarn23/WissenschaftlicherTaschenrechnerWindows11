package ui.history;

import common.history.VerlaufEintrag;
import common.history.VerlaufTextMapper;
import common.state.RechnerModus;
import ui.animation.AnimationSupport;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HistoryPanel extends JPanel
{
    private static final String SEARCH_PLACEHOLDER = "Suche...";
    private final DefaultListModel<VerlaufEintrag> allHistoryModel = new DefaultListModel<>();
    private final DefaultListModel<VerlaufEintrag> filteredHistoryModel = new DefaultListModel<>();

    private final JTextField historySearchField = new JTextField();
    private final JButton favoriteButton = new JButton("\u2606");
    private final JButton clearHistoryBtn = new JButton("Leeren");
    private final JList<VerlaufEintrag> historyList = new JList<>(filteredHistoryModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);
    private final JLabel emptyLabel = new JLabel("Noch nix gerechnet. Mutig.");

    private ActionListener clearHistoryListener;
    private ActionListener favoriteChangedListener;
    private Consumer<String> entryDoubleClickListener;

    private AppTheme currentTheme;

    public HistoryPanel()
    {
        setLayout(new BorderLayout(6, 6));
        setOpaque(false);
        setPreferredSize(new Dimension(220, 0));

        buildUi();
        setupSearchFiltering();
        setupInteractions();
    }

    private void buildUi()
    {
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

        clearHistoryBtn.setFocusable(false);
        clearHistoryBtn.addActionListener(e -> {
            clearEntries();
            if (clearHistoryListener != null)
            {
                clearHistoryListener.actionPerformed(e);
            }
        });

        favoriteButton.setFocusable(false);
        favoriteButton.setToolTipText("Favorit umschalten");
        favoriteButton.addActionListener(e -> toggleSelectedFavorite());

        JPanel historyActions = new JPanel(new GridLayout(1, 2, 6, 0));
        historyActions.setOpaque(false);
        historyActions.add(favoriteButton);
        historyActions.add(clearHistoryBtn);

        JPanel historyTop = new JPanel(new BorderLayout(6, 6));
        historyTop.setOpaque(false);
        historyTop.add(historySearchField, BorderLayout.CENTER);
        historyTop.add(historyActions, BorderLayout.EAST);

        add(historyTop, BorderLayout.NORTH);
        add(historyScroll, BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);
    }

    private void configureSearchField()
    {
        historySearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

    public void setClearHistoryListener(ActionListener listener)
    {
        this.clearHistoryListener = listener;
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
        List<VerlaufEintrag> result = new ArrayList<>();
        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            result.add(allHistoryModel.getElementAt(i));
        }
        return result;
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

        allHistoryModel.addElement(entry);
        applyFilter();
        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(historyList, currentTheme.softAccentBackground(), 180);
        }
    }

    public void clearEntries()
    {
        allHistoryModel.clear();
        filteredHistoryModel.clear();
        showPlaceholder();
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
        ModernButtonStyler.styleButton(clearHistoryBtn, theme, theme.specialButtonBackground(), theme.specialButtonForeground());

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

    void setSearchTextForTest(String text)
    {
        historySearchField.setForeground(getHistoryForeground());
        historySearchField.setText(text == null ? "" : text);
    }

    int getVisibleEntryCountForTest()
    {
        return filteredHistoryModel.size();
    }

    String getVisibleEntryTextForTest(int index)
    {
        return rendererText(filteredHistoryModel.getElementAt(index));
    }

    void selectVisibleEntryForTest(int index)
    {
        historyList.setSelectedIndex(index);
    }

    void toggleSelectedFavoriteForTest()
    {
        toggleSelectedFavorite();
    }

    private void toggleSelectedFavorite()
    {
        int selectedIndex = historyList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= filteredHistoryModel.size())
        {
            return;
        }

        VerlaufEintrag selected = filteredHistoryModel.getElementAt(selectedIndex);
        int modelIndex = findEntryIndex(selected);
        if (modelIndex < 0)
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

    private int findEntryIndex(VerlaufEintrag selected)
    {
        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            if (allHistoryModel.getElementAt(i).equals(selected))
            {
                return i;
            }
        }
        return -1;
    }

    private void selectUpdatedEntry(VerlaufEintrag updated)
    {
        for (int i = 0; i < filteredHistoryModel.size(); i++)
        {
            if (filteredHistoryModel.getElementAt(i).equals(updated))
            {
                historyList.setSelectedIndex(i);
                historyList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    private String rendererText(VerlaufEintrag entry)
    {
        return (entry.isFavorit() ? "\u2605 " : "\u2606 ") + entry.toDisplayText();
    }

    private void applyFilter()
    {
        filteredHistoryModel.clear();

        List<VerlaufEintrag> entries = new ArrayList<>();
        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            entries.add(allHistoryModel.getElementAt(i));
        }

        for (VerlaufEintrag entry : HistoryFilter.filter(entries, historySearchField.getText(), SEARCH_PLACEHOLDER))
        {
            filteredHistoryModel.addElement(entry);
        }

        int last = filteredHistoryModel.size() - 1;
        emptyLabel.setVisible(allHistoryModel.isEmpty());
        if (last >= 0)
        {
            historyList.ensureIndexIsVisible(last);
        }
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
