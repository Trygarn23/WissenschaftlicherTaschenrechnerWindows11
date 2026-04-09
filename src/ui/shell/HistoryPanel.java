package ui.shell;

import Theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class HistoryPanel extends JPanel
{
    private static final String SEARCH_PLACEHOLDER = "Suche…";

    private final DefaultListModel<String> allHistoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> filteredHistoryModel = new DefaultListModel<>();

    private final JTextField historySearchField = new JTextField();
    private final JButton clearHistoryBtn = new JButton("Clear");
    private final JList<String> historyList = new JList<>(filteredHistoryModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);

    private ActionListener clearHistoryListener;
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
        historyList.setCellRenderer(new HistoryHighlightRenderer());

        historyScroll.setBorder(null);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        configureSearchField();

        clearHistoryBtn.setFocusable(false);
        clearHistoryBtn.addActionListener(e -> {
            clearEntries();
            if (clearHistoryListener != null)
            {
                clearHistoryListener.actionPerformed(e);
            }
        });

        JPanel historyTop = new JPanel(new BorderLayout(6, 6));
        historyTop.setOpaque(false);
        historyTop.add(historySearchField, BorderLayout.CENTER);
        historyTop.add(clearHistoryBtn, BorderLayout.EAST);

        add(historyTop, BorderLayout.NORTH);
        add(historyScroll, BorderLayout.CENTER);
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

                entryDoubleClickListener.accept(filteredHistoryModel.getElementAt(idx));
            }
        });
    }

    public void setClearHistoryListener(ActionListener listener)
    {
        this.clearHistoryListener = listener;
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
        allHistoryModel.clear();

        for (String entry : entries)
        {
            if (entry != null && !entry.isBlank())
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

        allHistoryModel.addElement(entry);
        applyFilter();
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

        clearHistoryBtn.setBackground(theme.specialButtonBackground());
        clearHistoryBtn.setForeground(theme.specialButtonForeground());
        clearHistoryBtn.setBorderPainted(false);
        clearHistoryBtn.setOpaque(true);
        clearHistoryBtn.setFont(theme.buttonFont());

        historySearchField.setBackground(theme.historySearchBackground());
        historySearchField.setCaretColor(theme.historyForeground());

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

    private boolean matchesFilter(String entry)
    {
        String q = historySearchField.getText();
        if (q == null)
        {
            return true;
        }

        q = q.trim();
        if (q.isEmpty() || SEARCH_PLACEHOLDER.equals(q))
        {
            return true;
        }

        return entry.toLowerCase().contains(q.toLowerCase());
    }

    private void applyFilter()
    {
        filteredHistoryModel.clear();

        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            String entry = allHistoryModel.getElementAt(i);
            if (matchesFilter(entry))
            {
                filteredHistoryModel.addElement(entry);
            }
        }

        int last = filteredHistoryModel.size() - 1;
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

    private Color getHistoryBackground()
    {
        return currentTheme != null ? currentTheme.historyBackground() : new Color(35, 35, 35);
    }

    private Color getHistorySelectionBackground()
    {
        return currentTheme != null ? currentTheme.historySelectionBackground() : new Color(70, 70, 70);
    }

    private class HistoryHighlightRenderer extends DefaultListCellRenderer
    {
        private final EmptyBorder pad = new EmptyBorder(6, 8, 6, 8);

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String text = value == null ? "" : value.toString();

            String q = historySearchField.getText();
            q = (q == null) ? "" : q.trim();
            boolean hasQuery = !q.isEmpty() && !SEARCH_PLACEHOLDER.equals(q);

            if (hasQuery)
            {
                String safeText = escapeHtml(text);
                String safeQuery = escapeHtml(q);

                String highlighted = safeText.replaceAll(
                        "(?i)(" + Pattern.quote(safeQuery) + ")",
                        "<span style='background:#ffea00; color:#000; padding:1px 2px; border-radius:3px;'>$1</span>"
                );

                lbl.setText("<html><div style='white-space:nowrap;'>" + highlighted + "</div></html>");
            }
            else
            {
                lbl.setText(text);
            }

            lbl.setBorder(pad);

            if (isSelected)
            {
                lbl.setBackground(getHistorySelectionBackground());
                lbl.setForeground(getHistoryForeground());
            }
            else
            {
                lbl.setBackground(getHistoryBackground());
                lbl.setForeground(getHistoryForeground());
            }

            lbl.setOpaque(true);
            return lbl;
        }
    }

    private String escapeHtml(String s)
    {
        if (s == null)
        {
            return "";
        }

        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}