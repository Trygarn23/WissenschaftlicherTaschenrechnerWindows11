package common.logic;

final class AusdruckTermFinder
{
    private AusdruckTermFinder()
    {
    }

    static int startLetzterTerm(String ausdruck)
    {
        if (ausdruck == null || ausdruck.isEmpty())
        {
            return 0;
        }

        int i = ausdruck.length() - 1;
        char letztesZeichen = ausdruck.charAt(i);

        if (letztesZeichen == ')')
        {
            int klammerStand = 1;
            i--;

            while (i >= 0 && klammerStand > 0)
            {
                char zeichen = ausdruck.charAt(i);

                if (zeichen == ')') klammerStand++;
                else if (zeichen == '(') klammerStand--;

                i--;
            }

            if (klammerStand != 0) return 0;

            while (i >= 0 && Character.isLetter(ausdruck.charAt(i)))
            {
                i--;
            }

            return bezieheUnaeresMinusEin(ausdruck, i + 1);
        }

        if (Character.isDigit(letztesZeichen) || letztesZeichen == ',' || letztesZeichen == '.')
        {
            while (i >= 0)
            {
                char zeichen = ausdruck.charAt(i);

                if (Character.isDigit(zeichen) || zeichen == ',' || zeichen == '.')
                {
                    i--;
                }
                else
                {
                    break;
                }
            }

            return bezieheUnaeresMinusEin(ausdruck, i + 1);
        }

        if (Character.isLetter(letztesZeichen))
        {
            while (i >= 0 && Character.isLetter(ausdruck.charAt(i)))
            {
                i--;
            }

            return bezieheUnaeresMinusEin(ausdruck, i + 1);
        }

        return ausdruck.length();
    }

    static int startLetzteZahl(String ausdruck)
    {
        if (ausdruck == null || ausdruck.isEmpty())
        {
            return 0;
        }

        int i = ausdruck.length() - 1;

        while (i >= 0)
        {
            char zeichen = ausdruck.charAt(i);
            if (Character.isDigit(zeichen) || zeichen == ',' || zeichen == '.') i--;
            else break;
        }

        if (i >= 0 && ausdruck.charAt(i) == '-')
        {
            if (i == 0) return 0;

            char davor = ausdruck.charAt(i - 1);
            if (istOperatorZeichen(davor) || davor == '(')
            {
                return i;
            }
        }

        return i + 1;
    }

    static int zaehleOffeneKlammern(String ausdruck)
    {
        if (ausdruck == null || ausdruck.isEmpty())
        {
            return 0;
        }

        int stand = 0;

        for (int i = 0; i < ausdruck.length(); i++)
        {
            char zeichen = ausdruck.charAt(i);
            if (zeichen == '(') stand++;
            else if (zeichen == ')') stand--;
        }

        return stand;
    }

    static boolean istOperatorZeichen(char zeichen)
    {
        return "+-*/^%".indexOf(zeichen) >= 0;
    }

    private static int bezieheUnaeresMinusEin(String ausdruck, int start)
    {
        if (start > 0 && ausdruck.charAt(start - 1) == '-')
        {
            if (start - 1 == 0) return start - 1;

            char davor = ausdruck.charAt(start - 2);
            if (istOperatorZeichen(davor) || davor == '(')
            {
                return start - 1;
            }
        }

        return start;
    }
}
