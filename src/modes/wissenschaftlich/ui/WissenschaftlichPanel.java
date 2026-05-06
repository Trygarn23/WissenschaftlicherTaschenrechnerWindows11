package modes.wissenschaftlich.ui;

import ui.theme.AppTheme;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
    private JPopupMenu functionPopupMenu;
    private JPanel functionPopupContent;
    private JLabel functionPopupTitleLabel;
    private final List<JButton> functionPopupButtons = new ArrayList<>();

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

    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.panelBackground());

        if (functionPopupMenu != null)
        {
            functionPopupMenu.setBorder(BorderFactory.createLineBorder(theme.modeBorder(), 1));
        }

        if (functionPopupContent != null)
        {
            functionPopupContent.setBackground(theme.panelBackground());
        }

        if (functionPopupTitleLabel != null)
        {
            functionPopupTitleLabel.setFont(theme.buttonFont().deriveFont(Font.BOLD));
            functionPopupTitleLabel.setForeground(theme.displayForeground());
        }

        for (JButton button : functionPopupButtons)
        {
            button.setFont(theme.buttonFont());
            button.setBackground(theme.functionButtonBackground());
            button.setForeground(theme.functionButtonForeground());
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setOpaque(true);
        }
    }

    private JButton createButton(String text)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        ButtonTooltips.apply(button, text);
        return button;
    }

    private JButton createFunctionMenuButton()
    {
        JButton triggerButton = new JButton("f(x) ▼");
        triggerButton.setFocusable(false);
        ButtonTooltips.apply(triggerButton, "f(x)");

        functionPopupMenu = new JPopupMenu();

        functionPopupContent = new JPanel(new BorderLayout(0, 8));
        functionPopupContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        functionPopupTitleLabel = new JLabel("Funktionen");
        functionPopupTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel buttonGrid = new JPanel(new GridLayout(3, 3, 6, 6));
        buttonGrid.setOpaque(false);

        for (String functionName : FX_FUNCTIONS)
        {
            JButton fnButton = new JButton(functionName);
            fnButton.setFocusable(false);
            fnButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fnButton.setBorderPainted(false);
            fnButton.setOpaque(true);
            ButtonTooltips.apply(fnButton, functionName);

            fnButton.addActionListener(e -> {
                functionPopupMenu.setVisible(false);
                if (functionSelectionListener != null)
                {
                    functionSelectionListener.accept(functionName);
                }
            });

            functionPopupButtons.add(fnButton);
            buttonGrid.add(fnButton);
        }

        functionPopupContent.add(functionPopupTitleLabel, BorderLayout.NORTH);
        functionPopupContent.add(buttonGrid, BorderLayout.CENTER);
        functionPopupMenu.add(functionPopupContent);

        triggerButton.addActionListener(e -> functionPopupMenu.show(triggerButton, 0, triggerButton.getHeight()));

        return triggerButton;
    }
}
