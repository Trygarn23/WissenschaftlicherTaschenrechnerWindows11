package modes.netzwerk.logic;

import org.junit.jupiter.api.Test;

import static modes.netzwerk.logic.Ipv6Rechner.kuerze;
import static modes.netzwerk.logic.Ipv6Rechner.schreibeAus;
import static org.junit.jupiter.api.Assertions.*;

public class Ipv6RechnerTest
{
    @Test
    void kuerze_ShouldRemoveLeadingZerosAndCollapseLongestZeroRun()
    {
        // Act & Assert
        assertEquals("2001:db8::ff00:42:8329", kuerze("2001:0db8:0000:0000:0000:ff00:0042:8329"));
        assertEquals("fe80::1", kuerze("FE80:0000:0000:0000:0000:0000:0000:0001"));
        assertEquals("::", kuerze("0:0:0:0:0:0:0:0"));
        assertEquals("::1", kuerze("0:0:0:0:0:0:0:1"));
        assertEquals("1::", kuerze("1:0:0:0:0:0:0:0"));
    }

    @Test
    void kuerze_ShouldFollowRfc5952TieAndSingleGroupRules()
    {
        // Act & Assert
        assertEquals("2001:db8:0:1:1:1:1:1", kuerze("2001:db8:0:1:1:1:1:1"));
        assertEquals("2001:db8::1:0:0:1", kuerze("2001:db8:0:0:1:0:0:1"));
        assertEquals("2001:0:0:1::1", kuerze("2001:0:0:1:0:0:0:1"));
    }

    @Test
    void schreibeAus_ShouldExpandToEightFourDigitGroups()
    {
        // Act & Assert
        assertEquals("2001:0db8:0000:0000:0000:ff00:0042:8329", schreibeAus("2001:db8::ff00:42:8329"));
        assertEquals("0000:0000:0000:0000:0000:0000:0000:0001", schreibeAus("::1"));
        assertEquals("2001:0db8:0000:0000:0000:0000:0000:0000/32", schreibeAus("2001:DB8::/32"));
    }

    @Test
    void kuerze_ShouldRejectInvalidAddressesInGerman()
    {
        // Act & Assert
        for (String ungueltig : new String[] {"1::2::3", "12345::1", "1:2:3:4:5:6:7", "1:2:3:4:5:6:7:8:9",
                "1:2:3:4::5:6:7:8", "g::1", ":1:2:3:4:5:6:7", "1:::2", "::1/129", "192.168.1.1"})
        {
            IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> kuerze(ungueltig), ungueltig);
            assertFalse(fehler.getMessage().isBlank());
        }
        assertEquals("Bitte eine IPv6-Adresse eingeben.", assertThrows(IllegalArgumentException.class, () -> kuerze(" ")).getMessage());
    }
}
