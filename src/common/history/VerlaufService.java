package common.history;

import common.state.RechnerModus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VerlaufService
{
    private final VerlaufRepository repository;

    public VerlaufService(VerlaufRepository repository)
    {
        this.repository = repository;
    }

    public List<String> ladeEintraege()
    {
        return repository.ladeEintraege();
    }

    public void speichereEintraege(List<String> eintraege)
    {
        repository.speichereEintraege(eintraege);
    }

    public VerlaufEintrag erstelleEintrag(String ausdruck, String ergebnis, RechnerModus modus)
    {
        return new VerlaufEintrag(
                nullZuLeer(ausdruck),
                nullZuLeer(ergebnis),
                modus == null ? RechnerModus.STANDARD : modus,
                LocalDateTime.now(),
                false
        );
    }

    public List<VerlaufEintrag> ladeStrukturierteEintraege(RechnerModus fallbackModus)
    {
        RechnerModus modus = fallbackModus == null ? RechnerModus.STANDARD : fallbackModus;
        List<VerlaufEintrag> strukturierteEintraege = new ArrayList<>();

        for (String eintrag : ladeEintraege())
        {
            strukturierteEintraege.add(parseLegacyEintrag(eintrag, modus));
        }

        return strukturierteEintraege;
    }

    public void speichereStrukturierteEintraege(List<VerlaufEintrag> eintraege)
    {
        List<String> legacyEintraege = new ArrayList<>();

        for (VerlaufEintrag eintrag : eintraege)
        {
            legacyEintraege.add(formatiereLegacyEintrag(eintrag));
        }

        speichereEintraege(legacyEintraege);
    }

    public String formatiereLegacyEintrag(VerlaufEintrag eintrag)
    {
        return eintrag == null ? "" : eintrag.toLegacyText();
    }

    private VerlaufEintrag parseLegacyEintrag(String raw, RechnerModus modus)
    {
        String text = nullZuLeer(raw).trim();
        int separator = text.lastIndexOf(" = ");
        int offset = 3;

        if (separator < 0)
        {
            separator = text.lastIndexOf('=');
            offset = 1;
        }

        if (separator < 0)
        {
            return new VerlaufEintrag(text, "", modus, LocalDateTime.now(), false);
        }

        String ausdruck = text.substring(0, separator).trim();
        String ergebnis = text.substring(separator + offset).trim();
        return new VerlaufEintrag(ausdruck, ergebnis, modus, LocalDateTime.now(), false);
    }

    private String nullZuLeer(String text)
    {
        return text == null ? "" : text;
    }
}
