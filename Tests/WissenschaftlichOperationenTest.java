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
}
