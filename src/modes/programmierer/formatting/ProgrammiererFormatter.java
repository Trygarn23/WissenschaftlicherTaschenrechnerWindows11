package modes.programmierer.formatting;

import modes.programmierer.model.Wortbreite;

public class ProgrammiererFormatter
{
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
