package common.konstanten;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static common.konstanten.KonstantenKategorie.*;

/**
 * Eingebaute Konstanten. Physik/Chemie nach CODATA 2018; „exakt“ heißt seit der SI-Reform 2019 per Definition festgelegt.
 */
public final class KonstantenKatalog
{
    private static final double PLANCK = 6.62607015e-34;
    private static final double ELEMENTARLADUNG = 1.602176634e-19;
    private static final double BOLTZMANN = 1.380649e-23;
    private static final double AVOGADRO = 6.02214076e23;
    private static final double LICHTGESCHWINDIGKEIT = 299792458;

    public static final List<Konstante> STANDARD = List.of(
            // Mathematik
            new Konstante("Kreiszahl Pi", "π", Math.PI, "", MATHEMATIK, "Verhältnis von Kreisumfang zu Durchmesser"),
            new Konstante("Kreiszahl Tau", "τ", 2 * Math.PI, "", MATHEMATIK, "Zwei Pi, Vollwinkel im Bogenmaß"),
            new Konstante("Eulersche Zahl", "e", Math.E, "", MATHEMATIK, "Basis des natürlichen Logarithmus"),
            new Konstante("Goldener Schnitt", "φ", (1 + Math.sqrt(5)) / 2, "", MATHEMATIK, "(1 + √5) / 2"),
            new Konstante("Wurzel aus 2", "√2", Math.sqrt(2), "", MATHEMATIK, "Diagonale des Einheitsquadrats"),
            new Konstante("Wurzel aus 3", "√3", Math.sqrt(3), "", MATHEMATIK, "Raumdiagonale des Einheitswürfels"),
            new Konstante("Natürlicher Logarithmus von 2", "ln 2", Math.log(2), "", MATHEMATIK, "t½ = ln 2 / λ beim radioaktiven Zerfall"),
            new Konstante("Natürlicher Logarithmus von 10", "ln 10", Math.log(10), "", MATHEMATIK, "Umrechnung zwischen ln und log"),
            new Konstante("Euler-Mascheroni-Konstante", "γ", 0.5772156649015329, "", MATHEMATIK, "Grenzwert von (1 + 1/2 + … + 1/n) − ln n"),
            new Konstante("Apéry-Konstante", "ζ(3)", 1.2020569031595942, "", MATHEMATIK, "Summe der Kehrwerte aller Kubikzahlen"),
            new Konstante("Catalan-Konstante", "G", 0.915965594177219, "", MATHEMATIK, "1 − 1/9 + 1/25 − 1/49 + …"),

            // Physik
            new Konstante("Lichtgeschwindigkeit", "c", LICHTGESCHWINDIGKEIT, "m/s", PHYSIK, "Lichtgeschwindigkeit im Vakuum (exakt)"),
            new Konstante("Planck-Konstante", "h", PLANCK, "J·s", PHYSIK, "Plancksches Wirkungsquantum (exakt)"),
            new Konstante("Reduzierte Planck-Konstante", "ħ", PLANCK / (2 * Math.PI), "J·s", PHYSIK, "h geteilt durch 2π (exakt)"),
            new Konstante("Elementarladung", "e", ELEMENTARLADUNG, "C", PHYSIK, "Ladung eines Protons (exakt)"),
            new Konstante("Boltzmann-Konstante", "k_B", BOLTZMANN, "J/K", PHYSIK, "Verknüpft Temperatur und Energie (exakt)"),
            new Konstante("Gravitationskonstante", "G", 6.67430e-11, "m³/(kg·s²)", PHYSIK, "Newtonsche Gravitationskonstante"),
            new Konstante("Normfallbeschleunigung", "g_n", 9.80665, "m/s²", PHYSIK, "Normwert der Erdbeschleunigung (exakt)"),
            new Konstante("Elektrische Feldkonstante", "ε₀", 8.8541878128e-12, "F/m", PHYSIK, "Permittivität des Vakuums"),
            new Konstante("Magnetische Feldkonstante", "μ₀", 1.25663706212e-6, "N/A²", PHYSIK, "Permeabilität des Vakuums"),
            new Konstante("Elektronenmasse", "m_e", 9.1093837015e-31, "kg", PHYSIK, "Ruhemasse des Elektrons"),
            new Konstante("Protonenmasse", "m_p", 1.67262192369e-27, "kg", PHYSIK, "Ruhemasse des Protons"),
            new Konstante("Neutronenmasse", "m_n", 1.67492749804e-27, "kg", PHYSIK, "Ruhemasse des Neutrons"),
            new Konstante("Stefan-Boltzmann-Konstante", "σ", 5.670374419e-8, "W/(m²·K⁴)", PHYSIK, "Strahlungsleistung eines schwarzen Körpers"),
            new Konstante("Feinstrukturkonstante", "α", 7.2973525693e-3, "", PHYSIK, "Stärke der elektromagnetischen Wechselwirkung, etwa 1/137"),
            new Konstante("Rydberg-Konstante", "R∞", 10973731.568160, "1/m", PHYSIK, "Grundlage der Spektrallinien des Wasserstoffs"),
            new Konstante("Bohrscher Radius", "a₀", 5.29177210903e-11, "m", PHYSIK, "Radius der innersten Bahn im Bohrschen Atommodell"),
            new Konstante("Wiensche Verschiebungskonstante", "b", 2.897771955e-3, "m·K", PHYSIK, "Wellenlänge des Strahlungsmaximums mal Temperatur"),
            new Konstante("Astronomische Einheit", "AE", 149597870700.0, "m", PHYSIK, "Mittlerer Abstand Erde–Sonne (exakt per IAU)"),
            new Konstante("Lichtjahr", "ly", 9460730472580800.0, "m", PHYSIK, "Strecke, die Licht in einem julianischen Jahr zurücklegt (exakt)"),

            // Chemie
            new Konstante("Avogadro-Konstante", "N_A", AVOGADRO, "1/mol", CHEMIE, "Teilchen pro Mol (exakt)"),
            new Konstante("Universelle Gaskonstante", "R", AVOGADRO * BOLTZMANN, "J/(mol·K)", CHEMIE, "N_A · k_B (exakt)"),
            new Konstante("Faraday-Konstante", "F", AVOGADRO * ELEMENTARLADUNG, "C/mol", CHEMIE, "Ladung eines Mols Elementarladungen, N_A · e (exakt)"),
            new Konstante("Atomare Masseneinheit", "u", 1.66053906660e-27, "kg", CHEMIE, "Ein Zwölftel der Masse eines ¹²C-Atoms"),
            new Konstante("Molares Volumen idealer Gase", "V_m", 22.41396954, "L/mol", CHEMIE, "Bei 273,15 K und 101,325 kPa"),
            new Konstante("Normtemperatur", "T_n", 273.15, "K", CHEMIE, "0 °C in Kelvin (exakt)"),
            new Konstante("Normdruck", "p_n", 101325, "Pa", CHEMIE, "Physikalische Atmosphäre, 1 atm (exakt)"),

            // Informatik
            new Konstante("Bits pro Byte", "bit/B", 8, "bit", INFORMATIK, "Ein Byte hat acht Bit"),
            new Konstante("Zwei hoch 8", "2⁸", 256, "", INFORMATIK, "Anzahl der Werte in einem Byte"),
            new Konstante("Zwei hoch 10", "2¹⁰", 1024, "", INFORMATIK, "Basis der Binärpräfixe"),
            new Konstante("Zwei hoch 16", "2¹⁶", 65536, "", INFORMATIK, "Anzahl der Werte in 16 Bit, z. B. Ports"),
            new Konstante("Zwei hoch 20", "2²⁰", 1048576, "", INFORMATIK, "Ein Mebi"),
            new Konstante("Zwei hoch 32", "2³²", 4294967296.0, "", INFORMATIK, "Anzahl der Werte in 32 Bit, z. B. IPv4-Adressen"),
            new Konstante("Zwei hoch 64", "2⁶⁴", 18446744073709551616.0, "", INFORMATIK, "Anzahl der Werte in 64 Bit"),
            new Konstante("Größter int-Wert", "2³¹ − 1", Integer.MAX_VALUE, "", INFORMATIK, "Integer.MAX_VALUE in Java"),
            new Konstante("Bytes pro Kibibyte", "KiB", 1024, "B", INFORMATIK, "1 KiB = 2¹⁰ Byte"),
            new Konstante("Bytes pro Mebibyte", "MiB", 1048576, "B", INFORMATIK, "1 MiB = 2²⁰ Byte"),
            new Konstante("Bytes pro Gibibyte", "GiB", 1073741824, "B", INFORMATIK, "1 GiB = 2³⁰ Byte"),
            new Konstante("Maschinengenauigkeit double", "ε", Math.ulp(1.0), "", INFORMATIK, "Abstand von 1 zur nächsten double-Zahl, 2⁻⁵²"),
            new Konstante("Lichtlaufzeit pro Meter", "1/c", 1 / LICHTGESCHWINDIGKEIT, "s/m", INFORMATIK, "Signallaufzeit im Vakuum, etwa 3,34 ns pro Meter")
    );

    private KonstantenKatalog()
    {
    }

    /**
     * Findet Konstanten, bei denen jedes Wort der Suche in Name, Symbol oder Beschreibung vorkommt.
     * Groß-/Kleinschreibung und Akzente zählen nicht. {@code kategorie == null} heißt alle Kategorien.
     */
    public static List<Konstante> suche(Collection<Konstante> quelle, String suchtext, KonstantenKategorie kategorie)
    {
        String[] woerter = normalisiere(suchtext == null ? "" : suchtext).trim().split("\\s+");
        return quelle.stream()
                .filter(k -> kategorie == null || k.kategorie() == kategorie)
                .filter(k ->
                {
                    String heuhaufen = normalisiere(k.name() + " " + k.symbol() + " " + k.beschreibung());
                    return Arrays.stream(woerter).allMatch(heuhaufen::contains);
                })
                .toList();
    }

    static String normalisiere(String text)
    {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
