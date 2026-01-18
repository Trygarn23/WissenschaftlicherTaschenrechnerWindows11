import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TaschenrechnerUI extends JFrame
{
    private final JTextPane display;
    private final JTextPane recdisplay;
    private final JPanel buttonPanel;

    private boolean darkMode = true;

    private final TaschenrechnerLogik rechner = new TaschenrechnerLogik();

    public TaschenrechnerUI()
    {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(550, 650));

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(new Color(25, 25, 25));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        recdisplay = new JTextPane();
        recdisplay.setEditable(false);
        recdisplay.setBackground(new Color(25, 25, 25));
        recdisplay.setForeground(new Color(180, 180, 180));
        recdisplay.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        recdisplay.setOpaque(true);
        recdisplay.setBorder(null);
        alignRight(recdisplay);
        topPanel.add(recdisplay, BorderLayout.NORTH);

        display = new JTextPane();
        display.setEditable(false);
        display.setBackground(new Color(25, 25, 25));
        display.setForeground(Color.WHITE);
        display.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        display.setText("0");
        display.setBorder(null);
        display.setOpaque(true);
        alignRight(display);
        topPanel.add(display, BorderLayout.CENTER);

        contentPane.add(topPanel, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridLayout(8, 5, 6, 6));
        buttonPanel.setBackground(new Color(25, 25, 25));
        contentPane.add(buttonPanel, BorderLayout.CENTER);

        String[] buttons = {
                "DEG", "π", "e", "CE", "C",
                "sin", "cos", "tan", "←", "Dark",
                "x²", "1/x", "|x|", "exp", "mod",
                "√x", "(", ")", "n!", "÷",
                "xʸ", "7", "8", "9", "×",
                "10ˣ", "4", "5", "6", "-",
                "log", "1", "2", "3", "+",
                "ln", "+/_", "0", ",", "="
        };

        setupKeyboard();

        for (String text : buttons)
        {
            JButton btn = new JButton(text);
            styleButton(btn, text);
            btn.addActionListener(e -> handleButton((JButton) e.getSource()));
            buttonPanel.add(btn);
        }

        refresh();
    }

    private void alignRight(JTextPane pane)
    {
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(0, doc.getLength(), right, false);
    }

    private void refresh()
    {
        display.setText(rechner.formatLiveAnzeige());
        recdisplay.setText(rechner.getVerlauf());
    }

    private void refreshWithExtraInfo(String info)
    {
        display.setText(rechner.formatLiveAnzeige());
        String v = rechner.getVerlauf();
        recdisplay.setText(info + (v.isEmpty() ? "" : " | " + v));
    }

    private void handleButton(JButton sourceBtn)
    {
        String t = sourceBtn.getText();

        if (t.matches("\\d"))
        {
            rechner.eingabeZahl(t);
            refresh();
            return;
        }

        switch (t)
        {
            case ",":
                rechner.eingabeKomma();
                refresh();
                break;

            case "+":
            case "-":
            case "×":
            case "÷":
                rechner.operatorSetzen(t);
                refresh();
                break;

            case "=":
                display.setText(rechner.berechne());
                recdisplay.setText(rechner.getVerlauf());
                break;

            case "+/_":
                rechner.wechselVorzeichen();
                refresh();
                break;

            case "C":
                rechner.allesLoeschen();
                refresh();
                break;

            case "CE":
                rechner.ce();
                refresh();
                break;

            case "←":
                rechner.loeschen();
                refresh();
                break;

            case "mod":
                // Modulo-Operator
                rechner.operatorSetzen("%");
                refresh();
                break;

            case "x²":
                rechner.quadriere();
                refresh();
                break;

            case "√x":
                rechner.wurzel();
                refresh();
                break;

            case "1/x":
                rechner.reziprok();
                refresh();
                break;

            case "(":
                rechner.klammerAuf();
                refresh();
                break;

            case ")":
                rechner.klammerZu();
                refresh();
                break;

            case "n!":
                rechner.fakultaet();
                refresh();
                break;

            case "10ˣ":
                rechner.zehnHoch();
                refresh();
                break;

            case "xʸ":
                rechner.potenz();
                refresh();
                break;

            case "ln":
                rechner.ln();
                refresh();
                break;

            case "log":
                rechner.log();
                refresh();
                break;

            case "sin":
                rechner.sin();
                refresh();
                break;

            case "cos":
                rechner.cos();
                refresh();
                break;

            case "tan":
                rechner.tan();
                refresh();
                break;

            case "π":
                rechner.pi();
                refresh();
                break;

            case "e":
                rechner.e();
                refresh();
                break;

            case "DEG":
            case "RAD":
                rechner.toggleWinkelModus();
                sourceBtn.setText(rechner.getWinkelModus().name());
                refreshWithExtraInfo(rechner.getWinkelModus().name());
                break;

            case "Dark":
            case "Light":
                toggleDarkMode(sourceBtn);
                break;

            case "exp":
                rechner.exp();
                refresh();
                break; // WICHTIG!

            case "|x|":
                rechner.betrag();
                refresh();
                break;

            default:
                Toolkit.getDefaultToolkit().beep();
        }
    }

    private void styleButton(JButton btn, String text)
    {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        Color baseColor;
        Color textColor = Color.WHITE;

        if (text.matches("\\d"))
        {
            baseColor = new Color(45, 45, 45);
        } else if ("+-×÷".contains(text))
        {
            baseColor = new Color(255, 149, 0);
            textColor = Color.BLACK;
        } else if (text.equals("C") || text.equals("CE") || text.equals("←"))
        {
            baseColor = new Color(100, 60, 60);
        } else if (text.equals("Dark") || text.equals("Light"))
        {
            baseColor = new Color(70, 70, 120);
        } else if (text.equals("DEG") || text.equals("RAD"))
        {
            baseColor = new Color(80, 100, 140);
        } else
        {
            baseColor = new Color(60, 60, 60);
        }

        btn.setBackground(baseColor);
        btn.setForeground(textColor);

        btn.putClientProperty("baseColor", baseColor);
        btn.addMouseMotionListener(new MouseAdapter()
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
                btn.setBackground((Color) btn.getClientProperty("baseColor"));
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                btn.setBackground(helleColor(baseColor, 20));
            }
        });
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

    private void toggleDarkMode(JButton darkBtn)
    {
        darkMode = !darkMode;
        darkBtn.setText(darkMode ? "Dark" : "Light");

        Color startBg = display.getBackground();
        Color targetBg = darkMode ? new Color(25, 25, 25) : Color.WHITE;

        Color startFg = display.getForeground();
        Color targetFg = darkMode ? Color.WHITE : Color.BLACK;

        Timer timer = new Timer(15, null);
        final int[] step = {0};
        int steps = 20;

        timer.addActionListener(e -> {
            float t = step[0] / (float) steps;

            Color bg = lerp(startBg, targetBg, t);
            Color fg = lerp(startFg, targetFg, t);

            getContentPane().setBackground(bg);
            display.setBackground(bg);
            display.setForeground(fg);
            recdisplay.setBackground(bg);

            for (Component c : buttonPanel.getComponents())
            {
                if (c instanceof JButton b)
                {
                    Color base = (Color) b.getClientProperty("baseColor");
                    if (base != null) b.setBackground(base);
                }
            }

            step[0]++;
            if (step[0] > steps) timer.stop();
        });

        timer.start();
    }

    private Color lerp(Color a, Color b, float t)
    {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    private void setupKeyboard()
    {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        for (int i = 0; i <= 9; i++)
        {
            final String num = String.valueOf(i);

            bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), "digitTop" + i, () -> {
                rechner.eingabeZahl(num);
                refresh();
            });

            bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), "digitPad" + i, () -> {
                rechner.eingabeZahl(num);
                refresh();
            });

            bind(im, am, KeyStroke.getKeyStroke((char) ('0' + i)), "digitTyped" + i, () -> {
                rechner.eingabeZahl(num);
                refresh();
            });
        }

        Runnable commaAction = () -> {
            rechner.eingabeKomma();
            refresh();
        };

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), "commaVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0), "periodVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), "decimalVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(','), "commaTyped", commaAction);
        bind(im, am, KeyStroke.getKeyStroke('.'), "periodTyped", commaAction);

        Runnable plusAction = () -> {
            rechner.operatorSetzen("+");
            refresh();
        };
        Runnable minusAction = () -> {
            rechner.operatorSetzen("-");
            refresh();
        };
        Runnable mulAction = () -> {
            rechner.operatorSetzen("*");
            refresh();
        };
        Runnable divAction = () -> {
            rechner.operatorSetzen("/");
            refresh();
        };
        Runnable modAction = () -> {
            rechner.operatorSetzen("%");
            refresh();
        };

        bind(im, am, KeyStroke.getKeyStroke('+'), "plusTyped", plusAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "plusPad", plusAction);

        bind(im, am, KeyStroke.getKeyStroke('-'), "minusTyped", minusAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minusVK", minusAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "minusPad", minusAction);

        bind(im, am, KeyStroke.getKeyStroke('*'), "mulTyped", mulAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "mulPad", mulAction);

        bind(im, am, KeyStroke.getKeyStroke('/'), "divTyped", divAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "divVK", divAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), "divPad", divAction);

        bind(im, am, KeyStroke.getKeyStroke('%'), "modTyped", modAction);
        bind(im, am, KeyStroke.getKeyStroke('^'), "powTyped", () -> {
            rechner.potenz();
            refresh();
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterPress", () -> {
            display.setText(rechner.berechne());
            recdisplay.setText(rechner.getVerlauf());
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspacePress", () -> {
            rechner.loeschen();
            refresh();
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapePress", this::dispose);
    }

    private void bind(InputMap im, ActionMap am, KeyStroke ks, String name, Runnable action)
    {
        if (ks == null) return;
        im.put(ks, name);
        am.put(name, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                action.run();
            }
        });
    }
}
