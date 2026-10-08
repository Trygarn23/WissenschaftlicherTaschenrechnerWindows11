package modes.netzwerk.ui;

import common.state.RechnerModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.*;

public class NetzwerkPanelTest
{
    @Test
    void netzwerkPanel_ShouldCalculateSubnetAndShortenIpv6OnClick()
    {
        // Arrange
        NetzwerkPanel panel = new NetzwerkPanel();
        JTextField adresse = SwingSuche.finde(panel, JTextField.class, f -> f.getText().contains("/"));
        JTextField ipv6 = SwingSuche.finde(panel, JTextField.class, f -> f.getText().contains(":"));
        JTextArea ausgabe = SwingSuche.finde(panel, JTextArea.class);

        // Act
        adresse.setText("172.16.37.5/20");
        SwingSuche.button(panel, "Subnetz").doClick();
        String subnetz = ausgabe.getText();
        ipv6.setText("2001:0db8:0000:0000:0000:ff00:0042:8329");
        SwingSuche.button(panel, "IPv6 kürzen").doClick();

        // Assert
        assertEquals(RechnerModus.NETZWERK, panel.getRechnerModus());
        assertNotNull(SwingSuche.button(panel, "Teilnetze"));
        assertNotNull(SwingSuche.button(panel, "Maske umrechnen"));
        assertNotNull(SwingSuche.button(panel, "IPv6 ausschreiben"));
        assertTrue(subnetz.contains("172.16.47.255"));
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> "2001:db8::ff00:42:8329".equals(l.getText())));
    }

    @Test
    void netzwerkPanel_ShouldShowGermanErrorForInvalidAddress()
    {
        // Arrange
        NetzwerkPanel panel = new NetzwerkPanel();
        JTextField adresse = SwingSuche.finde(panel, JTextField.class, f -> f.getText().contains("/"));

        // Act
        adresse.setText("300.1.1.1/24");
        SwingSuche.button(panel, "Subnetz").doClick();

        // Assert
        assertNotNull(SwingSuche.finde(panel, JLabel.class, l -> l.getText().contains("keine gültige IPv4-Adresse")));
    }
}
