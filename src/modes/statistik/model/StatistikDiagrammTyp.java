package modes.statistik.model;

public enum StatistikDiagrammTyp
{
    HISTOGRAMM("Histogramm"),
    BOXPLOT("Boxplot"),
    STREUDIAGRAMM("Streudiagramm");

    private final String label;

    StatistikDiagrammTyp(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
