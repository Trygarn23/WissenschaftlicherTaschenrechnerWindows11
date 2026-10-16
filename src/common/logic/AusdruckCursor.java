package common.logic;

import common.state.RechnerZustand;

/** Cursor im Ausdruck: setzen, bewegen und Editor-Aktionen an der Cursorstelle ausführen. */
final class AusdruckCursor
{
    private final RechnerZustand zustand;

    AusdruckCursor(RechnerZustand zustand)
    {
        this.zustand = zustand;
    }

    int getCursor()
    {
        return zustand.getCursor();
    }

    /** Wer in ein Ergebnis klickt, will es weiterbearbeiten – deshalb zählt „=“ danach nicht mehr. */
    void setzeCursor(int index)
    {
        zustand.setGleichGedrueckt(false);
        zustand.setCursor(index);
    }

    void bewegeCursor(int schritte)
    {
        setzeCursor(zustand.getCursor() + schritte);
    }

    /**
     * Führt eine Eingabe an der Cursorstelle aus: Alle Editor-Aktionen arbeiten am Ende des Ausdrucks,
     * also wird der Teil hinter dem Cursor kurz abgeschnitten und danach wieder angehängt.
     * Steht der Cursor am Ende, läuft die Aktion unverändert.
     */
    void amCursor(Runnable aktion)
    {
        if (zustand.isCursorAmEnde() || zustand.isGleichGedrueckt())
        {
            aktion.run();
            return;
        }

        int cursor = zustand.getCursor();
        String rest = zustand.getAusdruckTeilText(cursor);
        zustand.setAusdruckLaenge(cursor);
        zustand.setCursor(cursor);
        aktion.run();

        // Fehler (z. B. 5000!) löschen den ganzen Ausdruck, dann gehört der Rest nicht mehr dazu.
        if (zustand.isGleichGedrueckt()) return;

        int neuerCursor = zustand.getAusdruckLaenge();
        zustand.appendAusdruck(rest);
        zustand.setCursor(neuerCursor);
    }
}
