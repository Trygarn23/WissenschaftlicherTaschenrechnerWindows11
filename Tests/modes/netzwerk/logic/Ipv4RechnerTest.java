package modes.netzwerk.logic;

import modes.netzwerk.model.Ipv4Netz;
import modes.netzwerk.model.Teilung;
import org.junit.jupiter.api.Test;

import static modes.netzwerk.logic.Ipv4Rechner.*;
import static org.junit.jupiter.api.Assertions.*;

public class Ipv4RechnerTest
{
    @Test
    void leseAdresse_ShouldReadValidAddressesIncludingHighOctets()
    {
        // Act & Assert
        assertEquals("192.168.1.10", alsText(leseAdresse(" 192.168.1.10 ")));
        assertEquals("255.255.255.255", alsText(leseAdresse("255.255.255.255")));
        assertEquals("0.0.0.0", alsText(leseAdresse("0.0.0.0")));
    }

    @Test
    void leseAdresse_ShouldRejectInvalidAddressesInGerman()
    {
        // Act & Assert
        for (String ungueltig : new String[] {"256.1.1.1", "1.2.3", "1.2.3.4.5", "1..2.3", "a.b.c.d", "1.2.3.-4", "1.2.3.1000", "1.2.3.4."})
        {
            IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> leseAdresse(ungueltig), ungueltig);
            assertTrue(fehler.getMessage().contains("keine gültige IPv4-Adresse"), fehler.getMessage());
        }
        assertEquals("Bitte eine IPv4-Adresse eingeben.", assertThrows(IllegalArgumentException.class, () -> leseAdresse("")).getMessage());
    }

    @Test
    void lesePraefix_ShouldConvertBetweenCidrAndDottedMask()
    {
        // Act & Assert
        assertEquals(24, lesePraefix("/24"));
        assertEquals(24, lesePraefix("24"));
        assertEquals(24, lesePraefix("255.255.255.0"));
        assertEquals(26, lesePraefix("255.255.255.192"));
        assertEquals(0, lesePraefix("0.0.0.0"));
        assertEquals(32, lesePraefix("255.255.255.255"));
        assertEquals("255.255.240.0", alsText(Ipv4Netz.maske(20)));
    }

    @Test
    void lesePraefix_ShouldRejectNonContiguousOrOutOfRangeMasks()
    {
        // Act
        IllegalArgumentException luecke = assertThrows(IllegalArgumentException.class, () -> lesePraefix("255.0.255.0"));

        // Assert
        assertTrue(luecke.getMessage().contains("keine gültige Subnetzmaske"));
        assertThrows(IllegalArgumentException.class, () -> lesePraefix("255.255.255.1"));
        assertThrows(IllegalArgumentException.class, () -> lesePraefix("/33"));
        assertThrows(IllegalArgumentException.class, () -> lesePraefix("/abc"));
    }

    @Test
    void leseNetz_ShouldAcceptCombinedOrSeparateInput()
    {
        // Act & Assert
        assertEquals(new Ipv4Netz(leseAdresse("192.168.1.10"), 26), leseNetz("192.168.1.10/26", ""));
        assertEquals(new Ipv4Netz(leseAdresse("192.168.1.10"), 26), leseNetz("192.168.1.10", "255.255.255.192"));
        assertEquals(new Ipv4Netz(leseAdresse("192.168.1.10"), 26), leseNetz("192.168.1.10", "/26"));
        assertThrows(IllegalArgumentException.class, () -> leseNetz("192.168.1.10/26", "/24"));
        assertThrows(IllegalArgumentException.class, () -> leseNetz("192.168.1.10", ""));
    }

    @Test
    void binaer_ShouldMarkBoundaryBetweenNetworkAndHostPart()
    {
        // Act & Assert
        assertEquals("11000000.10101000.00000001.00|001010", binaer(leseAdresse("192.168.1.10"), 26));
        assertEquals("11000000.10101000.00000001|00001010", binaer(leseAdresse("192.168.1.10"), 24));
        assertEquals("|00000000.00000000.00000000.00000000", binaer(0, 0));
        assertEquals("11111111.11111111.11111111.11111111|", binaer(-1, 32));
    }

    @Test
    void teile_ShouldSplitClassCNetIntoFourSubnets()
    {
        // Act
        Teilung teilung = teile(leseNetz("192.168.1.77/24", ""), 4);

        // Assert
        assertEquals(26, teilung.neuerPraefix());
        assertEquals(4, teilung.netze().size());
        assertFalse(teilung.wurdeAufgerundet());
        assertEquals("192.168.1.0", alsText(teilung.ausgang().adresse()));
        assertEquals("192.168.1.192", alsText(teilung.netze().get(3).netzadresse()));
        assertEquals("192.168.1.255", alsText(teilung.netze().get(3).broadcast()));
    }

    @Test
    void teile_ShouldRoundUpToPowerOfTwo()
    {
        // Act
        Teilung teilung = teile(leseNetz("10.0.0.0/16", ""), 5);

        // Assert
        assertTrue(teilung.wurdeAufgerundet());
        assertEquals(8, teilung.anzahl());
        assertEquals(19, teilung.neuerPraefix());
        assertEquals("10.0.224.0", alsText(teilung.netze().get(7).netzadresse()));
    }

    @Test
    void teile_ShouldLimitDisplayedSubnetsAndHandleTopOfAddressSpace()
    {
        // Act
        Teilung viele = teile(leseNetz("10.0.0.0/8", ""), 1000);
        Teilung oben = teile(leseNetz("255.255.255.0/24", ""), 2);

        // Assert
        assertEquals(1024, viele.anzahl());
        assertEquals(MAX_ANGEZEIGTE_TEILNETZE, viele.netze().size());
        assertTrue(viele.istGekuerzt());
        assertEquals("255.255.255.128", alsText(oben.netze().get(1).netzadresse()));
        assertEquals("255.255.255.255", alsText(oben.netze().get(1).broadcast()));
    }

    @Test
    void teile_ShouldRejectImpossibleSplits()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> teile(leseNetz("192.168.1.0/30", ""), 8));
        assertThrows(IllegalArgumentException.class, () -> teile(leseNetz("192.168.1.0/24", ""), 0));
        assertEquals(32, teile(leseNetz("192.168.1.0/30", ""), 4).neuerPraefix());
    }
}
