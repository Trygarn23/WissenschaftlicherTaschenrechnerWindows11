package modes.matrix.formatting;

import common.formatting.ZahlenAnzeige;
import modes.matrix.model.Matrix;
import modes.matrix.model.RechenSchritt;

import java.util.List;

public class MatrixFormatter
{
    public String formatiere(Matrix matrix)
    {
        StringBuilder builder = new StringBuilder();
        for (int z = 0; z < matrix.getZeilen(); z++)
        {
            if (z > 0)
            {
                builder.append(System.lineSeparator());
            }

            builder.append("[ ");
            for (int s = 0; s < matrix.getSpalten(); s++)
            {
                if (s > 0)
                {
                    builder.append("  ");
                }
                builder.append(formatiereDouble(matrix.get(z, s)));
            }
            builder.append(" ]");
        }
        return builder.toString();
    }

    public String formatiereDouble(double wert)
    {
        return ZahlenAnzeige.formatiere(wert);
    }

    /** Spaltenvektor als „x1 = …“-Zeilen. */
    public String formatiereLoesung(Matrix vektor)
    {
        StringBuilder builder = new StringBuilder();
        for (int z = 0; z < vektor.getZeilen(); z++)
        {
            if (z > 0)
            {
                builder.append(System.lineSeparator());
            }
            builder.append("x").append(z + 1).append(" = ").append(formatiereDouble(vektor.get(z, 0)));
        }
        return builder.toString();
    }

    public String formatiereSchritte(List<RechenSchritt> schritte)
    {
        StringBuilder builder = new StringBuilder("Rechenschritte:");
        for (int i = 0; i < schritte.size(); i++)
        {
            RechenSchritt schritt = schritte.get(i);
            builder.append(System.lineSeparator()).append(System.lineSeparator())
                    .append(i + 1).append(". ").append(schritt.beschreibung())
                    .append(System.lineSeparator()).append(formatiere(schritt.zwischenstand()));
        }
        return builder.toString();
    }
}
