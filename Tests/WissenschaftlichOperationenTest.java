import common.logic.RechnerService;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WissenschaftlichOperationenTest
{
    private RechnerService rechner;
    private WissenschaftlichOperationen wissenschaftlich;

    @BeforeEach
    void setUp()
    {
        rechner = new RechnerService();
        wissenschaftlich = new WissenschaftlichOperationen(rechner.getAusdruckEditor());
    }

    @Test
    void sin_ShouldWrapLastTermAndCalculate_WhenAngleIsInDegrees()
    {
        // Arrange
        rechner.eingabeZahl("9");
        rechner.eingabeZahl("0");

        // Act
        String expression = wissenschaftlich.sin();
        String result = rechner.berechne();

        // Assert
        assertEquals("sin(90)", expression);
        assertEquals("1", result);
    }

    @Test
    void inverseTrigonometry_ShouldCreateParserFunctions_WhenButtonsAreUsed()
    {
        // Arrange
        rechner.eingabeZahl("1");

        // Act
        String expression = wissenschaftlich.arcsin();
        String result = rechner.berechne();

        // Assert
        assertEquals("asin(1)", expression);
        assertEquals("90", result);
    }

    @Test
    void logarithmAndExp_ShouldCreateParserFunctions_WhenButtonsAreUsed()
    {
        // Arrange
        RechnerService logRechner = new RechnerService();
        WissenschaftlichOperationen logOps = new WissenschaftlichOperationen(logRechner.getAusdruckEditor());
        RechnerService expRechner = new RechnerService();
        WissenschaftlichOperationen expOps = new WissenschaftlichOperationen(expRechner.getAusdruckEditor());

        // Act
        logRechner.eingabeZahl("1");
        logRechner.eingabeZahl("0");
        logRechner.eingabeZahl("0");
        logOps.log();
        String logResult = logRechner.berechne();

        expRechner.eingabeZahl("0");
        expOps.exp();
        String expResult = expRechner.berechne();

        // Assert
        assertEquals("2", logResult);
        assertEquals("1", expResult);
    }

    @Test
    void constants_ShouldInsertPiAndE_WhenButtonsAreUsed()
    {
        // Arrange
        RechnerService piRechner = new RechnerService();
        WissenschaftlichOperationen piOps = new WissenschaftlichOperationen(piRechner.getAusdruckEditor());
        RechnerService eRechner = new RechnerService();
        WissenschaftlichOperationen eOps = new WissenschaftlichOperationen(eRechner.getAusdruckEditor());

        // Act
        piOps.pi();
        String piResult = piRechner.berechne();
        eOps.e();
        String eResult = eRechner.berechne();

        // Assert
        assertFalse(piResult.isBlank());
        assertTrue(piResult.startsWith("3,14159"));
        assertFalse(eResult.isBlank());
        assertTrue(eResult.startsWith("2,71828"));
    }

    @Test
    void zehnHoch_ShouldWrapLastTerm_WhenButtonIsUsed()
    {
        // Arrange
        rechner.eingabeZahl("2");

        // Act
        String expression = wissenschaftlich.zehnHoch();
        String result = rechner.berechne();

        // Assert
        assertEquals("10^(2)", expression);
        assertEquals("100", result);
    }

    @Test
    void zehnHoch_ShouldInsertPrefix_WhenExpressionIsEmpty()
    {
        // Act
        String expression = wissenschaftlich.zehnHoch();

        // Assert
        assertEquals("10^(", expression);
    }

    @Test
    void zehnHoch_ShouldWrapLastParenthesizedTerm_WhenButtonIsUsed()
    {
        // Arrange
        rechner.klammerAuf();
        rechner.eingabeZahl("2");
        rechner.operatorSetzen("+");
        rechner.eingabeZahl("1");
        rechner.klammerZu();

        // Act
        String expression = wissenschaftlich.zehnHoch();
        String result = rechner.berechne();

        // Assert
        assertEquals("10^((2+1))", expression);
        assertEquals("1.000", result);
    }

    @Test
    void lnAndLog_ShouldWrapLastTerm_WhenButtonsAreUsed()
    {
        // Arrange
        RechnerService lnRechner = new RechnerService();
        WissenschaftlichOperationen lnOps = new WissenschaftlichOperationen(lnRechner.getAusdruckEditor());
        RechnerService logRechner = new RechnerService();
        WissenschaftlichOperationen logOps = new WissenschaftlichOperationen(logRechner.getAusdruckEditor());

        // Act
        lnRechner.eingabeZahl("1");
        String lnExpression = lnOps.ln();
        String lnResult = lnRechner.berechne();

        logRechner.eingabeZahl("1");
        logRechner.eingabeZahl("0");
        String logExpression = logOps.log();
        String logResult = logRechner.berechne();

        // Assert
        assertEquals("ln(1)", lnExpression);
        assertEquals("0", lnResult);
        assertEquals("log(10)", logExpression);
        assertEquals("1", logResult);
    }

    @Test
    void fakultaet_ShouldDelegateToExpressionEditor_WhenLastNumberIsValid()
    {
        // Arrange
        rechner.eingabeZahl("5");

        // Act
        String expression = wissenschaftlich.fakultaet();

        // Assert
        assertEquals("120", expression);
        assertEquals("120", rechner.formatiereLiveAnzeige());
    }

    @Test
    void fakultaet_ShouldReturnFehler_WhenInputIsNegativeOrDecimalOrTooLarge()
    {
        // Arrange
        RechnerService negative = new RechnerService();
        WissenschaftlichOperationen negativeOps = new WissenschaftlichOperationen(negative.getAusdruckEditor());
        RechnerService decimal = new RechnerService();
        WissenschaftlichOperationen decimalOps = new WissenschaftlichOperationen(decimal.getAusdruckEditor());
        RechnerService tooLarge = new RechnerService();
        WissenschaftlichOperationen tooLargeOps = new WissenschaftlichOperationen(tooLarge.getAusdruckEditor());

        // Act
        negative.wechselVorzeichen();
        negative.eingabeZahl("3");
        String negativeResult = negativeOps.fakultaet();

        decimal.eingabeZahl("3");
        decimal.eingabeKomma();
        decimal.eingabeZahl("5");
        String decimalResult = decimalOps.fakultaet();

        tooLarge.eingabeZahl("5");
        tooLarge.eingabeZahl("0");
        tooLarge.eingabeZahl("0");
        tooLarge.eingabeZahl("1");
        String tooLargeResult = tooLargeOps.fakultaet();

        // Assert
        assertEquals("Fehler", negativeResult);
        assertEquals("Fehler", decimalResult);
        assertEquals("Fehler", tooLargeResult);
    }
}
