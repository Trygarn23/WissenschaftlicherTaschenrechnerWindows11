package modes.netzwerk.formatting;

import modes.netzwerk.logic.Ipv4Rechner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NetzwerkBerichtTest
{
    @Test
    void subnetz_ShouldListAllValuesBinaryAndHintFor31()
    {
        // Act
        String text = NetzwerkBericht.subnetz(Ipv4Rechner.leseNetz("10.0.0.1/31", ""));

        // Assert
        assertTrue(text.contains("Netzadresse     10.0.0.0"));
        assertTrue(text.contains("Nutzbare Hosts  2"));
        assertTrue(text.contains("RFC 3021"));
        assertTrue(text.contains("00001010.00000000.00000000.0000000|1"));
    }

    @Test
    void teilnetze_ShouldShowRoundingAndTruncationHints()
    {
        // Act
        String text = NetzwerkBericht.teilnetze(Ipv4Rechner.teile(Ipv4Rechner.leseNetz("10.0.0.0/8", ""), 1000));

        // Assert
        assertTrue(text.contains("aufgerundet auf 1.024"));
        assertTrue(text.contains("nur die ersten 256 von 1.024"));
        assertTrue(text.contains("10.0.0.1 – 10.0.63.254"));
    }
}
