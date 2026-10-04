package common.history;

import java.time.format.DateTimeFormatter;
import java.util.List;

/** Wandelt Verlaufseinträge in Text zum Speichern um. Das eigentliche Schreiben der Datei macht die Oberfläche. */
public final class VerlaufExport
{
    private static final String ZEILENENDE = "\r\n";
    private static final DateTimeFormatter ZEITPUNKT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private VerlaufExport()
    {
    }

    /** Eine Zeile pro Eintrag, so wie er auch im Verlauf steht. Favoriten bekommen einen Stern davor. */
    public static String alsText(List<VerlaufEintrag> eintraege)
    {
        StringBuilder text = new StringBuilder();
        for (VerlaufEintrag eintrag : eintraege)
        {
            text.append(eintrag.isFavorit() ? "★ " : "")
                    .append(eintrag.toDisplayText())
                    .append(ZEILENENDE);
        }
        return text.toString();
    }

    /** Semikolon als Trenner, damit ein deutsches Excel die Spalten direkt erkennt. */
    public static String alsCsv(List<VerlaufEintrag> eintraege)
    {
        StringBuilder csv = new StringBuilder("Zeitpunkt;Modus;Ausdruck;Ergebnis;Favorit").append(ZEILENENDE);
        for (VerlaufEintrag eintrag : eintraege)
        {
            csv.append(eintrag.getZeitpunkt().format(ZEITPUNKT_FORMAT)).append(';')
                    .append(csvFeld(eintrag.getModus().getLabel())).append(';')
                    .append(csvFeld(eintrag.getAusdruck())).append(';')
                    .append(csvFeld(eintrag.getErgebnis())).append(';')
                    .append(eintrag.isFavorit() ? "ja" : "nein")
                    .append(ZEILENENDE);
        }
        return csv.toString();
    }

    private static String csvFeld(String wert)
    {
        if (wert.contains(";") || wert.contains("\"") || wert.contains("\n") || wert.contains("\r"))
        {
            return "\"" + wert.replace("\"", "\"\"") + "\"";
        }
        return wert;
    }
}
