package common.history;

import common.state.RechnerModus;

import java.time.LocalDateTime;

public final class VerlaufTextMapper
{
    private VerlaufTextMapper()
    {
    }

    public static VerlaufEintrag ausLegacyText(String raw, RechnerModus fallbackModus)
    {
        RechnerModus modus = fallbackModus == null ? RechnerModus.STANDARD : fallbackModus;
        String text = raw == null ? "" : raw.trim();
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

    public static String zuLegacyText(VerlaufEintrag eintrag)
    {
        return eintrag == null ? "" : eintrag.toLegacyText();
    }
}
