package ui.modes;

import javax.swing.*;
import java.awt.*;

public class KomplexPlaceholderPanel extends JPanel
{
    public KomplexPlaceholderPanel()
    {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Komplex-Modus folgt bald", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));

        add(label, BorderLayout.CENTER);
    }
}