package modes.statistik.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VerteilungenTest
{
    @Test
    void binomial_ShouldMatchExactValuesForSmallN()
    {
        // Act & Assert: B(10; 0,5), Werte als Brüche über 1024
        assertEquals(252.0 / 1024.0, Verteilungen.binomialGenau(10, 0.5, 5), 1e-12);
        assertEquals(638.0 / 1024.0, Verteilungen.binomialHoechstens(10, 0.5, 5), 1e-12);
        assertEquals(638.0 / 1024.0, Verteilungen.binomialMindestens(10, 0.5, 5), 1e-12);
        assertEquals(1.0, Verteilungen.binomialHoechstens(10, 0.5, 10), 1e-12);
    }

    @Test
    void binomialHoechstens_ShouldMatchAbiTable()
    {
        // Act & Assert: Tafelwert F(20; 0,3; 5) = 0,4164
        assertEquals(0.4164, Verteilungen.binomialHoechstens(20, 0.3, 5), 1e-4);
        assertEquals(0.9520, Verteilungen.binomialHoechstens(20, 0.3, 9), 1e-4);
    }

    @Test
    void binomial_ShouldStayAccurateForLargeN()
    {
        // Act
        double mitte = Verteilungen.binomialGenau(10_000, 0.5, 5_000);
        double haelfte = Verteilungen.binomialHoechstens(1_001, 0.5, 500);

        // Assert: (1 - p)^n wäre hier ohne Logarithmus längst 0
        assertEquals(0.0079786461, mitte, 1e-9);
        assertEquals(0.5, haelfte, 1e-12);
    }

    @Test
    void binomial_ShouldHandleEdgeCases()
    {
        // Act & Assert
        assertEquals(1.0, Verteilungen.binomialGenau(5, 0.0, 0), 1e-12);
        assertEquals(0.0, Verteilungen.binomialGenau(5, 0.0, 1), 1e-12);
        assertEquals(1.0, Verteilungen.binomialGenau(5, 1.0, 5), 1e-12);
        assertEquals(0.0, Verteilungen.binomialGenau(5, 0.5, 6), 1e-12);
        assertEquals(0.0, Verteilungen.binomialGenau(5, 0.5, -1), 1e-12);
        assertEquals(1.0, Verteilungen.binomialMindestens(5, 0.5, 0), 1e-12);
    }

    @Test
    void binomialKennwerte_ShouldBeNpAndSqrtNpq()
    {
        // Act & Assert
        assertEquals(20.0, Verteilungen.binomialErwartungswert(100, 0.2), 1e-12);
        assertEquals(4.0, Verteilungen.binomialStandardabweichung(100, 0.2), 1e-12);
    }

    @Test
    void binomial_ShouldRejectInvalidParameters()
    {
        // Act
        IllegalArgumentException p = assertThrows(IllegalArgumentException.class,
                () -> Verteilungen.binomialGenau(10, 1.5, 2));
        IllegalArgumentException n = assertThrows(IllegalArgumentException.class,
                () -> Verteilungen.binomialGenau(-1, 0.5, 0));

        // Assert
        assertEquals("p muss zwischen 0 und 1 liegen.", p.getMessage());
        assertEquals("n darf nicht negativ sein.", n.getMessage());
    }

    @Test
    void normalVerteilung_ShouldMatchTableValues()
    {
        // Act & Assert
        assertEquals(0.5, Verteilungen.normalVerteilung(0, 1, 0), 1e-12);
        assertEquals(0.8413447461, Verteilungen.normalVerteilung(0, 1, 1), 1e-9);
        assertEquals(0.9750021049, Verteilungen.normalVerteilung(0, 1, 1.96), 1e-9);
        assertEquals(0.1586552539, Verteilungen.normalVerteilung(0, 1, -1), 1e-9);
        assertEquals(0.9772498681, Verteilungen.normalVerteilung(100, 15, 130), 1e-9);
        assertEquals(1.0, Verteilungen.normalVerteilung(0, 1, 50), 1e-12);
        assertEquals(0.0, Verteilungen.normalVerteilung(0, 1, -50), 1e-12);
    }

    @Test
    void normalIntervall_ShouldGiveSigmaRules()
    {
        // Act & Assert
        assertEquals(0.6826894921, Verteilungen.normalIntervall(50, 5, 45, 55), 1e-9);
        assertEquals(0.9544997361, Verteilungen.normalIntervall(0, 1, -2, 2), 1e-9);
    }

    @Test
    void normalQuantil_ShouldInvertDistribution()
    {
        // Act & Assert
        assertEquals(1.959963985, Verteilungen.normalQuantil(0, 1, 0.975), 1e-8);
        assertEquals(100.0, Verteilungen.normalQuantil(100, 15, 0.5), 1e-9);
        assertEquals(-1.644853627, Verteilungen.normalQuantil(0, 1, 0.05), 1e-8);
    }

    @Test
    void normal_ShouldRejectInvalidParameters()
    {
        // Act
        IllegalArgumentException sigma = assertThrows(IllegalArgumentException.class,
                () -> Verteilungen.normalVerteilung(0, 0, 1));
        IllegalArgumentException grenzen = assertThrows(IllegalArgumentException.class,
                () -> Verteilungen.normalIntervall(0, 1, 2, 1));
        IllegalArgumentException quantil = assertThrows(IllegalArgumentException.class,
                () -> Verteilungen.normalQuantil(0, 1, 1.0));

        // Assert
        assertEquals("σ muss größer als 0 sein.", sigma.getMessage());
        assertEquals("Die untere Grenze a darf nicht größer als b sein.", grenzen.getMessage());
        assertEquals("Die Wahrscheinlichkeit für das Quantil muss zwischen 0 und 1 liegen.", quantil.getMessage());
    }
}
