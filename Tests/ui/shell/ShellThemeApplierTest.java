package ui.shell;

import org.junit.jupiter.api.Test;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShellThemeApplierTest
{
    @Test
    void styleButton_ShouldUseThemeRolesForCommonCalculatorButtons()
    {
        AppTheme theme = new DarkTheme();

        JButton number = new JButton("7");
        JButton operator = new JButton("\u00d7");
        JButton special = new JButton("\u2190");
        JButton function = new JButton("sqrt");

        ShellThemeApplier.styleButton(number, theme);
        ShellThemeApplier.styleButton(operator, theme);
        ShellThemeApplier.styleButton(special, theme);
        ShellThemeApplier.styleButton(function, theme);

        assertEquals(theme.numberButtonBackground(), number.getBackground());
        assertEquals(theme.operatorButtonBackground(), operator.getBackground());
        assertEquals(theme.specialButtonBackground(), special.getBackground());
        assertEquals(theme.functionButtonBackground(), function.getBackground());
    }
}
