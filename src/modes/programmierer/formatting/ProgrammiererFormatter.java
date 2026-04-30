package modes.programmierer.formatting;

public class ProgrammiererFormatter
{
    public String emptyAsZero(String value)
    {
        return (value == null || value.isBlank()) ? "0" : value;
    }

    public String formatBinary(String raw)
    {
        String text = emptyAsZero(raw).replace(" ", "");

        if (text.length() <= 4)
        {
            return text;
        }

        StringBuilder sb = new StringBuilder();

        int firstGroupLength = text.length() % 4;
        if (firstGroupLength == 0)
        {
            firstGroupLength = 4;
        }

        for (int i = 0; i < text.length(); i++)
        {
            if (i > 0)
            {
                boolean groupBreak =
                        i == firstGroupLength ||
                                (i > firstGroupLength && (i - firstGroupLength) % 4 == 0);

                if (groupBreak)
                {
                    sb.append(' ');
                }
            }

            sb.append(text.charAt(i));
        }

        return sb.toString();
    }

    public String formatHex(String value)
    {
        return emptyAsZero(value).toUpperCase();
    }

    public String formatOct(String value)
    {
        return emptyAsZero(value);
    }

    public String formatDec(String value)
    {
        return emptyAsZero(value);
    }
}