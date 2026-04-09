package ui.modes;

import javax.swing.*;
import java.awt.*;

public class WissenschaftlichPanel extends JPanel
{
    private static final String[] BUTTONS = {
            "asin", "acos", "atan", "sinh", "cosh",
            "tanh", "floor", "ceil", "mod", "rand",
            "MC", "MR", "M+", "M-", "Ans",
            "π", "e", "sin", "cos", "tan",
            "x²", "1/x", "|x|", "CE", "C",
            "√x", "(", ")", "←", "÷",
            "xʸ", "7", "8", "9", "×",
            "10ˣ", "4", "5", "6", "-",
            "log", "1", "2", "3", "+",
            "ln", "±", "0", ",", "="
    };

    public WissenschaftlichPanel()
    {
        setLayout(new GridLayout(10, 5, 6, 6));
        setOpaque(true);

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