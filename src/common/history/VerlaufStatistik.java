package common.history;

import common.state.RechnerModus;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Kleine Auswertung des Verlaufs: Lieblingsfunktion, Anzahl Rechnungen, meistgenutzter Modus. */
public record VerlaufStatistik(int anzahlRechnungen, Optional<String> lieblingsFunktion, int funktionsAnzahl,
                               Optional<RechnerModus> lieblingsModus)
{
    /** Ein Name direkt vor „(“ ist ein Funktionsaufruf, „√“ zählt als sqrt. */
    private static final Pattern FUNKTION = Pattern.compile("([a-zA-Z]+)\\s*\\(|√");

    public static VerlaufStatistik aus(List<VerlaufEintrag> eintraege)
    {
        Map<String, Integer> funktionen = new LinkedHashMap<>();
        Map<RechnerModus, Integer> modi = new LinkedHashMap<>();
        for (VerlaufEintrag eintrag : eintraege)
        {
            modi.merge(eintrag.getModus(), 1, Integer::sum);
            Matcher matcher = FUNKTION.matcher(eintrag.getAusdruck());
            while (matcher.find())
            {
                String name = matcher.group(1) == null ? "sqrt" : matcher.group(1).toLowerCase(Locale.ROOT);
                funktionen.merge(name, 1, Integer::sum);
            }
        }

        // Bei Gleichstand gewinnt, was zuerst im Verlauf vorkam.
        Optional<Map.Entry<String, Integer>> top = funktionen.entrySet().stream().max(Map.Entry.comparingByValue());
        Optional<RechnerModus> modus = modi.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey);
        return new VerlaufStatistik(eintraege.size(), top.map(Map.Entry::getKey), top.map(Map.Entry::getValue).orElse(0), modus);
    }

    public String alsText()
    {
        return "Rechnungen im Verlauf: " + anzahlRechnungen + "\n"
                + lieblingsFunktion.map(f -> "Deine Lieblingsfunktion: " + f + " (" + funktionsAnzahl + "×)")
                .orElse("Noch keine Funktion benutzt") + "\n"
                + lieblingsModus.map(m -> "Meistgenutzter Modus: " + m.getLabel()).orElse("Noch kein Modus benutzt");
    }
}
