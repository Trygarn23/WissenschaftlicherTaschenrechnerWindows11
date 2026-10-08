package modes.logik.model;

import java.util.Map;
import java.util.Set;

/** Syntaxbaum eines aussagenlogischen Ausdrucks. */
public sealed interface Ausdruck
{
    boolean werteAus(Map<Character, Boolean> belegung);

    void sammleVariablen(Set<Character> ziel);

    record Variable(char name) implements Ausdruck
    {
        @Override
        public boolean werteAus(Map<Character, Boolean> belegung)
        {
            Boolean wert = belegung.get(name);
            if (wert == null)
            {
                throw new IllegalArgumentException("Für " + name + " fehlt ein Wert.");
            }
            return wert;
        }

        @Override
        public void sammleVariablen(Set<Character> ziel)
        {
            ziel.add(name);
        }
    }

    record Konstante(boolean wert) implements Ausdruck
    {
        @Override
        public boolean werteAus(Map<Character, Boolean> belegung)
        {
            return wert;
        }

        @Override
        public void sammleVariablen(Set<Character> ziel)
        {
        }
    }

    record Nicht(Ausdruck inneres) implements Ausdruck
    {
        @Override
        public boolean werteAus(Map<Character, Boolean> belegung)
        {
            return !inneres.werteAus(belegung);
        }

        @Override
        public void sammleVariablen(Set<Character> ziel)
        {
            inneres.sammleVariablen(ziel);
        }
    }

    record Verknuepfung(Operator operator, Ausdruck links, Ausdruck rechts) implements Ausdruck
    {
        @Override
        public boolean werteAus(Map<Character, Boolean> belegung)
        {
            // beide Seiten auswerten, damit fehlende Belegungen immer auffallen
            boolean l = links.werteAus(belegung);
            boolean r = rechts.werteAus(belegung);
            return operator.anwenden(l, r);
        }

        @Override
        public void sammleVariablen(Set<Character> ziel)
        {
            links.sammleVariablen(ziel);
            rechts.sammleVariablen(ziel);
        }
    }
}
