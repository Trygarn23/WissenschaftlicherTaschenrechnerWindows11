package common.formatting;

import common.formatting.ZahlenFormatModus;
import common.formatting.ZahlenFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZahlenFormatterTest
{
    private ZahlenFormatter formatter;

    @BeforeEach
    void setUp()
    {
        formatter = new ZahlenFormatter();
    }

    @Test
    void formatiereZahl_ShouldUseScientificNotation_WhenNumberIsVeryLarge()
    {
        // Act
        String actual = formatter.formatiereZahl(1_200_000_000_000.0);

        // Assert
        assertEquals("1,2e12", actual);
    }

    @Test
    void formatiereZahl_ShouldUseScientificNotation_WhenNumberIsVerySmall()
    {
        // Act
        String actual = formatter.formatiereZahl(0.00000000012);

        // Assert
        assertEquals("1,2e-10", actual);
    }

    @Test
    void formatiereLiveAnzeige_ShouldFormatNumbersInsideLongerExpression()
    {
        // Act
        String actual = formatter.formatiereLiveAnzeige("1000+sin(2000,5)*3000000");

        // Assert
        assertEquals("1.000+sin(2.000,5)*3.000.000", actual);
    }

    @Test
    void formatiereLiveAnzeige_ShouldKeepScientificInputReadable_WhenExpressionContainsExponent()
    {
        // Act
        String actual = formatter.formatiereLiveAnzeige("1,2e-5+1000");

        // Assert
        assertEquals("1,2e-5+1.000", actual);
    }

    @Test
    void formatiereZahl_ShouldRespectPrecisionSetting()
    {
        // Arrange
        formatter.setNachkommastellen(2);

        // Act
        String actual = formatter.formatiereZahl(1.23456);

        // Assert
        assertEquals("1,23", actual);
    }

    @Test
    void formatiereZahl_ShouldRespectScientificFormatSetting()
    {
        // Arrange
        formatter.setFormatModus(ZahlenFormatModus.WISSENSCHAFTLICH);

        // Act
        String actual = formatter.formatiereZahl(1234.0);

        // Assert
        assertEquals("1,234e3", actual);
    }

    @Test
    void volleGenauigkeit_ShouldShowAllDoubleDigits_WhenPrecisionSettingIsLow()
    {
        // Arrange
        formatter.setNachkommastellen(2);

        // Act
        String drittel = formatter.volleGenauigkeit(1.0 / 3);
        String summe = formatter.volleGenauigkeit(0.1 + 0.2);
        String gross = formatter.volleGenauigkeit(1.25e20);

        // Assert
        assertEquals("0,3333333333333333", drittel);
        assertEquals("0,30000000000000004", summe);
        assertEquals("1,25e20", gross);
    }

    @Test
    void anzeigeIndex_ShouldCountThousandsSeparators_WhenNumberIsGrouped()
    {
        // Arrange: intern „1234567+8“, angezeigt „1.234.567+8“
        String ausdruck = "1234567+8";

        // Act & Assert
        assertEquals(0, formatter.anzeigeIndex(ausdruck, 0));
        assertEquals(2, formatter.anzeigeIndex(ausdruck, 1));
        assertEquals(9, formatter.anzeigeIndex(ausdruck, 7));
        assertEquals(11, formatter.anzeigeIndex(ausdruck, 9));
    }

    @Test
    void internerIndex_ShouldBeInverseOfAnzeigeIndex_WhenClickedOnDigitOrSeparator()
    {
        // Arrange
        String ausdruck = "1234567+8";

        // Act & Assert
        assertEquals(1, formatter.internerIndex(ausdruck, 1));
        assertEquals(1, formatter.internerIndex(ausdruck, 2));
        assertEquals(7, formatter.internerIndex(ausdruck, 9));
        assertEquals(9, formatter.internerIndex(ausdruck, 99));
    }

    @Test
    void anzeigeIndex_ShouldKeepIndices_WhenDecimalPartIsNotGrouped()
    {
        // Arrange: Nachkommastellen bekommen keine Punkte
        String ausdruck = "0,12345*2";

        // Act & Assert
        assertEquals(7, formatter.anzeigeIndex(ausdruck, 7));
        assertEquals(7, formatter.internerIndex(ausdruck, 7));
    }
}
