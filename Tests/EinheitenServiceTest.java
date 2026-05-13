import common.units.Einheit;
import common.units.EinheitKategorie;
import common.units.EinheitenService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EinheitenServiceTest
{
    private final EinheitenService service = new EinheitenService();

    @Test
    void rechneUm_ShouldConvertLengthUnits()
    {
        Einheit meter = finde(EinheitKategorie.LAENGE, "Meter");
        Einheit kilometer = finde(EinheitKategorie.LAENGE, "Kilometer");

        assertEquals(1.5, service.rechneUm(1500, meter, kilometer), 1e-10);
    }

    @Test
    void rechneUm_ShouldConvertTemperaturesWithOffset()
    {
        Einheit celsius = finde(EinheitKategorie.TEMPERATUR, "Celsius");
        Einheit fahrenheit = finde(EinheitKategorie.TEMPERATUR, "Fahrenheit");
        Einheit kelvin = finde(EinheitKategorie.TEMPERATUR, "Kelvin");

        assertEquals(32.0, service.rechneUm(0, celsius, fahrenheit), 1e-9);
        assertEquals(273.15, service.rechneUm(0, celsius, kelvin), 1e-9);
    }

    @Test
    void rechneUm_ShouldRejectDifferentCategories()
    {
        Einheit meter = finde(EinheitKategorie.LAENGE, "Meter");
        Einheit kilogramm = finde(EinheitKategorie.MASSE, "Kilogramm");

        assertThrows(IllegalArgumentException.class, () -> service.rechneUm(1, meter, kilogramm));
    }

    @Test
    void einheitenFuer_ShouldReturnStableCatalog()
    {
        List<Einheit> laengen = service.einheitenFuer(EinheitKategorie.LAENGE);

        assertEquals("Millimeter", laengen.getFirst().name());
        assertEquals("Meile", laengen.getLast().name());
    }

    private Einheit finde(EinheitKategorie kategorie, String name)
    {
        return service.einheitenFuer(kategorie).stream()
                .filter(einheit -> name.equals(einheit.name()))
                .findFirst()
                .orElseThrow();
    }
}
