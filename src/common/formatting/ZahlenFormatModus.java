package common.formatting;

public enum ZahlenFormatModus
{
    AUTO("Automatisch"),
    DEZIMAL("Dezimal"),
    WISSENSCHAFTLICH("Wissenschaftlich");

    private final String label;

    ZahlenFormatModus(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
