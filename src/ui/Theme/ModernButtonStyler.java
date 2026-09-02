package ui.theme;

import ui.animation.AnimationSupport;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ModernButtonStyler
{
    private static final String BASE_BACKGROUND_KEY = "modern.baseBackground";
    private static final String BASE_FOREGROUND_KEY = "modern.baseForeground";
    private static final String THEME_KEY = "modern.theme";
    private static final String FOCUSABLE_KEY = "modern.focusable";
    private static final String LISTENER_KEY = "modern.listenerInstalled";
    private static final int BUTTON_ANIMATION_MS = 130;

    private ModernButtonStyler()
    {
    }

    public static void styleButton(JButton button, AppTheme theme, Color background, Color foreground)
    {
        styleButton(button, theme, background, foreground, false);
    }

    public static void styleButton(JButton button, AppTheme theme, Color background, Color foreground, boolean focusable)
    {
        button.setFont(theme.buttonFont());
        AnimationSupport.stopComponentAnimation(button);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setFocusable(focusable);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.putClientProperty(BASE_BACKGROUND_KEY, background);
        button.putClientProperty(BASE_FOREGROUND_KEY, foreground);
        button.putClientProperty(THEME_KEY, theme);
        button.putClientProperty(FOCUSABLE_KEY, focusable);
        button.setBorder(buttonBorder(theme, background, focusable && button.isFocusOwner()));

        if (!Boolean.TRUE.equals(button.getClientProperty(LISTENER_KEY)))
        {
            button.addMouseListener(new HoverListener());
            button.addFocusListener(new FocusListener());
            button.addActionListener(e -> pulseButton(button));
            button.putClientProperty(LISTENER_KEY, Boolean.TRUE);
        }
    }

    public static void styleInput(JComponent component, AppTheme theme)
    {
        component.setBackground(theme.inputBackground());
        component.setForeground(theme.displayForeground());
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.inputBorder(), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static Border cardBorder(AppTheme theme)
    {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        );
    }

    private static Border buttonBorder(AppTheme theme, Color background, boolean focused)
    {
        Color borderColor = focused ? theme.focusBorder() : theme.blend(background, theme.displayForeground(), 0.18);
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, focused ? 2 : 1, true),
                BorderFactory.createEmptyBorder(focused ? 8 : 9, 13, focused ? 8 : 9, 13)
        );
    }

    private static void pulseButton(JButton button)
    {
        if (!button.isEnabled())
        {
            return;
        }

        AppTheme theme = (AppTheme) button.getClientProperty(THEME_KEY);
        Color base = (Color) button.getClientProperty(BASE_BACKGROUND_KEY);
        if (theme != null && base != null)
        {
            AnimationSupport.pulseBackground(button, theme.hoverBackground(base), 140);
        }
    }

    private static final class HoverListener extends MouseAdapter
    {
        @Override
        public void mouseEntered(MouseEvent e)
        {
            updateBackground(e, true, false);
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            updateBackground(e, false, false);
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            updateBackground(e, true, true);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            updateBackground(e, true, false);
        }

        private void updateBackground(MouseEvent e, boolean hover, boolean pressed)
        {
            if (!(e.getComponent() instanceof JButton button) || !button.isEnabled())
            {
                return;
            }

            AppTheme theme = (AppTheme) button.getClientProperty(THEME_KEY);
            Color base = (Color) button.getClientProperty(BASE_BACKGROUND_KEY);
            if (theme == null || base == null)
            {
                return;
            }

            if (pressed)
            {
                AnimationSupport.animateBackground(button, button.getBackground(), theme.pressedBackground(base), BUTTON_ANIMATION_MS);
            }
            else if (hover)
            {
                AnimationSupport.animateBackground(button, button.getBackground(), theme.hoverBackground(base), BUTTON_ANIMATION_MS);
            }
            else
            {
                AnimationSupport.animateBackground(button, button.getBackground(), base, BUTTON_ANIMATION_MS);
            }
        }
    }

    private static final class FocusListener extends FocusAdapter
    {
        @Override
        public void focusGained(FocusEvent e)
        {
            updateBorder(e, true);
        }

        @Override
        public void focusLost(FocusEvent e)
        {
            updateBorder(e, false);
        }

        private void updateBorder(FocusEvent e, boolean focused)
        {
            if (!(e.getComponent() instanceof JButton button))
            {
                return;
            }

            AppTheme theme = (AppTheme) button.getClientProperty(THEME_KEY);
            Color base = (Color) button.getClientProperty(BASE_BACKGROUND_KEY);
            boolean focusable = Boolean.TRUE.equals(button.getClientProperty(FOCUSABLE_KEY));
            if (theme != null && base != null)
            {
                button.setBorder(buttonBorder(theme, base, focusable && focused));
            }
        }
    }
}
