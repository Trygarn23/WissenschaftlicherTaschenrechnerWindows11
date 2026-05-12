package common.history;

import common.state.RechnerModus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class VerlaufEintrag
{
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final String ausdruck;
    private final String ergebnis;
    private final RechnerModus modus;
    private final LocalDateTime zeitpunkt;
    private final boolean favorit;

    public VerlaufEintrag(String ausdruck, String ergebnis, RechnerModus modus, LocalDateTime zeitpunkt, boolean favorit)
    {
        this.ausdruck = Objects.requireNonNull(ausdruck, "ausdruck");
        this.ergebnis = Objects.requireNonNull(ergebnis, "ergebnis");
        this.modus = Objects.requireNonNull(modus, "modus");
        this.zeitpunkt = Objects.requireNonNull(zeitpunkt, "zeitpunkt");
        this.favorit = favorit;
    }

    public String getAusdruck()
    {
        return ausdruck;
    }

    public String getErgebnis()
    {
        return ergebnis;
    }

    public RechnerModus getModus()
    {
        return modus;
    }

    public LocalDateTime getZeitpunkt()
    {
        return zeitpunkt;
    }

    public boolean isFavorit()
    {
        return favorit;
    }

    public String toLegacyText()
    {
        if (ergebnis.isBlank())
        {
            return ausdruck;
        }

        return ausdruck + " = " + ergebnis;
    }

    public String toDisplayText()
    {
        return "[" + modus.getLabel() + "] "
                + zeitpunkt.format(DISPLAY_FORMATTER)
                + " · "
                + toLegacyText();
    }

    public boolean matchesSuchtext(String suchtext)
    {
        if (suchtext == null || suchtext.isBlank())
        {
            return true;
        }

        String query = suchtext.toLowerCase();
        return ausdruck.toLowerCase().contains(query)
                || ergebnis.toLowerCase().contains(query)
                || modus.name().toLowerCase().contains(query)
                || modus.getLabel().toLowerCase().contains(query)
                || toLegacyText().toLowerCase().contains(query);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof VerlaufEintrag that)) return false;
        return favorit == that.favorit
                && ausdruck.equals(that.ausdruck)
                && ergebnis.equals(that.ergebnis)
                && modus == that.modus
                && zeitpunkt.equals(that.zeitpunkt);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(ausdruck, ergebnis, modus, zeitpunkt, favorit);
    }

    @Override
    public String toString()
    {
        return "VerlaufEintrag{"
                + "ausdruck='" + ausdruck + '\''
                + ", ergebnis='" + ergebnis + '\''
                + ", modus=" + modus
                + ", zeitpunkt=" + zeitpunkt
                + ", favorit=" + favorit
                + '}';
    }
}
