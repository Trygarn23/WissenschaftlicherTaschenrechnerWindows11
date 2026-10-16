package common.formatting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZahlenAnzeigeTest
{
    @Test
    void formatiere_ShouldUseGermanSeparators()
    {
        // Act
        String text = ZahlenAnzeige.formatiere(1234567.125);

        // Assert
        assertEquals("1.234.567,125", text);
    }

    @Test
    void formatiere_ShouldShowZero_WhenValueIsTiny()
    {
        // Act & Assert
        assertEquals("0", ZahlenAnzeige.formatiere(1e-12));
        assertEquals("0", ZahlenAnzeige.formatiere(-1e-12));
    }

    @Test
    void formatiere_ShouldShowNichtDefiniert_WhenValueIsNotFinite()
    {
        // Act & Assert
        assertEquals("nicht definiert", ZahlenAnzeige.formatiere(Double.NaN));
        assertEquals("nicht definiert", ZahlenAnzeige.formatiere(Double.POSITIVE_INFINITY));
    }

    @Test
    void formatiere_ShouldRoundToTenDecimals()
    {
        // Act
        String text = ZahlenAnzeige.formatiere(1.0 / 3);

        // Assert
        assertEquals("0,3333333333", text);
    }
}
