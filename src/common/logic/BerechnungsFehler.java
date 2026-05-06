package common.logic;

public enum BerechnungsFehler
{
    KEINER(""),
    SYNTAX("Der Ausdruck ist unvollständig oder falsch aufgebaut."),
    DIVISION_DURCH_NULL("Division durch 0 ist nicht definiert."),
    UNGUELTIGER_FUNKTIONSBEREICH("Diese Funktion ist für den eingegebenen Wert nicht definiert."),
    UNBEKANNTE_FUNKTION("Diese Funktion oder Variable ist unbekannt."),
    KLAMMERN_UNAUSGEGLICHEN("Die Klammern sind nicht ausgeglichen."),
    UNGUELTIGES_ERGEBNIS("Das Ergebnis ist nicht darstellbar.");

    private final String meldung;

    BerechnungsFehler(String meldung)
    {
        this.meldung = meldung;
    }

    public String getMeldung()
    {
        return meldung;
    }
}
