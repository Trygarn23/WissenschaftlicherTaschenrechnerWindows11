package common.history;

import common.state.RechnerModus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class VerlaufService
{
    private static final String STRUCTURED_PREFIX = "WT_HISTORY_V2";

    private final VerlaufRepository repository;

    public VerlaufService(VerlaufRepository repository)
    {
        this.repository = repository;
    }

    public List<String> ladeEintraege()
    {
        List<String> legacyEintraege = new ArrayList<>();
        for (VerlaufEintrag eintrag : ladeStrukturierteEintraege(RechnerModus.STANDARD))
        {
            legacyEintraege.add(formatiereLegacyEintrag(eintrag));
        }
        return legacyEintraege;
    }

    public void speichereEintraege(List<String> eintraege)
    {
        List<VerlaufEintrag> strukturierteEintraege = new ArrayList<>();
        for (String eintrag : eintraege)
        {
            strukturierteEintraege.add(parseEintrag(eintrag, RechnerModus.STANDARD));
        }
        speichereStrukturierteEintraege(strukturierteEintraege);
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

    public VerlaufEintrag erstelleEintragAusText(String text, RechnerModus modus)
    {
        return parseEintrag(text, modus == null ? RechnerModus.STANDARD : modus);
    }

    public List<VerlaufEintrag> ladeStrukturierteEintraege(RechnerModus fallbackModus)
    {
        RechnerModus modus = fallbackModus == null ? RechnerModus.STANDARD : fallbackModus;
        List<VerlaufEintrag> strukturierteEintraege = new ArrayList<>();

        for (String eintrag : repository.ladeEintraege())
        {
            strukturierteEintraege.add(parseEintrag(eintrag, modus));
        }

        return strukturierteEintraege;
    }

    public void speichereStrukturierteEintraege(List<VerlaufEintrag> eintraege)
    {
        List<String> formatierteEintraege = new ArrayList<>();

        for (VerlaufEintrag eintrag : eintraege)
        {
            if (eintrag != null)
            {
                formatierteEintraege.add(formatiereStrukturiertenEintrag(eintrag));
            }
        }

        repository.speichereEintraege(formatierteEintraege);
    }

    public List<VerlaufEintrag> suche(List<VerlaufEintrag> eintraege, String suchtext)
    {
        List<VerlaufEintrag> treffer = new ArrayList<>();
        for (VerlaufEintrag eintrag : eintraege)
        {
            if (eintrag != null && eintrag.matchesSuchtext(suchtext))
            {
                treffer.add(eintrag);
            }
        }
        return treffer;
    }

    public String formatiereLegacyEintrag(VerlaufEintrag eintrag)
    {
        return eintrag == null ? "" : eintrag.toLegacyText();
    }

    String formatiereStrukturiertenEintrag(VerlaufEintrag eintrag)
    {
        return String.join("\t",
                STRUCTURED_PREFIX,
                encode(eintrag.getAusdruck()),
                encode(eintrag.getErgebnis()),
                eintrag.getModus().name(),
                eintrag.getZeitpunkt().toString(),
                Boolean.toString(eintrag.isFavorit())
        );
    }

    private VerlaufEintrag parseEintrag(String raw, RechnerModus fallbackModus)
    {
        String text = nullZuLeer(raw).trim();
        if (text.startsWith(STRUCTURED_PREFIX + "\t"))
        {
            VerlaufEintrag strukturierterEintrag = parseStrukturierterEintrag(text);
            if (strukturierterEintrag != null)
            {
                return strukturierterEintrag;
            }
        }

        return parseLegacyEintrag(text, fallbackModus);
    }

    private VerlaufEintrag parseStrukturierterEintrag(String raw)
    {
        String[] parts = raw.split("\t", -1);
        if (parts.length != 6)
        {
            return null;
        }

        try
        {
            return new VerlaufEintrag(
                    decode(parts[1]),
                    decode(parts[2]),
                    RechnerModus.valueOf(parts[3]),
                    LocalDateTime.parse(parts[4]),
                    Boolean.parseBoolean(parts[5])
            );
        }
        catch (IllegalArgumentException | DateTimeParseException ignored)
        {
            return null;
        }
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

    private String encode(String text)
    {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(nullZuLeer(text).getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String text)
    {
        return new String(Base64.getUrlDecoder().decode(text), StandardCharsets.UTF_8);
    }

    private String nullZuLeer(String text)
    {
        return text == null ? "" : text;
    }
}
