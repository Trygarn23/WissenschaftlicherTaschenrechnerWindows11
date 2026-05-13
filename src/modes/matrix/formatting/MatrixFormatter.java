package modes.matrix.formatting;

import modes.matrix.model.Matrix;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MatrixFormatter
{
    private final DecimalFormat format;

    public MatrixFormatter()
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator(',');
        format = new DecimalFormat("#,##0.##########", symbols);
    }

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
        if (Math.abs(wert) < 1e-10)
        {
            return "0";
        }
        return format.format(wert);
    }
}
