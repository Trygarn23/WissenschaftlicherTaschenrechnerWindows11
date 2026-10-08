package modes.netzwerk.model;

import org.junit.jupiter.api.Test;

import static modes.netzwerk.logic.Ipv4Rechner.alsText;
import static modes.netzwerk.logic.Ipv4Rechner.leseAdresse;
import static org.junit.jupiter.api.Assertions.*;

public class Ipv4NetzTest
{
    @Test
    void netz_ShouldSolveTypicalExamTaskFor26()
    {
        // Arrange
        Ipv4Netz netz = new Ipv4Netz(leseAdresse("192.168.1.130"), 26);

        // Assert
        assertEquals("255.255.255.192", alsText(netz.maske()));
        assertEquals("0.0.0.63", alsText(netz.wildcard()));
        assertEquals("192.168.1.128", alsText(netz.netzadresse()));
        assertEquals("192.168.1.191", alsText(netz.broadcast()));
        assertEquals("192.168.1.129", alsText(netz.ersterHost()));
        assertEquals("192.168.1.190", alsText(netz.letzterHost()));
        assertEquals(64, netz.anzahlAdressen());
        assertEquals(62, netz.anzahlHosts());
    }

    @Test
    void netz_ShouldHandleClassBNetworkWith20()
    {
        // Arrange
        Ipv4Netz netz = new Ipv4Netz(leseAdresse("172.16.37.5"), 20);

        // Assert
        assertEquals("172.16.32.0", alsText(netz.netzadresse()));
        assertEquals("172.16.47.255", alsText(netz.broadcast()));
        assertEquals(4094, netz.anzahlHosts());
    }

    @Test
    void netz_ShouldTreat31AsPointToPointWithTwoUsableAddresses()
    {
        // Arrange
        Ipv4Netz netz = new Ipv4Netz(leseAdresse("10.0.0.1"), 31);

        // Assert
        assertEquals("10.0.0.0", alsText(netz.ersterHost()));
        assertEquals("10.0.0.1", alsText(netz.letzterHost()));
        assertEquals(2, netz.anzahlHosts());
    }

    @Test
    void netz_ShouldTreat32AsSingleHost()
    {
        // Arrange
        Ipv4Netz netz = new Ipv4Netz(leseAdresse("10.1.2.3"), 32);

        // Assert
        assertEquals("255.255.255.255", alsText(netz.maske()));
        assertEquals("10.1.2.3", alsText(netz.netzadresse()));
        assertEquals("10.1.2.3", alsText(netz.broadcast()));
        assertEquals("10.1.2.3", alsText(netz.ersterHost()));
        assertEquals(1, netz.anzahlHosts());
    }

    @Test
    void netz_ShouldCoverWholeAddressSpaceFor0()
    {
        // Arrange
        Ipv4Netz netz = new Ipv4Netz(leseAdresse("200.1.2.3"), 0);

        // Assert
        assertEquals("0.0.0.0", alsText(netz.maske()));
        assertEquals("0.0.0.0", alsText(netz.netzadresse()));
        assertEquals("255.255.255.255", alsText(netz.broadcast()));
        assertEquals(4_294_967_296L, netz.anzahlAdressen());
        assertEquals(4_294_967_294L, netz.anzahlHosts());
    }

    @Test
    void netz_ShouldRejectPrefixOutsideRange()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Ipv4Netz(0, 33));
        assertThrows(IllegalArgumentException.class, () -> new Ipv4Netz(0, -1));
    }
}
