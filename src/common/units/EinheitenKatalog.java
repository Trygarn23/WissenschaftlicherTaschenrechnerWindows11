package common.units;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class EinheitenKatalog
{
    private final Map<EinheitKategorie, List<Einheit>> einheiten = new EnumMap<>(EinheitKategorie.class);

    public EinheitenKatalog()
    {
        registriereStandardEinheiten();
    }

    public List<EinheitKategorie> kategorien()
    {
        return List.of(EinheitKategorie.values());
    }

    public List<Einheit> einheitenFuer(EinheitKategorie kategorie)
    {
        return einheiten.getOrDefault(kategorie, List.of());
    }

    private void registriereStandardEinheiten()
    {
        add(EinheitKategorie.LAENGE, "Millimeter", "mm", 0.001);
        add(EinheitKategorie.LAENGE, "Zentimeter", "cm", 0.01);
        add(EinheitKategorie.LAENGE, "Meter", "m", 1.0);
        add(EinheitKategorie.LAENGE, "Kilometer", "km", 1000.0);
        add(EinheitKategorie.LAENGE, "Zoll", "in", 0.0254);
        add(EinheitKategorie.LAENGE, "Fuss", "ft", 0.3048);
        add(EinheitKategorie.LAENGE, "Meile", "mi", 1609.344);

        add(EinheitKategorie.FLAECHE, "Quadratmeter", "m^2", 1.0);
        add(EinheitKategorie.FLAECHE, "Quadratkilometer", "km^2", 1_000_000.0);
        add(EinheitKategorie.FLAECHE, "Hektar", "ha", 10_000.0);
        add(EinheitKategorie.FLAECHE, "Quadratfuss", "ft^2", 0.09290304);

        add(EinheitKategorie.VOLUMEN, "Milliliter", "ml", 0.001);
        add(EinheitKategorie.VOLUMEN, "Liter", "l", 1.0);
        add(EinheitKategorie.VOLUMEN, "Kubikmeter", "m^3", 1000.0);
        add(EinheitKategorie.VOLUMEN, "US Gallone", "gal", 3.785411784);

        add(EinheitKategorie.MASSE, "Milligramm", "mg", 0.000001);
        add(EinheitKategorie.MASSE, "Gramm", "g", 0.001);
        add(EinheitKategorie.MASSE, "Kilogramm", "kg", 1.0);
        add(EinheitKategorie.MASSE, "Tonne", "t", 1000.0);
        add(EinheitKategorie.MASSE, "Pfund", "lb", 0.45359237);

        add(EinheitKategorie.ZEIT, "Millisekunde", "ms", 0.001);
        add(EinheitKategorie.ZEIT, "Sekunde", "s", 1.0);
        add(EinheitKategorie.ZEIT, "Minute", "min", 60.0);
        add(EinheitKategorie.ZEIT, "Stunde", "h", 3600.0);
        add(EinheitKategorie.ZEIT, "Tag", "d", 86400.0);

        add(EinheitKategorie.GESCHWINDIGKEIT, "Meter pro Sekunde", "m/s", 1.0);
        add(EinheitKategorie.GESCHWINDIGKEIT, "Kilometer pro Stunde", "km/h", 1.0 / 3.6);
        add(EinheitKategorie.GESCHWINDIGKEIT, "Meilen pro Stunde", "mph", 0.44704);
        add(EinheitKategorie.GESCHWINDIGKEIT, "Knoten", "kn", 0.514444444);

        add(EinheitKategorie.TEMPERATUR, "Kelvin", "K", 1.0, 0.0);
        add(EinheitKategorie.TEMPERATUR, "Celsius", "C", 1.0, 273.15);
        add(EinheitKategorie.TEMPERATUR, "Fahrenheit", "F", 5.0 / 9.0, 255.3722222222222);

        add(EinheitKategorie.DATENMENGE, "Bit", "bit", 0.125);
        add(EinheitKategorie.DATENMENGE, "Byte", "B", 1.0);
        add(EinheitKategorie.DATENMENGE, "Kilobyte", "KB", 1000.0);
        add(EinheitKategorie.DATENMENGE, "Megabyte", "MB", 1_000_000.0);
        add(EinheitKategorie.DATENMENGE, "Gigabyte", "GB", 1_000_000_000.0);
        add(EinheitKategorie.DATENMENGE, "Kibibyte", "KiB", 1024.0);
        add(EinheitKategorie.DATENMENGE, "Mebibyte", "MiB", 1_048_576.0);
        add(EinheitKategorie.DATENMENGE, "Gibibyte", "GiB", 1_073_741_824.0);
    }

    private void add(EinheitKategorie kategorie, String name, String symbol, double faktor)
    {
        add(kategorie, name, symbol, faktor, 0.0);
    }

    private void add(EinheitKategorie kategorie, String name, String symbol, double faktor, double offset)
    {
        einheiten.computeIfAbsent(kategorie, ignored -> new ArrayList<>())
                .add(new Einheit(kategorie, name, symbol, faktor, offset));
    }
}
