package common.konstanten;

import common.formatting.ZahlenEingabe;
import common.persistence.DateiPersistenz;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** Vom Nutzer angelegte Konstanten, gespeichert als {@code name;symbol;wert;einheit} pro Zeile. */
public final class EigeneKonstanten
{
    private static final String BESCHREIBUNG = "Eigene Konstante";

    private final Path datei;
    private final List<Konstante> konstanten = new ArrayList<>();

    public EigeneKonstanten(Path datei)
    {
        this.datei = datei;
        for (String zeile : DateiPersistenz.ladeZeilen(datei))
        {
            String[] teile = zeile.split(";", -1);
            if (teile.length != 4 || teile[0].isBlank() || existiert(teile[0].trim()))
            {
                continue;
            }

            try
            {
                double wert = Double.parseDouble(teile[2].trim());
                if (Double.isFinite(wert))
                {
                    konstanten.add(neu(teile[0].trim(), teile[1].trim(), wert, teile[3].trim()));
                }
            }
            catch (NumberFormatException ignored)
            {
                // Kaputte Zeile überspringen, der Rest bleibt nutzbar.
            }
        }
    }

    public List<Konstante> alle()
    {
        return List.copyOf(konstanten);
    }

    public Konstante fuegeHinzu(String name, String symbol, String wertText, String einheit)
    {
        String bereinigterName = name == null ? "" : name.trim();
        String bereinigtesSymbol = symbol == null ? "" : symbol.trim();
        String bereinigteEinheit = einheit == null ? "" : einheit.trim();

        if (bereinigterName.isEmpty())
        {
            throw new IllegalArgumentException("Bitte einen Namen eingeben.");
        }
        if (Stream.of(bereinigterName, bereinigtesSymbol, bereinigteEinheit).anyMatch(t -> t.contains(";") || t.contains("\n") || t.contains("\r")))
        {
            throw new IllegalArgumentException("Name, Symbol und Einheit dürfen kein „;“ enthalten.");
        }
        double wert = ZahlenEingabe.lese(wertText);
        if (!Double.isFinite(wert))
        {
            throw new IllegalArgumentException("Der Wert muss eine endliche Zahl sein.");
        }
        if (existiert(bereinigterName))
        {
            throw new IllegalArgumentException("Eine Konstante „" + bereinigterName + "“ gibt es schon.");
        }

        Konstante konstante = neu(bereinigterName, bereinigtesSymbol, wert, bereinigteEinheit);
        konstanten.add(konstante);
        speichere();
        return konstante;
    }

    public void entferne(String name)
    {
        if (konstanten.removeIf(k -> k.name().equals(name)))
        {
            speichere();
        }
    }

    // Namen müssen eindeutig sein, weil Favoriten über den Namen gespeichert werden.
    private boolean existiert(String name)
    {
        return Stream.concat(KonstantenKatalog.STANDARD.stream(), konstanten.stream())
                .anyMatch(k -> k.name().equalsIgnoreCase(name));
    }

    private void speichere()
    {
        DateiPersistenz.speichereZeilen(datei, konstanten.stream()
                .map(k -> k.name() + ";" + k.symbol() + ";" + k.wert() + ";" + k.einheit())
                .toList());
    }

    private static Konstante neu(String name, String symbol, double wert, String einheit)
    {
        return new Konstante(name, symbol, wert, einheit, KonstantenKategorie.EIGENE, BESCHREIBUNG);
    }
}
