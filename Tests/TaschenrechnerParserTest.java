import common.parser.AusdruckParser;
import common.parser.AusdruckParserException;
import common.parser.ParserFehler;
import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TaschenrechnerParserTest
{
    private static final double EPSILON = 1e-10;

    @ParameterizedTest(name = "{0} = {1}")
    @MethodSource("basicExpressions")
    void auswerten_ShouldEvaluateBasicExpression_WhenExpressionIsValid(String expression, double expected)
    {
        // Arrange
        WinkelModus winkelModus = WinkelModus.DEG;

        // Act
        double actual = AusdruckParser.auswerten(expression, 0.0, winkelModus);

        // Assert
        assertEquals(expected, actual, EPSILON);
    }

    static Stream<Object[]> basicExpressions()
    {
        return Stream.of(
                new Object[]{"1+2", 3.0},
                new Object[]{"2*3+4", 10.0},
                new Object[]{"2*(3+4)", 14.0},
                new Object[]{"10/4", 2.5},
                new Object[]{"2^3^2", 512.0},
                new Object[]{"-3+5", 2.0},
                new Object[]{"-(3+5)", -8.0},
                new Object[]{"5--3", 8.0},
                new Object[]{"5*-3", -15.0},
                new Object[]{"10%4", 2.0}
        );
    }

    @Test
    void auswerten_ShouldSupportImplicitMultiplication_WhenNumberIsBeforeParenthesis()
    {
        // Arrange
        String expression = "2(3+4)";

        // Act
        double actual = AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(14.0, actual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportImplicitMultiplication_WhenNumberIsBeforeConstant()
    {
        // Arrange
        String expression = "2pi";

        // Act
        double actual = AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(2.0 * Math.PI, actual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportImplicitMultiplication_WithFunctionConstantAndParentheses()
    {
        // Act
        double functionActual = AusdruckParser.auswerten("2sin(30)", 0.0, WinkelModus.DEG);
        double constantGroupActual = AusdruckParser.auswerten("pi(2+1)", 0.0, WinkelModus.DEG);
        double groupedActual = AusdruckParser.auswerten("(1+2)(3+4)", 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(1.0, functionActual, 1e-10);
        assertEquals(3.0 * Math.PI, constantGroupActual, 1e-10);
        assertEquals(21.0, groupedActual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportConstants_WhenPiAndEAreUsed()
    {
        // Arrange
        String piExpression = "pi";
        String eExpression = "e";

        // Act
        double piActual = AusdruckParser.auswerten(piExpression, 0.0, WinkelModus.DEG);
        double eActual = AusdruckParser.auswerten(eExpression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(Math.PI, piActual, EPSILON);
        assertEquals(Math.E, eActual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportUnicodePi_WhenExpressionUsesPiSymbol()
    {
        // Arrange
        String expression = "2π";

        // Act
        double actual = AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(2.0 * Math.PI, actual, EPSILON);
    }

    @Test
    void auswerten_ShouldUsePreviousAnswer_WhenAnsIdentifierIsUsed()
    {
        // Arrange
        String expression = "ans+2";
        double previousAnswer = 5.0;

        // Act
        double actual = AusdruckParser.auswerten(expression, previousAnswer, WinkelModus.DEG);

        // Assert
        assertEquals(7.0, actual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportCommaAndDotDecimals_WhenDecimalSeparatorsAreUsed()
    {
        // Arrange
        String commaExpression = "3,5";
        String dotExpression = "3.5";

        // Act
        double commaActual = AusdruckParser.auswerten(commaExpression, 0.0, WinkelModus.DEG);
        double dotActual = AusdruckParser.auswerten(dotExpression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(3.5, commaActual, EPSILON);
        assertEquals(3.5, dotActual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportScientificNotation_WhenExponentIsUsed()
    {
        // Arrange
        String commaExpression = "1,2e-5";
        String dotExpression = "1.2E3";

        // Act
        double commaActual = AusdruckParser.auswerten(commaExpression, 0.0, WinkelModus.DEG);
        double dotActual = AusdruckParser.auswerten(dotExpression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(0.000012, commaActual, EPSILON);
        assertEquals(1200.0, dotActual, EPSILON);
    }

    @Test
    void auswerten_ShouldKeepScientificNotationStableInsideLongerExpression()
    {
        // Act
        double actual = AusdruckParser.auswerten("1e3+2,5e-1", 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(1000.25, actual, EPSILON);
    }

    @Test
    void auswerten_ShouldNormalizeUnicodeOperators()
    {
        // Act
        double actual = AusdruckParser.auswerten("6\u00d77\u22122\u00f72", 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(41.0, actual, EPSILON);
    }

    @Test
    void auswerten_ShouldSupportFractionsWithParenthesizedNumeratorOrDenominator()
    {
        // Act
        double parenthesizedBoth = AusdruckParser.auswerten("(2+4)/(1+2)", 0.0, WinkelModus.DEG);
        double denominatorExpression = AusdruckParser.auswerten("6/(1+2)", 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(2.0, parenthesizedBoth, EPSILON);
        assertEquals(2.0, denominatorExpression, EPSILON);
    }

    @Test
    void auswerten_ShouldAddTrailingZero_WhenExpressionEndsWithSeparator()
    {
        // Arrange
        String commaExpression = "1,";
        String dotExpression = "1.";

        // Act
        double commaActual = AusdruckParser.auswerten(commaExpression, 0.0, WinkelModus.DEG);
        double dotActual = AusdruckParser.auswerten(dotExpression, 0.0, WinkelModus.DEG);

        // Assert
        assertEquals(1.0, commaActual, EPSILON);
        assertEquals(1.0, dotActual, EPSILON);
    }

    @Test
    void auswerten_ShouldEvaluateTrigonometryInDegrees_WhenWinkelModusIsDeg()
    {
        // Arrange
        WinkelModus winkelModus = WinkelModus.DEG;

        // Act
        double sinActual = AusdruckParser.auswerten("sin(90)", 0.0, winkelModus);
        double cosActual = AusdruckParser.auswerten("cos(90)", 0.0, winkelModus);
        double tanActual = AusdruckParser.auswerten("tan(45)", 0.0, winkelModus);

        // Assert
        assertEquals(1.0, sinActual, 1e-9);
        assertEquals(0.0, cosActual, 1e-9);
        assertEquals(1.0, tanActual, 1e-9);
    }

    @Test
    void auswerten_ShouldEvaluateTrigonometryInRadians_WhenWinkelModusIsRad()
    {
        // Arrange
        WinkelModus winkelModus = WinkelModus.RAD;

        // Act
        double sinActual = AusdruckParser.auswerten("sin(pi/2)", 0.0, winkelModus);
        double cosActual = AusdruckParser.auswerten("cos(pi/2)", 0.0, winkelModus);

        // Assert
        assertEquals(1.0, sinActual, 1e-9);
        assertEquals(0.0, cosActual, 1e-9);
    }

    @Test
    void auswerten_ShouldEvaluateInverseTrigonometry_WhenInputIsInDomain()
    {
        // Arrange
        WinkelModus winkelModus = WinkelModus.DEG;

        // Act
        double asinActual = AusdruckParser.auswerten("asin(1)", 0.0, winkelModus);
        double acosActual = AusdruckParser.auswerten("acos(0)", 0.0, winkelModus);
        double atanActual = AusdruckParser.auswerten("atan(1)", 0.0, winkelModus);

        // Assert
        assertEquals(90.0, asinActual, 1e-9);
        assertEquals(90.0, acosActual, 1e-9);
        assertEquals(45.0, atanActual, 1e-9);
    }

    @Test
    void auswerten_ShouldEvaluateInverseTrigonometryInRadians_WhenWinkelModusIsRad()
    {
        // Act
        double asinActual = AusdruckParser.auswerten("asin(1)", 0.0, WinkelModus.RAD);
        double acosActual = AusdruckParser.auswerten("acos(0)", 0.0, WinkelModus.RAD);
        double atanActual = AusdruckParser.auswerten("atan(1)", 0.0, WinkelModus.RAD);

        // Assert
        assertEquals(Math.PI / 2.0, asinActual, 1e-9);
        assertEquals(Math.PI / 2.0, acosActual, 1e-9);
        assertEquals(Math.PI / 4.0, atanActual, 1e-9);
    }

    @Test
    void auswerten_ShouldEvaluateCommonFunctions_WhenFunctionsAreValid()
    {
        // Arrange
        WinkelModus winkelModus = WinkelModus.DEG;

        // Act
        double lnActual = AusdruckParser.auswerten("ln(2)", 0.0, winkelModus);
        double logActual = AusdruckParser.auswerten("log(1000)", 0.0, winkelModus);
        double sqrtActual = AusdruckParser.auswerten("sqrt(9)", 0.0, winkelModus);
        double absActual = AusdruckParser.auswerten("abs(-5)", 0.0, winkelModus);
        double expActual = AusdruckParser.auswerten("exp(2)", 0.0, winkelModus);

        // Assert
        assertEquals(Math.log(2.0), lnActual, EPSILON);
        assertEquals(3.0, logActual, EPSILON);
        assertEquals(3.0, sqrtActual, EPSILON);
        assertEquals(5.0, absActual, EPSILON);
        assertEquals(Math.exp(2.0), expActual, EPSILON);
    }

    @Test
    void auswerten_ShouldEvaluateHyperbolicFunctionsIndependentFromAngleMode()
    {
        // Arrange
        String expression = "sinh(1)+cosh(1)+tanh(1)";

        // Act
        double degActual = AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG);
        double radActual = AusdruckParser.auswerten(expression, 0.0, WinkelModus.RAD);

        // Assert
        assertEquals(radActual, degActual, EPSILON);
        assertEquals(Math.sinh(1.0) + Math.cosh(1.0) + Math.tanh(1.0), degActual, EPSILON);
    }

    @Test
    void auswerten_ShouldEvaluateRoundingFunctions_WhenInputHasDecimals()
    {
        // Arrange
        WinkelModus winkelModus = WinkelModus.DEG;

        // Act
        double floorActual = AusdruckParser.auswerten("floor(2,9)", 0.0, winkelModus);
        double ceilActual = AusdruckParser.auswerten("ceil(2,1)", 0.0, winkelModus);
        double roundActual = AusdruckParser.auswerten("round(2,5)", 0.0, winkelModus);

        // Assert
        assertEquals(2.0, floorActual, EPSILON);
        assertEquals(3.0, ceilActual, EPSILON);
        assertEquals(3.0, roundActual, EPSILON);
    }

    @Test
    void auswerten_ShouldReturnRandomBetweenZeroAndOne_WhenRandIsUsed()
    {
        // Arrange
        String expression = "rand()";

        // Act
        double actual = AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG);

        // Assert
        assertTrue(actual >= 0.0 && actual < 1.0);
    }

    @Test
    void auswerten_ShouldThrowException_WhenParenthesesAreUnbalanced()
    {
        // Arrange
        String missingClosingParenthesis = "(1+2";
        String missingOpeningParenthesis = "1+2)";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> AusdruckParser.auswerten(missingClosingParenthesis, 0.0, WinkelModus.DEG));
        assertThrows(IllegalArgumentException.class,
                () -> AusdruckParser.auswerten(missingOpeningParenthesis, 0.0, WinkelModus.DEG));
    }

    @Test
    void auswerten_ShouldThrowException_WhenUnknownIdentifierIsUsed()
    {
        // Arrange
        String unknownVariable = "1+a";
        String unknownFunction = "foo(2)";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> AusdruckParser.auswerten(unknownVariable, 0.0, WinkelModus.DEG));
        assertThrows(IllegalArgumentException.class,
                () -> AusdruckParser.auswerten(unknownFunction, 0.0, WinkelModus.DEG));
    }

    @Test
    void auswerten_ShouldExposeSpecificParserError_WhenUnknownIdentifierIsUsed()
    {
        AusdruckParserException variableException = assertThrows(AusdruckParserException.class,
                () -> AusdruckParser.auswerten("1+a", 0.0, WinkelModus.DEG));
        AusdruckParserException functionException = assertThrows(AusdruckParserException.class,
                () -> AusdruckParser.auswerten("foo(2)", 0.0, WinkelModus.DEG));

        assertEquals(ParserFehler.UNBEKANNTE_FUNKTION, variableException.getFehler());
        assertEquals(ParserFehler.UNBEKANNTE_FUNKTION, functionException.getFehler());
    }

    @Test
    void auswerten_ShouldThrowException_WhenFunctionDomainIsInvalid()
    {
        // Arrange
        String sqrtNegative = "sqrt(-1)";
        String lnZero = "ln(0)";
        String logNegative = "log(-1)";
        String asinOutOfDomain = "asin(2)";
        String acosOutOfDomain = "acos(-2)";
        String tanUndefined = "tan(90)";

        // Act & Assert
        assertFunctionDomainError(sqrtNegative);
        assertFunctionDomainError(lnZero);
        assertFunctionDomainError(logNegative);
        assertFunctionDomainError(asinOutOfDomain);
        assertFunctionDomainError(acosOutOfDomain);
        assertFunctionDomainError(tanUndefined);
    }

    @Test
    void auswerten_ShouldExposeSpecificParserError_WhenDivisionByZeroIsUsed()
    {
        // Arrange
        String expression = "1/0";

        // Act
        AusdruckParserException exception = assertThrows(AusdruckParserException.class,
                () -> AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG));

        // Assert
        assertEquals(ParserFehler.DIVISION_DURCH_NULL, exception.getFehler());
    }

    private void assertFunctionDomainError(String expression)
    {
        AusdruckParserException exception = assertThrows(AusdruckParserException.class,
                () -> AusdruckParser.auswerten(expression, 0.0, WinkelModus.DEG));
        assertEquals(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, exception.getFehler());
    }
}
