package modes.graph.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphState
{
    private static final double MIN_SPAN = 1.0;
    private static final double MAX_SPAN = 200.0;

    private final List<FunktionsDefinition> funktionen = new ArrayList<>();

    private double xMin = -10.0;
    private double xMax = 10.0;
    private double yMin = -10.0;
    private double yMax = 10.0;

    public GraphState()
    {
        funktionen.add(new FunktionsDefinition("f", "sin(x)", new Color(24, 153, 219)));
    }

    public List<FunktionsDefinition> getFunktionen()
    {
        return Collections.unmodifiableList(funktionen);
    }

    public FunktionsDefinition getHauptfunktion()
    {
        return funktionen.get(0);
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

    public void resetAnsicht()
    {
        setBereich(-10.0, 10.0, -10.0, 10.0);
    }

    private double begrenzeSpan(double span)
    {
        return Math.max(MIN_SPAN, Math.min(MAX_SPAN, span));
    }
}
