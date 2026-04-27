package modes.programmierer.model;

public enum Wortbreite
{

    BYTE(8),
    WORD(16),
    DWORD(32),
    QWORD(64);

    private final int bits;

    Wortbreite(int bits)
    {
        this.bits = bits;
    }

    public int getBits()
    {
        return bits;
    }
}
