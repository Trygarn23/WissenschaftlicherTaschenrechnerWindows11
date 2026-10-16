package common.logic;

import common.state.RechnerZustand;

/**
 * Setzt den letzten Term in eine Funktion oder Klammer, z. B. {@code 5+9} → {@code 5+sqrt(9)}.
 * Gibt es keinen Term, wird je nach Aktion nur der Anfang angehängt oder nichts getan.
 */
final class AusdruckTermRahmen
{
    private final RechnerZustand zustand;

    AusdruckTermRahmen(RechnerZustand zustand)
    {
        this.zustand = zustand;
    }

    String funktionEinfuegenOderUmklammern(String funktionsName)
    {
        return einrahmenOderAnhaengen(funktionsName + "(", ")");
    }

    String praefixOperatorEinfuegenOderUmklammern(String praefix)
    {
        return einrahmenOderAnhaengen(praefix, ")");
    }

    /** Ohne letzten Term passiert nichts (z. B. x² auf „5+“). */
    String letztenTermEinrahmen(String praefix, String suffix)
    {
        AusdruckEditor.resetNachGleichWennNoetig(zustand);

        int start = startEinrahmbarerTerm();
        if (start < 0) return zustand.getAusdruckText();

        einrahmen(start, praefix, suffix);
        return zustand.getAusdruckText();
    }

    /**
     * Für Funktionen mit zwei Argumenten wie nCr(n;k): der letzte Term wird das erste Argument,
     * danach steht schon das {@code ;} und man tippt direkt das zweite.
     */
    String zweiArgumentFunktion(String funktionsName)
    {
        AusdruckEditor.resetNachGleichWennNoetig(zustand);

        if (AusdruckEditor.endetMitOperatorOderKlammerAuf(zustand))
        {
            zustand.appendAusdruck(funktionsName + "(");
            return zustand.getAusdruckText();
        }

        int start = AusdruckTermFinder.startLetzterTerm(zustand.getAusdruckText());
        String term = start < zustand.getAusdruckLaenge() ? zustand.getAusdruckTeilText(start) : "";
        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(funktionsName + "(" + term + (term.isEmpty() ? "" : ";"));
        zustand.setGleichGedrueckt(false);
        return zustand.getAusdruckText();
    }

    /** Ohne letzten Term wird nur der Anfang angehängt, z. B. „sqrt(“. */
    private String einrahmenOderAnhaengen(String praefix, String suffix)
    {
        AusdruckEditor.resetNachGleichWennNoetig(zustand);

        int start = startEinrahmbarerTerm();
        if (start < 0)
        {
            zustand.appendAusdruck(praefix);
            zustand.setGleichGedrueckt(false);
        } else
        {
            einrahmen(start, praefix, suffix);
        }
        return zustand.getAusdruckText();
    }

    /** Start des letzten Terms oder -1, wenn es keinen gibt. */
    private int startEinrahmbarerTerm()
    {
        if (AusdruckEditor.endetMitOperatorOderKlammerAuf(zustand)) return -1;

        int start = AusdruckTermFinder.startLetzterTerm(zustand.getAusdruckText());
        return start >= zustand.getAusdruckLaenge() ? -1 : start;
    }

    private void einrahmen(int start, String praefix, String suffix)
    {
        String term = zustand.getAusdruckTeilText(start);
        zustand.deleteAusdruck(start, zustand.getAusdruckLaenge());
        zustand.appendAusdruck(praefix + term + suffix);
        zustand.setGleichGedrueckt(false);
    }
}
