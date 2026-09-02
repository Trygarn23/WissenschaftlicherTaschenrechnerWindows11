package modes.graph.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphState
{
    private static final double MIN_SPAN = 1.0;
    private static final double MAX_SPAN = 200.0;
    private static final Color[] FUNKTIONS_FARBEN = {
            new Color(24, 153, 219),
            new Color(240, 92, 146),
            new Color(76, 190, 120),
            new Color(255, 170, 55),
            new Color(155, 110, 230),
            new Color(35, 190, 190),
            new Color(50,50,235),
            new Color(150,170,200),
    };

    private final List<FunktionsDefinition> funktionen = new ArrayList<>();
    private int aktiveFunktionIndex;
    private int naechsteFunktionNummer;

    private double xMin = -10.0;
    private double xMax = 10.0;
    private double yMin = -10.0;
    private double yMax = 10.0;

    public GraphState()
    {
        funktionen.add(neueFunktion("sin(x)"));
        funktionen.add(neueFunktion("x^2-4"));
    }

    public List<FunktionsDefinition> getFunktionen()
    {
        return Collections.unmodifiableList(funktionen);
    }

    public FunktionsDefinition getHauptfunktion()
    {
        return funktionen.get(0);
    }

    public FunktionsDefinition getAktiveFunktion()
    {
        return funktionen.get(aktiveFunktionIndex);
    }

    public int getAktiveFunktionIndex()
    {
        return aktiveFunktionIndex;
    }

    public void setAktiveFunktion(int index)
    {
        if (index >= 0 && index < funktionen.size())
        {
            aktiveFunktionIndex = index;
        }
    }

    public FunktionsDefinition getFunktion(int index)
    {
        return funktionen.get(index);
    }

    public FunktionsDefinition fuegeFunktionHinzu(String ausdruck)
    {
        FunktionsDefinition funktion = neueFunktion(ausdruck == null ? "" : ausdruck);
        funktionen.add(funktion);
        aktiveFunktionIndex = funktionen.size() - 1;
        return funktion;
    }

    public boolean entferneFunktion(int index)
    {
        if (funktionen.size() <= 1 || index < 0 || index >= funktionen.size())
        {
            return false;
        }

        funktionen.remove(index);
        if (aktiveFunktionIndex > index)
        {
            aktiveFunktionIndex--;
        }
        else if (aktiveFunktionIndex >= funktionen.size())
        {
            aktiveFunktionIndex = funktionen.size() - 1;
        }
        return true;
    }

    public double getXMin()
    {
        return xMin;
    }

    public double getXMax()
    {
        return xMax;
    }

    public double getYMin()
    {
        return yMin;
    }

    public double getYMax()
    {
        return yMax;
    }

    public void setBereich(double xMin, double xMax, double yMin, double yMax)
    {
        if (xMax <= xMin || yMax <= yMin)
        {
            return;
        }

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void zoom(double faktor)
    {
        if (faktor <= 0.0)
        {
            return;
        }

        double xCenter = (xMin + xMax) / 2.0;
        double yCenter = (yMin + yMax) / 2.0;
        double xSpan = begrenzeSpan((xMax - xMin) * faktor);
        double ySpan = begrenzeSpan((yMax - yMin) * faktor);
        setBereich(xCenter - xSpan / 2.0, xCenter + xSpan / 2.0, yCenter - ySpan / 2.0, yCenter + ySpan / 2.0);
    }

    public void verschiebe(double deltaX, double deltaY)
    {
        setBereich(xMin + deltaX, xMax + deltaX, yMin + deltaY, yMax + deltaY);
    }

    public void resetAnsicht()
    {
        setBereich(-10.0, 10.0, -10.0, 10.0);
    }

    private double begrenzeSpan(double span)
    {
        return Math.max(MIN_SPAN, Math.min(MAX_SPAN, span));
    }

    private FunktionsDefinition neueFunktion(String ausdruck)
    {
        int nummer = naechsteFunktionNummer++;
        String name = nummer < 21 ? Character.toString((char) ('f' + nummer)) : "f" + (nummer + 1);
        Color farbe = FUNKTIONS_FARBEN[nummer % FUNKTIONS_FARBEN.length];
        return new FunktionsDefinition(name, ausdruck, farbe);
    }
}
