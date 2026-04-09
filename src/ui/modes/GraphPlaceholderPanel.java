package ui.modes;

import javax.swing.*;
import java.awt.*;

public class GraphPlaceholderPanel extends JPanel
{
    public GraphPlaceholderPanel()
    {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Graph-Modus folgt bald", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));

        add(label, BorderLayout.CENTER);
    }
}