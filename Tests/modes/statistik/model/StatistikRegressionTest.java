package modes.statistik.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatistikRegressionTest
{
    @Test
    void vorhersage_ShouldEvaluateLine_WhenRegressionIsLinear()
    {
        // Arrange
        StatistikRegression regression = StatistikRegression.linear(2.0, 1.0, 0.99);

        // Act
        double y = regression.vorhersage(4.0);

        // Assert
        assertEquals(9.0, y, 1e-12);
        assertEquals("Linear", regression.getTyp());
        assertEquals(0.0, regression.getA(), 0.0);
        assertEquals(0.99, regression.getBestimmtheitsmass(), 0.0);
    }

    @Test
    void vorhersage_ShouldEvaluateParabola_WhenRegressionIsQuadratic()
    {
        // Arrange
        StatistikRegression regression = StatistikRegression.quadratisch(1.0, -2.0, 3.0, 1.0);

        // Act
        double y = regression.vorhersage(3.0);

        // Assert
        assertEquals(6.0, y, 1e-12);
        assertEquals("Quadratisch", regression.getTyp());
    }
}
