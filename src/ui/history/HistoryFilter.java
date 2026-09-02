package ui.history;

import common.history.VerlaufEintrag;

import java.util.ArrayList;
import java.util.List;

final class HistoryFilter
{
    private HistoryFilter()
    {
    }

    static List<VerlaufEintrag> filter(List<VerlaufEintrag> entries, String searchText, String placeholder)
    {
        String query = searchText == null ? "" : searchText.trim();
        if (query.isEmpty() || query.equals(placeholder))
        {
            return List.copyOf(entries);
        }

        List<VerlaufEintrag> result = new ArrayList<>();
        for (VerlaufEintrag entry : entries)
        {
            if (entry != null && entry.matchesSuchtext(query))
            {
                result.add(entry);
            }
        }
        return result;
    }
}
