package common.history;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerlaufExportTest
{
    private static final LocalDateTime ZEITPUNKT = LocalDateTime.of(2026, 5, 12, 10, 30);

    @Test
    void alsText_ShouldWriteOneLinePerEntryAndMarkFavorites()
    {
        // Arrange
        List<VerlaufEintrag> eintraege = List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, ZEITPUNKT, false),
                new VerlaufEintrag("sin(90)", "1", RechnerModus.WISSENSCHAFTLICH, ZEITPUNKT, true)
        );

        // Act
        String text = VerlaufExport.alsText(eintraege);

        // Assert
        assertEquals("[Standard] 12.05.2026 10:30 · 2+3 = 5\r\n"
                + "★ [Wissenschaftlich] 12.05.2026 10:30 · sin(90) = 1\r\n", text);
    }

    @Test
    void alsCsv_ShouldWriteHeaderAndColumns()
    {
        // Arrange
        List<VerlaufEintrag> eintraege = List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, ZEITPUNKT, true)
        );

        // Act
        String csv = VerlaufExport.alsCsv(eintraege);

        // Assert
        assertEquals("Zeitpunkt;Modus;Ausdruck;Ergebnis;Favorit\r\n"
                + "12.05.2026 10:30;Standard;2+3;5;ja\r\n", csv);
    }

    @Test
    void alsCsv_ShouldQuoteFieldsWithSeparatorOrQuotes()
    {
        // Arrange
        List<VerlaufEintrag> eintraege = List.of(
                new VerlaufEintrag("a;b", "\"x\"", RechnerModus.STANDARD, ZEITPUNKT, false)
        );

        // Act
        String csv = VerlaufExport.alsCsv(eintraege);

        // Assert
        assertTrue(csv.endsWith("12.05.2026 10:30;Standard;\"a;b\";\"\"\"x\"\"\";nein\r\n"));
    }

    @Test
    void export_ShouldOnlyContainHeader_WhenListIsEmpty()
    {
        assertEquals("", VerlaufExport.alsText(List.of()));
        assertEquals("Zeitpunkt;Modus;Ausdruck;Ergebnis;Favorit\r\n", VerlaufExport.alsCsv(List.of()));
    }
}
