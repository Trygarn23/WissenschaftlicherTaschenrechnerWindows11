package modes.komplex.ui;

import modes.komplex.model.KomplexeZahl;

/**
 * Was der Presenter von der Oberfläche braucht.
 * Zwei Implementierungen: das echte KomplexPanel und eine Fake-View im Presenter-Test.
 */
public interface KomplexView
{
    String ersteReal();

    String ersteImaginaer();

    String zweiteReal();

    String zweiteImaginaer();

    void zeigeErgebnis(String ergebnis, String detail);

    void zeigeStatus(String text);

    void zeigeFehler(String meldung);

    void zeigeZahlenebene(KomplexeZahl z1, KomplexeZahl z2, KomplexeZahl ergebnis);

    void kopiere(String text);
}
