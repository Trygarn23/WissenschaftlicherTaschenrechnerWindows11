import modes.statistik.formatting.StatistikFormatter;
import modes.statistik.logic.StatistikRechnerService;
import modes.statistik.model.StatistikErgebnis;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatistikFormatterTest
{
    @Test
    void formatiereErgebnis_ShouldContainMainSections()
    {
        StatistikRechnerService service = new StatistikRechnerService();
        StatistikErgebnis ergebnis = service.berechne(service.parseText("1\n2\n2\n4\n5\n8"));

        String text = new StatistikFormatter().formatiereErgebnis(ergebnis);

        assertTrue(text.contains("Kennzahlen"));
        assertTrue(text.contains("Mittelwert"));
        assertTrue(text.contains("Streuung"));
        assertTrue(text.contains("Regression"));
    }
}
