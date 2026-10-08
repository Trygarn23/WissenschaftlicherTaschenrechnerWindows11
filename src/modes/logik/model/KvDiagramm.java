package modes.logik.model;

import java.util.List;

/** KV-Diagramm mit Zeilen- und Spaltenköpfen in Gray-Code-Reihenfolge. */
public record KvDiagramm(String zeilenVariablen, String spaltenVariablen,
                         List<String> zeilenKoepfe, List<String> spaltenKoepfe, List<List<Boolean>> werte)
{
    public KvDiagramm
    {
        zeilenKoepfe = List.copyOf(zeilenKoepfe);
        spaltenKoepfe = List.copyOf(spaltenKoepfe);
        werte = werte.stream().map(List::copyOf).toList();
    }

    public boolean wert(int zeile, int spalte)
    {
        return werte.get(zeile).get(spalte);
    }
}
