import common.logic.RechnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaschenrechnerLogikTest
{
    private RechnerService rechner;

    @BeforeEach
    void setUp()
    {
        rechner = new RechnerService();
    }

    @Test
    void rechnerService_ShouldKeepLegacySmokeTestGreen_WhenBasicCalculationIsUsed()
    {
        // Arrange
        rechner.eingabeZahl("2");
        rechner.operatorSetzen("+");
        rechner.eingabeZahl("3");

        // Act
        String result = rechner.berechne();

        // Assert
        assertEquals("5", result);
        assertEquals("2+3 = 5", rechner.getVerlauf());
    }
}
