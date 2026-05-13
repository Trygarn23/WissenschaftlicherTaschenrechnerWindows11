package ui.history;

import common.history.VerlaufEintrag;
import ui.theme.AppTheme;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.util.function.Supplier;
import java.util.regex.Pattern;

final class HistoryEntryRenderer extends DefaultListCellRenderer
{
    private final EmptyBorder pad = new EmptyBorder(6, 8, 6, 8);
    private final Supplier<String> searchTextSupplier;
    private final Supplier<AppTheme> themeSupplier;
    private final String placeholder;

    HistoryEntryRenderer(Supplier<String> searchTextSupplier, Supplier<AppTheme> themeSupplier, String placeholder)
    {
        this.searchTextSupplier = searchTextSupplier;
        this.themeSupplier = themeSupplier;
        this.placeholder = placeholder;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        String text = value instanceof VerlaufEintrag eintrag ? eintrag.toDisplayText() : "";
        String query = normalizedQuery();

        if (!query.isEmpty())
        {
            label.setText(highlight(text, query));
        }
        else
        {
            label.setText(text);
        }

        label.setBorder(pad);
        label.setBackground(isSelected ? historySelectionBackground() : historyBackground());
        label.setForeground(historyForeground());
        label.setOpaque(true);
        return label;
    }

    private String normalizedQuery()
    {
        String query = searchTextSupplier.get();
        query = query == null ? "" : query.trim();
        return placeholder.equals(query) ? "" : query;
    }

    private String highlight(String text, String query)
    {
        String safeText = escapeHtml(text);
        String safeQuery = escapeHtml(query);

        String highlighted = safeText.replaceAll(
                "(?i)(" + Pattern.quote(safeQuery) + ")",
                "<span style='background:#ffea00; color:#000; padding:1px 2px; border-radius:3px;'>$1</span>"
        );

        return "<html><div style='white-space:nowrap;'>" + highlighted + "</div></html>";
    }

    private Color historyForeground()
    {
        AppTheme theme = themeSupplier.get();
        return theme != null ? theme.historyForeground() : Color.WHITE;
    }

    private Color historyBackground()
    {
        AppTheme theme = themeSupplier.get();
        return theme != null ? theme.historyBackground() : new Color(35, 35, 35);
    }

    private Color historySelectionBackground()
    {
        AppTheme theme = themeSupplier.get();
        return theme != null ? theme.historySelectionBackground() : new Color(70, 70, 70);
    }

    private static String escapeHtml(String text)
    {
        if (text == null)
        {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
