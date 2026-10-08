package modes.komplex.ui;

import common.formatting.ZahlenEingabe;
import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.logic.KomplexRechnerService;
import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexState;
import modes.komplex.model.KomplexeZahl;

import java.util.function.BinaryOperator;

/** Ablauf des Komplexmodus: Eingaben lesen, rechnen, Ergebnis an die View geben. Kennt kein Swing. */
public class KomplexPresenter
{
    private final KomplexView view;
    private final KomplexState state;
    private final KomplexRechnerService service;
    private final KomplexFormatter formatter;

    public KomplexPresenter(KomplexView view, KomplexState state, KomplexRechnerService service, KomplexFormatter formatter)
    {
        this.view = view;
        this.state = state;
        this.service = service;
        this.formatter = formatter;
    }

    public void addiere()
    {
        rechne(service::addiere, "Addition");
    }

    public void subtrahiere()
    {
        rechne(service::subtrahiere, "Subtraktion");
    }

    public void multipliziere()
    {
        rechne(service::multipliziere, "Multiplikation");
    }

    public void dividiere()
    {
        rechne(service::dividiere, "Division");
    }

    public void konjugiere()
    {
        rechne((erste, zweite) -> service.konjugiert(erste), "Konjugation");
    }

    public void waehleDarstellung(KomplexDarstellung darstellung)
    {
        state.setDarstellung(darstellung);
        aktualisiere();
    }

    public void kopiere()
    {
        view.kopiere(formatiertesErgebnis());
        state.setStatus("Ergebnis kopiert");
        aktualisiere();
    }

    public void aktualisiere()
    {
        KomplexeZahl ergebnis = state.getErgebnis();
        String detail = "|z| = " + formatter.formatiereDouble(ergebnis.betrag())
                + " | arg = " + formatter.formatiereDouble(ergebnis.phaseDeg()) + "°";
        view.zeigeErgebnis(formatiertesErgebnis(), detail);
        view.zeigeStatus(state.getStatus());
        view.zeigeZahlenebene(state.getErsteZahl(), state.getZweiteZahl(), ergebnis);
    }

    private void rechne(BinaryOperator<KomplexeZahl> operation, String status)
    {
        try
        {
            KomplexeZahl erste = new KomplexeZahl(ZahlenEingabe.lese(view.ersteReal()), ZahlenEingabe.lese(view.ersteImaginaer()));
            KomplexeZahl zweite = new KomplexeZahl(ZahlenEingabe.lese(view.zweiteReal()), ZahlenEingabe.lese(view.zweiteImaginaer()));
            KomplexeZahl ergebnis = operation.apply(erste, zweite);
            state.setErsteZahl(erste);
            state.setZweiteZahl(zweite);
            state.setErgebnis(ergebnis);
            state.setStatus(status);
            aktualisiere();
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            String meldung = e.getMessage() == null || e.getMessage().isBlank() ? "Ungültige Eingabe" : e.getMessage();
            state.setStatus(meldung);
            view.zeigeFehler(meldung);
        }
    }

    private String formatiertesErgebnis()
    {
        return formatter.formatiere(state.getErgebnis(), state.getDarstellung());
    }
}
