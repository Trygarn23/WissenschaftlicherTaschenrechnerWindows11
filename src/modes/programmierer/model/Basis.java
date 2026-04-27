package modes.programmierer.model;

public enum Basis
{
    BIN(2),
    OCT(8),
    DEC(10),
    HEX(16);

    private final int radix;

    Basis(int radix)
    {
        this.radix = radix;
    }

    public int getRadix()
    {
        return radix;
    }
}