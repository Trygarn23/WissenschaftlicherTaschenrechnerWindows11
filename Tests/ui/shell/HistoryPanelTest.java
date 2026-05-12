package ui.shell;

import common.history.VerlaufEintrag;
import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryPanelTest
{
    @Test
    void historyPanel_ShouldKeepLegacyAccess_WhenStructuredEntriesAreUsed()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        VerlaufEintrag eintrag = new VerlaufEintrag(
                "sin(90)",
                "1",
                RechnerModus.WISSENSCHAFTLICH,
                LocalDateTime.of(2026, 5, 12, 10, 30),
                false
        );

        // Act
        panel.setAllStructuredEntries(List.of(eintrag));

        // Assert
        assertEquals(List.of("sin(90) = 1"), panel.getAllEntries());
        assertEquals(List.of(eintrag), panel.getAllStructuredEntries());
    }

    @Test
    void historyPanel_ShouldSearchExpressionResultAndMode()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();
        panel.setAllStructuredEntries(List.of(
                new VerlaufEintrag("2+3", "5", RechnerModus.STANDARD, LocalDateTime.now(), false),
                new VerlaufEintrag("sin(90)", "1", RechnerModus.WISSENSCHAFTLICH, LocalDateTime.now(), false)
        ));

        // Act / Assert
        panel.setSearchTextForTest("sin");
        assertEquals(1, panel.getVisibleEntryCountForTest());

        panel.setSearchTextForTest("5");
        assertEquals(1, panel.getVisibleEntryCountForTest());

        panel.setSearchTextForTest("wissenschaftlich");
        assertEquals(1, panel.getVisibleEntryCountForTest());
    }

    @Test
    void historyPanel_ShouldReadLegacyEntriesAsStructuredEntries()
    {
        // Arrange
        HistoryPanel panel = new HistoryPanel();

        // Act
        panel.setAllEntries(List.of("7*6 = 42"));

        // Assert
        assertEquals(1, panel.getAllStructuredEntries().size());
        assertEquals("7*6", panel.getAllStructuredEntries().getFirst().getAusdruck());
        assertEquals("42", panel.getAllStructuredEntries().getFirst().getErgebnis());
        assertEquals(RechnerModus.STANDARD, panel.getAllStructuredEntries().getFirst().getModus());
    }
}
