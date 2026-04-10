package ui.modes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class WissenschaftlichPanel extends JPanel
{
    private static final String[] BUTTONS = {
            "f(x)", "floor", "ceil", "mod", "rand",
            "MC", "MR", "M+", "M-", "Ans",
            "π", "e", "sin", "cos", "tan",
            "x²", "1/x", "|x|", "CE", "C",
            "√x", "(", ")", "←", "÷",
            "xʸ", "7", "8", "9", "×",
            "10ˣ", "4", "5", "6", "-",
            "log", "1", "2", "3", "+",
            "ln", "±", "0", ",", "="
    };

    private static final String[] FX_FUNCTIONS = {
            "sin", "cos", "tan",
            "asin", "acos", "atan",
            "sinh", "cosh", "tanh"
    };

    private Consumer<String> functionSelectionListener;

    public WissenschaftlichPanel()
    {
        setLayout(new GridLayout(9, 5, 6, 6));
        setOpaque(true);

        for (String text : BUTTONS)
        {
            if ("f(x)".equals(text))
            {
                add(createFunctionMenuButton());
            }
            else
            {
                add(createButton(text));
            }
        }
    }

    public void setFunctionSelectionListener(Consumer<String> listener)
    {
        this.functionSelectionListener = listener;
    }

    private JButton createButton(String text)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        return button;
    }

    private JButton createFunctionMenuButton()
    {
        JButton triggerButton = new JButton("f(x) ▼");
        triggerButton.setFocusable(false);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55), 1));

        JPanel popupContent = new JPanel(new BorderLayout(0, 8));
        popupContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        popupContent.setBackground(new Color(28, 28, 28));

        JLabel titleLabel = new JLabel("Funktionen");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        JPanel buttonGrid = new JPanel(new GridLayout(3, 3, 6, 6));
        buttonGrid.setOpaque(false);

        for (String functionName : FX_FUNCTIONS)
        {
            JButton fnButton = new JButton(functionName);
            fnButton.setFocusable(false);
            fnButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fnButton.setBackground(new Color(55, 55, 55));
            fnButton.setForeground(Color.WHITE);
            fnButton.setBorderPainted(false);
            fnButton.setOpaque(true);

            fnButton.addActionListener(e -> {
                popupMenu.setVisible(false);
                if (functionSelectionListener != null)
                {
                    functionSelectionListener.accept(functionName);
                }
            });

            buttonGrid.add(fnButton);
        }

        popupContent.add(titleLabel, BorderLayout.NORTH);
        popupContent.add(buttonGrid, BorderLayout.CENTER);
        popupMenu.add(popupContent);

        triggerButton.addActionListener(e -> popupMenu.show(triggerButton, 0, triggerButton.getHeight()));

        return triggerButton;
    }
}