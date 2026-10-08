package modes.finanz.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ergebnis einer Finanzrechnung: gerundeter Wert, fertige Anzeige und der Rechenweg,
 * damit das Ergebnis nicht wie aus einer schwarzen Box wirkt.
 */
public record FinanzErgebnis(BigDecimal wert, String anzeige, List<String> rechenweg)
{
    public FinanzErgebnis
    {
        rechenweg = List.copyOf(rechenweg);
    }
}
