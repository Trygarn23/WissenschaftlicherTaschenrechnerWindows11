package common.parser;

import common.state.WinkelModus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FunktionsRegistryTest
{
    private static final double EPSILON = 1e-10;

    @AfterEach
    void aufraeumen()
    {
        FunktionsRegistry.entferneAlleBenutzerfunktionen();
    }

    @Test
    void nCrUndNPr_ShouldUseSemicolonAsArgumentSeparator()
    {
        // Act & Assert
        assertEquals(10.0, rechne("nCr(5;2)"), EPSILON);
        assertEquals(20.0, rechne("nPr(5;2)"), EPSILON);
        assertEquals(1.0, rechne("nCr(7;0)"), EPSILON);
        assertEquals(1.0, rechne("nPr(0;0)"), EPSILON);
        assertEquals(2598960.0, rechne("nCr(52;5)"), EPSILON);
        assertEquals(13.0, rechne("3+nCr(5;2)"), EPSILON);
        assertEquals(6.0, rechne("nCr(2+2; 1+1)"), EPSILON);
    }

    @Test
    void nCr_ShouldStayExactForLargeValuesWithoutOverflow()
    {
        // Act
        double gross = rechne("nCr(1000;3)");
        double symmetrisch = rechne("nCr(1000;997)");
        double riesig = rechne("nCr(1000;500)");

        // Assert
        assertEquals(166167000.0, gross, EPSILON);
        assertEquals(gross, symmetrisch, EPSILON);
        assertTrue(Double.isFinite(riesig) && riesig > 1e298);
    }

    @Test
    void nCrUndNPr_ShouldRejectInvalidArgumentsWithGermanMessage()
    {
        // Act
        AusdruckParserException kGroesserN = fehler("nCr(2;5)");

        // Assert
        assertEquals(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, kGroesserN.getFehler());
        assertEquals("nCr(n;k): n und k müssen ganze Zahlen ab 0 sein, mit k ≤ n an Stelle 1", kGroesserN.getMessage());
        assertEquals(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, fehler("nPr(5,5;2)").getFehler());
        assertEquals(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, fehler("nCr(-1;0)").getFehler());
        assertEquals(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, fehler("nPr(5000;5000)").getFehler());
    }

    @Test
    void funktionen_ShouldCheckArgumentCount()
    {
        // Act
        AusdruckParserException zuWenig = fehler("2+nCr(5)");
        AusdruckParserException zuViel = fehler("sin(1;2)");
        AusdruckParserException leer = fehler("nCr(5;)");
        AusdruckParserException ohneFunktion = fehler("(1;2)");

        // Assert
        assertEquals(ParserFehler.SYNTAX, zuWenig.getFehler());
        assertEquals("ncr erwartet 2 Argumente, gefunden: 1 an Stelle 3", zuWenig.getMessage());
        assertEquals(2, zuWenig.getPosition());
        assertEquals("sin erwartet 1 Argument, gefunden: 2 an Stelle 1", zuViel.getMessage());
        assertEquals(ParserFehler.SYNTAX, leer.getFehler());
        assertEquals(ParserFehler.SYNTAX, ohneFunktion.getFehler());
    }

    @Test
    void ggTUndKgV_ShouldWorkForIntegers()
    {
        // Act & Assert
        assertEquals(6.0, rechne("ggT(12;18)"), EPSILON);
        assertEquals(36.0, rechne("kgV(12;18)"), EPSILON);
        assertEquals(2.0, rechne("ggT(-4;6)"), EPSILON);
        assertEquals(5.0, rechne("ggT(0;5)"), EPSILON);
        assertEquals(0.0, rechne("kgV(0;5)"), EPSILON);
        assertEquals(1.0, rechne("ggT(17;13)"), EPSILON);
    }

    @Test
    void ggT_ShouldRejectDecimals()
    {
        // Act
        AusdruckParserException exception = fehler("ggT(2,5;5)");

        // Assert
        assertEquals(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, exception.getFehler());
        assertTrue(exception.getMessage().startsWith("ggT ist nur für ganze Zahlen definiert"));
    }

    @Test
    void benutzerfunktion_ShouldBeUsableAfterRegistration()
    {
        // Arrange
        FunktionsRegistry.registriereBenutzerfunktion("Quad", "x^2+1");
        FunktionsRegistry.registriereBenutzerfunktion("doppelt", "2quad(x)");

        // Act
        double einfach = rechne("quad(3)");
        double verschachtelt = rechne("doppelt(2)+QUAD(0)");

        // Assert
        assertEquals(10.0, einfach, EPSILON);
        assertEquals(11.0, verschachtelt, EPSILON);
        assertEquals(Map.of("doppelt", "2quad(x)", "quad", "x^2+1"), FunktionsRegistry.benutzerfunktionen());
    }

    @Test
    void benutzerfunktion_ShouldUseOwnArgumentInsteadOfOuterX()
    {
        // Arrange
        FunktionsRegistry.registriereBenutzerfunktion("plus", "x+10");

        // Act
        double wert = AusdruckParser.auswerten("plus(1)*x", 0.0, WinkelModus.RAD, Map.of("x", 2.0));

        // Assert
        assertEquals(22.0, wert, EPSILON);
    }

    @Test
    void benutzerfunktion_ShouldBeGoneAfterRemoval()
    {
        // Arrange
        FunktionsRegistry.registriereBenutzerfunktion("f", "x");

        // Act
        boolean entfernt = FunktionsRegistry.entferneBenutzerfunktion("F");

        // Assert
        assertTrue(entfernt);
        assertFalse(FunktionsRegistry.entferneBenutzerfunktion("f"));
        assertEquals(ParserFehler.UNBEKANNTE_FUNKTION, fehler("f(2)").getFehler());
    }

    @Test
    void benutzerfunktion_ShouldRejectInvalidNames()
    {
        // Act & Assert
        assertEquals("Der Funktionsname darf nur Buchstaben (a–z) enthalten.", registriereFehler("f1", "x").getMessage());
        assertEquals("„sin“ ist schon eingebaut und kann nicht überschrieben werden.", registriereFehler("SIN", "x").getMessage());
        assertEquals("„pi“ ist schon eingebaut und kann nicht überschrieben werden.", registriereFehler("pi", "x").getMessage());
        assertEquals("„x“ ist die Variable und kann kein Funktionsname sein.", registriereFehler("x", "x").getMessage());
        assertEquals("Bitte einen Funktionsnamen eingeben.", registriereFehler(" ", "x").getMessage());
        assertTrue(FunktionsRegistry.istReservierterName("nCr"));
        assertFalse(FunktionsRegistry.istReservierterName("f"));
    }

    @Test
    void benutzerfunktion_ShouldRejectRecursion()
    {
        // Arrange
        FunktionsRegistry.registriereBenutzerfunktion("g", "x+1");
        FunktionsRegistry.registriereBenutzerfunktion("h", "g(x)*2");

        // Act
        IllegalArgumentException selbst = registriereFehler("f", "f(x)+1");
        IllegalArgumentException kreis = registriereFehler("g", "h(x)");

        // Assert
        assertEquals("Eine Funktion darf sich nicht selbst aufrufen.", selbst.getMessage());
        assertEquals("Funktionskreis: „h“ ruft „g“ auf – Rekursion ist nicht erlaubt.", kreis.getMessage());
        assertEquals("x+1", FunktionsRegistry.benutzerfunktionen().get("g"));
    }

    @Test
    void benutzerfunktion_ShouldRejectInvalidExpressionButAllowDomainGaps()
    {
        // Act
        IllegalArgumentException unbekannt = registriereFehler("f", "x+y");
        IllegalArgumentException unvollstaendig = registriereFehler("f", "x+");
        IllegalArgumentException klammer = registriereFehler("f", "(x");
        IllegalArgumentException leer = registriereFehler("f", "");

        // Assert
        assertEquals("Unbekannter Name „y“ – als Variable ist nur x erlaubt an Stelle 3", unbekannt.getMessage());
        assertInstanceOf(AusdruckParserException.class, unvollstaendig);
        assertInstanceOf(AusdruckParserException.class, klammer);
        assertEquals("Bitte einen Funktionsausdruck eingeben.", leer.getMessage());
        assertDoesNotThrow(() -> FunktionsRegistry.validiereBenutzerfunktion("f", "ln(x-1)/(x-1)"));
        assertTrue(FunktionsRegistry.benutzerfunktionen().isEmpty());
    }

    @Test
    void benutzerfunktion_ShouldReportCallPositionWhenBodyFails()
    {
        // Arrange
        FunktionsRegistry.registriereBenutzerfunktion("kehr", "1/x");

        // Act
        AusdruckParserException exception = fehler("1+kehr(0)");

        // Assert
        assertEquals(ParserFehler.DIVISION_DURCH_NULL, exception.getFehler());
        assertEquals(2, exception.getPosition());
    }

    private static double rechne(String ausdruck)
    {
        return AusdruckParser.auswerten(ausdruck, 0.0, WinkelModus.DEG);
    }

    private static AusdruckParserException fehler(String ausdruck)
    {
        return assertThrows(AusdruckParserException.class, () -> rechne(ausdruck));
    }

    private static IllegalArgumentException registriereFehler(String name, String ausdruck)
    {
        return assertThrows(IllegalArgumentException.class,
                () -> FunktionsRegistry.registriereBenutzerfunktion(name, ausdruck));
    }
}
