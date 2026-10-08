package modes.netzwerk.model;

import java.util.List;

/**
 * Ergebnis beim Aufteilen eines Netzes. {@code netze} enthält höchstens die
 * angezeigten Teilnetze, {@code anzahl} die tatsächliche Zahl.
 */
public record Teilung(Ipv4Netz ausgang, int gewuenscht, long anzahl, int neuerPraefix, List<Ipv4Netz> netze)
{
    public Teilung
    {
        netze = List.copyOf(netze);
    }

    public boolean wurdeAufgerundet()
    {
        return anzahl != gewuenscht;
    }

    public boolean istGekuerzt()
    {
        return netze.size() < anzahl;
    }
}
