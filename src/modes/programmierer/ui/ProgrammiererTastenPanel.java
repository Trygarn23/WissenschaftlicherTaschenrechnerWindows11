package modes.programmierer.ui;

import modes.programmierer.model.Basis;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

class ProgrammiererTastenPanel extends JPanel
{
    private static final String BASE_COLOR_KEY = "baseColor";

    private final Map<String, JButton> tastenButtons = new HashMap<>();
    private AppTheme currentTheme = new DarkTheme();
    private Basis currentBasis = Basis.DEC;
    private boolean currentUnsigned;
    private JButton unsignedButton;

    ProgrammiererTastenPanel(Consumer<String> buttonListener)
    {
        setLayout(new GridLayout(6, 5, 6, 6));
        setBackground(currentTheme.panelBackground());
        setOpaque(true);

        String[] texte = {
                "A", "B", "C", "D", "←",
                "E", "F", "NOT", "<<", ">>",
                "7", "8", "9", "AND", "OR",
                "4", "5", "6", "XOR", "CLR",
                "1", "2", "3", ">>>", "SIGNED",
                "±", "0", "=", "+", "-"
        };

        for (String text : texte)
        {
            JButton btn = createStyledButton(text);
            ButtonTooltips.apply(btn, tooltipKey(text));
            btn.addActionListener(e -> buttonListener.accept(btn.getText()));
            add(btn);

            tastenButtons.put(text, btn);

            if ("SIGNED".equals(text))
            {
                unsignedButton = btn;
            }
        }
    }

    void refresh(Basis basis, boolean unsigned)
    {
        currentBasis = basis;
        currentUnsigned = unsigned;
        updateDigitButtonsByBasis(basis, unsigned);
        updateActionButtons();
        updateUnsignedButton(unsigned);
    }

    void applyTheme(AppTheme theme)
    {
        currentTheme = theme;
        setBackground(theme.panelBackground());
        refresh(currentBasis, currentUnsigned);
    }

    private JButton createStyledButton(String text)
    {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color baseColor = getButtonBaseColor(text);
        Color textColor = getButtonTextColor(text);

        btn.setBackground(baseColor);
        btn.setForeground(textColor);
        btn.putClientProperty(BASE_COLOR_KEY, baseColor);
        btn.addMouseListener(new ButtonHoverAdapter(btn));

        return btn;
    }

    private void updateDigitButtonsByBasis(Basis basis, boolean unsigned)
    {
        for (Map.Entry<String, JButton> entry : tastenButtons.entrySet())
        {
            String text = entry.getKey();
            JButton button = entry.getValue();

            if (!text.matches("[0-9A-F]"))
            {
                continue;
            }

            boolean enabled = istTasteGueltigFuerBasis(text, basis);
            setButtonEnabledState(button, text, enabled);
        }

        JButton plusMinusButton = tastenButtons.get("±");
        if (plusMinusButton != null)
        {
            boolean enabled = basis == Basis.DEC && !unsigned;
            setButtonEnabledState(plusMinusButton, "±", enabled);
        }
    }

    private void updateActionButtons()
    {
        for (Map.Entry<String, JButton> entry : tastenButtons.entrySet())
        {
            String text = entry.getKey();
            JButton button = entry.getValue();

            if (text.matches("[0-9A-F]") || button == unsignedButton || "Â±".equals(text))
            {
                continue;
            }

            setButtonEnabledState(button, button.getText(), true);
        }
    }

    private void updateUnsignedButton(boolean unsigned)
    {
        if (unsignedButton == null)
        {
            return;
        }

        unsignedButton.setText(unsigned ? "UNSIGNED" : "SIGNED");
        ButtonTooltips.apply(unsignedButton, unsignedButton.getText());
        setButtonEnabledState(unsignedButton, unsignedButton.getText(), true);
    }

    private void setButtonEnabledState(JButton button, String text, boolean enabled)
    {
        button.setEnabled(enabled);

        if (enabled)
        {
            Color baseColor = getButtonBaseColor(text);
            button.putClientProperty(BASE_COLOR_KEY, baseColor);
            button.setBackground(baseColor);
            button.setForeground(getButtonTextColor(text));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else
        {
            Color disabledBackground = ProgrammiererButtonStyler.disabledBackground(currentTheme);
            button.putClientProperty(BASE_COLOR_KEY, disabledBackground);
            button.setBackground(disabledBackground);
            button.setForeground(ProgrammiererButtonStyler.disabledForeground(currentTheme));
            button.setCursor(Cursor.getDefaultCursor());
        }
    }

    private boolean istTasteGueltigFuerBasis(String text, Basis basis)
    {
        return switch (basis)
        {
            case BIN -> text.matches("[01]");
            case OCT -> text.matches("[0-7]");
            case DEC -> text.matches("[0-9]");
            case HEX -> text.matches("[0-9A-F]");
        };
    }

    private String tooltipKey(String text)
    {
        return "C".equals(text) ? "PRG:C" : text;
    }

    private Color getButtonBaseColor(String text)
    {
        return ProgrammiererButtonStyler.buttonBackground(text, currentTheme);
    }

    private Color getButtonTextColor(String text)
    {
        return ProgrammiererButtonStyler.buttonForeground(text, currentTheme);
    }

    private static class ButtonHoverAdapter extends MouseAdapter
    {
        private final JButton button;

        private ButtonHoverAdapter(JButton button)
        {
            this.button = button;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(ProgrammiererButtonStyler.pressedBackground(baseColor()));
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(ProgrammiererButtonStyler.hoverBackground(baseColor()));
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(ProgrammiererButtonStyler.hoverBackground(baseColor()));
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(baseColor());
        }

        private Color baseColor()
        {
            return (Color) button.getClientProperty(BASE_COLOR_KEY);
        }
    }
}
