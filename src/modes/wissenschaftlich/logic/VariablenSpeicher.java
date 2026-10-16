package modes.wissenschaftlich.logic;

import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Properties;

/**
 * Variablen A–F für STO/RCL. Wird bei jedem Speichern in eine Datei geschrieben,
 * damit die Werte einen Neustart überleben. Ohne Datei ({@code null}) nur im Speicher.
 */
public final class VariablenSpeicher
{
    public static final List<String> NAMEN = List.of("A", "B", "C", "D", "E", "F");

    private final Path datei;
    private final Properties werte = new Properties();

    public VariablenSpeicher(Path datei)
    {
        this.datei = datei;
        if (datei != null)
        {
            DateiPersistenz.ladeProperties(datei).ifPresent(werte::putAll);
        }
    }

    public void speichere(String name, double wert)
    {
        if (!NAMEN.contains(name) || !Double.isFinite(wert))
        {
            throw new IllegalArgumentException("Ungültige Variable oder ungültiger Wert: " + name);
        }
        werte.setProperty(name, Double.toString(wert));
        if (datei != null)
        {
            DateiPersistenz.speichereProperties(datei, werte, "Variablen A-F");
        }
    }

    /** Leer, wenn die Variable noch nie belegt wurde (oder die Datei kaputt ist). */
    public OptionalDouble wert(String name)
    {
        double wert = DateiPersistenz.leseDouble(werte, name, Double.NaN);
        return Double.isNaN(wert) ? OptionalDouble.empty() : OptionalDouble.of(wert);
    }
}
