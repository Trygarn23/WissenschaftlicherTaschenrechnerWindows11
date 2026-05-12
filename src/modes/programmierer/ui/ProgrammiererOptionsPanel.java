package modes.programmierer.ui;

import modes.programmierer.model.Basis;
import modes.programmierer.model.Wortbreite;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

class ProgrammiererOptionsPanel extends JPanel
{
    private static final String ACTIVE_KEY = "active";

    private final Map<Basis, JButton> basisButtons = new EnumMap<>(Basis.class);
    private final Map<Wortbreite, JButton> wortbreiteButtons = new EnumMap<>(Wortbreite.class);
    private AppTheme currentTheme = new DarkTheme();
    private Basis currentBasis = Basis.DEC;
    private Wortbreite currentWortbreite = Wortbreite.QWORD;

    ProgrammiererOptionsPanel(Consumer<Basis> basisListener, Consumer<Wortbreite> wortbreiteListener)
    {
        setLayout(new BorderLayout(8, 8));
        setOpaque(false);

        add(buildBasisPanel(basisListener), BorderLayout.NORTH);
        add(buildWortbreitePanel(wortbreiteListener), BorderLayout.SOUTH);
    }

    void setTastenPanel(Component tastenPanel)
    {
        add(tastenPanel, BorderLayout.CENTER);
    }

    JPanel buildBasisPanel(Consumer<Basis> basisListener)
    {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 6, 0));

        panel.add(createBasisButton("BIN", Basis.BIN, basisListener));
        panel.add(createBasisButton("OCT", Basis.OCT, basisListener));
        panel.add(createBasisButton("DEC", Basis.DEC, basisListener));
        panel.add(createBasisButton("HEX", Basis.HEX, basisListener));

        return panel;
    }

    JPanel buildWortbreitePanel(Consumer<Wortbreite> wortbreiteListener)
    {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        panel.add(createWortbreiteButton("BYTE", Wortbreite.BYTE, wortbreiteListener));
        panel.add(createWortbreiteButton("WORD", Wortbreite.WORD, wortbreiteListener));
        panel.add(createWortbreiteButton("DWORD", Wortbreite.DWORD, wortbreiteListener));
        panel.add(createWortbreiteButton("QWORD", Wortbreite.QWORD, wortbreiteListener));

        return panel;
    }

    void refresh(Basis basis, Wortbreite wortbreite)
    {
        currentBasis = basis;
        currentWortbreite = wortbreite;

        for (Map.Entry<Basis, JButton> entry : basisButtons.entrySet())
        {
            setModeButtonActive(entry.getValue(), entry.getKey() == basis);
        }

        for (Map.Entry<Wortbreite, JButton> entry : wortbreiteButtons.entrySet())
        {
            setModeButtonActive(entry.getValue(), entry.getKey() == wortbreite);
        }
    }

    void applyTheme(AppTheme theme)
    {
        currentTheme = theme;
        refresh(currentBasis, currentWortbreite);
    }

    private JButton createBasisButton(String text, Basis basis, Consumer<Basis> listener)
    {
        JButton btn = new JButton(text);
        styleModeButton(btn);
        ButtonTooltips.apply(btn, text);
        btn.addActionListener(e -> listener.accept(basis));
        basisButtons.put(basis, btn);
        return btn;
    }

    private JButton createWortbreiteButton(String text, Wortbreite wortbreite, Consumer<Wortbreite> listener)
    {
        JButton btn = new JButton(text);
        styleModeButton(btn);
        ButtonTooltips.apply(btn, text);
        btn.addActionListener(e -> listener.accept(wortbreite));
        wortbreiteButtons.put(wortbreite, btn);
        return btn;
    }

    private void styleModeButton(JButton button)
    {
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(true);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setModeBorder(button);

        button.putClientProperty(ACTIVE_KEY, Boolean.FALSE);
        button.addMouseListener(new ModeButtonMouseAdapter(button));
        setModeButtonActive(button, false);
    }

    private void setModeButtonActive(JButton button, boolean active)
    {
        button.putClientProperty(ACTIVE_KEY, active);
        button.setBackground(ProgrammiererButtonStyler.modeButtonBackground(currentTheme, active));
        button.setForeground(ProgrammiererButtonStyler.modeButtonForeground(currentTheme, active));
        setModeBorder(button);
    }

    private void setModeBorder(JButton button)
    {
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentTheme.modeBorder(), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
    }

    private boolean isActive(JButton button)
    {
        Object value = button.getClientProperty(ACTIVE_KEY);
        return value instanceof Boolean b && b;
    }

    private class ModeButtonMouseAdapter extends MouseAdapter
    {
        private final JButton button;

        private ModeButtonMouseAdapter(JButton button)
        {
            this.button = button;
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            if (!isActive(button))
            {
                button.setBackground(ProgrammiererButtonStyler.hoverBackground(
                        ProgrammiererButtonStyler.modeButtonBackground(currentTheme, false)));
            }
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (!isActive(button))
            {
                button.setBackground(ProgrammiererButtonStyler.modeButtonBackground(currentTheme, false));
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (!isActive(button))
            {
                button.setBackground(ProgrammiererButtonStyler.pressedBackground(
                        ProgrammiererButtonStyler.modeButtonBackground(currentTheme, false)));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (!isActive(button))
            {
                button.setBackground(ProgrammiererButtonStyler.hoverBackground(
                        ProgrammiererButtonStyler.modeButtonBackground(currentTheme, false)));
            }
        }
    }
}
