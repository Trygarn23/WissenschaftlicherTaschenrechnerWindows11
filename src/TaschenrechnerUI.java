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
import java.util.HashMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TaschenrechnerUI extends JFrame
{
    private final JTextPane display;
    private final JTextPane recdisplay;
    private final JPanel buttonPanel;

    private boolean darkMode = true;

    private final TaschenrechnerLogik rechner = new TaschenrechnerLogik();

    private final DefaultListModel<String> allHistoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(historyModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);
    private final JButton clearHistoryBtn = new JButton("Clear");
    private final JTextField historySearchField = new JTextField();
    private final HashMap<String, Runnable> actions = new HashMap<>();


    public TaschenrechnerUI()
    {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 850);
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
        recdisplay.setFocusable(false);
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
        display.setFocusable(false);
        alignRight(display);
        topPanel.add(display, BorderLayout.CENTER);

        contentPane.add(topPanel, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridLayout(9, 5, 6, 6));
        buttonPanel.setBackground(new Color(25, 25, 25));

        JPanel centerWrap = new JPanel(new BorderLayout(10, 10));
        centerWrap.setOpaque(false);
        centerWrap.add(buttonPanel, BorderLayout.CENTER);

        JPanel historyPanel = new JPanel(new BorderLayout(6, 6));
        historyPanel.setOpaque(false);

        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFocusable(false);

        historyScroll.setBorder(null);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        historyScroll.setPreferredSize(new Dimension(240, 0));

        clearHistoryBtn.setFocusable(false);

        centerWrap.add(historyPanel, BorderLayout.EAST);

        contentPane.add(centerWrap, BorderLayout.CENTER);

        historySearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historySearchField.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        historySearchField.setOpaque(true);
        historySearchField.setText("Suche…");
        historySearchField.setForeground(new Color(140, 140, 140));

        historyList.setCellRenderer(new HistoryHighlightRenderer());


        historySearchField.addFocusListener(new java.awt.event.FocusAdapter()
        {
            @Override
            public void focusGained(java.awt.event.FocusEvent e)
            {
                if ("Suche…".equals(historySearchField.getText()))
                {
                    historySearchField.setText("");
                    historySearchField.setForeground(darkMode ? Color.WHITE : Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e)
            {
                if (historySearchField.getText().isBlank())
                {
                    historySearchField.setText("Suche…");
                    historySearchField.setForeground(new Color(140, 140, 140));
                }
            }
        });


        JPanel historyTop = new JPanel(new BorderLayout(6, 6));
        historyTop.setOpaque(false);
        historyTop.add(historySearchField, BorderLayout.CENTER);
        historyTop.add(clearHistoryBtn, BorderLayout.EAST);

        clearHistoryBtn.setFocusable(false);
        clearHistoryBtn.addActionListener(e -> {
            allHistoryModel.clear();
            historyModel.clear();
            historySearchField.setText("");
        });


        historyPanel.add(historyTop, BorderLayout.NORTH);
        historyPanel.add(historyScroll, BorderLayout.CENTER);


        String[] buttons = {
                "MC", "MR", "M+", "M-", "Ans",
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


        historySearchField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                applyHistoryFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                applyHistoryFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                applyHistoryFilter();
            }
        });

        historySearchField.addActionListener(e -> requestFocusInWindow());

        historyList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    int idx = historyList.locationToIndex(e.getPoint());
                    if (idx < 0) return;

                    Rectangle r = historyList.getCellBounds(idx, idx);
                    if (r == null || !r.contains(e.getPoint())) return;

                    String entry = historyModel.getElementAt(idx);
                    int eq = entry.lastIndexOf('=');
                    if (eq < 0) return;

                    String resultPart = entry.substring(eq + 1).trim();
                    rechner.setAusdruckVonHistoryResult(resultPart);
                    refresh();
                }
            }
        });

        buttonPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                defocusSearchIfNeeded();
            }
        });



        for (String text : buttons)
        {
            JButton btn = new JButton(text);
            styleButton(btn, text);
            btn.addActionListener(e -> handleButton((JButton) e.getSource()));
            buttonPanel.add(btn);
        }

        refresh();
        applyHistoryColors();
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        initActions();
        setupSearchFieldKeyForwarding();

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
        defocusSearchIfNeeded();

        String t = sourceBtn.getText();

        if (t.matches("\\d"))
        {
            rechner.eingabeZahl(t);
            refresh();
            return;
        }

        if ("DEG".equals(t) || "RAD".equals(t))
        {
            rechner.toggleWinkelModus();
            sourceBtn.setText(rechner.getWinkelModus().name());
            refreshWithExtraInfo(rechner.getWinkelModus().name());
            return;
        }

        if ("Dark".equals(t) || "Light".equals(t))
        {
            toggleDarkMode(sourceBtn);
            return;
        }

        actions.getOrDefault(t, Toolkit.getDefaultToolkit()::beep).run();
    }

    private void styleButton(JButton btn, String text)
    {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);

        Color baseColor;
        Color textColor = Color.WHITE;

        if (text.matches("\\d"))
        {
            baseColor = new Color(45, 45, 45);
        } else if ("+-×÷".contains(text))
        {
            baseColor = new Color(173, 41, 99);
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
            recdisplay.setForeground(darkMode ? new Color(180, 180, 180) : new Color(70, 70, 70));

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
            applyHistoryColors();

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

    private void applyHistoryColors()
    {
        Color bg = darkMode ? new Color(25, 25, 25) : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        historyList.setBackground(bg);
        historyList.setForeground(fg);
        historyList.setSelectionBackground(helleColor(bg, 30));
        historyList.setSelectionForeground(fg);

        historyScroll.getViewport().setBackground(bg);

        clearHistoryBtn.setBackground(darkMode ? new Color(60, 60, 60) : new Color(220, 220, 220));
        clearHistoryBtn.setForeground(fg);
        clearHistoryBtn.setBorderPainted(false);
        clearHistoryBtn.setOpaque(true);
        clearHistoryBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        historySearchField.setBackground(darkMode ? new Color(35, 35, 35) : new Color(245, 245, 245));
        historySearchField.setForeground(darkMode ? Color.WHITE : Color.BLACK);
        historySearchField.setCaretColor(darkMode ? Color.WHITE : Color.BLACK);

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
        }

        Runnable commaAction = () -> {
            rechner.eingabeKomma();
            refresh();
        };

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), "commaVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0), "periodVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), "decimalVK", commaAction);

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

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "plusPad", plusAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "plusVK", plusAction);

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "minusPad", minusAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minusVK", minusAction);

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "mulPad", mulAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK, 0), "mulVK", mulAction);

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), "divPad", divAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "divVK", divAction);

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "modVK", modAction);

        Runnable evalAction = () -> {
            String res = rechner.berechne();
            display.setText(res);
            String v = rechner.getVerlauf();
            recdisplay.setText(v);

            if (!"Fehler".equals(res))
            {
                addHistoryEntry(v);
            }
        };

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterMain", evalAction);

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
                if (keyboardBlockedBySearch())
                {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                action.run();
            }
        });
    }


    private boolean matchesHistoryFilter(String entry)
    {
        String q = historySearchField.getText();
        if (q == null) return true;

        q = q.trim();
        if (q.isEmpty() || "Suche…".equals(q)) return true;

        q = q.toLowerCase();
        return entry.toLowerCase().contains(q);
    }


    private void applyHistoryFilter()
    {
        historyModel.clear();
        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            String entry = allHistoryModel.getElementAt(i);
            if (matchesHistoryFilter(entry))
            {
                historyModel.addElement(entry);
            }
        }
        int last = historyModel.size() - 1;
        if (last >= 0) historyList.ensureIndexIsVisible(last);
    }

    private void addHistoryEntry(String entry)
    {
        if (entry == null || entry.isBlank()) return;

        allHistoryModel.addElement(entry);

        if (matchesHistoryFilter(entry))
        {
            historyModel.addElement(entry);
            int last = historyModel.size() - 1;
            if (last >= 0) historyList.ensureIndexIsVisible(last);
        }
    }
    private class HistoryHighlightRenderer extends DefaultListCellRenderer
    {
        private final EmptyBorder pad = new EmptyBorder(6, 8, 6, 8);

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String text = value == null ? "" : value.toString();

            String q = historySearchField.getText();
            q = (q == null) ? "" : q.trim();
            boolean hasQuery = !q.isEmpty() && !"Suche…".equals(q);

            if (hasQuery)
            {
                String safeText = escapeHtml(text);
                String safeQuery = escapeHtml(q);

                String highlighted = safeText.replaceAll("(?i)(" + java.util.regex.Pattern.quote(safeQuery) + ")",
                        "<span style='background:#ffea00; color:#000; padding:1px 2px; border-radius:3px;'>$1</span>");

                lbl.setText("<html><div style='white-space:nowrap;'>" + highlighted + "</div></html>");
            }
            else
            {
                lbl.setText(text);
            }

            lbl.setBorder(pad);

            Color bg = darkMode ? new Color(25, 25, 25) : Color.WHITE;
            Color fg = darkMode ? Color.WHITE : Color.BLACK;

            if (isSelected)
            {
                lbl.setBackground(helleColor(bg, 30));
                lbl.setForeground(fg);
            }
            else
            {
                lbl.setBackground(bg);
                lbl.setForeground(fg);
            }

            lbl.setOpaque(true);
            return lbl;
        }
    }

    private String escapeHtml(String s)
    {
        if (s == null) return "";
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void evaluate()
    {
        String res = rechner.berechne();
        display.setText(res);

        String v = rechner.getVerlauf();
        recdisplay.setText(v);

        if (!"Fehler".equals(res))
        {
            addHistoryEntry(v);
        }
    }


    private void setupSearchFieldKeyForwarding()
    {
        historySearchField.addKeyListener(new java.awt.event.KeyAdapter()
        {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e)
            {
                char ch = e.getKeyChar();

                if (Character.isDigit(ch))
                {
                    e.consume();
                    rechner.eingabeZahl(String.valueOf(ch));
                    refresh();
                    getRootPane().requestFocusInWindow();
                    return;
                }

                if (ch == ',' || ch == '.')
                {
                    e.consume();
                    rechner.eingabeKomma();
                    refresh();
                    getRootPane().requestFocusInWindow();
                    return;
                }

                if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%')
                {
                    e.consume();
                    String op = String.valueOf(ch);
                    rechner.operatorSetzen(op);
                    refresh();
                    getRootPane().requestFocusInWindow();
                    return;
                }

                if (ch == '\n')
                {
                    e.consume();
                    String res = rechner.berechne();
                    display.setText(res);
                    String v = rechner.getVerlauf();
                    recdisplay.setText(v);

                    if (!"Fehler".equals(res))
                    {
                        addHistoryEntry(v);
                    }

                    getRootPane().requestFocusInWindow();
                }
            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e)
            {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE)
                {
                    if (historySearchField.getSelectionStart() != historySearchField.getSelectionEnd()
                            || historySearchField.getCaretPosition() > 0)
                    {
                        return;
                    }

                    e.consume();
                    rechner.loeschen();
                    refresh();
                    getRootPane().requestFocusInWindow();
                }

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE)
                {
                    historySearchField.setText("");
                    getRootPane().requestFocusInWindow();
                }
            }
        });
    }

    private boolean keyboardBlockedBySearch()
    {
        return historySearchField.isFocusOwner();
    }

    private void defocusSearchIfNeeded()
    {
        if (historySearchField.isFocusOwner())
        {
            getRootPane().requestFocusInWindow();
        }
    }

    private void initActions()
    {
        actions.put(",", () -> { rechner.eingabeKomma(); refresh(); });

        actions.put("+", () -> { rechner.operatorSetzen("+"); refresh(); });
        actions.put("-", () -> { rechner.operatorSetzen("-"); refresh(); });
        actions.put("×", () -> { rechner.operatorSetzen("×"); refresh(); });
        actions.put("÷", () -> { rechner.operatorSetzen("÷"); refresh(); });

        actions.put("=", this::evaluate);

        actions.put("+/_", () -> { rechner.wechselVorzeichen(); refresh(); });

        actions.put("C",  () -> { rechner.allesLoeschen(); refresh(); });
        actions.put("CE", () -> { rechner.ce(); refresh(); });
        actions.put("←",  () -> { rechner.loeschen(); refresh(); });

        actions.put("mod", () -> { rechner.operatorSetzen("%"); refresh(); });

        actions.put("x²", () -> { rechner.quadriere(); refresh(); });
        actions.put("√x", () -> { rechner.wurzel(); refresh(); });
        actions.put("1/x", () -> { rechner.reziprok(); refresh(); });

        actions.put("(", () -> { rechner.klammerAuf(); refresh(); });
        actions.put(")", () -> { rechner.klammerZu(); refresh(); });

        actions.put("n!", () -> { rechner.fakultaet(); refresh(); });

        actions.put("10ˣ", () -> { rechner.zehnHoch(); refresh(); });
        actions.put("xʸ",  () -> { rechner.potenz(); refresh(); });

        actions.put("ln",  () -> { rechner.ln(); refresh(); });
        actions.put("log", () -> { rechner.log(); refresh(); });

        actions.put("sin", () -> { rechner.sin(); refresh(); });
        actions.put("cos", () -> { rechner.cos(); refresh(); });
        actions.put("tan", () -> { rechner.tan(); refresh(); });

        actions.put("π", () -> { rechner.pi(); refresh(); });
        actions.put("e", () -> { rechner.e(); refresh(); });

        actions.put("exp", () -> { rechner.exp(); refresh(); });
        actions.put("|x|", () -> { rechner.betrag(); refresh(); });

        actions.put("MC", () -> { rechner.memoryClear(); refreshWithExtraInfo("M = 0"); });
        actions.put("MR", () -> { rechner.memoryRecall(); refresh(); });
        actions.put("M+", () -> refreshWithExtraInfo("M = " + rechner.memoryAdd()));
        actions.put("M-", () -> refreshWithExtraInfo("M = " + rechner.memorySub()));

        actions.put("Ans", () -> { rechner.ans(); refresh(); });
    }

}
