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
}
