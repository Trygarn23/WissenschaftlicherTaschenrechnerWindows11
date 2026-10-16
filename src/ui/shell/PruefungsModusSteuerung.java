package ui.shell;

import common.state.RechnerModus;
import ui.units.EinheitenSidePanelHost;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.util.function.BooleanSupplier;

/**
 * Setzt die Regeln aus {@link PruefungsModus} in der Shell um: Sperren mit Piep, Fenstertitel, Leisten, Einheiten-Panel.
 * Ob der Modus aktiv ist, wird jedes Mal frisch gefragt, weil die Settings zur Laufzeit ersetzt werden.
 */
final class PruefungsModusSteuerung
{
    private final BooleanSupplier aktiv;
    private final JFrame fenster;
    private final ModeBarPanel modeBarPanel;
    private final GlobalActionBarPanel globalActionBarPanel;
    private final EinheitenSidePanelHost einheitenSidePanelHost;

    PruefungsModusSteuerung(BooleanSupplier aktiv, JFrame fenster, ModeBarPanel modeBarPanel,
                            GlobalActionBarPanel globalActionBarPanel, EinheitenSidePanelHost einheitenSidePanelHost)
    {
        this.aktiv = aktiv;
        this.fenster = fenster;
        this.modeBarPanel = modeBarPanel;
        this.globalActionBarPanel = globalActionBarPanel;
        this.einheitenSidePanelHost = einheitenSidePanelHost;
    }

    boolean istAktiv()
    {
        return aktiv.getAsBoolean();
    }

    boolean sindWerkzeugeErlaubt()
    {
        return PruefungsModus.sindWerkzeugeErlaubt(istAktiv());
    }

    RechnerModus erlaubterModus(RechnerModus gewuenscht)
    {
        return PruefungsModus.erlaubterModus(istAktiv(), gewuenscht);
    }

    /** Im Prüfungsmodus gibt es keine Hilfswerkzeuge – kurzer Piep statt stillem Nichts. */
    boolean werkzeugGesperrt()
    {
        return piepWenn(!sindWerkzeugeErlaubt());
    }

    boolean modusGesperrt(RechnerModus modus)
    {
        return piepWenn(!PruefungsModus.istModusErlaubt(istAktiv(), modus));
    }

    /** Titel und Leisten anpassen, Einheiten-Panel schließen. Den erlaubten Modus setzt die Shell danach selbst. */
    void anwenden()
    {
        boolean an = istAktiv();
        fenster.setTitle(PruefungsModus.fensterTitel(an));
        modeBarPanel.setPruefungsModus(an);
        globalActionBarPanel.setPruefungsModus(an);
        if (an && einheitenSidePanelHost.isGeoeffnet())
        {
            einheitenSidePanelHost.setGeoeffnet(false);
        }
    }

    private static boolean piepWenn(boolean gesperrt)
    {
        if (gesperrt)
        {
            Toolkit.getDefaultToolkit().beep();
        }
        return gesperrt;
    }
}
