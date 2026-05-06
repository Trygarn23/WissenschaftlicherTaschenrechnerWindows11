package common.parser;

public class AusdruckParserException extends IllegalArgumentException
{
    private final ParserFehler fehler;

    public AusdruckParserException(ParserFehler fehler, String message)
    {
        super(message);
        this.fehler = fehler;
    }

    public ParserFehler getFehler()
    {
        return fehler;
    }
}
