package modes.logik.logic;

import modes.logik.model.KvDiagramm;
import modes.logik.model.Wahrheitstabelle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Leitet aus einer Wahrheitstabelle kanonische DNF/KNF und das KV-Diagramm ab. */
public class TabellenAuswertung
{
    public String dnf(Wahrheitstabelle tabelle)
    {
        return normalform(tabelle, true);
    }

    public String knf(Wahrheitstabelle tabelle)
    {
        return normalform(tabelle, false);
    }

    /** DNF: Minterme der wahren Zeilen mit ∨ verbunden. KNF: Maxterme der falschen Zeilen mit ∧ verbunden. */
    private String normalform(Wahrheitstabelle tabelle, boolean dnf)
    {
        List<String> terme = new ArrayList<>();
        for (int zeile = 0; zeile < tabelle.anzahlZeilen(); zeile++)
        {
            if (tabelle.ergebnis(zeile) == dnf)
            {
                terme.add(term(tabelle, zeile, dnf));
            }
        }
        if (terme.isEmpty())
        {
            return dnf ? "0" : "1";
        }
        if (tabelle.variablen().isEmpty())
        {
            return dnf ? "1" : "0";
        }
        return String.join(dnf ? " ∨ " : " ∧ ", terme);
    }

    private String term(Wahrheitstabelle tabelle, int zeile, boolean dnf)
    {
        List<String> literale = new ArrayList<>();
        for (int spalte = 0; spalte < tabelle.variablen().size(); spalte++)
        {
            // Minterm: Variable negiert, wenn 0. Maxterm: negiert, wenn 1.
            boolean negiert = tabelle.wert(zeile, spalte) != dnf;
            literale.add((negiert ? "¬" : "") + tabelle.variablen().get(spalte));
        }
        String term = String.join(dnf ? " ∧ " : " ∨ ", literale);
        return literale.size() > 1 ? "(" + term + ")" : term;
    }

    public KvDiagramm kvDiagramm(Wahrheitstabelle tabelle)
    {
        int n = tabelle.variablen().size();
        if (n < 2 || n > 4)
        {
            throw new IllegalArgumentException("Ein KV-Diagramm gibt es hier nur für 2 bis 4 Variablen.");
        }

        int zeilenBits = n / 2;
        int spaltenBits = n - zeilenBits;
        List<Integer> zeilenCodes = grayCodes(zeilenBits);
        List<Integer> spaltenCodes = grayCodes(spaltenBits);

        List<List<Boolean>> werte = new ArrayList<>();
        for (int zeilenCode : zeilenCodes)
        {
            List<Boolean> reihe = new ArrayList<>();
            for (int spaltenCode : spaltenCodes)
            {
                reihe.add(tabelle.ergebnis((zeilenCode << spaltenBits) | spaltenCode));
            }
            werte.add(reihe);
        }

        List<Character> variablen = tabelle.variablen();
        return new KvDiagramm(
                namen(variablen.subList(0, zeilenBits)),
                namen(variablen.subList(zeilenBits, n)),
                koepfe(zeilenCodes, zeilenBits),
                koepfe(spaltenCodes, spaltenBits),
                werte);
    }

    private static List<Integer> grayCodes(int bits)
    {
        List<Integer> codes = new ArrayList<>();
        for (int i = 0; i < (1 << bits); i++)
        {
            codes.add(i ^ (i >> 1));
        }
        return codes;
    }

    private static List<String> koepfe(List<Integer> codes, int bits)
    {
        return codes.stream()
                .map(code -> String.format("%" + bits + "s", Integer.toBinaryString(code)).replace(' ', '0'))
                .toList();
    }

    private static String namen(List<Character> variablen)
    {
        return variablen.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
