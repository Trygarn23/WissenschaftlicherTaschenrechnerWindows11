package modes.programmierer.formatting;

import modes.programmierer.model.Ieee754Darstellung;
import modes.programmierer.model.Wortbreite;

public class ProgrammiererFormatter
{
    private static final String[] STEUERZEICHEN = {
            "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "HT", "LF", "VT", "FF", "CR", "SO", "SI",
            "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US"
    };

    /** Bitmuster als Unicode-Zeichen, z. B. „Zeichen: 'A' (U+0041)“ oder „Zeichen: LF (U+000A)“. */
    public String formatZeichen(long unsignedWert)
    {
        if (unsignedWert < 0 || unsignedWert > Character.MAX_CODE_POINT)
        {
            return "Zeichen: –";
        }

        int codepoint = (int) unsignedWert;
        String code = String.format("U+%04X", codepoint);

        if (codepoint < STEUERZEICHEN.length)
        {
            return "Zeichen: " + STEUERZEICHEN[codepoint] + " (" + code + ")";
        }

        if (codepoint == 0x7F)
        {
            return "Zeichen: DEL (" + code + ")";
        }

        // Surrogate und C1-Steuerzeichen lassen sich nicht sinnvoll einzeln darstellen.
        boolean surrogat = codepoint >= Character.MIN_SURROGATE && codepoint <= Character.MAX_SURROGATE;
        if (surrogat || Character.isISOControl(codepoint))
        {
            return "Zeichen: – (" + code + ")";
        }

        return "Zeichen: '" + Character.toString(codepoint) + "' (" + code + ")";
    }

    public String formatIeee754(Ieee754Darstellung d)
    {
        int gesamtBits = 1 + d.exponentBits().length() + d.mantisseBits().length();
        String exponentText = switch (d.art())
        {
            case NORMAL -> d.exponentRoh() + " − " + d.bias() + " = " + d.tatsaechlicherExponent();
            case SUBNORMAL -> "subnormal, fest 1 − " + d.bias() + " = " + d.tatsaechlicherExponent();
            case NULL -> "Null";
            case UNENDLICH -> "alle Bits 1, Mantisse 0 → Unendlich";
            case NAN -> "alle Bits 1, Mantisse ≠ 0 → NaN";
        };
        String mantisseText = switch (d.art())
        {
            case NORMAL -> "  → 1,… (verstecktes 1-Bit)";
            case SUBNORMAL -> "  → 0,… (kein verstecktes Bit)";
            case NULL, UNENDLICH, NAN -> "";
        };

        return d.formatName() + " (" + gesamtBits + " Bit), gespeichert: " + d.gespeicherterWert() + "\n"
                + "  Vorzeichen  " + d.vorzeichenBit() + "  → " + (d.vorzeichenBit() == 0 ? "+" : "−") + "\n"
                + "  Exponent    " + d.exponentBits() + "  → " + exponentText + "\n"
                + "  Mantisse    " + d.mantisseBits() + mantisseText;
    }

    public String emptyAsZero(String value)
    {
        return (value == null || value.isBlank()) ? "0" : value;
    }

    public String formatBinary(String raw)
    {
        String text = emptyAsZero(raw).replace(" ", "");
        return gruppiereVonRechts(text, 4);
    }

    public String formatBinary(String raw, Wortbreite wortbreite)
    {
        String text = emptyAsZero(raw).replace(" ", "");
        return gruppiereVonRechts(fuelleLinksAuf(text, wortbreite.getBits()), 4);
    }

    public String formatHex(String value)
    {
        return gruppiereVonRechts(emptyAsZero(value).toUpperCase(), 4);
    }

    public String formatHex(String value, Wortbreite wortbreite)
    {
        int stellen = wortbreite.getBits() / 4;
        String text = emptyAsZero(value).toUpperCase();
        return gruppiereVonRechts(fuelleLinksAuf(text, stellen), 4);
    }

    public String formatOct(String value)
    {
        return gruppiereVonRechts(emptyAsZero(value), 3);
    }

    public String formatDec(String value)
    {
        return emptyAsZero(value);
    }

    private String fuelleLinksAuf(String text, int zielLaenge)
    {
        if (text.length() >= zielLaenge)
        {
            return text;
        }

        StringBuilder sb = new StringBuilder();
        while (sb.length() + text.length() < zielLaenge)
        {
            sb.append('0');
        }
        sb.append(text);
        return sb.toString();
    }

    private String gruppiereVonRechts(String text, int gruppenGroesse)
    {
        if (text.length() <= gruppenGroesse)
        {
            return text;
        }

        int firstGroupLength = text.length() % gruppenGroesse;
        if (firstGroupLength == 0)
        {
            firstGroupLength = gruppenGroesse;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++)
        {
            if (i > 0)
            {
                boolean groupBreak =
                        i == firstGroupLength ||
                                (i > firstGroupLength && (i - firstGroupLength) % gruppenGroesse == 0);

                if (groupBreak)
                {
                    sb.append(' ');
                }
            }

            sb.append(text.charAt(i));
        }

        return sb.toString();
    }
}
