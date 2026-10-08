package modes.finanz.logic;

import modes.finanz.model.FinanzErgebnis;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/** Die wählbaren Rechnungen mit Feldbeschriftungen und sinnvollen Startwerten. */
public enum FinanzRechnung
{
    PROZENTWERT("Wie viel sind x % von y?",
            List.of("Prozentsatz x in %", "Grundwert y in €"), List.of("19", "250"),
            w -> FinanzRechner.prozentwert(w.get(0), w.get(1))),
    PROZENTSATZ("x ist wie viel % von y?",
            List.of("Prozentwert x in €", "Grundwert y in €"), List.of("47,5", "250"),
            w -> FinanzRechner.prozentsatz(w.get(0), w.get(1))),
    GRUNDWERT("y ist x % von welchem Grundwert?",
            List.of("Prozentwert y in €", "Prozentsatz x in %"), List.of("47,5", "19"),
            w -> FinanzRechner.grundwert(w.get(0), w.get(1))),
    VERAENDERUNG("Veränderung von a nach b in %",
            List.of("Alter Wert a", "Neuer Wert b"), List.of("80", "100"),
            w -> FinanzRechner.veraenderung(w.get(0), w.get(1))),
    NETTO_ZU_BRUTTO("Netto → Brutto (MwSt)",
            List.of("Nettobetrag in €", "Steuersatz in % (19 oder 7)"), List.of("100", "19"),
            w -> FinanzRechner.bruttoAusNetto(w.get(0), w.get(1))),
    BRUTTO_ZU_NETTO("Brutto → Netto (MwSt)",
            List.of("Bruttobetrag in €", "Steuersatz in % (19 oder 7)"), List.of("119", "19"),
            w -> FinanzRechner.nettoAusBrutto(w.get(0), w.get(1))),
    RABATT("Rabatt auf einen Preis",
            List.of("Preis in €", "Rabatt in %"), List.of("59,99", "20"),
            w -> FinanzRechner.rabatt(w.get(0), w.get(1))),
    TRINKGELD("Trinkgeld und Aufteilen",
            List.of("Rechnungsbetrag in €", "Trinkgeld in %", "Anzahl Personen"), List.of("84,5", "10", "2"),
            w -> FinanzRechner.trinkgeld(w.get(0), w.get(1), w.get(2))),
    EINFACHE_ZINSEN("Einfache Zinsen",
            List.of("Kapital in €", "Zinssatz in % p. a.", "Laufzeit in Jahren"), List.of("1000", "3", "5"),
            w -> FinanzRechner.einfacheZinsen(w.get(0), w.get(1), w.get(2))),
    ZINSESZINS_JAEHRLICH("Zinseszins (jährlich)",
            List.of("Kapital in €", "Zinssatz in % p. a.", "Laufzeit in Jahren"), List.of("1000", "3", "5"),
            w -> FinanzRechner.zinseszins(w.get(0), w.get(1), w.get(2), 1)),
    ZINSESZINS_MONATLICH("Zinseszins (monatlich)",
            List.of("Kapital in €", "Zinssatz in % p. a.", "Laufzeit in Jahren"), List.of("1000", "3", "5"),
            w -> FinanzRechner.zinseszins(w.get(0), w.get(1), w.get(2), 12)),
    KREDIT("Kredit: Monatsrate",
            List.of("Kreditsumme in €", "Sollzins in % p. a.", "Laufzeit in Monaten"), List.of("10000", "5", "48"),
            w -> FinanzRechner.kreditrate(w.get(0), w.get(1), w.get(2)));

    private final String titel;
    private final List<String> felder;
    private final List<String> vorgaben;
    private final Function<List<BigDecimal>, FinanzErgebnis> rechnung;

    FinanzRechnung(String titel, List<String> felder, List<String> vorgaben,
                   Function<List<BigDecimal>, FinanzErgebnis> rechnung)
    {
        this.titel = titel;
        this.felder = felder;
        this.vorgaben = vorgaben;
        this.rechnung = rechnung;
    }

    public List<String> felder()
    {
        return felder;
    }

    public List<String> vorgaben()
    {
        return vorgaben;
    }

    public FinanzErgebnis berechne(List<BigDecimal> werte)
    {
        if (werte.size() != felder.size())
        {
            throw new IllegalArgumentException("Es werden " + felder.size() + " Werte gebraucht.");
        }
        return rechnung.apply(werte);
    }

    @Override
    public String toString()
    {
        return titel;
    }
}
