package common.parser;

public class AusdruckParserException extends IllegalArgumentException
{
    private final ParserFehler fehler;
    private final String grundMeldung;
    private final int position;

    public AusdruckParserException(ParserFehler fehler, String message)
    {
        this(fehler, message, -1);
    }

    /**
     * @param position 0-basierte Stelle im Original-Ausdruck, -1 wenn unbekannt.
     *                 In der Meldung erscheint sie 1-basiert („an Stelle 3“).
     */
    public AusdruckParserException(ParserFehler fehler, String message, int position)
    {
        super(position >= 0 ? message + " an Stelle " + (position + 1) : message);
        this.fehler = fehler;
        this.grundMeldung = message;
        this.position = position;
    }

    public ParserFehler getFehler()
    {
        return fehler;
    }

    /** 0-basierte Stelle im Original-Ausdruck oder -1, wenn sie nicht bekannt ist. */
    public int getPosition()
    {
        return position;
    }

    AusdruckParserException mitPosition(int neuePosition)
    {
        return new AusdruckParserException(fehler, grundMeldung, neuePosition);
    }
}
