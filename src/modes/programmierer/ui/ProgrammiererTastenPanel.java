package modes.programmierer.ui;

import modes.programmierer.model.Basis;
import ui.theme.AppTheme;
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
    private static final Color BG = new Color(25, 25, 25);
    private static final String BASE_COLOR_KEY = "baseColor";

    private final Map<String, JButton> tastenButtons = new HashMap<>();
    private JButton unsignedButton;

    ProgrammiererTastenPanel(Consumer<String> buttonListener)
    {
        setLayout(new GridLayout(6, 5, 6, 6));
        setBackground(BG);
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
        updateDigitButtonsByBasis(basis, unsigned);
        updateUnsignedButton(unsigned);
    }

    void applyTheme(AppTheme theme)
    {
        for (JButton button : tastenButtons.values())
        {
            String text = button.getText();

            if (button.isEnabled())
            {
                Color baseColor = getButtonBaseColor(text);
                button.putClientProperty(BASE_COLOR_KEY, baseColor);
                button.setBackground(baseColor);
                button.setForeground(getButtonTextColor(text));
            }
        }
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
        btn.addMouseListener(new ButtonHoverAdapter(btn, baseColor));

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

    private void updateUnsignedButton(boolean unsigned)
    {
        if (unsignedButton == null)
        {
            return;
        }

        unsignedButton.setText(unsigned ? "UNSIGNED" : "SIGNED");
        ButtonTooltips.apply(unsignedButton, unsignedButton.getText());
        unsignedButton.setBackground(unsigned ? ProgrammiererButtonStyler.MODE_ACTIVE_BG : getButtonBaseColor(unsignedButton.getText()));
        unsignedButton.setForeground(Color.WHITE);
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
            button.setBackground(ProgrammiererButtonStyler.DISABLED_BG);
            button.setForeground(ProgrammiererButtonStyler.DISABLED_FG);
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
        return ProgrammiererButtonStyler.buttonBackground(text);
    }

    private Color getButtonTextColor(String text)
    {
        return ProgrammiererButtonStyler.buttonForeground(text);
    }

    private static class ButtonHoverAdapter extends MouseAdapter
    {
        private final JButton button;
        private final Color baseColor;

        private ButtonHoverAdapter(JButton button, Color baseColor)
        {
            this.button = button;
            this.baseColor = baseColor;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(ProgrammiererButtonStyler.darken(baseColor, 25));
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(ProgrammiererButtonStyler.brighten(baseColor, 20));
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground(ProgrammiererButtonStyler.brighten(baseColor, 20));
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (!button.isEnabled()) return;
            button.setBackground((Color) button.getClientProperty(BASE_COLOR_KEY));
        }
    }
}
