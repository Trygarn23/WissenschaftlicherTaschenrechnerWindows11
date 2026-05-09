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
        assertEquals("2+3", eintraege.get(0).getAusdruck());
        assertEquals("5", eintraege.get(0).getErgebnis());
        assertEquals(RechnerModus.WISSENSCHAFTLICH, eintraege.get(0).getModus());
        assertFalse(eintraege.get(0).isFavorit());
    }

    @Test
    void speichereStrukturierteEintraege_ShouldKeepLegacyStringHistoryCompatible()
    {
        // Arrange
        InMemoryVerlaufRepository repository = new InMemoryVerlaufRepository();
        VerlaufService service = new VerlaufService(repository);
        VerlaufEintrag eintrag = service.erstelleEintrag("6*7", "42", RechnerModus.STANDARD);

        // Act
        service.speichereStrukturierteEintraege(List.of(eintrag));

        // Assert
        assertEquals(List.of("6*7 = 42"), repository.ladeEintraege());
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
