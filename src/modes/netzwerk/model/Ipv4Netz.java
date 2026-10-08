package modes.netzwerk.model;

/**
 * IPv4-Adresse mit Präfixlänge. Die Adresse liegt als vorzeichenloses int vor,
 * damit Masken und Broadcast einfache Bit-Operationen bleiben.
 */
public record Ipv4Netz(int adresse, int praefix)
{
    public Ipv4Netz
    {
        if (praefix < 0 || praefix > 32)
        {
            throw new IllegalArgumentException("Präfix muss zwischen 0 und 32 liegen.");
        }
    }

    public static int maske(int praefix)
    {
        // Ein Shift um 32 wäre in Java ein Shift um 0, deshalb /0 extra.
        return praefix == 0 ? 0 : -1 << (32 - praefix);
    }

    public int maske()
    {
        return maske(praefix);
    }

    public int wildcard()
    {
        return ~maske();
    }

    public int netzadresse()
    {
        return adresse & maske();
    }

    public int broadcast()
    {
        return netzadresse() | wildcard();
    }

    /** Bei /31 (RFC 3021) und /32 gibt es keine reservierte Netz- bzw. Broadcastadresse. */
    public int ersterHost()
    {
        return praefix >= 31 ? netzadresse() : netzadresse() + 1;
    }

    public int letzterHost()
    {
        return praefix >= 31 ? broadcast() : broadcast() - 1;
    }

    public long anzahlAdressen()
    {
        return 1L << (32 - praefix);
    }

    public long anzahlHosts()
    {
        return praefix >= 31 ? anzahlAdressen() : anzahlAdressen() - 2;
    }

    public Ipv4Netz alsNetz()
    {
        return new Ipv4Netz(netzadresse(), praefix);
    }
}
