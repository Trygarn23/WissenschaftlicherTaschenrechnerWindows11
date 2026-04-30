package modes.programmierer.ui;

import modes.programmierer.formatting.ProgrammiererFormatter;
import modes.programmierer.logic.ProgrammiererLogik;
import modes.programmierer.model.Basis;
import modes.programmierer.model.Wortbreite;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ProgrammiererPanel extends JPanel
{
    private static final Color BG = new Color(25, 25, 25);
    private static final Color PANEL_BG = new Color(18, 22, 30);

    private static final Color DISPLAY_MAIN = Color.WHITE;
    private static final Color DISPLAY_SECONDARY = new Color(180, 180, 180);
    private static final Color DISPLAY_ACCENT = new Color(0, 145, 210);

    private static final Color MODE_ACTIVE_BG = new Color(0, 145, 210);
    private static final Color MODE_INACTIVE_BG = new Color(34, 39, 52);
    private static final Color MODE_BORDER = new Color(58, 66, 84);

    private static final Color DISABLED_BG = new Color(35, 35, 35);
    private static final Color DISABLED_FG = new Color(105, 105, 105);

    private static final String BASE_COLOR_KEY = "baseColor";
    private static final String ACTIVE_KEY = "active";

    private final ProgrammiererLogik logik = new ProgrammiererLogik();
    private final ProgrammiererFormatter formatter = new ProgrammiererFormatter();

    private final JLabel aktuelleBasisLabel = new JLabel("DEC: 0", SwingConstants.RIGHT);
    private final JLabel hexLabel = new JLabel("HEX: 0", SwingConstants.RIGHT);
    private final JLabel decLabel = new JLabel("DEC: 0", SwingConstants.RIGHT);
    private final JLabel octLabel = new JLabel("OCT: 0", SwingConstants.RIGHT);
    private final JLabel binLabel = new JLabel("BIN: 0", SwingConstants.RIGHT);

    private final Map<Basis, JButton> basisButtons = new EnumMap<>(Basis.class);
    private final Map<Wortbreite, JButton> wortbreiteButtons = new EnumMap<>(Wortbreite.class);
    private final Map<String, JButton> tastenButtons = new HashMap<>();

    private JButton unsignedButton;

    public ProgrammiererPanel()
    {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setOpaque(true);
        setBorder(new EmptyBorder(6, 0, 0, 0));

        add(buildDisplayPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);

        refreshAnzeige();
    }

    private JPanel buildDisplayPanel()
    {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel displayPanel = new JPanel(new BorderLayout(0, 10));
        displayPanel.setBackground(BG);
        displayPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        aktuelleBasisLabel.setForeground(DISPLAY_MAIN);
        aktuelleBasisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 42));
        aktuelleBasisLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel conversions = new JPanel(new GridLayout(4, 1, 0, 6));
        conversions.setOpaque(false);

        styleSecondaryLabel(hexLabel);
        styleSecondaryLabel(decLabel);
        styleSecondaryLabel(octLabel);
        styleSecondaryLabel(binLabel);

        conversions.add(hexLabel);
        conversions.add(decLabel);
        conversions.add(octLabel);
        conversions.add(binLabel);

        displayPanel.add(aktuelleBasisLabel, BorderLayout.NORTH);
        displayPanel.add(conversions, BorderLayout.CENTER);

        wrap.add(displayPanel, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildCenterPanel()
    {
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);

        center.add(buildBasisPanel(), BorderLayout.NORTH);
        center.add(buildMainButtonGrid(), BorderLayout.CENTER);
        center.add(buildWortbreitePanel(), BorderLayout.SOUTH);

        return center;
    }

    private JPanel buildBasisPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 6, 0));

        panel.add(createBasisButton("BIN", Basis.BIN));
        panel.add(createBasisButton("OCT", Basis.OCT));
        panel.add(createBasisButton("DEC", Basis.DEC));
        panel.add(createBasisButton("HEX", Basis.HEX));

        return panel;
    }

    private JPanel buildMainButtonGrid()
    {
        JPanel panel = new JPanel(new GridLayout(6, 5, 6, 6));
        panel.setBackground(BG);
        panel.setOpaque(true);

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
            btn.addActionListener(e -> handleButton(text));
            panel.add(btn);

            tastenButtons.put(text, btn);

            if ("SIGNED".equals(text))
            {
                unsignedButton = btn;
            }
        }

        return panel;
    }

    private JPanel buildWortbreitePanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        panel.add(createWortbreiteButton("BYTE", Wortbreite.BYTE));
        panel.add(createWortbreiteButton("WORD", Wortbreite.WORD));
        panel.add(createWortbreiteButton("DWORD", Wortbreite.DWORD));
        panel.add(createWortbreiteButton("QWORD", Wortbreite.QWORD));

        return panel;
    }

    private JButton createBasisButton(String text, Basis basis)
    {
        JButton btn = new JButton(text);
        styleModeButton(btn);

        btn.addActionListener(e -> {
            logik.setBasis(basis);
            refreshAnzeige();
        });

        basisButtons.put(basis, btn);
        return btn;
    }

    private JButton createWortbreiteButton(String text, Wortbreite wortbreite)
    {
        JButton btn = new JButton(text);
        styleModeButton(btn);

        btn.addActionListener(e -> {
            logik.setWortbreite(wortbreite);
            refreshAnzeige();
        });

        wortbreiteButtons.put(wortbreite, btn);
        return btn;
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

        btn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (!btn.isEnabled()) return;
                btn.setBackground(dunkelColor(baseColor, 25));
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (!btn.isEnabled()) return;
                btn.setBackground(helleColor(baseColor, 20));
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                if (!btn.isEnabled()) return;
                btn.setBackground(helleColor(baseColor, 20));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                if (!btn.isEnabled()) return;
                btn.setBackground((Color) btn.getClientProperty(BASE_COLOR_KEY));
            }
        });

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
        button.setForeground(Color.WHITE);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MODE_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        button.putClientProperty(ACTIVE_KEY, Boolean.FALSE);

        button.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                if (!isActive(button))
                {
                    button.setBackground(helleColor(MODE_INACTIVE_BG, 12));
                }
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                if (!isActive(button))
                {
                    button.setBackground(MODE_INACTIVE_BG);
                }
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (!isActive(button))
                {
                    button.setBackground(helleColor(MODE_INACTIVE_BG, 20));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (!isActive(button))
                {
                    button.setBackground(helleColor(MODE_INACTIVE_BG, 12));
                }
            }
        });

        button.setBackground(MODE_INACTIVE_BG);
    }

    private void styleSecondaryLabel(JLabel label)
    {
        label.setForeground(DISPLAY_SECONDARY);
        label.setFont(new Font("Consolas", Font.PLAIN, 18));
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private void handleButton(String text)
    {
        switch (text)
        {
            case "CLR" -> logik.clear();
            case "←" -> logik.backspace();
            case "NOT" -> logik.not();
            case "<<" -> logik.shiftLeft();
            case ">>" -> logik.shiftRightArithmetic();
            case ">>>" -> logik.shiftRightLogical();
            case "AND" -> logik.and();
            case "OR" -> logik.or();
            case "XOR" -> logik.xor();
            case "+" -> logik.plus();
            case "-" -> logik.minus();
            case "=" -> logik.berechne();
            case "±" -> logik.vorzeichenWechseln();
            case "SIGNED", "UNSIGNED" -> logik.toggleUnsigned();
            default -> logik.digitEingeben(text);
        }

        refreshAnzeige();
    }

    private void refreshAnzeige()
    {
        String op = logik.getPendingOperationText();

        aktuelleBasisLabel.setText(
                logik.getBasis().name() + ": "
                        + formatter.emptyAsZero(logik.getAktuelleEingabe())
                        + (op.isEmpty() ? "" : "   [" + op + "]")
        );

        hexLabel.setText("HEX: " + formatter.formatHex(logik.getAnzeige(Basis.HEX)));
        decLabel.setText("DEC: " + formatter.formatDec(logik.getAnzeige(Basis.DEC)));
        octLabel.setText("OCT: " + formatter.formatOct(logik.getAnzeige(Basis.OCT)));
        binLabel.setText("BIN: " + formatter.formatBinary(logik.getAnzeige(Basis.BIN)));

        updateBasisButtons();
        updateWortbreiteButtons();
        updateDigitButtonsByBasis();
        updateUnsignedButton();
    }

    private void updateBasisButtons()
    {
        for (Map.Entry<Basis, JButton> entry : basisButtons.entrySet())
        {
            boolean active = entry.getKey() == logik.getBasis();
            setModeButtonActive(entry.getValue(), active);
        }
    }

    private void updateWortbreiteButtons()
    {
        for (Map.Entry<Wortbreite, JButton> entry : wortbreiteButtons.entrySet())
        {
            boolean active = entry.getKey() == logik.getWortbreite();
            setModeButtonActive(entry.getValue(), active);
        }
    }

    private void updateDigitButtonsByBasis()
    {
        for (Map.Entry<String, JButton> entry : tastenButtons.entrySet())
        {
            String text = entry.getKey();
            JButton button = entry.getValue();

            if (!text.matches("[0-9A-F]"))
            {
                continue;
            }

            boolean enabled = istTasteGueltigFuerBasis(text, logik.getBasis());
            setButtonEnabledState(button, text, enabled);
        }

        JButton plusMinusButton = tastenButtons.get("±");
        if (plusMinusButton != null)
        {
            boolean enabled = logik.getBasis() == Basis.DEC && !logik.isUnsigned();
            setButtonEnabledState(plusMinusButton, "±", enabled);
        }
    }

    private void updateUnsignedButton()
    {
        if (unsignedButton == null)
        {
            return;
        }

        unsignedButton.setText(logik.isUnsigned() ? "UNSIGNED" : "SIGNED");
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
            button.setBackground(DISABLED_BG);
            button.setForeground(DISABLED_FG);
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

    private void setModeButtonActive(JButton button, boolean active)
    {
        button.putClientProperty(ACTIVE_KEY, active);
        button.setBackground(active ? MODE_ACTIVE_BG : MODE_INACTIVE_BG);
        button.setForeground(Color.WHITE);
    }

    private boolean isActive(JButton button)
    {
        Object value = button.getClientProperty(ACTIVE_KEY);
        return value instanceof Boolean b && b;
    }

    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.panelBackground());

        aktuelleBasisLabel.setForeground(theme.displayForeground());
        hexLabel.setForeground(theme.displayForeground());
        decLabel.setForeground(theme.displayForeground());
        octLabel.setForeground(theme.displayForeground());
        binLabel.setForeground(theme.displayForeground());

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

        for (JButton button : basisButtons.values())
        {
            button.setForeground(theme.functionButtonForeground());
        }

        for (JButton button : wortbreiteButtons.values())
        {
            button.setForeground(theme.functionButtonForeground());
        }

        revalidate();
        repaint();
    }

    private Color getButtonBaseColor(String text)
    {
        if (text.matches("\\d"))
        {
            return new Color(45, 45, 45);
        }

        if ("+-".contains(text))
        {
            return new Color(173, 41, 99);
        }

        if (text.equals("CLR") || text.equals("←"))
        {
            return new Color(100, 60, 60);
        }

        if (text.equals("NOT") || text.equals("AND") || text.equals("OR") || text.equals("XOR")
                || text.equals("<<") || text.equals(">>") || text.equals(">>>"))
        {
            return new Color(173, 41, 99);
        }

        if (text.equals("="))
        {
            return new Color(70, 70, 70);
        }

        if (text.matches("[A-F]") || text.equals("±") || text.equals("SIGNED") || text.equals("UNSIGNED"))
        {
            return new Color(60, 60, 60);
        }

        return new Color(60, 60, 60);
    }

    private Color getButtonTextColor(String text)
    {
        if ("+-".contains(text) || text.equals("NOT") || text.equals("AND") || text.equals("OR")
                || text.equals("XOR") || text.equals("<<") || text.equals(">>") || text.equals(">>>"))
        {
            return Color.BLACK;
        }

        return Color.WHITE;
    }

    private Color helleColor(Color c, int amount)
    {
        return new Color(
                Math.min(255, c.getRed() + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount)
        );
    }

    private Color dunkelColor(Color c, int amount)
    {
        return new Color(
                Math.max(0, c.getRed() - amount),
                Math.max(0, c.getGreen() - amount),
                Math.max(0, c.getBlue() - amount)
        );
    }
}