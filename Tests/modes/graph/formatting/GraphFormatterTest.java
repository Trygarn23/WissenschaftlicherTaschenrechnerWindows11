package modes.graph.formatting;

import modes.graph.formatting.GraphFormatter;
import modes.graph.model.GraphPunkt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphFormatterTest
{
    private final GraphFormatter formatter = new GraphFormatter();

    @Test
    void formatiereZahl_ShouldDropTrailingZerosAndRoundIntegers()
    {
        // Act & Assert
        assertEquals("0", formatter.formatiereZahl(1e-12));
        assertEquals("3", formatter.formatiereZahl(3.0));
        assertEquals("-2", formatter.formatiereZahl(-2.0000000001));
        assertTrue(formatter.formatiereZahl(1.5).matches("1[,.]5"));
    }

    @Test
    void formatiereAchsenwert_ShouldUseOneDecimal()
    {
        // Act & Assert
        assertEquals("4", formatter.formatiereAchsenwert(4.0));
        assertTrue(formatter.formatiereAchsenwert(0.25).matches("0[,.][23]"));
    }

    @Test
    void formatierePunkte_ShouldJoinPointsOrReportEmptyList()
    {
        // Act & Assert
        assertEquals("keine gefunden", formatter.formatierePunkte(List.of()));
        assertEquals("(1 | 2), (3 | 4)", formatter.formatierePunkte(List.of(new GraphPunkt(1, 2), new GraphPunkt(3, 4))));
        assertEquals("nicht definiert", formatter.formatierePunkt(null));
    }
}
