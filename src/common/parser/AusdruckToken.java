package common.parser;

public record AusdruckToken(String text)
{
    public AusdruckToken
    {
        if (text == null || text.isBlank())
        {
            throw new IllegalArgumentException("Token darf nicht leer sein.");
        }
    }
}
