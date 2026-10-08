package modes.datum.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZeitSummiererTest
{
    @Test
    void summiereMinuten_ShouldMixFormatsAndSeparators()
    {
        // Act & Assert
        assertEquals(420, ZeitSummierer.summiereMinuten("7:30\n-0:30"));
        assertEquals(495, ZeitSummierer.summiereMinuten("7h 30min, 45min"));
        assertEquals(480, ZeitSummierer.summiereMinuten("8h"));
        assertEquals(615, ZeitSummierer.summiereMinuten("2:00 3:15; 5H 0MIN\n\n-0:00"));
    }

    @Test
    void summiereMinuten_ShouldAllowNegativeTotal()
    {
        // Act & Assert
        assertEquals(-60, ZeitSummierer.summiereMinuten("-1:00"));
        assertEquals(-15, ZeitSummierer.summiereMinuten("30min -45min"));
    }

    @Test
    void summiereMinuten_ShouldExplainInvalidInputInGerman()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class, () -> ZeitSummierer.summiereMinuten("  "));
        IllegalArgumentException text = assertThrows(IllegalArgumentException.class, () -> ZeitSummierer.summiereMinuten("7:30 abc"));
        IllegalArgumentException minuten = assertThrows(IllegalArgumentException.class, () -> ZeitSummierer.summiereMinuten("7:75"));

        // Assert
        assertEquals("Bitte mindestens eine Zeit eingeben.", leer.getMessage());
        assertEquals("„abc“ ist keine gültige Zeit.", text.getMessage());
        assertEquals("„7:75“: Minuten müssen unter 60 liegen.", minuten.getMessage());
        assertThrows(IllegalArgumentException.class, () -> ZeitSummierer.summiereMinuten("7:3"));
        assertThrows(IllegalArgumentException.class, () -> ZeitSummierer.summiereMinuten("12345678:00"));
    }

    @Test
    void formatiere_ShouldGiveHoursMinutesAndDecimal()
    {
        // Act & Assert
        assertEquals("07:30", ZeitSummierer.formatiereStunden(450));
        assertEquals("-00:30", ZeitSummierer.formatiereStunden(-30));
        assertEquals("125:05", ZeitSummierer.formatiereStunden(7505));
        assertEquals("7,50 h", ZeitSummierer.formatiereDezimal(450));
        assertEquals("-0,50 h", ZeitSummierer.formatiereDezimal(-30));
    }
}
