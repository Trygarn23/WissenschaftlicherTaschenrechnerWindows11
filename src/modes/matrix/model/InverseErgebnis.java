package modes.matrix.model;

import java.util.List;

public record InverseErgebnis(Matrix inverse, List<RechenSchritt> schritte)
{
    public InverseErgebnis
    {
        schritte = List.copyOf(schritte);
    }
}
