package common.parser;

/**
 * @param position 0-basierte Stelle im Original-Ausdruck, -1 wenn unbekannt.
 */
public record AusdruckToken(String text, int position)
{
    public AusdruckToken
    {
        if (text == null || text.isBlank())
        {
            throw new IllegalArgumentException("Token darf nicht leer sein.");
        }
    }

    public AusdruckToken(String text)
    {
        this(text, -1);
    }
}
