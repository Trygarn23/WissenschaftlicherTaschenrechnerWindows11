package modes.logik.logic;

import modes.logik.model.Ausdruck;
import modes.logik.model.Wahrheitstabelle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class WahrheitstabellenRechner
{
    public static final int MAX_VARIABLEN = 6;

    private final LogikParser parser = new LogikParser();

    public Wahrheitstabelle erstelle(String eingabe)
    {
        return erstelle(parser.parse(eingabe));
    }

    public Wahrheitstabelle erstelle(Ausdruck ausdruck)
    {
        TreeSet<Character> sortiert = new TreeSet<>();
        ausdruck.sammleVariablen(sortiert);
        List<Character> variablen = new ArrayList<>(sortiert);
        if (variablen.size() > MAX_VARIABLEN)
        {
            throw new IllegalArgumentException("Maximal " + MAX_VARIABLEN + " Variablen erlaubt, gefunden: "
                    + variablen.size() + ".");
        }

        int n = variablen.size();
        List<Boolean> ergebnisse = new ArrayList<>();
        Map<Character, Boolean> belegung = new HashMap<>();
        for (int zeile = 0; zeile < (1 << n); zeile++)
        {
            for (int spalte = 0; spalte < n; spalte++)
            {
                belegung.put(variablen.get(spalte), ((zeile >> (n - 1 - spalte)) & 1) == 1);
            }
            ergebnisse.add(ausdruck.werteAus(belegung));
        }
        return new Wahrheitstabelle(variablen, ergebnisse);
    }
}
