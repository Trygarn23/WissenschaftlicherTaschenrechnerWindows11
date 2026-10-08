package common.history;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerlaufJsonTest
{
    private static final LocalDateTime ZEITPUNKT = LocalDateTime.of(2026, 5, 12, 10, 30);

    @Test
    void schreibeUndLese_ShouldKeepAllFieldsIncludingSpecialCharacters()
    {
        // Arrange
        List<VerlaufEintrag> eintraege = List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, ZEITPUNKT, false),
                new VerlaufEintrag("\"x\"\\ √2\n\u0001", "1,414", RechnerModus.WISSENSCHAFTLICH, ZEITPUNKT.plusMinutes(1), true)
        );

        // Act
        List<VerlaufEintrag> gelesen = VerlaufJson.lese(VerlaufJson.schreibe(eintraege));

        // Assert
        assertEquals(eintraege, gelesen);
    }

    @Test
    void lese_ShouldUseDefaultsForMissingOrUnknownValues()
    {
        // Act
        List<VerlaufEintrag> gelesen = VerlaufJson.lese(
                "\uFEFF[{\"ausdruck\": \"1+1\", \"modus\": \"GIBTSNICHT\", \"zeitpunkt\": \"kaputt\", \"extra\": [1, 2.5e3, null]}]");

        // Assert
        assertEquals(1, gelesen.size());
        assertEquals("1+1", gelesen.get(0).getAusdruck());
        assertEquals("", gelesen.get(0).getErgebnis());
        assertEquals(RechnerModus.STANDARD, gelesen.get(0).getModus());
        assertFalse(gelesen.get(0).isFavorit());
    }

    @Test
    void lese_ShouldAcceptEmptyListAndRejectBrokenJson()
    {
        // Act & Assert
        assertTrue(VerlaufJson.lese(" [ ] ").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> VerlaufJson.lese("[{\"ausdruck\": \"1+1\""));
        assertThrows(IllegalArgumentException.class, () -> VerlaufJson.lese("{\"ausdruck\": \"1\"}"));
        assertThrows(IllegalArgumentException.class, () -> VerlaufJson.lese(""));
        assertThrows(IllegalArgumentException.class, () -> VerlaufJson.lese("[1] Müll"));
        assertThrows(IllegalArgumentException.class, () -> VerlaufJson.lese("[\"\\u12\"]"));
    }
}
