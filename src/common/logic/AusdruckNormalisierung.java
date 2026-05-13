package common.logic;

final class AusdruckNormalisierung
{
    private AusdruckNormalisierung()
    {
    }

    static String verlaufErgebnis(String text)
    {
        if (text == null)
        {
            return null;
        }

        return text.trim()
                .replace(" ", "")
                .replace(".", "")
                .replace('\u2212', '-')
                .replace('\u2013', '-')
                .replace('\u2014', '-');
    }

    static String zwischenablage(String text)
    {
        if (text == null)
        {
            return null;
        }

        String normalisiert = text.trim()
                .replaceAll("\\s+", "")
                .replace('\u00d7', '*')
                .replace('\u00f7', '/')
                .replace('\u2212', '-')
                .replace('\u2013', '-')
                .replace('\u2014', '-');

        if (normalisiert.isBlank())
        {
            return normalisiert;
        }

        return normalisiereTausenderpunkte(normalisiert);
    }

    static String operator(String operator)
    {
        if ("\u00d7".equals(operator)) return "*";
        if ("\u00f7".equals(operator)) return "/";
        return operator;
    }

    private static String normalisiereTausenderpunkte(String text)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++)
        {
            char zeichen = text.charAt(i);
            if (Character.isDigit(zeichen) || zeichen == ',' || zeichen == '.')
            {
                int start = i;
                while (i + 1 < text.length())
                {
                    char next = text.charAt(i + 1);
                    if (Character.isDigit(next) || next == ',' || next == '.')
                    {
                        i++;
                    }
                    else
                    {
                        break;
                    }
                }

                String zahl = text.substring(start, i + 1);
                result.append(zahl.contains(",") ? zahl.replace(".", "") : zahl);
            }
            else
            {
                result.append(zeichen);
            }
        }

        return result.toString();
    }
}
