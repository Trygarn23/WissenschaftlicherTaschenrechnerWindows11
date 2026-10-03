package modes.matrix.formatting;

import common.formatting.ZahlenAnzeige;
import modes.matrix.model.Matrix;

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
}
