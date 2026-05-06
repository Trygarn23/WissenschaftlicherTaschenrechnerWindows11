import common.logic.RechnerService;
import common.state.WinkelModus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RechnerServiceTest
{
    private RechnerService rechner;

    @BeforeEach
    void setUp()
    {
        rechner = new RechnerService();
    }

    @Test
    void neuerRechner_ShouldStartWithZeroDisplayAndEmptyHistory()
    {
        // Arrange

        // Act
        String liveAnzeige = rechner.formatiereLiveAnzeige();
        String verlauf = rechner.getVerlauf();

        // Assert
        assertEquals("0", liveAnzeige);
        assertEquals("", verlauf);
    }

    @Test
    void eingabeZahl_ShouldBuildExpressionAndFormatThousands_WhenDigitsAreTyped()
    {
        // Arrange

        // Act
        rechner.eingabeZahl("1");
        rechner.eingabeZahl("0");
        rechner.eingabeZahl("0");
        rechner.eingabeZahl("0");

        // Assert
        assertEquals("1.000", rechner.formatiereLiveAnzeige());
    }

    @Test
    void eingabeKomma_ShouldPreventSecondComma_WhenNumberAlreadyHasComma()
    {
        // Arrange
        rechner.eingabeZahl("1");
        rechner.eingabeKomma();
        rechner.eingabeZahl("2");

        // Act
        rechner.eingabeKomma();

        // Assert
        assertEquals("1,2", rechner.formatiereLiveAnzeige());
    }

    @Test
    void berechne_ShouldCalculateExpressionAndUpdateHistory_WhenExpressionIsValid()
    {
        // Arrange
        rechner.eingabeZahl("2");
        rechner.operatorSetzen("+");
        rechner.eingabeZahl("3");

        // Act
        String result = rechner.berechne();

        // Assert
        assertEquals("5", result);
        assertEquals("2+3 = 5", rechner.getVerlauf());
        assertEquals("5", rechner.formatiereLiveAnzeige());
    }

    @Test
    void berechne_ShouldNormalizeMultiplicationSymbol_WhenUiOperatorIsUsed()
    {
        // Arrange
        rechner.eingabeZahl("6");
        rechner.operatorSetzen("×");
        rechner.eingabeZahl("7");

        // Act
        String result = rechner.berechne();

        // Assert
        assertEquals("42", result);
        assertEquals("6*7 = 42", rechner.getVerlauf());
    }

    @Test
    void berechne_ShouldDisplayScientificNotation_WhenResultIsVeryLargeOrVerySmall()
    {
        // Arrange
        RechnerService large = new RechnerService();
        RechnerService small = new RechnerService();

        large.setzeAusdruckAusZwischenablage("1200000000000");
        small.setzeAusdruckAusZwischenablage("1,2e-10");

        // Act
        String largeResult = large.berechne();
        String smallResult = small.berechne();

        // Assert
        assertEquals("1,2e12", largeResult);
        assertEquals("1,2e-10", smallResult);
    }

    @Test
    void eingabeZahl_ShouldStartNewExpression_WhenDigitIsTypedAfterEquals()
    {
        // Arrange
        rechner.eingabeZahl("2");
        rechner.operatorSetzen("+");
        rechner.eingabeZahl("3");
        rechner.berechne();

        // Act
        rechner.eingabeZahl("7");

        // Assert
        assertEquals("7", rechner.formatiereLiveAnzeige());
    }

    @Test
    void ans_ShouldInsertPreviousAnswer_WhenUsedAfterCalculation()
    {
        // Arrange
        rechner.eingabeZahl("2");
        rechner.operatorSetzen("+");
        rechner.eingabeZahl("3");
        rechner.berechne();

        // Act
        rechner.operatorSetzen("*");
        rechner.ans();
        String result = rechner.berechne();

        // Assert
        assertEquals("25", result);
    }

    @Test
    void wechselVorzeichen_ShouldStartNegativeNumber_WhenExpressionIsEmpty()
    {
        // Arrange

        // Act
        String expression = rechner.wechselVorzeichen();
        rechner.eingabeZahl("5");

        // Assert
        assertEquals("-", expression);
        assertEquals("-5", rechner.formatiereLiveAnzeige());
    }

    @Test
    void commonOperations_ShouldCalculateSquareSqrtReciprocalPowerAndPercent()
    {
        // Arrange
        RechnerService square = new RechnerService();
        RechnerService sqrt = new RechnerService();
        RechnerService reciprocal = new RechnerService();
        RechnerService power = new RechnerService();
        RechnerService percent = new RechnerService();

        // Act
        square.eingabeZahl("5");
        square.quadriere();
        String squareResult = square.berechne();

        sqrt.eingabeZahl("9");
        sqrt.wurzel();
        String sqrtResult = sqrt.berechne();

        reciprocal.eingabeZahl("4");
        reciprocal.reziprok();
        String reciprocalResult = reciprocal.berechne();

        power.eingabeZahl("2");
        power.potenz();
        power.eingabeZahl("3");
        String powerResult = power.berechne();

        percent.eingabeZahl("5");
        percent.eingabeZahl("0");
        String percentExpression = percent.prozent();

        // Assert
        assertEquals("25", squareResult);
        assertEquals("3", sqrtResult);
        assertEquals("0,25", reciprocalResult);
        assertEquals("8", powerResult);
        assertEquals("0,5", percentExpression);
        assertEquals("0,5", percent.formatiereLiveAnzeige());
    }

    @Test
    void berechne_ShouldReturnFehler_WhenExpressionIsInvalid()
    {
        // Arrange
        rechner.eingabeZahl("9");
        rechner.wurzel();
        rechner.wechselVorzeichen();

        // Act
        rechner.allesLoeschen();
        rechner.eingabeZahl("1");
        rechner.operatorSetzen("/");
        rechner.eingabeZahl("0");
        String result = rechner.berechne();

        // Assert
        assertEquals("Fehler", result);
        assertEquals("0", rechner.formatiereLiveAnzeige());
    }

    @Test
    void speicher_ShouldAddRecallAndClearValue_WhenMemoryMethodsAreUsed()
    {
        // Arrange
        rechner.eingabeZahl("1");
        rechner.eingabeZahl("0");

        // Act
        String memoryAfterAdd = rechner.speicherAddieren();
        rechner.allesLoeschen();
        rechner.speicherAbrufen();
        String recalled = rechner.formatiereLiveAnzeige();
        String memoryAfterClear = rechner.speicherLoeschen();
        rechner.allesLoeschen();
        rechner.speicherAbrufen();
        String recalledAfterClear = rechner.formatiereLiveAnzeige();

        // Assert
        assertEquals("10", memoryAfterAdd);
        assertEquals("10", recalled);
        assertEquals("0", memoryAfterClear);
        assertEquals("0", recalledAfterClear);
    }

    @Test
    void hatSpeicherWert_ShouldReflectMemoryState_WhenMemoryChanges()
    {
        // Arrange
        rechner.eingabeZahl("5");

        // Act & Assert
        assertFalse(rechner.hatSpeicherWert());
        rechner.speicherAddieren();
        assertTrue(rechner.hatSpeicherWert());
        rechner.speicherLoeschen();
        assertFalse(rechner.hatSpeicherWert());
    }

    @Test
    void setzeAusdruckAusZwischenablage_ShouldReplaceCurrentExpression_WhenTextIsPasted()
    {
        // Arrange
        rechner.eingabeZahl("9");

        // Act
        rechner.setzeAusdruckAusZwischenablage(" 2 × (3 + 4) ");
        String result = rechner.berechne();

        // Assert
        assertEquals("14", result);
        assertEquals("14", rechner.formatiereLiveAnzeige());
    }

    @Test
    void setzeAusdruckAusZwischenablage_ShouldIgnoreBlankText_WhenTextIsBlank()
    {
        // Arrange
        rechner.eingabeZahl("9");

        // Act
        rechner.setzeAusdruckAusZwischenablage("   ");

        // Assert
        assertEquals("9", rechner.formatiereLiveAnzeige());
    }

    @Test
    void setzeAusdruckAusZwischenablage_ShouldAcceptGermanFormattedNumber_WhenTextUsesThousandsAndComma()
    {
        // Act
        rechner.setzeAusdruckAusZwischenablage(" 1.234,5 + 5 ");
        String result = rechner.berechne();

        // Assert
        assertEquals("1.239,5", result);
    }

    @Test
    void setzeAusdruckAusZwischenablage_ShouldAcceptScientificNotation_WhenTextUsesExponent()
    {
        // Act
        rechner.setzeAusdruckAusZwischenablage("1,2e-5");
        String result = rechner.berechne();

        // Assert
        assertEquals("0,000012", result);
    }

    @Test
    void setzeAusdruckAusZwischenablage_ShouldReturnFehler_WhenTextIsInvalidExpression()
    {
        // Act
        rechner.setzeAusdruckAusZwischenablage("abc");
        String result = rechner.berechne();

        // Assert
        assertEquals("Fehler", result);
    }

    @Test
    void winkelModusUmschalten_ShouldToggleBetweenDegAndRad()
    {
        // Arrange
        WinkelModus initialMode = rechner.getWinkelModus();

        // Act
        rechner.winkelModusUmschalten();
        WinkelModus afterFirstToggle = rechner.getWinkelModus();
        rechner.winkelModusUmschalten();
        WinkelModus afterSecondToggle = rechner.getWinkelModus();

        // Assert
        assertEquals(WinkelModus.DEG, initialMode);
        assertEquals(WinkelModus.RAD, afterFirstToggle);
        assertEquals(WinkelModus.DEG, afterSecondToggle);
    }

    @Test
    void setzeAusdruckAusVerlaufErgebnis_ShouldAcceptFormattedHistoryResult()
    {
        // Arrange
        String historyResult = "  1.234,5  ";

        // Act
        rechner.setzeAusdruckAusVerlaufErgebnis(historyResult);
        String liveAnzeige = rechner.formatiereLiveAnzeige();
        String result = rechner.berechne();

        // Assert
        assertEquals("1.234,5", liveAnzeige);
        assertEquals("1.234,5", result);
    }
}
