package modes.wissenschaftlich.logic;

import common.logic.AusdruckEditor;

public class WissenschaftlichOperationen
{
    private final AusdruckEditor ausdruckEditor;

    public WissenschaftlichOperationen(AusdruckEditor ausdruckEditor)
    {
        this.ausdruckEditor = ausdruckEditor;
    }

    public String zehnHoch()
    {
        return ausdruckEditor.praefixOperatorEinfuegenOderUmklammern("10^(");
    }

    public String ln()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("ln");
    }

    public String log()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("log");
    }

    public String sin()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("sin");
    }

    public String cos()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("cos");
    }

    public String tan()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("tan");
    }

    public String arcsin()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("asin");
    }

    public String arccos()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("acos");
    }

    public String arctan()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("atan");
    }

    public String sinusHyperbolicus()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("sinh");
    }

    public String cosinusHyperbolicus()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("cosh");
    }

    public String tangensHyperbolicus()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("tanh");
    }

    public String exp()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("exp");
    }

    public String betrag()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("abs");
    }

    public String abrunden()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("floor");
    }

    public String aufrunden()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("ceil");
    }

    public String runden()
    {
        return ausdruckEditor.funktionEinfuegenOderUmklammern("round");
    }

    public String zufall()
    {
        return ausdruckEditor.funktionOhneArgumenteEinfuegen("rand");
    }

    public String fakultaet()
    {
        return ausdruckEditor.fakultaet();
    }

    public String pi()
    {
        return ausdruckEditor.konstanteEinsetzen(Math.PI);
    }

    public String e()
    {
        return ausdruckEditor.konstanteEinsetzen(Math.E);
    }
}
