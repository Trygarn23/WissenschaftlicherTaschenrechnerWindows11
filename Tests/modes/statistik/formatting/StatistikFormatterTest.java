package modes.statistik.formatting;

import modes.statistik.formatting.StatistikFormatter;
import modes.statistik.logic.StatistikRechnerService;
import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikErgebnis;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatistikFormatterTest
{
    private final StatistikRechnerService service = new StatistikRechnerService();
    private final StatistikFormatter formatter = new StatistikFormatter();

    @Test
    void formatiereErgebnis_ShouldContainMainSections()
    {
        StatistikErgebnis ergebnis = service.berechne(service.parseText("1\n2\n2\n4\n5\n8"));

        String text = formatter.formatiereErgebnis(ergebnis);

        assertTrue(text.contains("Kennzahlen"));
        assertTrue(text.contains("Mittelwert"));
        assertTrue(text.contains("Streuung"));
        assertTrue(text.contains("Regression"));
        assertTrue(text.contains("Ausreißer: 0 (bei 1,5×IQR)"));
    }

    @Test
    void formatiereAusreisser_ShouldCountAndListValues()
    {
        // Act
        String text = formatter.formatiereAusreisser(List.of(40.0, -12.5));

        // Assert
        assertEquals("Ausreißer: 2 (bei 1,5×IQR) → 40, -12,5", text);
    }

    @Test
    void deuteBestimmtheitsmass_ShouldExplainShareOfVariance()
    {
        // Act & Assert
        assertEquals("R² = 0,93 → die Gerade erklärt 93 % der Streuung",
                formatter.deuteBestimmtheitsmass(0.93, "Gerade"));
        assertEquals("R² = -0,2 → die Gerade erklärt 0 % der Streuung",
                formatter.deuteBestimmtheitsmass(-0.2, "Gerade"));
    }

    @Test
    void formatiereErgebnis_ShouldShowRegressionEquationWithMinusSign()
    {
        // Arrange: y = 2x − 3 exakt
        StatistikErgebnis ergebnis = service.berechne(List.of(
                new StatistikDatenpunkt(1, -1, 1),
                new StatistikDatenpunkt(2, 1, 1),
                new StatistikDatenpunkt(3, 3, 1)));

        // Act
        String text = formatter.formatiereErgebnis(ergebnis);

        // Assert
        assertTrue(text.contains("Linear: y = 2·x − 3"), text);
        assertTrue(text.contains("R² = 1 → die Gerade erklärt 100 % der Streuung"), text);
    }
}
