package modes.netzwerk.formatting;

import modes.netzwerk.logic.Ipv4Rechner;
import modes.netzwerk.model.Ipv4Netz;
import modes.netzwerk.model.Teilung;

import java.util.Locale;

import static modes.netzwerk.logic.Ipv4Rechner.alsText;
import static modes.netzwerk.logic.Ipv4Rechner.binaer;

/** Baut die mehrzeiligen Ergebnistexte für den Ergebnisbereich (feste Schriftbreite). */
public final class NetzwerkBericht
{
    private NetzwerkBericht()
    {
    }

    public static String subnetz(Ipv4Netz netz)
    {
        int p = netz.praefix();
        StringBuilder text = new StringBuilder();
        zeile(text, "Adresse", alsText(netz.adresse()));
        zeile(text, "Maske", alsText(netz.maske()) + " (/" + p + ")");
        zeile(text, "Wildcard", alsText(netz.wildcard()));
        zeile(text, "Netzadresse", alsText(netz.netzadresse()));
        zeile(text, "Broadcast", alsText(netz.broadcast()));
        zeile(text, "Erster Host", alsText(netz.ersterHost()));
        zeile(text, "Letzter Host", alsText(netz.letzterHost()));
        zeile(text, "Adressen", zahl(netz.anzahlAdressen()));
        zeile(text, "Nutzbare Hosts", zahl(netz.anzahlHosts()));

        String hinweis = hinweis(p);
        if (!hinweis.isEmpty())
        {
            text.append('\n').append(hinweis).append('\n');
        }

        text.append("\nBinär (Netzteil | Hostteil)\n");
        zeile(text, "Adresse", binaer(netz.adresse(), p));
        zeile(text, "Maske", binaer(netz.maske(), p));
        zeile(text, "Netzadresse", binaer(netz.netzadresse(), p));
        zeile(text, "Broadcast", binaer(netz.broadcast(), p));
        return text.toString();
    }

    public static String hinweis(int praefix)
    {
        return switch (praefix)
        {
            case 31 -> "Hinweis: /31 ist ein Point-to-Point-Netz (RFC 3021) – beide Adressen sind nutzbar, es gibt keinen Broadcast.";
            case 32 -> "Hinweis: /32 ist eine einzelne Hostadresse.";
            case 0 -> "Hinweis: /0 umfasst den gesamten IPv4-Adressraum (Default-Route).";
            default -> "";
        };
    }

    public static String teilnetze(Teilung teilung)
    {
        Ipv4Netz probe = teilung.netze().getFirst();
        StringBuilder text = new StringBuilder();
        text.append(alsText(teilung.ausgang().netzadresse())).append('/').append(teilung.ausgang().praefix())
                .append(" in ").append(zahl(teilung.anzahl())).append(" Teilnetze → je /").append(teilung.neuerPraefix())
                .append(", ").append(zahl(probe.anzahlHosts())).append(" nutzbare Hosts\n");
        text.append("Neue Maske: ").append(alsText(probe.maske())).append('\n');

        if (teilung.wurdeAufgerundet())
        {
            text.append("Hinweis: ").append(teilung.gewuenscht()).append(" ist keine Zweierpotenz, aufgerundet auf ")
                    .append(zahl(teilung.anzahl())).append(".\n");
        }
        String hinweis = hinweis(teilung.neuerPraefix());
        if (!hinweis.isEmpty())
        {
            text.append(hinweis).append('\n');
        }

        text.append('\n').append(String.format("%-6s%-20s%-17s%s\n", "Nr", "Netz", "Broadcast", "Hostbereich"));
        int nummer = 1;
        for (Ipv4Netz netz : teilung.netze())
        {
            text.append(String.format("%-6d%-20s%-17s%s – %s\n", nummer++,
                    alsText(netz.netzadresse()) + "/" + netz.praefix(), alsText(netz.broadcast()),
                    alsText(netz.ersterHost()), alsText(netz.letzterHost())));
        }

        if (teilung.istGekuerzt())
        {
            text.append("… nur die ersten ").append(Ipv4Rechner.MAX_ANGEZEIGTE_TEILNETZE).append(" von ")
                    .append(zahl(teilung.anzahl())).append(" Teilnetzen angezeigt.\n");
        }
        return text.toString();
    }

    public static String maske(int praefix)
    {
        int maske = Ipv4Netz.maske(praefix);
        StringBuilder text = new StringBuilder();
        zeile(text, "CIDR", "/" + praefix);
        zeile(text, "Maske", alsText(maske));
        zeile(text, "Wildcard", alsText(~maske));
        zeile(text, "Binär", binaer(maske, praefix));
        zeile(text, "Adressen", zahl(new Ipv4Netz(0, praefix).anzahlAdressen()));
        zeile(text, "Nutzbare Hosts", zahl(new Ipv4Netz(0, praefix).anzahlHosts()));
        return text.toString();
    }

    public static String ipv6(String gekuerzt, String ausgeschrieben)
    {
        StringBuilder text = new StringBuilder();
        zeile(text, "Gekürzt", gekuerzt);
        zeile(text, "Ausgeschrieben", ausgeschrieben);
        text.append("\nRegeln (RFC 5952): führende Nullen weg, längste Nullfolge (ab 2 Gruppen) wird „::“,\n")
                .append("bei Gleichstand die erste, Kleinbuchstaben.\n");
        return text.toString();
    }

    public static String zahl(long wert)
    {
        return String.format(Locale.GERMANY, "%,d", wert);
    }

    private static void zeile(StringBuilder text, String name, String wert)
    {
        text.append(String.format("%-16s%s\n", name, wert));
    }
}
