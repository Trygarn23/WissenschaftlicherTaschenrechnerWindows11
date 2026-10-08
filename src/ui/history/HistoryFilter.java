package ui.history;

import common.history.VerlaufEintrag;
import common.state.RechnerModus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

final class HistoryFilter
{
    private HistoryFilter()
    {
    }

    static List<VerlaufEintrag> filter(List<VerlaufEintrag> entries, String searchText, String placeholder)
    {
        return filter(entries, searchText, placeholder, null, false);
    }

    /** {@code mode == null} heißt: alle Modi. */
    static List<VerlaufEintrag> filter(List<VerlaufEintrag> entries, String searchText, String placeholder,
                                       RechnerModus mode, boolean favoritesOnly)
    {
        String query = searchText == null ? "" : searchText.trim();
        if (query.equals(placeholder))
        {
            query = "";
        }

        List<VerlaufEintrag> result = new ArrayList<>();
        for (VerlaufEintrag entry : entries)
        {
            if (entry != null
                    && entry.matchesSuchtext(query)
                    && (mode == null || entry.getModus() == mode)
                    && (!favoritesOnly || entry.isFavorit()))
            {
                result.add(entry);
            }
        }
        return result;
    }

    /** Gleiche Rechnung (Ausdruck und Ergebnis) nur einmal, und zwar an der Stelle ihres neuesten Auftretens. */
    static List<VerlaufEintrag> zusammenfassen(List<VerlaufEintrag> entries)
    {
        Map<String, VerlaufEintrag> neuester = new LinkedHashMap<>();
        for (VerlaufEintrag entry : entries)
        {
            String schluessel = entry.toLegacyText();
            neuester.remove(schluessel);
            neuester.put(schluessel, entry);
        }
        return new ArrayList<>(neuester.values());
    }
}
