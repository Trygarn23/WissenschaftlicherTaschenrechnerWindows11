import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TaschenrechnerUI extends JFrame
{
    private final JTextPane display;
    private final JTextPane recdisplay;
    private final JPanel buttonPanel;

    private boolean darkMode = true;

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

        //Oberer Bereich mit Verlauf +Display
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Verlauf
        recdisplay = new JTextPane();
        recdisplay.setEditable(false);
        recdisplay.setBackground(new Color(25, 25, 25));
        recdisplay.setForeground(new Color(180, 180, 180));
        recdisplay.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        recdisplay.setOpaque(true);
        recdisplay.setBorder(null);
        StyledDocument doc2 = recdisplay.getStyledDocument();
        SimpleAttributeSet right2 = new SimpleAttributeSet();
        StyleConstants.setAlignment(right2, StyleConstants.ALIGN_RIGHT);
        doc2.setParagraphAttributes(0, doc2.getLength(), right2, false);
        topPanel.add(recdisplay, BorderLayout.NORTH);

        // Hauptdisplay
        display = new JTextPane();
        display.setEditable(false);
        display.setBackground(new Color(25, 25, 25));
        display.setForeground(new Color(255, 255, 255));
        display.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        display.setText("0");
        display.setBorder(null);
        display.setOpaque(true);
        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(0, doc.getLength(), right, false);
        topPanel.add(display, BorderLayout.CENTER);
        contentPane.add(topPanel, BorderLayout.NORTH); // Buttons
        buttonPanel = new JPanel(new GridLayout(8, 5, 6, 6));
        buttonPanel.setBackground(new Color(25, 25, 25));
        contentPane.add(buttonPanel, BorderLayout.CENTER);

        String[] buttons = {
                "2nd", "π", "e", "CE", "C",
                "Sin", "cos", "tan", "←", "Dark",
                "x²", "1/x", "|x|", "exp", "mod",
                "√x", "(", ")", "n!", "÷",
                "xʸ", "7", "8", "9", "×",
                "10ˣ", "4", "5", "6", "-",
                "log", "1", "2", "3", "+",
                "ln", "+/_", "0", ",", "="
        };

        TaschenrechnerLogik rechner = new TaschenrechnerLogik();
        setupKeyboard(rechner);
        for (String text : buttons)
        {
            JButton btn = new JButton(text);
            styleButton(btn, text);
            btn.addActionListener(e -> {
                String t = ((JButton) e.getSource()).getText();
                switch (t)
                {
                    case "0":
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                        display.setText(rechner.eingabeZahl(t));
                        recdisplay.setText(rechner.getVerlauf());
                        break;
                    case ",":
                        display.setText(rechner.eingabeKomma());
                        break;
                    case "+":
                    case "-":
                    case "×":
                    case "÷":
                        display.setText(rechner.operatorSetzen(t));
                        recdisplay.setText(rechner.getVerlauf());
                        break;
                    case "=":
                        display.setText(rechner.berechne());
                        recdisplay.setText(rechner.getVerlauf());
                        break;
                    case "+/_":
                        display.setText(rechner.wechselVorzeichen());
                        break;
                    case "C":
                        display.setText(rechner.allesLoeschen());
                        recdisplay.setText(rechner.getVerlauf());
                        break;
                    case "CE":
                        display.setText(rechner.ce());
                        recdisplay.setText(rechner.getVerlauf());
                        break;
                    case "←":
                        display.setText(rechner.loeschen());
                        break;
                    case "%":
                        display.setText(rechner.prozent());
                        break;
                    case "x²":
                        display.setText(rechner.quadriere());
                        break;
                    case "√x":
                        display.setText(rechner.wurzel());
                        break;
                    case "1/x":
                        display.setText(rechner.reziprok());
                        break;
                    case "(":
                        display.setText(rechner.klammerAuf());
                        break;
                    case ")":
                        display.setText(rechner.klammerZu());
                        break;
                    case "n!":
                        display.setText(rechner.fakultaet());
                        break;
                    case "10ˣ":
                        display.setText(rechner.zehnHoch());
                        break;
                    case "xʸ":
                        display.setText(rechner.potenz());
                        break;
                    case "ln":
                        display.setText(rechner.ln());
                        break;
                    case "log":
                        display.setText(rechner.log());
                        break;
                    case "sin":
                        display.setText(rechner.sin());
                        break;
                    case "cos":
                        display.setText(rechner.cos());
                        break;
                    case "tan":
                        display.setText(rechner.tan());
                        break;
                    case "π":
                        display.setText(rechner.pi());
                        break;
                    case "e":
                        display.setText(rechner.e());
                        break;
                    case "Dark":
                    case "Light":
                        toggleDarkMode();
                        break;
                }
            });

            buttonPanel.add(btn);
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
        return new Color(Math.min(255, c.getRed() + amount), Math.min(255, c.getGreen() + amount), Math.min(255, c.getBlue() + amount));
    }

    private Color dunkelColor(Color c, int amount)
    {

        return new Color(Math.max(0, c.getRed() + amount), Math.max(0, c.getGreen() + amount), Math.max(0, c.getBlue() + amount));
    }

    private void toggleDarkMode()
    {
        darkMode = !darkMode;

        Color bg = darkMode ? new Color(25, 25, 25) : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;
        Color recFg = darkMode ? Color.RED : Color.BLUE;

        getContentPane().setBackground(bg);

        display.setBackground(bg);
        display.setForeground(fg);
        recdisplay.setBackground(bg);
        recdisplay.setForeground(recFg);

        // Buttons
        for (Component comp : buttonPanel.getComponents())
        {
            if (comp instanceof JButton btn)
            {
                btn.setBackground(bg);
                btn.setForeground(fg);
            }
        }

        // Texte auf Buttons
        for (Component comp : buttonPanel.getComponents())
        {
            if (comp instanceof JButton btn && (btn.getText().equalsIgnoreCase("Dark") || btn.getText().equalsIgnoreCase("Light")))
            {
                btn.setText(darkMode ? "Dark" : "Light");
            }
        }
        revalidate();
        repaint();

    }

    private void setupKeyboard(TaschenrechnerLogik rechner)
    {
        JRootPane root = SwingUtilities.getRootPane(buttonPanel);
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        for (int i = 0; i <= 9; i++)
        {
            final String num = String.valueOf(i);
            im.put(KeyStroke.getKeyStroke(num), "num" + num);
            am.put("num" + num, new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    display.setText(rechner.eingabeZahl(num));
                    recdisplay.setText(rechner.getVerlauf());
                }
            });
        }

        im.put(KeyStroke.getKeyStroke(","), "komma");
        im.put(KeyStroke.getKeyStroke("."), "komma");
        am.put("komma", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.eingabeKomma());
            }
        });

        im.put(KeyStroke.getKeyStroke("+"), "plus");
        im.put(KeyStroke.getKeyStroke("-"), "minus");
        im.put(KeyStroke.getKeyStroke("*"), "mal");
        im.put(KeyStroke.getKeyStroke("/"), "geteilt");

        am.put("plus", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.operatorSetzen("+"));
                recdisplay.setText(rechner.getVerlauf());
            }
        });
        am.put("minus", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.operatorSetzen("-"));
                recdisplay.setText(rechner.getVerlauf());
            }
        });
        am.put("mal", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.operatorSetzen("*"));
                recdisplay.setText(rechner.getVerlauf());
            }
        });
        am.put("geteilt", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.operatorSetzen("/"));
                recdisplay.setText(rechner.getVerlauf());
            }
        });

        im.put(KeyStroke.getKeyStroke("P"), "pi");
        im.put(KeyStroke.getKeyStroke("p"), "pi");
        am.put("pi", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.pi());
                display.setText(rechner.getVerlauf());
            }
        });

        im.put(KeyStroke.getKeyStroke("F"), "Fakultät");
        im.put(KeyStroke.getKeyStroke("f"), "Fakultät");
        am.put("Fakultät", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.fakultaet());
                display.setText(rechner.getVerlauf());
            }
        });

        im.put(KeyStroke.getKeyStroke("S"), "Squareroot");
        im.put(KeyStroke.getKeyStroke("s"), "Squareroot");
        am.put("Squareroot", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.wurzel());
                recdisplay.setText(rechner.getVerlauf());
            }
        });

        im.put(KeyStroke.getKeyStroke("E"), "euler");
        im.put(KeyStroke.getKeyStroke("e"), "euler");
        am.put("euler", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.e());
            }
        });

        im.put(KeyStroke.getKeyStroke("enter"), "enter");
        am.put("enter", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.berechne());
                recdisplay.setText(rechner.getVerlauf());
            }
        });

        im.put(KeyStroke.getKeyStroke("BACK_SPACE"), "back");
        am.put("back", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                display.setText(rechner.loeschen());
            }
        });

        im.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        am.put("escape", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
    }
}