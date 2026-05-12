import common.history.VerlaufEintrag;
import common.history.VerlaufRepository;
import common.history.VerlaufService;
import common.state.RechnerModus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VerlaufServiceTest
{
    @Test
    void ladeStrukturierteEintraege_ShouldPrepareLegacyHistoryAsStructuredEntries()
    {
        // Arrange
        InMemoryVerlaufRepository repository = new InMemoryVerlaufRepository();
        repository.speichereEintraege(List.of("2+3 = 5"));
        VerlaufService service = new VerlaufService(repository);

        // Act
        List<VerlaufEintrag> eintraege = service.ladeStrukturierteEintraege(RechnerModus.WISSENSCHAFTLICH);

        // Assert
        assertEquals(1, eintraege.size());
        assertEquals("2+3", eintraege.getFirst().getAusdruck());
        assertEquals("5", eintraege.getFirst().getErgebnis());
        assertEquals(RechnerModus.WISSENSCHAFTLICH, eintraege.getFirst().getModus());
        assertNotNull(eintraege.getFirst().getZeitpunkt());
        assertFalse(eintraege.getFirst().isFavorit());
    }

    @Test
    void speichereStrukturierteEintraege_ShouldUseStructuredFormatAndRemainReadable()
    {
        // Arrange
        InMemoryVerlaufRepository repository = new InMemoryVerlaufRepository();
        VerlaufService service = new VerlaufService(repository);
        VerlaufEintrag eintrag = service.erstelleEintrag("6*7", "42", RechnerModus.STANDARD);

        // Act
        service.speichereStrukturierteEintraege(List.of(eintrag));
        List<VerlaufEintrag> geladen = service.ladeStrukturierteEintraege(RechnerModus.WISSENSCHAFTLICH);

        // Assert
        assertEquals(1, repository.ladeEintraege().size());
        assertTrue(repository.ladeEintraege().getFirst().startsWith("WT_HISTORY_V2\t"));
        assertEquals(1, geladen.size());
        assertEquals("6*7", geladen.getFirst().getAusdruck());
        assertEquals("42", geladen.getFirst().getErgebnis());
        assertEquals(RechnerModus.STANDARD, geladen.getFirst().getModus());
    }

    @Test
    void ladeEintraege_ShouldExposeLegacyText_WhenStructuredEntriesAreStored()
    {
        // Arrange
        InMemoryVerlaufRepository repository = new InMemoryVerlaufRepository();
        VerlaufService service = new VerlaufService(repository);
        service.speichereStrukturierteEintraege(List.of(service.erstelleEintrag("8/2", "4", RechnerModus.STANDARD)));

        // Act / Assert
        assertEquals(List.of("8/2 = 4"), service.ladeEintraege());
    }

    @Test
    void suche_ShouldMatchExpressionResultAndMode()
    {
        // Arrange
        VerlaufService service = new VerlaufService(new InMemoryVerlaufRepository());
        List<VerlaufEintrag> eintraege = List.of(
                service.erstelleEintrag("2+3", "5", RechnerModus.STANDARD),
                service.erstelleEintrag("sin(90)", "1", RechnerModus.WISSENSCHAFTLICH)
        );

        // Act / Assert
        assertEquals(1, service.suche(eintraege, "sin").size());
        assertEquals(1, service.suche(eintraege, "5").size());
        assertEquals(1, service.suche(eintraege, "wissenschaftlich").size());
    }

    private static class InMemoryVerlaufRepository implements VerlaufRepository
    {
        private List<String> eintraege = new ArrayList<>();

        @Override
        public List<String> ladeEintraege()
        {
            return eintraege;
        }

        @Override
        public void speichereEintraege(List<String> eintraege)
        {
            this.eintraege = new ArrayList<>(eintraege);
        }
    }
}
