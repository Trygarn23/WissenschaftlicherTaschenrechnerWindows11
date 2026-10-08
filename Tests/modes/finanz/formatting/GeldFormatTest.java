package modes.finanz.formatting;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GeldFormatTest
{
    @Test
    void euro_ShouldUseGermanSeparatorsAndTwoDecimals()
    {
        // Act & Assert
        assertEquals("1.234,56 €", GeldFormat.euro(new BigDecimal("1234.56")));
        assertEquals("1.234.567,50 €", GeldFormat.euro(new BigDecimal("1234567.5")));
        assertEquals("0,00 €", GeldFormat.euro(BigDecimal.ZERO));
        assertEquals("-5,00 €", GeldFormat.euro(new BigDecimal("-5")));
    }

    @Test
    void euro_ShouldRoundHalfUp()
    {
        // Act & Assert
        assertEquals("0,01 €", GeldFormat.euro(new BigDecimal("0.005")));
        assertEquals("2,35 €", GeldFormat.euro(new BigDecimal("2.345")));
    }

    @Test
    void prozentUndZahl_ShouldDropUnneededDecimals()
    {
        // Act & Assert
        assertEquals("19 %", GeldFormat.prozent(new BigDecimal("19.00")));
        assertEquals("33,33 %", GeldFormat.prozent(new BigDecimal("33.33")));
        assertEquals("0,19", GeldFormat.zahl(new BigDecimal("0.19")));
        assertEquals("1,0025", GeldFormat.zahl(new BigDecimal("1.0025")));
    }
}
