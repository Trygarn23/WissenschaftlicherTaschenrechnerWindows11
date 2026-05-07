package modes.graph.model;

import java.util.List;

public class KurvendiskussionResult
{
    private final GraphPunkt yAchsenSchnittpunkt;
    private final List<GraphPunkt> nullstellen;
    private final List<GraphPunkt> extremstellen;
    private final List<GraphPunkt> wendestellen;

    public KurvendiskussionResult(
            GraphPunkt yAchsenSchnittpunkt,
            List<GraphPunkt> nullstellen,
            List<GraphPunkt> extremstellen,
            List<GraphPunkt> wendestellen)
    {
        this.yAchsenSchnittpunkt = yAchsenSchnittpunkt;
        this.nullstellen = List.copyOf(nullstellen);
        this.extremstellen = List.copyOf(extremstellen);
        this.wendestellen = List.copyOf(wendestellen);
    }

    public GraphPunkt getYAchsenSchnittpunkt()
    {
        return yAchsenSchnittpunkt;
    }

    public List<GraphPunkt> getNullstellen()
    {
        return nullstellen;
    }

    public List<GraphPunkt> getExtremstellen()
    {
        return extremstellen;
    }

    public List<GraphPunkt> getWendestellen()
    {
        return wendestellen;
    }
}
