package modes.programmierer.ui;

import modes.programmierer.logic.ProgrammiererLogik;
import modes.programmierer.model.Basis;
import modes.programmierer.model.Wortbreite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
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

    private static final String BASE_COLOR_KEY = "baseColor";
    private static final String ACTIVE_KEY = "active";

    private final ProgrammiererLogik logik = new ProgrammiererLogik();

    private final JLabel aktuelleBasisLabel = new JLabel("DEC: 0", SwingConstants.RIGHT);
    private final JLabel hexLabel = new JLabel("HEX: 0", SwingConstants.RIGHT);
    private final JLabel decLabel = new JLabel("DEC: 0", SwingConstants.RIGHT);
    private final JLabel octLabel = new JLabel("OCT: 0", SwingConstants.RIGHT);
    private final JLabel binLabel = new JLabel("BIN: 0", SwingConstants.RIGHT);

    private final Map<Basis, JButton> basisButtons = new EnumMap<>(Basis.class);
    private final Map<Wortbreite, JButton> wortbreiteButtons = new EnumMap<>(Wortbreite.class);

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
                "4", "5", "6", "XOR", "C",
                "1", "2", "3", "(", ")",
                "±", "0", "=", "+", "-"
        };

        for (String text : texte)
        {
            JButton btn = createStyledButton(text);
            btn.addActionListener(e -> handleButton(text));
            panel.add(btn);
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
                btn.setBackground(dunkelColor(baseColor, 25));
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                btn.setBackground(helleColor(baseColor, 20));
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                btn.setBackground(helleColor(baseColor, 20));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
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
            case "C" -> logik.clear();
            case "←" -> logik.backspace();
            case "NOT" -> logik.not();
            case "<<" -> logik.shiftLeft();
            case ">>" -> logik.shiftRight();
            case "AND" -> logik.and();
            case "OR" -> logik.or();
            case "XOR" -> logik.xor();
            case "+" -> logik.plus();
            case "-" -> logik.minus();
            case "=" -> logik.berechne();
            case "±" -> logik.vorzeichenWechseln();
            case "(", ")" ->
            {
            }
            default -> logik.digitEingeben(text);
        }

        refreshAnzeige();
    }

    private void refreshAnzeige()
    {
        String op = logik.getPendingOperationText();
        aktuelleBasisLabel.setText(
                logik.getBasis().name() + ": "
                        + emptyAsZero(logik.getAktuelleEingabe())
                        + (op.isEmpty() ? "" : "   [" + op + "]")
        );
        hexLabel.setText("HEX: " + emptyAsZero(logik.getAnzeige(Basis.HEX)));
        decLabel.setText("DEC: " + emptyAsZero(logik.getAnzeige(Basis.DEC)));
        octLabel.setText("OCT: " + emptyAsZero(logik.getAnzeige(Basis.OCT)));
        binLabel.setText("BIN: " + formatBinary(emptyAsZero(logik.getAnzeige(Basis.BIN))));

        updateBasisButtons();
        updateWortbreiteButtons();
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

        if (text.equals("C") || text.equals("←"))
        {
            return new Color(100, 60, 60);
        }

        if (text.equals("NOT") || text.equals("AND") || text.equals("OR") || text.equals("XOR")
                || text.equals("<<") || text.equals(">>"))
        {
            return new Color(173, 41, 99);
        }

        if (text.equals("="))
        {
            return new Color(70, 70, 70);
        }

        if (text.matches("[A-F]") || text.equals("(") || text.equals(")") || text.equals("±"))
        {
            return new Color(60, 60, 60);
        }

        return new Color(60, 60, 60);
    }

    private Color getButtonTextColor(String text)
    {
        if ("+-".contains(text) || text.equals("NOT") || text.equals("AND") || text.equals("OR")
                || text.equals("XOR") || text.equals("<<") || text.equals(">>"))
        {
            return Color.BLACK;
        }

        return Color.WHITE;
    }

    private String emptyAsZero(String value)
    {
        return (value == null || value.isBlank()) ? "0" : value;
    }

    private String formatBinary(String raw)
    {
        String text = emptyAsZero(raw).replace(" ", "");
        StringBuilder sb = new StringBuilder();

        int firstGroupLen = text.length() % 4;
        if (firstGroupLen == 0)
        {
            firstGroupLen = 4;
        }

        for (int i = 0; i < text.length(); i++)
        {
            if (i > 0)
            {
                boolean groupBreak = (i == firstGroupLen) || (i > firstGroupLen && (i - firstGroupLen) % 4 == 0);
                if (groupBreak)
                {
                    sb.append(' ');
                }
            }
            sb.append(text.charAt(i));
        }

        return sb.toString();
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