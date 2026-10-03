package ui.history;

import common.history.VerlaufEintrag;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

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
    private static final AppTheme FALLBACK_THEME = new DarkTheme();

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

        String text = value instanceof VerlaufEintrag eintrag ? favoritePrefix(eintrag) + eintrag.toDisplayText() : "";
        String query = normalizedQuery();

        if (!query.isEmpty())
        {
            label.setText(highlight(text, query));
        }
        else
        {
            label.setText(text);
        }

        AppTheme theme = theme();
        label.setBorder(pad);
        label.setBackground(isSelected ? theme.historySelectionBackground() : rowBackground(theme, index));
        label.setForeground(theme.historyForeground());
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

    /** Solange noch kein Theme gesetzt ist, gelten die Farben des Standard-Themes statt eigener Magic Numbers. */
    private AppTheme theme()
    {
        AppTheme theme = themeSupplier.get();
        return theme != null ? theme : FALLBACK_THEME;
    }

    private Color rowBackground(AppTheme theme, int index)
    {
        return index % 2 == 0 ? theme.cardBackground() : theme.historyBackground();
    }

    private String favoritePrefix(VerlaufEintrag eintrag)
    {
        return eintrag.isFavorit() ? "\u2605 " : "\u2606 ";
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
