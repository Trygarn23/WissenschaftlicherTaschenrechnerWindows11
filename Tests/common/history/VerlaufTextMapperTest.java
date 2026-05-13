package common.history;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerlaufTextMapperTest
{
    @Test
    void ausLegacyText_ShouldParseExpressionAndResultWithOrWithoutSpaces()
    {
        VerlaufEintrag spaced = VerlaufTextMapper.ausLegacyText("2+3 = 5", RechnerModus.STANDARD);
        VerlaufEintrag compact = VerlaufTextMapper.ausLegacyText("sin(90)=1", RechnerModus.WISSENSCHAFTLICH);

        assertEquals("2+3", spaced.getAusdruck());
        assertEquals("5", spaced.getErgebnis());
        assertEquals(RechnerModus.STANDARD, spaced.getModus());

        assertEquals("sin(90)", compact.getAusdruck());
        assertEquals("1", compact.getErgebnis());
        assertEquals(RechnerModus.WISSENSCHAFTLICH, compact.getModus());
    }

    @Test
    void zuLegacyText_ShouldKeepNullSafe()
    {
        assertEquals("", VerlaufTextMapper.zuLegacyText(null));
    }
}
