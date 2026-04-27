package modes.standard.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StandardPanel extends JPanel
{
    private static final String[] BUTTONS = {
            "%", "CE", "C", "←",
            "1/x", "x²", "√x", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ",", "="
    };

    public StandardPanel()
    {
        setLayout(new GridLayout(0, 4, 6, 6));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        for (String text : BUTTONS)
        {
            add(createButton(text));
        }
    }

    private JButton createButton(String text)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        return button;
    }
}