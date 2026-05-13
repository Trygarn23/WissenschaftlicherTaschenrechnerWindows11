import modes.statistik.logic.StatistikRechnerService;
import modes.statistik.model.StatistikDatenpunkt;
import modes.statistik.model.StatistikErgebnis;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StatistikRechnerServiceTest
{
    private final StatistikRechnerService service = new StatistikRechnerService();

    @Test
    void parseText_ShouldReadSingleValuesAsYValues()
    {
        List<StatistikDatenpunkt> daten = service.parseText("1\n2,5\n3e1");

        assertEquals(3, daten.size());
        assertEquals(1.0, daten.get(0).x(), 1e-10);
        assertEquals(1.0, daten.get(0).y(), 1e-10);
        assertEquals(2.5, daten.get(1).y(), 1e-10);
        assertEquals(30.0, daten.get(2).y(), 1e-10);
    }

    @Test
    void parseText_ShouldReadTabularValuesWithOptionalWeight()
    {
        List<StatistikDatenpunkt> daten = service.parseText("1;2;0,5\n2;4;1,5");

        assertEquals(2, daten.size());
        assertEquals(1.0, daten.get(0).x(), 1e-10);
        assertEquals(2.0, daten.get(0).y(), 1e-10);
        assertEquals(0.5, daten.get(0).gewicht(), 1e-10);
        assertEquals(1.5, daten.get(1).gewicht(), 1e-10);
    }

    @Test
    void berechne_ShouldCalculateCoreStatistics()
    {
        StatistikErgebnis ergebnis = service.berechne(service.parseText("1\n2\n2\n4\n5\n8"));

        assertEquals(6, ergebnis.getAnzahl());
        assertEquals(22.0, ergebnis.getSumme(), 1e-10);
        assertEquals(1.0, ergebnis.getMinimum(), 1e-10);
        assertEquals(8.0, ergebnis.getMaximum(), 1e-10);
        assertEquals(22.0 / 6.0, ergebnis.getMittelwert(), 1e-10);
        assertEquals(3.0, ergebnis.getMedian(), 1e-10);
        assertEquals(2.0, ergebnis.getQ1(), 1e-10);
        assertEquals(4.75, ergebnis.getQ3(), 1e-10);
        assertEquals(List.of(2.0), ergebnis.getModalwerte());
        assertEquals(7.0, ergebnis.getSpannweite(), 1e-10);
    }

    @Test
    void berechne_ShouldCalculateLinearRegression()
    {
        StatistikErgebnis ergebnis = service.berechne(List.of(
                new StatistikDatenpunkt(1, 2, 1),
                new StatistikDatenpunkt(2, 4, 1),
                new StatistikDatenpunkt(3, 6, 1)
        ));

        assertEquals(2.0, ergebnis.getLineareRegression().getB(), 1e-10);
        assertEquals(0.0, ergebnis.getLineareRegression().getC(), 1e-10);
        assertEquals(1.0, ergebnis.getLineareRegression().getBestimmtheitsmass(), 1e-10);
    }

    @Test
    void berechne_ShouldCalculateQuadraticRegression()
    {
        StatistikErgebnis ergebnis = service.berechne(List.of(
                new StatistikDatenpunkt(1, 1, 1),
                new StatistikDatenpunkt(2, 4, 1),
                new StatistikDatenpunkt(3, 9, 1),
                new StatistikDatenpunkt(4, 16, 1)
        ));

        assertEquals(1.0, ergebnis.getQuadratischeRegression().getA(), 1e-10);
        assertEquals(0.0, ergebnis.getQuadratischeRegression().getB(), 1e-10);
        assertEquals(0.0, ergebnis.getQuadratischeRegression().getC(), 1e-10);
        assertEquals(1.0, ergebnis.getQuadratischeRegression().getBestimmtheitsmass(), 1e-10);
    }

    @Test
    void berechne_ShouldRejectEmptyData()
    {
        assertThrows(IllegalArgumentException.class, () -> service.berechne(List.of()));
    }

    @Test
    void datenpunkt_ShouldRejectInvalidWeight()
    {
        assertThrows(IllegalArgumentException.class, () -> new StatistikDatenpunkt(1, 2, 0));
    }
}
