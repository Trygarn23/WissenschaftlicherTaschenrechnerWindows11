package ui.theme;

import org.junit.jupiter.api.Test;
import ui.theme.themes.DarkTheme;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorButtonStylerTest
{
    @Test
    void ordnetGemeinsameRechnerrollenNachButtontextZu()
    {
        AppTheme theme = new DarkTheme();
        JButton number = new JButton("7");
        JButton operator = new JButton("+");
        JButton special = new JButton("CE");
        JButton function = new JButton("sin");

        CalculatorButtonStyler.styleButton(number, theme);
        CalculatorButtonStyler.styleButton(operator, theme);
        CalculatorButtonStyler.styleButton(special, theme);
        CalculatorButtonStyler.styleButton(function, theme);

        assertEquals(theme.numberButtonBackground(), number.getBackground());
        assertEquals(theme.operatorButtonBackground(), operator.getBackground());
        assertEquals(theme.specialButtonBackground(), special.getBackground());
        assertEquals(theme.functionButtonBackground(), function.getBackground());
    }
}
