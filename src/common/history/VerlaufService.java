package common.history;

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
}
