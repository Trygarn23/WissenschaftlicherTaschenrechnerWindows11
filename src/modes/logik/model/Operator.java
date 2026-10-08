package modes.logik.model;

public enum Operator
{
    UND("∧"),
    XOR("⊕"),
    ODER("∨"),
    IMPLIKATION("→"),
    AEQUIVALENZ("↔");

    private final String symbol;

    Operator(String symbol)
    {
        this.symbol = symbol;
    }

    public String symbol()
    {
        return symbol;
    }

    public boolean anwenden(boolean links, boolean rechts)
    {
        return switch (this)
        {
            case UND -> links && rechts;
            case XOR -> links != rechts;
            case ODER -> links || rechts;
            case IMPLIKATION -> !links || rechts;
            case AEQUIVALENZ -> links == rechts;
        };
    }
}
