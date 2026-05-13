import modes.wissenschaftlich.logic.WissenschaftlichRechnerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WissenschaftlichRechnerServiceAdapterTest
{
    @Test
    @SuppressWarnings("deprecation")
    void wissenschaftlichRechnerService_ShouldRemainCompatible_WhenScientificMethodIsCalled()
    {
        // Arrange
        WissenschaftlichRechnerService rechner = new WissenschaftlichRechnerService();
        rechner.eingabeZahl("9");
        rechner.eingabeZahl("0");

        // Act
        rechner.sin();
        String result = rechner.berechne();

        // Assert
        assertEquals("1", result);
    }
}
