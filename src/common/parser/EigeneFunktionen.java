package common.parser;

import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Vom Nutzer angelegte Funktionen {@code name(x) = ausdruck}, gespeichert als {@code name;ausdruck} pro Zeile.
 * Beim Laden werden sie in der {@link FunktionsRegistry} registriert und gelten damit überall im Parser.
 */
public final class EigeneFunktionen
{
    private final Path datei;
    private final Map<String, String> funktionen = new LinkedHashMap<>();

    public EigeneFunktionen(Path datei)
    {
        this.datei = datei;
        for (String zeile : DateiPersistenz.ladeZeilen(datei))
        {
            int trenner = zeile.indexOf(';');
            if (trenner <= 0)
            {
                continue;
            }
            try
            {
                registriere(zeile.substring(0, trenner), zeile.substring(trenner + 1));
            }
            catch (IllegalArgumentException ignored)
            {
                // Ungültige Zeile überspringen, der Rest bleibt nutzbar.
            }
        }
    }

    /** Name → Ausdruck in der Reihenfolge des Anlegens. */
    public Map<String, String> alle()
    {
        return Collections.unmodifiableMap(new LinkedHashMap<>(funktionen));
    }

    /** @throws IllegalArgumentException mit deutscher Meldung, wenn Name oder Ausdruck ungültig sind */
    public void fuegeHinzu(String name, String ausdruck)
    {
        if (ausdruck != null && ausdruck.contains("\n") || ausdruck.contains("\r"))
        {
            throw new IllegalArgumentException("Der Ausdruck darf keinen Zeilenumbruch enthalten.");
        }
        registriere(name, ausdruck);
        speichere();
    }

    public void entferne(String name)
    {
        if (funktionen.remove(name.toLowerCase(Locale.ROOT)) != null)
        {
            FunktionsRegistry.entferneBenutzerfunktion(name);
            speichere();
        }
    }

    private void registriere(String name, String ausdruck)
    {
        FunktionsRegistry.registriereBenutzerfunktion(name, ausdruck);
        funktionen.put(name.strip().toLowerCase(Locale.ROOT), ausdruck.strip());
    }

    private void speichere()
    {
        DateiPersistenz.speichereZeilen(datei, funktionen.entrySet().stream()
                .map(e -> e.getKey() + ";" + e.getValue())
                .toList());
    }
}
