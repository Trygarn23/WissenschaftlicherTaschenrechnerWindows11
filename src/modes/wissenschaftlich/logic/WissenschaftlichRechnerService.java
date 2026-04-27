package modes.wissenschaftlich.logic;

import common.logic.RechnerService;

public class WissenschaftlichRechnerService extends RechnerService
{
    private final WissenschaftlichOperationen wissenschaftlichOperationen;

    public WissenschaftlichRechnerService()
    {
        super();
        this.wissenschaftlichOperationen = new WissenschaftlichOperationen(getAusdruckEditor());
    }

    public String zehnHoch()
    {
        return wissenschaftlichOperationen.zehnHoch();
    }

    public String ln()
    {
        return wissenschaftlichOperationen.ln();
    }

    public String log()
    {
        return wissenschaftlichOperationen.log();
    }

    public String sin()
    {
        return wissenschaftlichOperationen.sin();
    }

    public String cos()
    {
        return wissenschaftlichOperationen.cos();
    }

    public String tan()
    {
        return wissenschaftlichOperationen.tan();
    }

    public String arcsin()
    {
        return wissenschaftlichOperationen.arcsin();
    }

    public String arccos()
    {
        return wissenschaftlichOperationen.arccos();
    }

    public String arctan()
    {
        return wissenschaftlichOperationen.arctan();
    }

    public String sinusHyperbolicus()
    {
        return wissenschaftlichOperationen.sinusHyperbolicus();
    }

    public String cosinusHyperbolicus()
    {
        return wissenschaftlichOperationen.cosinusHyperbolicus();
    }

    public String tangensHyperbolicus()
    {
        return wissenschaftlichOperationen.tangensHyperbolicus();
    }

    public String exp()
    {
        return wissenschaftlichOperationen.exp();
    }

    public String betrag()
    {
        return wissenschaftlichOperationen.betrag();
    }

    public String abrunden()
    {
        return wissenschaftlichOperationen.abrunden();
    }

    public String aufrunden()
    {
        return wissenschaftlichOperationen.aufrunden();
    }

    public String runden()
    {
        return wissenschaftlichOperationen.runden();
    }

    public String zufall()
    {
        return wissenschaftlichOperationen.zufall();
    }

    public String fakultaet()
    {
        return wissenschaftlichOperationen.fakultaet();
    }

    public String pi()
    {
        return wissenschaftlichOperationen.pi();
    }

    public String e()
    {
        return wissenschaftlichOperationen.e();
    }
}
