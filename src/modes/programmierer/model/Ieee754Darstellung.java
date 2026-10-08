package modes.programmierer.model;

/**
 * Bitmuster einer Gleitkommazahl, aufgeteilt in Vorzeichen, Exponent und Mantisse.
 * gespeicherterWert ist der Wert nach dem Runden auf das Format (bei float z. B. 0.1 → 0.10000000149…).
 */
public record Ieee754Darstellung(
        String formatName,
        double gespeicherterWert,
        int vorzeichenBit,
        String exponentBits,
        String mantisseBits,
        int exponentRoh,
        int bias,
        Art art)
{
    public enum Art
    {
        NULL,
        NORMAL,
        SUBNORMAL,
        UNENDLICH,
        NAN
    }

    /** Tatsächlicher Exponent (2^e); bei Null, Unendlich und NaN gibt es keinen. */
    public Integer tatsaechlicherExponent()
    {
        return switch (art)
        {
            case NORMAL -> exponentRoh - bias;
            case SUBNORMAL -> 1 - bias;
            case NULL, UNENDLICH, NAN -> null;
        };
    }
}
