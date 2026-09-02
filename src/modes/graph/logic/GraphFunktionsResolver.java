package modes.graph.logic;

import common.parser.AusdruckParser;
import common.state.WinkelModus;
import modes.graph.model.FunktionsDefinition;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class GraphFunktionsResolver
{
    double auswerten(
            String ausdruck,
            double x,
            WinkelModus winkelModus,
            List<FunktionsDefinition> funktionen)
    {
        Map<String, FunktionsDefinition> definitionen = new LinkedHashMap<>();
        for (FunktionsDefinition funktion : funktionen)
        {
            definitionen.put(funktion.getName().toLowerCase(Locale.ROOT), funktion);
        }

        return auswerten(ausdruck, x, winkelModus, definitionen, new LinkedHashSet<>());
    }

    private double auswerten(
            String ausdruck,
            double x,
            WinkelModus winkelModus,
            Map<String, FunktionsDefinition> definitionen,
            Set<String> aufrufKette)
    {
        String aufgeloest = ersetzeFunktionsVerweise(ausdruck, x, winkelModus, definitionen, aufrufKette);
        return AusdruckParser.auswerten(aufgeloest, 0.0, winkelModus, Map.of("x", x));
    }

    private String ersetzeFunktionsVerweise(
            String ausdruck,
            double x,
            WinkelModus winkelModus,
            Map<String, FunktionsDefinition> definitionen,
            Set<String> aufrufKette)
    {
        if (ausdruck == null)
        {
            return null;
        }

        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < ausdruck.length())
        {
            char zeichen = ausdruck.charAt(index);
            if (!Character.isLetter(zeichen))
            {
                result.append(zeichen);
                index++;
                continue;
            }

            int nameEnde = index + 1;
            while (nameEnde < ausdruck.length() && istTeilVomNamen(ausdruck.charAt(nameEnde)))
            {
                nameEnde++;
            }

            String originalName = ausdruck.substring(index, nameEnde);
            String name = originalName.toLowerCase(Locale.ROOT);
            FunktionsDefinition definition = definitionen.get(name);
            if (definition == null)
            {
                result.append(originalName);
                index = nameEnde;
                continue;
            }

            int klammerStart = ueberspringeLeerzeichen(ausdruck, nameEnde);
            double argument = x;
            int naechsterIndex = nameEnde;
            if (klammerStart < ausdruck.length() && ausdruck.charAt(klammerStart) == '(')
            {
                int klammerEnde = findeSchliessendeKlammer(ausdruck, klammerStart);
                String argumentAusdruck = ausdruck.substring(klammerStart + 1, klammerEnde);
                argument = auswerten(argumentAusdruck, x, winkelModus, definitionen, aufrufKette);
                naechsterIndex = klammerEnde + 1;
            }

            double wert = werteDefinitionAus(definition, argument, winkelModus, definitionen, aufrufKette);
            result.append('(').append(Double.toString(wert)).append(')');
            index = naechsterIndex;
        }
        return result.toString();
    }

    private double werteDefinitionAus(
            FunktionsDefinition definition,
            double argument,
            WinkelModus winkelModus,
            Map<String, FunktionsDefinition> definitionen,
            Set<String> aufrufKette)
    {
        String name = definition.getName().toLowerCase(Locale.ROOT);
        if (aufrufKette.contains(name))
        {
            String kette = String.join(" → ", aufrufKette) + " → " + name;
            throw new IllegalArgumentException("Funktionskreis erkannt: " + kette);
        }

        Set<String> naechsteKette = new LinkedHashSet<>(aufrufKette);
        naechsteKette.add(name);
        return auswerten(definition.getAusdruck(), argument, winkelModus, definitionen, naechsteKette);
    }

    private int findeSchliessendeKlammer(String ausdruck, int klammerStart)
    {
        int tiefe = 0;
        for (int index = klammerStart; index < ausdruck.length(); index++)
        {
            char zeichen = ausdruck.charAt(index);
            if (zeichen == '(')
            {
                tiefe++;
            }
            else if (zeichen == ')' && --tiefe == 0)
            {
                return index;
            }
        }
        throw new IllegalArgumentException("Schließende Klammer bei Funktionsaufruf fehlt");
    }

    private int ueberspringeLeerzeichen(String ausdruck, int index)
    {
        while (index < ausdruck.length() && Character.isWhitespace(ausdruck.charAt(index)))
        {
            index++;
        }
        return index;
    }

    private boolean istTeilVomNamen(char zeichen)
    {
        return Character.isLetterOrDigit(zeichen) || zeichen == '_';
    }
}
