package modes.graph.logic;

import common.parser.AusdruckParser;
import common.state.WinkelModus;
import modes.graph.model.FunktionsDefinition;
import modes.graph.model.Tangente;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphEvaluator
{
    private static final double DEFAULT_H = 1e-4;
    private static final Set<String> PARAMETER_NAMEN = Set.of("a", "b", "c", "d", "k");
    // Wie im Tokenizer des Parsers: ein Bezeichner ist eine ununterbrochene Folge von Buchstaben ("abs" ist kein "a").
    private static final Pattern BEZEICHNER = Pattern.compile("\\p{L}+");

    private final GraphFunktionsResolver funktionsResolver = new GraphFunktionsResolver();
    private List<FunktionsDefinition> funktionen = List.of();
    private Map<String, Double> parameter = Map.of();

    public void setFunktionen(List<FunktionsDefinition> funktionen)
    {
        this.funktionen = funktionen == null ? List.of() : funktionen;
    }

    /** Werte für die Parameter a, b, c, d, k; werden dem Parser als Variablen neben x mitgegeben. */
    public void setParameter(Map<String, Double> parameter)
    {
        this.parameter = parameter == null ? Map.of() : Map.copyOf(parameter);
        funktionsResolver.setParameter(this.parameter);
    }

    /** Alle Parameter (a, b, c, d, k), die in den Ausdrücken vorkommen und nicht selbst Funktionsnamen sind. */
    public static Set<String> findeParameter(List<FunktionsDefinition> funktionen)
    {
        Set<String> funktionsNamen = new TreeSet<>();
        for (FunktionsDefinition funktion : funktionen)
        {
            funktionsNamen.add(funktion.getName().toLowerCase(Locale.ROOT));
        }

        Set<String> gefunden = new TreeSet<>();
        for (FunktionsDefinition funktion : funktionen)
        {
            String ausdruck = funktion.getAusdruck() == null ? "" : funktion.getAusdruck().replaceAll("\\s+", "");
            Matcher matcher = BEZEICHNER.matcher(ausdruck);
            while (matcher.find())
            {
                String name = matcher.group().toLowerCase(Locale.ROOT);
                if (PARAMETER_NAMEN.contains(name) && !funktionsNamen.contains(name))
                {
                    gefunden.add(name);
                }
            }
        }
        return gefunden;
    }

    static Map<String, Double> variablen(Map<String, Double> parameter, double x)
    {
        Map<String, Double> variablen = new HashMap<>(parameter);
        variablen.put("x", x);
        return variablen;
    }

    public double auswerten(String ausdruck, double x, WinkelModus winkelModus)
    {
        if (funktionen.isEmpty())
        {
            return AusdruckParser.auswerten(ausdruck, 0.0, winkelModus, variablen(parameter, x));
        }
        return funktionsResolver.auswerten(ausdruck, x, winkelModus, funktionen);
    }

    /**
     * Wie {@link #auswerten}, liefert bei ungültigem Ausdruck oder undefinierter Stelle aber {@code NaN}
     * statt einer Exception. Fürs Zeichnen und die Kurvendiskussion ist das der Normalfall.
     */
    public double wertOderNaN(String ausdruck, double x, WinkelModus winkelModus)
    {
        try
        {
            double y = auswerten(ausdruck, x, winkelModus);
            return Double.isFinite(y) ? y : Double.NaN;
        }
        catch (IllegalArgumentException e)
        {
            return Double.NaN;
        }
    }

    public double ersteAbleitung(String ausdruck, double x, WinkelModus winkelModus)
    {
        double links = wertOderNaN(ausdruck, x - DEFAULT_H, winkelModus);
        double rechts = wertOderNaN(ausdruck, x + DEFAULT_H, winkelModus);
        return (rechts - links) / (2.0 * DEFAULT_H);
    }

    public double zweiteAbleitung(String ausdruck, double x, WinkelModus winkelModus)
    {
        double links = wertOderNaN(ausdruck, x - DEFAULT_H, winkelModus);
        double mitte = wertOderNaN(ausdruck, x, winkelModus);
        double rechts = wertOderNaN(ausdruck, x + DEFAULT_H, winkelModus);
        return (links - 2.0 * mitte + rechts) / (DEFAULT_H * DEFAULT_H);
    }

    /** Tangente an der Stelle x0; {@code null}, wenn Funktion oder Ableitung dort nicht definiert sind. */
    public Tangente tangente(int funktionIndex, String ausdruck, double x0, WinkelModus winkelModus)
    {
        double y0 = wertOderNaN(ausdruck, x0, winkelModus);
        double steigung = ersteAbleitung(ausdruck, x0, winkelModus);
        if (!Double.isFinite(y0) || !Double.isFinite(steigung))
        {
            return null;
        }
        return new Tangente(funktionIndex, x0, y0, steigung);
    }

    public boolean istGueltig(String ausdruck, WinkelModus winkelModus)
    {
        return Double.isFinite(wertOderNaN(ausdruck, 0.0, winkelModus));
    }
}
