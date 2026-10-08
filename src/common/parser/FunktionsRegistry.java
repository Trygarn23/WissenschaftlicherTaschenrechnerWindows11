package common.parser;

import common.state.WinkelModus;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Einzige Stelle für Funktionen und Konstanten des Parsers: Name → Stelligkeit + Rechnung.
 * Benutzerdefinierte Funktionen {@code name(x) = ausdruck} werden zur Laufzeit hier registriert
 * und gelten danach für alle Aufrufe von {@link AusdruckParser}.
 */
public final class FunktionsRegistry
{
    static final String VARIABLE = "x";

    @FunctionalInterface
    private interface Rechnung
    {
        double rechne(double[] argumente, WinkelModus winkelModus);
    }

    private record Funktion(int stelligkeit, Rechnung rechnung)
    {
    }

    private record Benutzerfunktion(String ausdruck, List<AusdruckToken> postfix)
    {
    }

    private static final double GROESSTE_EXAKTE_GANZZAHL = 9_007_199_254_740_992.0; // 2^53

    private static final Map<String, Funktion> EINGEBAUT = Map.ofEntries(
            Map.entry("sin", einstellig((a, m) -> Math.sin(bogenmass(a[0], m)))),
            Map.entry("cos", einstellig((a, m) -> Math.cos(bogenmass(a[0], m)))),
            Map.entry("tan", einstellig((a, m) -> tangens(bogenmass(a[0], m)))),
            Map.entry("asin", einstellig((a, m) -> winkel(Math.asin(a[0]), m))),
            Map.entry("acos", einstellig((a, m) -> winkel(Math.acos(a[0]), m))),
            Map.entry("atan", einstellig((a, m) -> winkel(Math.atan(a[0]), m))),
            Map.entry("sinh", einstellig((a, m) -> Math.sinh(a[0]))),
            Map.entry("cosh", einstellig((a, m) -> Math.cosh(a[0]))),
            Map.entry("tanh", einstellig((a, m) -> Math.tanh(a[0]))),
            Map.entry("ln", einstellig((a, m) -> Math.log(positiv(a[0], "ln")))),
            Map.entry("log", einstellig((a, m) -> Math.log10(positiv(a[0], "log")))),
            Map.entry("sqrt", einstellig((a, m) -> wurzel(a[0]))),
            Map.entry("abs", einstellig((a, m) -> Math.abs(a[0]))),
            Map.entry("exp", einstellig((a, m) -> Math.exp(a[0]))),
            Map.entry("floor", einstellig((a, m) -> Math.floor(a[0]))),
            Map.entry("ceil", einstellig((a, m) -> Math.ceil(a[0]))),
            Map.entry("round", einstellig((a, m) -> (double) Math.round(a[0]))),
            Map.entry("rand", new Funktion(0, (a, m) -> Math.random())),
            Map.entry("ncr", new Funktion(2, (a, m) -> kombinationen(a[0], a[1]))),
            Map.entry("npr", new Funktion(2, (a, m) -> variationen(a[0], a[1]))),
            Map.entry("ggt", new Funktion(2, (a, m) -> ggT(ganzzahl(a[0], "ggT"), ganzzahl(a[1], "ggT")))),
            Map.entry("kgv", new Funktion(2, (a, m) -> kgV(ganzzahl(a[0], "kgV"), ganzzahl(a[1], "kgV"))))
    );

    private static final Map<String, Benutzerfunktion> BENUTZER = new ConcurrentHashMap<>();

    private FunktionsRegistry()
    {
    }

    // ---- Öffentliche API für benutzerdefinierte Funktionen ----

    /**
     * Registriert {@code name(x) = ausdruck} oder ersetzt eine gleichnamige Benutzerfunktion.
     * Der Name wird klein geschrieben gespeichert (der Parser unterscheidet keine Groß-/Kleinschreibung).
     *
     * @throws IllegalArgumentException mit deutscher Meldung, wenn Name oder Ausdruck ungültig sind
     */
    public static synchronized void registriereBenutzerfunktion(String name, String ausdruck)
    {
        String schluessel = pruefeName(name);
        BENUTZER.put(schluessel, new Benutzerfunktion(ausdruck.strip(), pruefeAusdruck(schluessel, ausdruck)));
    }

    /** Prüft wie {@link #registriereBenutzerfunktion}, ohne etwas zu speichern. */
    public static synchronized void validiereBenutzerfunktion(String name, String ausdruck)
    {
        pruefeAusdruck(pruefeName(name), ausdruck);
    }

    /** @return {@code true}, wenn es eine Benutzerfunktion mit diesem Namen gab. */
    public static synchronized boolean entferneBenutzerfunktion(String name)
    {
        return name != null && BENUTZER.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    public static synchronized void entferneAlleBenutzerfunktionen()
    {
        BENUTZER.clear();
    }

    /** Alphabetisch sortierte Kopie: Name → Ausdruck. */
    public static Map<String, String> benutzerfunktionen()
    {
        Map<String, String> kopie = new TreeMap<>();
        BENUTZER.forEach((name, funktion) -> kopie.put(name, funktion.ausdruck()));
        return kopie;
    }

    /** {@code true} für eingebaute Funktionen, Konstanten und die Variable x. */
    public static boolean istReservierterName(String name)
    {
        if (name == null) return false;
        String klein = name.toLowerCase(Locale.ROOT);
        return EINGEBAUT.containsKey(klein) || istKonstante(klein) || VARIABLE.equals(klein);
    }

    // ---- Abfragen für Tokenizer, Konverter und Auswerter ----

    static boolean istFunktion(String name)
    {
        return name != null && (EINGEBAUT.containsKey(name) || BENUTZER.containsKey(name));
    }

    static int stelligkeit(String name)
    {
        Funktion eingebaut = EINGEBAUT.get(name);
        return eingebaut != null ? eingebaut.stelligkeit() : 1;
    }

    static double rechne(String name, double[] argumente, double ans, WinkelModus winkelModus)
    {
        Funktion eingebaut = EINGEBAUT.get(name);
        if (eingebaut != null)
        {
            return eingebaut.rechnung().rechne(argumente, winkelModus);
        }

        Benutzerfunktion benutzer = BENUTZER.get(name);
        if (benutzer == null)
        {
            throw new AusdruckParserException(ParserFehler.UNBEKANNTE_FUNKTION, "Unbekannte Funktion „" + name + "“");
        }
        return AusdruckPostfixAuswerter.werteAus(benutzer.postfix(), ans, winkelModus, Map.of(VARIABLE, argumente[0]));
    }

    static boolean istKonstante(String name)
    {
        return "pi".equals(name) || "e".equals(name) || "ans".equals(name);
    }

    static double konstante(String name, double ans)
    {
        return switch (name)
        {
            case "pi" -> Math.PI;
            case "e" -> Math.E;
            case "ans" -> ans;
            default -> throw new IllegalStateException("Keine Konstante: " + name);
        };
    }

    // ---- Validierung ----

    private static String pruefeName(String name)
    {
        if (name == null || name.isBlank())
        {
            throw new IllegalArgumentException("Bitte einen Funktionsnamen eingeben.");
        }
        String klein = name.strip().toLowerCase(Locale.ROOT);
        if (!klein.matches("[a-z]+"))
        {
            throw new IllegalArgumentException("Der Funktionsname darf nur Buchstaben (a–z) enthalten.");
        }
        if (VARIABLE.equals(klein))
        {
            throw new IllegalArgumentException("„x“ ist die Variable und kann kein Funktionsname sein.");
        }
        if (istReservierterName(klein))
        {
            throw new IllegalArgumentException("„" + klein + "“ ist schon eingebaut und kann nicht überschrieben werden.");
        }
        return klein;
    }

    private static List<AusdruckToken> pruefeAusdruck(String schluessel, String ausdruck)
    {
        if (ausdruck == null || ausdruck.isBlank())
        {
            throw new IllegalArgumentException("Bitte einen Funktionsausdruck eingeben.");
        }

        List<AusdruckToken> tokens = AusdruckTokenizer.tokenisiere(ausdruck);
        for (AusdruckToken token : tokens)
        {
            String text = token.text();
            if (!AusdruckTokenizer.istName(text)) continue;

            if (schluessel.equals(text))
            {
                throw new IllegalArgumentException("Eine Funktion darf sich nicht selbst aufrufen.");
            }
            if (!istFunktion(text) && !istKonstante(text) && !VARIABLE.equals(text))
            {
                throw new AusdruckParserException(ParserFehler.UNBEKANNTE_FUNKTION,
                        "Unbekannter Name „" + text + "“ – als Variable ist nur x erlaubt", token.position());
            }
            if (BENUTZER.containsKey(text) && ruftAuf(text, schluessel, new HashSet<>()))
            {
                throw new IllegalArgumentException(
                        "Funktionskreis: „" + text + "“ ruft „" + schluessel + "“ auf – Rekursion ist nicht erlaubt.");
            }
        }

        List<AusdruckToken> postfix = AusdruckPostfixKonverter.konvertiere(tokens);
        pruefeStruktur(postfix);
        return postfix;
    }

    private static boolean ruftAuf(String von, String ziel, Set<String> besucht)
    {
        Benutzerfunktion funktion = BENUTZER.get(von);
        if (funktion == null || !besucht.add(von)) return false;

        for (AusdruckToken token : funktion.postfix())
        {
            String text = token.text();
            if (ziel.equals(text) || ruftAuf(text, ziel, besucht))
            {
                return true;
            }
        }
        return false;
    }

    /** Zählt nur den Stapel durch – rein syntaktisch, damit z. B. ln(x−1) trotz x=1 gültig bleibt. */
    private static void pruefeStruktur(List<AusdruckToken> postfix)
    {
        int tiefe = 0;
        for (AusdruckToken token : postfix)
        {
            String text = token.text();
            int stelligkeit = OperatorRegistry.istOperator(text) ? OperatorRegistry.stelligkeit(text)
                    : istFunktion(text) ? stelligkeit(text)
                    : 0;
            if (tiefe < stelligkeit)
            {
                throw new AusdruckParserException(ParserFehler.SYNTAX, "Hier fehlt eine Zahl", token.position());
            }
            tiefe = tiefe - stelligkeit + 1;
        }
        if (tiefe != 1)
        {
            throw new AusdruckParserException(ParserFehler.SYNTAX, "Der Ausdruck ist unvollständig.");
        }
    }

    // ---- Eingebaute Rechnungen ----

    private static Funktion einstellig(Rechnung rechnung)
    {
        return new Funktion(1, rechnung);
    }

    private static double bogenmass(double x, WinkelModus winkelModus)
    {
        return winkelModus == WinkelModus.DEG ? Math.toRadians(x) : x;
    }

    private static double winkel(double bogen, WinkelModus winkelModus)
    {
        return winkelModus == WinkelModus.DEG ? Math.toDegrees(bogen) : bogen;
    }

    private static double tangens(double bogen)
    {
        if (Math.abs(Math.cos(bogen)) < 1e-12)
        {
            throw bereichsFehler("tan ist hier nicht definiert");
        }
        return Math.tan(bogen);
    }

    private static double positiv(double x, String name)
    {
        if (x <= 0.0)
        {
            throw bereichsFehler(name + " ist nur für Zahlen größer 0 definiert");
        }
        return x;
    }

    private static double wurzel(double x)
    {
        if (x < 0.0)
        {
            throw bereichsFehler("Die Wurzel ist nur für Zahlen ab 0 definiert");
        }
        return Math.sqrt(x);
    }

    private static double kombinationen(double n, double k)
    {
        pruefeNk("nCr", n, k);
        double kleinesK = Math.min(k, n - k);
        double ergebnis = 1.0;
        // Multiplikativ statt über Fakultäten: kein Überlauf der Zwischenwerte, Abbruch sobald unendlich.
        for (long i = 1; i <= kleinesK && Double.isFinite(ergebnis); i++)
        {
            ergebnis = ergebnis * (n - kleinesK + i) / i;
        }
        return Math.rint(ergebnis);
    }

    private static double variationen(double n, double k)
    {
        pruefeNk("nPr", n, k);
        double ergebnis = 1.0;
        for (long i = 0; i < k && Double.isFinite(ergebnis); i++)
        {
            ergebnis *= n - i;
        }
        return ergebnis;
    }

    private static void pruefeNk(String name, double n, double k)
    {
        if (!istGanzzahl(n) || !istGanzzahl(k) || n < 0 || k < 0 || k > n)
        {
            throw bereichsFehler(name + "(n;k): n und k müssen ganze Zahlen ab 0 sein, mit k ≤ n");
        }
    }

    private static long ganzzahl(double x, String name)
    {
        if (!istGanzzahl(x))
        {
            throw bereichsFehler(name + " ist nur für ganze Zahlen definiert");
        }
        if (Math.abs(x) > GROESSTE_EXAKTE_GANZZAHL)
        {
            throw bereichsFehler(name + ": Die Zahl ist zu groß");
        }
        return (long) x;
    }

    private static boolean istGanzzahl(double x)
    {
        return Double.isFinite(x) && x == Math.rint(x);
    }

    private static double ggT(long a, long b)
    {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0)
        {
            long rest = a % b;
            a = b;
            b = rest;
        }
        return a;
    }

    private static double kgV(long a, long b)
    {
        if (a == 0 || b == 0) return 0.0;
        return Math.abs((double) (a / (long) ggT(a, b)) * b);
    }

    private static AusdruckParserException bereichsFehler(String meldung)
    {
        return new AusdruckParserException(ParserFehler.UNGUELTIGER_FUNKTIONSBEREICH, meldung);
    }
}
