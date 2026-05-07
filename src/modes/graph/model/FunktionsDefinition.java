package modes.graph.model;

import java.awt.Color;

public class FunktionsDefinition
{
    private final String name;
    private String ausdruck;
    private Color farbe;
    private boolean sichtbar = true;

    public FunktionsDefinition(String name, String ausdruck, Color farbe)
    {
        this.name = name;
        this.ausdruck = ausdruck;
        this.farbe = farbe;
    }

    public String getName()
    {
        return name;
    }

    public String getAusdruck()
    {
        return ausdruck;
    }

    public void setAusdruck(String ausdruck)
    {
        this.ausdruck = ausdruck;
    }

    public Color getFarbe()
    {
        return farbe;
    }

    public void setFarbe(Color farbe)
    {
        this.farbe = farbe;
    }

    public boolean isSichtbar()
    {
        return sichtbar;
    }

    public void setSichtbar(boolean sichtbar)
    {
        this.sichtbar = sichtbar;
    }
}
