import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TaschenrechnerUI extends JFrame
{
    private static final String SEARCH_PLACEHOLDER = "Suche…";
    private static final String BASE_COLOR_KEY = "baseColor";

    private static final Color DARK_BG = new Color(25, 25, 25);
    private static final Color PLACEHOLDER_FG = new Color(140, 140, 140);

    private static final String[] BUTTONS = {
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

    private final JTextPane display = new JTextPane();
    private final JTextPane recDisplay = new JTextPane();
    private final JPanel buttonPanel = new JPanel(new GridLayout(9, 5, 6, 6));

    private boolean darkMode = true;

    private final TaschenrechnerLogik rechner = new TaschenrechnerLogik();

    private final DefaultListModel<String> allHistoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(historyModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);
    private final JButton clearHistoryBtn = new JButton("Clear");
    private final JTextField historySearchField = new JTextField();

    private final Map<String, Runnable> actions = new HashMap<>();

    public TaschenrechnerUI()
    {
        configureFrame();

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(DARK_BG);
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        contentPane.add(buildTopPanel(), BorderLayout.NORTH);
        contentPane.add(buildCenterPanel(), BorderLayout.CENTER);

        initActions();
        buildButtons();
        setupKeyboard();
        setupHistorySearch();
        setupHistoryInteractions();
        setupSearchFieldKeyForwarding();

        refresh();
        applyHistoryColors();

        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
    }

    private void configureFrame()
    {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 850);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(550, 650));
    }

    private JPanel buildTopPanel()
    {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        configureRecDisplay();
        configureDisplay();

        topPanel.add(recDisplay, BorderLayout.NORTH);
        topPanel.add(display, BorderLayout.CENTER);

        return topPanel;
    }

    private JPanel buildCenterPanel()
    {
        buttonPanel.setBackground(DARK_BG);

        JPanel centerWrap = new JPanel(new BorderLayout(10, 10));
        centerWrap.setOpaque(false);

        centerWrap.add(buttonPanel, BorderLayout.CENTER);
        centerWrap.add(buildHistoryPanel(), BorderLayout.EAST);

        buttonPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                defocusSearchIfNeeded();
            }
        });

        return centerWrap;
    }

    private JPanel buildHistoryPanel()
    {
        JPanel historyPanel = new JPanel(new BorderLayout(6, 6));
        historyPanel.setOpaque(false);

        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFocusable(false);
        historyList.setCellRenderer(new HistoryHighlightRenderer());

        historyScroll.setBorder(null);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        historyScroll.setPreferredSize(new Dimension(240, 0));

        clearHistoryBtn.setFocusable(false);
        clearHistoryBtn.addActionListener(e -> clearHistory());

        configureSearchField();

        JPanel historyTop = new JPanel(new BorderLayout(6, 6));
        historyTop.setOpaque(false);
        historyTop.add(historySearchField, BorderLayout.CENTER);
        historyTop.add(clearHistoryBtn, BorderLayout.EAST);

        historyPanel.add(historyTop, BorderLayout.NORTH);
        historyPanel.add(historyScroll, BorderLayout.CENTER);

        return historyPanel;
    }

    private void configureRecDisplay()
    {
        recDisplay.setEditable(false);
        recDisplay.setBackground(DARK_BG);
        recDisplay.setForeground(new Color(180, 180, 180));
        recDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        recDisplay.setOpaque(true);
        recDisplay.setBorder(null);
        recDisplay.setFocusable(false);
        alignRight(recDisplay);
    }

    private void configureDisplay()
    {
        display.setEditable(false);
        display.setBackground(DARK_BG);
        display.setForeground(Color.WHITE);
        display.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        display.setText("0");
        display.setBorder(null);
        display.setOpaque(true);
        display.setFocusable(false);
        alignRight(display);
    }

    private void configureSearchField()
    {
        historySearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historySearchField.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        historySearchField.setOpaque(true);
        historySearchField.setText(SEARCH_PLACEHOLDER);
        historySearchField.setForeground(PLACEHOLDER_FG);

        historySearchField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (SEARCH_PLACEHOLDER.equals(historySearchField.getText()))
                {
                    historySearchField.setText("");
                    historySearchField.setForeground(darkMode ? Color.WHITE : Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (historySearchField.getText().isBlank())
                {
                    historySearchField.setText(SEARCH_PLACEHOLDER);
                    historySearchField.setForeground(PLACEHOLDER_FG);
                }
            }
        });

        historySearchField.addActionListener(e -> requestFocusInWindow());
    }

    private void clearHistory()
    {
        allHistoryModel.clear();
        historyModel.clear();
        historySearchField.setText("");
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
        recDisplay.setText(rechner.getVerlauf());
    }

    private void refreshWithExtraInfo(String info)
    {
        display.setText(rechner.formatLiveAnzeige());
        String v = rechner.getVerlauf();
        recDisplay.setText(info + (v.isEmpty() ? "" : " | " + v));
    }

    private void buildButtons()
    {
        for (String text : BUTTONS)
        {
            JButton btn = new JButton(text);
            styleButton(btn, text);
            btn.addActionListener(e -> handleButton((JButton) e.getSource()));
            buttonPanel.add(btn);
        }
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
        }
        else if ("+-×÷".contains(text))
        {
            baseColor = new Color(173, 41, 99);
            textColor = Color.BLACK;
        }
        else if (text.equals("C") || text.equals("CE") || text.equals("←"))
        {
            baseColor = new Color(100, 60, 60);
        }
        else if (text.equals("Dark") || text.equals("Light"))
        {
            baseColor = new Color(70, 70, 120);
        }
        else if (text.equals("DEG") || text.equals("RAD"))
        {
            baseColor = new Color(80, 100, 140);
        }
        else
        {
            baseColor = new Color(60, 60, 60);
        }

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
        Color targetBg = darkMode ? DARK_BG : Color.WHITE;

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

            recDisplay.setBackground(bg);
            recDisplay.setForeground(darkMode ? new Color(180, 180, 180) : new Color(70, 70, 70));

            for (Component c : buttonPanel.getComponents())
            {
                if (c instanceof JButton b)
                {
                    Color base = (Color) b.getClientProperty(BASE_COLOR_KEY);
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
        Color bg = darkMode ? DARK_BG : Color.WHITE;
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
        historySearchField.setCaretColor(fg);

        if (SEARCH_PLACEHOLDER.equals(historySearchField.getText()))
        {
            historySearchField.setForeground(PLACEHOLDER_FG);
        }
        else
        {
            historySearchField.setForeground(fg);
        }
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

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "plusPad", () -> { rechner.operatorSetzen("+"); refresh(); });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "plusVK", () -> { rechner.operatorSetzen("+"); refresh(); });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "minusPad", () -> { rechner.operatorSetzen("-"); refresh(); });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minusVK", () -> { rechner.operatorSetzen("-"); refresh(); });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "mulPad", () -> { rechner.operatorSetzen("*"); refresh(); });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK, 0), "mulVK", () -> { rechner.operatorSetzen("*"); refresh(); });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), "divPad", () -> { rechner.operatorSetzen("/"); refresh(); });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "divVK", () -> { rechner.operatorSetzen("/"); refresh(); });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "modVK", () -> { rechner.operatorSetzen("%"); refresh(); });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterMain", this::evaluate);

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

    private void setupHistorySearch()
    {
        historySearchField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent e) { applyHistoryFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyHistoryFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyHistoryFilter(); }
        });
    }

    private void setupHistoryInteractions()
    {
        historyList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() != 2) return;

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
        });
    }

    private boolean matchesHistoryFilter(String entry)
    {
        String q = historySearchField.getText();
        if (q == null) return true;

        q = q.trim();
        if (q.isEmpty() || SEARCH_PLACEHOLDER.equals(q)) return true;

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
            boolean hasQuery = !q.isEmpty() && !SEARCH_PLACEHOLDER.equals(q);

            if (hasQuery)
            {
                String safeText = escapeHtml(text);
                String safeQuery = escapeHtml(q);

                String highlighted = safeText.replaceAll(
                        "(?i)(" + Pattern.quote(safeQuery) + ")",
                        "<span style='background:#ffea00; color:#000; padding:1px 2px; border-radius:3px;'>$1</span>"
                );

                lbl.setText("<html><div style='white-space:nowrap;'>" + highlighted + "</div></html>");
            }
            else
            {
                lbl.setText(text);
            }

            lbl.setBorder(pad);

            Color bg = darkMode ? DARK_BG : Color.WHITE;
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
        recDisplay.setText(v);

        if (!"Fehler".equals(res))
        {
            addHistoryEntry(v);
        }
    }

    private void setupSearchFieldKeyForwarding()
    {
        historySearchField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
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
                    rechner.operatorSetzen(String.valueOf(ch));
                    refresh();
                    getRootPane().requestFocusInWindow();
                    return;
                }

                if (ch == '\n')
                {
                    e.consume();
                    evaluate();
                    getRootPane().requestFocusInWindow();
                }
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
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

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
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
