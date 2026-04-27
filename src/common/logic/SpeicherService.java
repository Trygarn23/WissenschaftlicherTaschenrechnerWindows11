package common.logic;

import common.formatting.ZahlenFormatter;
import common.state.SpeicherState;

public class SpeicherService
{
    private final SpeicherState speicherState;
    private final BerechnungsService berechnungsService;
    private final AusdruckEditor ausdruckEditor;
    private final ZahlenFormatter zahlenFormatierer;

    public SpeicherService(
            SpeicherState speicherState,
            BerechnungsService berechnungsService,
            AusdruckEditor ausdruckEditor,
            ZahlenFormatter zahlenFormatierer)
    {
        this.speicherState = speicherState;
        this.berechnungsService = berechnungsService;
        this.ausdruckEditor = ausdruckEditor;
        this.zahlenFormatierer = zahlenFormatierer;
    }

    public String speicherLoeschen()
    {
        speicherState.loeschen();
        return "0";
    }

    public String speicherAbrufen()
    {
        return ausdruckEditor.konstanteEinsetzen(speicherState.getWert());
    }

    public String speicherAddieren()
    {
        speicherState.addiere(berechnungsService.aktuellerWertOder0());
        return zahlenFormatierer.formatiereZahl(speicherState.getWert());
    }

    public String speicherSubtrahieren()
    {
        speicherState.subtrahiere(berechnungsService.aktuellerWertOder0());
        return zahlenFormatierer.formatiereZahl(speicherState.getWert());
    }
}
