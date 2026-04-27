package common.history;

import java.util.List;

public interface VerlaufRepository
{
    List<String> ladeEintraege();

    void speichereEintraege(List<String> eintraege);
}
