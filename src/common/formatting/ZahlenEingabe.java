package common.formatting;

/**
 * Liest Zahlen aus Eingabefeldern (Komma oder Punkt als Dezimaltrennzeichen).
 * Fehlermeldungen sind direkt für die Statuszeile gedacht, statt Javas "For input string: ...".
 */
public final class ZahlenEingabe
{
    private ZahlenEingabe()
    {
    }

    public static double lese(String text)
    {
        String bereinigt = text == null ? "" : text.trim();
        if (bereinigt.isEmpty())
        {
            throw new IllegalArgumentException("Bitte eine Zahl eingeben.");
        }

        try
        {
            return Double.parseDouble(bereinigt.replace(',', '.'));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("„" + bereinigt + "“ ist keine gültige Zahl.", e);
        }
    }
}
