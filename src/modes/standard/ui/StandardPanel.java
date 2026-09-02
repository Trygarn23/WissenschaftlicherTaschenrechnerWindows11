package modes.standard.ui;

import common.state.RechnerModus;
import ui.shell.ModePanel;
import ui.theme.AppTheme;
import ui.theme.CalculatorButtonStyler;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StandardPanel extends JPanel implements ModePanel
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
        setLayout(new GridLayout(0, 4, 8, 8));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        for (String text : BUTTONS)
        {
            add(createButton(text));
        }
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.STANDARD;
    }

    @Override
    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.panelBackground());
        CalculatorButtonStyler.stylePanel(this, theme);
    }

    private JButton createButton(String text)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        ButtonTooltips.apply(button, text);
        return button;
    }
}
