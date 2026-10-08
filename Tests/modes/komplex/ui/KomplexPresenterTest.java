package modes.komplex.ui;

import modes.komplex.formatting.KomplexFormatter;
import modes.komplex.logic.KomplexRechnerService;
import modes.komplex.model.KomplexDarstellung;
import modes.komplex.model.KomplexState;
import modes.komplex.model.KomplexeZahl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KomplexPresenterTest
{
    private final FakeView view = new FakeView();
    private final KomplexState state = new KomplexState();
    private final KomplexPresenter presenter = new KomplexPresenter(view, state, new KomplexRechnerService(), new KomplexFormatter());

    @Test
    void addiere_ShouldShowSumDetailStatusAndPlane()
    {
        // Arrange
        view.setzeEingaben("1", "2", "3", "-6");

        // Act
        presenter.addiere();

        // Assert
        assertEquals("4 - 4i", view.ergebnis);
        assertTrue(view.detail.startsWith("|z| = 5,6568542495"), view.detail);
        assertEquals("Addition", view.status);
        assertEquals(4, view.ebeneErgebnis.getReal());
        assertEquals(-4, view.ebeneErgebnis.getImaginaer());
        assertEquals(1, view.ebeneZ1.getReal());
        assertEquals(-6, view.ebeneZ2.getImaginaer());
        assertNull(view.fehler);
    }

    @Test
    void multipliziere_ShouldAcceptGermanDecimalComma()
    {
        // Arrange
        view.setzeEingaben("1,5", "0", "2", "0");

        // Act
        presenter.multipliziere();

        // Assert
        assertEquals("3 + 0i", view.ergebnis);
        assertEquals("Multiplikation", view.status);
    }

    @Test
    void subtrahiere_ShouldSubtractSecondFromFirst()
    {
        // Arrange
        view.setzeEingaben("5", "5", "2", "7");

        // Act
        presenter.subtrahiere();

        // Assert
        assertEquals("3 - 2i", view.ergebnis);
    }

    @Test
    void konjugiere_ShouldOnlyUseFirstNumber()
    {
        // Arrange
        view.setzeEingaben("2", "3", "100", "100");

        // Act
        presenter.konjugiere();

        // Assert
        assertEquals("2 - 3i", view.ergebnis);
        assertEquals("Konjugation", view.status);
    }

    @Test
    void dividiere_ShouldShowErrorAndKeepOldResult_WhenDividingByZero()
    {
        // Arrange
        view.setzeEingaben("1", "1", "1", "0");
        presenter.dividiere();
        view.setzeEingaben("1", "1", "0", "0");

        // Act
        presenter.dividiere();

        // Assert
        assertEquals("Division durch 0 + 0i", view.fehler);
        assertEquals("Division durch 0 + 0i", state.getStatus());
        assertEquals(1, state.getErgebnis().getReal());
        assertEquals(1, state.getZweiteZahl().getReal());
    }

    @Test
    void addiere_ShouldShowGermanError_WhenInputIsNoNumber()
    {
        // Arrange
        view.setzeEingaben("abc", "0", "1", "1");

        // Act
        presenter.addiere();

        // Assert
        assertNotNull(view.fehler);
        assertTrue(view.fehler.contains("keine gültige Zahl"), view.fehler);
        assertNull(view.ergebnis);
    }

    @Test
    void waehleDarstellung_ShouldReformatCurrentResult()
    {
        // Arrange
        view.setzeEingaben("3", "-4", "0", "0");
        presenter.addiere();

        // Act
        presenter.waehleDarstellung(KomplexDarstellung.POLAR_DEG);

        // Assert
        assertEquals("5 ∠ -53,1301023542°", view.ergebnis);
        assertEquals(KomplexDarstellung.POLAR_DEG, state.getDarstellung());
    }

    @Test
    void kopiere_ShouldHandFormattedResultToViewAndReport()
    {
        // Arrange
        view.setzeEingaben("0", "7", "0", "0");
        presenter.addiere();

        // Act
        presenter.kopiere();

        // Assert
        assertEquals("0 + 7i", view.kopiert);
        assertEquals("Ergebnis kopiert", view.status);
    }

    @Test
    void aktualisiere_ShouldShowInjectedState()
    {
        // Arrange
        state.setErgebnis(new KomplexeZahl(2, 2));
        state.setStatus("Geladen");

        // Act
        presenter.aktualisiere();

        // Assert
        assertEquals("2 + 2i", view.ergebnis);
        assertEquals("Geladen", view.status);
        assertEquals(2, view.ebeneErgebnis.getImaginaer());
    }

    /** Merkt sich nur, was der Presenter anzeigen will – kein Fenster nötig. */
    private static class FakeView implements KomplexView
    {
        private String[] eingaben = {"0", "0", "0", "0"};
        private String ergebnis;
        private String detail;
        private String status;
        private String fehler;
        private String kopiert;
        private KomplexeZahl ebeneZ1;
        private KomplexeZahl ebeneZ2;
        private KomplexeZahl ebeneErgebnis;

        void setzeEingaben(String aReal, String aImag, String bReal, String bImag)
        {
            eingaben = new String[] {aReal, aImag, bReal, bImag};
        }

        @Override
        public String ersteReal()
        {
            return eingaben[0];
        }

        @Override
        public String ersteImaginaer()
        {
            return eingaben[1];
        }

        @Override
        public String zweiteReal()
        {
            return eingaben[2];
        }

        @Override
        public String zweiteImaginaer()
        {
            return eingaben[3];
        }

        @Override
        public void zeigeErgebnis(String ergebnis, String detail)
        {
            this.ergebnis = ergebnis;
            this.detail = detail;
        }

        @Override
        public void zeigeStatus(String text)
        {
            status = text;
        }

        @Override
        public void zeigeFehler(String meldung)
        {
            fehler = meldung;
        }

        @Override
        public void zeigeZahlenebene(KomplexeZahl z1, KomplexeZahl z2, KomplexeZahl ergebnis)
        {
            ebeneZ1 = z1;
            ebeneZ2 = z2;
            ebeneErgebnis = ergebnis;
        }

        @Override
        public void kopiere(String text)
        {
            kopiert = text;
        }
    }
}
