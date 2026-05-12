package common.logic;

import common.formatting.ZahlenFormatModus;
import common.formatting.ZahlenFormatter;
import common.state.RechnerZustand;
import common.state.SpeicherState;
import common.state.WinkelModus;

public class RechnerService
{
    private final ZahlenFormatter zahlenFormatierer;
    private final RechnerZustand zustand;
    private final SpeicherState speicherState;
    private final AusdruckEditor ausdruckEditor;
    private final BerechnungsService berechnungsService;
    private final SpeicherService speicherService;

    public RechnerService()
    {
        this.zahlenFormatierer = new ZahlenFormatter();
        this.zustand = new RechnerZustand();
        this.speicherState = new SpeicherState();
        this.ausdruckEditor = new AusdruckEditor(zustand, zahlenFormatierer);
        this.berechnungsService = new BerechnungsService(zustand, zahlenFormatierer);
        this.speicherService = new SpeicherService(speicherState, berechnungsService, ausdruckEditor, zahlenFormatierer);
    }

    public AusdruckEditor getAusdruckEditor()
    {
        return ausdruckEditor;
    }

    protected RechnerZustand getZustand()
    {
        return zustand;
    }

    public String eingabeZahl(String ziffer)
    {
        return ausdruckEditor.eingabeZahl(ziffer);
    }

    public String eingabeKomma()
    {
        return ausdruckEditor.eingabeKomma();
    }

    public String wechselVorzeichen()
    {
        return ausdruckEditor.wechselVorzeichen();
    }

    public String klammerAuf()
    {
        return ausdruckEditor.klammerAuf();
    }

    public String klammerZu()
    {
        return ausdruckEditor.klammerZu();
    }

    public String loeschen()
    {
        return ausdruckEditor.loeschen();
    }

    public String ce()
    {
        return ausdruckEditor.ce();
    }

    public String allesLoeschen()
    {
        return ausdruckEditor.allesLoeschen();
    }

    public String operatorSetzen(String operator)
    {
        return ausdruckEditor.operatorSetzen(operator);
    }

    public String potenz()
    {
        return ausdruckEditor.potenz();
    }

    public String prozent()
    {
        return ausdruckEditor.prozent();
    }

    public String quadriere()
    {
        return ausdruckEditor.quadriere();
    }

    public String wurzel()
    {
        return ausdruckEditor.wurzel();
    }

    public String reziprok()
    {
        return ausdruckEditor.reziprok();
    }

    public String ans()
    {
        return ausdruckEditor.ans();
    }

    public void setzeAusdruckAusVerlaufErgebnis(String verlaufErgebnis)
    {
        ausdruckEditor.setzeAusdruckAusVerlaufErgebnis(verlaufErgebnis);
    }

    public void setzeAusdruckAusZwischenablage(String text)
    {
        ausdruckEditor.setzeAusdruckAusZwischenablage(text);
    }

    public String berechne()
    {
        return berechnungsService.berechne();
    }

    public BerechnungsErgebnis berechneDetailliert()
    {
        return berechnungsService.berechneDetailliert();
    }

    public String formatiereZahl(double zahl)
    {
        return zahlenFormatierer.formatiereZahl(zahl);
    }

    public String formatiereLiveAnzeige()
    {
        return zahlenFormatierer.formatiereLiveAnzeige(zustand.getAusdruckText());
    }

    public String getVerlauf()
    {
        return zustand.getVerlaufText();
    }

    public void setVerlauf(String verlauf)
    {
        zustand.setVerlaufText(verlauf);
    }

    public String getAusdruckText()
    {
        return zustand.getAusdruckText();
    }

    public void setAusdruckText(String ausdruck)
    {
        zustand.setAusdruckText(ausdruck);
        zustand.setGleichGedrueckt(false);
    }

    public void winkelModusUmschalten()
    {
        zustand.winkelModusUmschalten();
    }

    public WinkelModus getWinkelModus()
    {
        return zustand.getWinkelModus();
    }

    public void setWinkelModus(WinkelModus winkelModus)
    {
        zustand.setWinkelModus(winkelModus);
    }

    public void setNachkommastellen(int nachkommastellen)
    {
        zahlenFormatierer.setNachkommastellen(nachkommastellen);
    }

    public int getNachkommastellen()
    {
        return zahlenFormatierer.getNachkommastellen();
    }

    public void setZahlenFormatModus(ZahlenFormatModus formatModus)
    {
        zahlenFormatierer.setFormatModus(formatModus);
    }

    public ZahlenFormatModus getZahlenFormatModus()
    {
        return zahlenFormatierer.getFormatModus();
    }

    public String speicherLoeschen()
    {
        return speicherService.speicherLoeschen();
    }

    public String speicherAbrufen()
    {
        return speicherService.speicherAbrufen();
    }

    public String speicherAddieren()
    {
        return speicherService.speicherAddieren();
    }

    public String speicherSubtrahieren()
    {
        return speicherService.speicherSubtrahieren();
    }

    public boolean hatSpeicherWert()
    {
        return speicherService.hatSpeicherWert();
    }

    public double getSpeicherWert()
    {
        return speicherState.getWert();
    }

    public void setSpeicherWert(double wert)
    {
        speicherState.setWert(Double.isFinite(wert) ? wert : 0.0);
    }
}
