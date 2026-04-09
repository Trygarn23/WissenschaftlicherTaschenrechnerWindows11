import ProgrammierRechner.ProgrammiererPanel;
import Theme.AppTheme;
import Theme.ThemeManager;
import Theme.ThemeType;

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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;

public class TaschenrechnerUI extends JFrame
{
    private static final String SEARCH_PLACEHOLDER = "Suche…";

    private final ThemeManager themeManager = new ThemeManager();

    private enum Modus
    {
        STANDARD, WISSENSCHAFTLICH, PROGRAMMIERER, GRAPH, KOMPLEX
    }

    private static final String[] STANDARD_BUTTONS = {
            "%", "CE", "C", "←",
            "1/x", "x²", "√x", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ",", "=",
    };

    private static final String[] WISSENSCHAFTLICH_BUTTONS = {
            "asin", "acos", "atan", "sinh", "cosh",
            "tanh", "floor", "ceil", "round", "rand",

            "MC", "MR", "M+", "M-", "Ans",
            "DEG", "π", "e", "CE", "C",
            "sin", "cos", "tan", "←", "Dark",
            "x²", "1/x", "|x|", "exp", "mod",
            "√x", "(", ")", "n!", "÷",
            "xʸ", "7", "8", "9", "×",
            "10ˣ", "4", "5", "6", "-",
            "log", "1", "2", "3", "+",
            "ln", "±", "0", ",", "="
    };

    private final CardLayout modusLayout = new CardLayout();
    private final JPanel modusKarten = new JPanel(modusLayout);
    private final Map<Modus, JButton> modusButtons = new EnumMap<>(Modus.class);
    private final Map<Modus, JPanel> modusButtonPanels = new EnumMap<>(Modus.class);

    private Modus aktuellerModus = Modus.STANDARD;
    private static final Path HISTORY_FILE =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_history.txt");

    private final JTextPane display = new JTextPane();
    private final JTextPane recDisplay = new JTextPane();

    private final TaschenrechnerLogik rechner = new TaschenrechnerLogik();
    private final ProgrammiererPanel prgPanel = new ProgrammiererPanel();

    private final DefaultListModel<String> allHistoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(historyModel);
    private final JScrollPane historyScroll = new JScrollPane(historyList);
    private final JButton clearHistoryBtn = new JButton("Clear");
    private final JTextField historySearchField = new JTextField();

    private final Map<String, Runnable> actions = new HashMap<>();

    private final JPanel modeBar = new JPanel(new GridLayout(1, 5, 8, 0));

    private final Map<Modus, JPanel> placeholderCards = new EnumMap<>(Modus.class);
    private final Map<Modus, JLabel> placeholderLabels = new EnumMap<>(Modus.class);

    public TaschenrechnerUI()
    {
        configureFrame();

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBackground(theme().windowBackground());
        contentPane.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(contentPane);

        initActions();

        contentPane.add(buildModeBar(), BorderLayout.NORTH);
        contentPane.add(buildMainContent(), BorderLayout.CENTER);

        setupKeyboard();
        setupHistorySearch();
        setupHistoryInteractions();
        ladeVerlauf();
        setupSearchFieldKeyForwarding();

        refresh();
        applyCurrentTheme();

        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
    }

    private void configureFrame()
    {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(820, 720));
    }

    private AppTheme theme()
    {
        return themeManager.getCurrentTheme();
    }

    private JPanel buildModeBar()
    {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 0, 12, 0));

        modeBar.setBackground(theme().modeBarBackground());
        modeBar.setBorder(new EmptyBorder(8, 8, 8, 8));

        modeBar.add(buildModeButton("Standard", Modus.STANDARD));
        modeBar.add(buildModeButton("Wissenschaftlich", Modus.WISSENSCHAFTLICH));
        modeBar.add(buildModeButton("PRG", Modus.PROGRAMMIERER));
        modeBar.add(buildModeButton("Graph", Modus.GRAPH));
        modeBar.add(buildModeButton("Komplex", Modus.KOMPLEX));

        wrap.add(modeBar, BorderLayout.CENTER);
        return wrap;
    }


    private void applyHistoryTheme()
    {
        historyList.setBackground(theme().historyBackground());
        historyList.setForeground(theme().historyForeground());
        historyList.setSelectionBackground(theme().historySelectionBackground());
        historyList.setSelectionForeground(theme().historyForeground());

        historyScroll.getViewport().setBackground(theme().historyBackground());

        clearHistoryBtn.setBackground(theme().specialButtonBackground());
        clearHistoryBtn.setForeground(theme().specialButtonForeground());
        clearHistoryBtn.setBorderPainted(false);
        clearHistoryBtn.setOpaque(true);
        clearHistoryBtn.setFont(theme().buttonFont());

        historySearchField.setBackground(theme().historySearchBackground());
        historySearchField.setCaretColor(theme().historyForeground());

        if (SEARCH_PLACEHOLDER.equals(historySearchField.getText()))
        {
            historySearchField.setForeground(theme().placeholderForeground());
        }
        else
        {
            historySearchField.setForeground(theme().historyForeground());
        }
    }

    private JButton buildModeButton(String text, Modus modus)
    {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(true);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme().modeBorder(), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        button.addActionListener(e -> setAktuellerModus(modus));

        modusButtons.put(modus, button);
        aktualisiereModusButtons();

        return button;
    }

    private JPanel buildMainContent()
    {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        mainPanel.add(buildTopPanel(), BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new BorderLayout(10, 10));
        centerWrap.setOpaque(false);
        centerWrap.add(buildModeCards(), BorderLayout.CENTER);
        centerWrap.add(buildHistoryPanel(), BorderLayout.EAST);

        mainPanel.add(centerWrap, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel buildModeCards()
    {
        modusKarten.setOpaque(false);

        JPanel standardPanel = buildButtonGrid(STANDARD_BUTTONS, 6, 4);
        JPanel wissenschaftlichPanel = buildButtonGrid(WISSENSCHAFTLICH_BUTTONS, 11, 5);

        modusButtonPanels.put(Modus.STANDARD, standardPanel);
        modusButtonPanels.put(Modus.WISSENSCHAFTLICH, wissenschaftlichPanel);
        modusButtonPanels.put(Modus.PROGRAMMIERER, prgPanel);

        modusKarten.add(standardPanel, Modus.STANDARD.name());
        modusKarten.add(wissenschaftlichPanel, Modus.WISSENSCHAFTLICH.name());
        modusKarten.add(prgPanel, Modus.PROGRAMMIERER.name());
        modusKarten.add(buildPlaceholderPanel("Graph-Modus", Modus.GRAPH), Modus.GRAPH.name());
        modusKarten.add(buildPlaceholderPanel("Komplex-Modus", Modus.KOMPLEX), Modus.KOMPLEX.name());

        modusLayout.show(modusKarten, aktuellerModus.name());
        return modusKarten;
    }

    private void styleButton(JButton btn, String text)
    {
        btn.setFont(theme().buttonFont());
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);

        Color bg;
        Color fg;

        if (text.matches("\\d"))
        {
            bg = theme().numberButtonBackground();
            fg = theme().numberButtonForeground();
        } else if ("+-×÷".contains(text))
        {
            bg = theme().operatorButtonBackground();
            fg = theme().operatorButtonForeground();
        } else if (text.equals("C") || text.equals("CE") || text.equals("←"))
        {
            bg = theme().specialButtonBackground();
            fg = theme().specialButtonForeground();
        } else if (text.equals("Dark") || text.equals("Light") || text.equals("DEG") || text.equals("RAD"))
        {
            bg = theme().toggleButtonBackground();
            fg = theme().toggleButtonForeground();
        } else
        {
            bg = theme().functionButtonBackground();
            fg = theme().functionButtonForeground();
        }

        btn.setBackground(bg);
        btn.setForeground(fg);
    }

    private JPanel buildButtonGrid(String[] buttons, int rows, int cols)
    {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 6, 6));
        panel.setBackground(theme().panelBackground());
        panel.setOpaque(true);

        for (String text : buttons)
        {
            JButton btn = new JButton(text);
            styleButton(btn, text);
            btn.addActionListener(e -> handleButton((JButton) e.getSource()));
            panel.add(btn);
        }

        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                defocusSearchIfNeeded();
            }
        });

        return panel;
    }

    private JPanel buildPlaceholderPanel(String titel, Modus modus)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(theme().panelBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme().modeBorder(), 1),
                new EmptyBorder(40, 30, 40, 30)
        ));

        JLabel label = new JLabel(titel + " folgt bald", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(theme().displayForeground());

        card.add(label, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);

        placeholderCards.put(modus, card);
        placeholderLabels.put(modus, label);

        return panel;
    }
    private void setAktuellerModus(Modus modus)
    {
        aktuellerModus = modus;
        modusLayout.show(modusKarten, modus.name());
        aktualisiereModusButtons();
        defocusSearchIfNeeded();
    }

    private void aktualisiereModusButtons()
    {
        for (Map.Entry<Modus, JButton> entry : modusButtons.entrySet())
        {
            boolean aktiv = entry.getKey() == aktuellerModus;
            JButton button = entry.getValue();

            button.setBackground(
                    aktiv
                            ? theme().modeButtonActiveBackground()
                            : theme().modeButtonInactiveBackground()
            );
            button.setForeground(Color.WHITE);
        }
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
        historyScroll.setPreferredSize(new Dimension(200, 0));

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
        recDisplay.setBackground(theme().displayBackground());
        recDisplay.setForeground(theme().secondaryDisplayForeground());
        recDisplay.setFont(theme().secondaryDisplayFont());
        recDisplay.setOpaque(true);
        recDisplay.setBorder(null);
        recDisplay.setFocusable(false);
        alignRight(recDisplay);
    }

    private void configureDisplay()
    {
        display.setEditable(false);
        display.setBackground(theme().displayBackground());
        display.setForeground(theme().displayForeground());
        display.setFont(theme().displayFont());
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
        historySearchField.setBackground(theme().historySearchBackground());
        historySearchField.setText(SEARCH_PLACEHOLDER);
        historySearchField.setForeground(theme().placeholderForeground());
        historySearchField.setCaretColor(theme().historyForeground());

        historySearchField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (SEARCH_PLACEHOLDER.equals(historySearchField.getText()))
                {
                    historySearchField.setText("");
                    historySearchField.setForeground(theme().historyForeground());
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (historySearchField.getText().isBlank())
                {
                    historySearchField.setText(SEARCH_PLACEHOLDER);
                    historySearchField.setForeground(theme().placeholderForeground());
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
        speichereVerlauf();
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
        display.setText(rechner.formatiereLiveAnzeige());
        recDisplay.setText(rechner.getVerlauf());
    }

    private void refreshWithExtraInfo(String info)
    {
        display.setText(rechner.formatiereLiveAnzeige());
        String v = rechner.getVerlauf();
        recDisplay.setText(info + (v.isEmpty() ? "" : " | " + v));
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
            rechner.winkelModusUmschalten();
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


    private void toggleDarkMode(JButton darkBtn)
    {
        ThemeType nextType =
                themeManager.getCurrentThemeType() == ThemeType.DARK
                        ? ThemeType.LIGHT
                        : ThemeType.DARK;

        themeManager.setTheme(nextType);
        darkBtn.setText(themeManager.getCurrentTheme().getDisplayName());

        applyCurrentTheme();
    }

    private void applyCurrentTheme()
    {
        getContentPane().setBackground(theme().windowBackground());
        modusKarten.setBackground(theme().panelBackground());

        display.setBackground(theme().displayBackground());
        display.setForeground(theme().displayForeground());
        display.setFont(theme().displayFont());

        recDisplay.setBackground(theme().displayBackground());
        recDisplay.setForeground(theme().secondaryDisplayForeground());
        recDisplay.setFont(theme().secondaryDisplayFont());

        applyHistoryTheme();

        for (JPanel panel : modusButtonPanels.values())
        {
            panel.setBackground(theme().panelBackground());

            for (Component c : panel.getComponents())
            {
                if (c instanceof JButton btn)
                {
                    styleButton(btn, btn.getText());
                }
            }
        }

        aktualisiereModusButtons();

        repaint();
        revalidate();

        modeBar.setBackground(theme().modeBarBackground());

        for (JButton button : modusButtons.values())
        {
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme().modeBorder(), 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));
        }

        for (JPanel card : placeholderCards.values())
        {
            card.setBackground(theme().panelBackground());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme().modeBorder(), 1),
                    new EmptyBorder(40, 30, 40, 30)
            ));
        }

        for (JLabel label : placeholderLabels.values())
        {
            label.setForeground(theme().displayForeground());
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

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "plusPad", () -> {
            rechner.operatorSetzen("+");
            refresh();
        });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "plusVK", () -> {
            rechner.operatorSetzen("+");
            refresh();
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "minusPad", () -> {
            rechner.operatorSetzen("-");
            refresh();
        });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minusVK", () -> {
            rechner.operatorSetzen("-");
            refresh();
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "mulPad", () -> {
            rechner.operatorSetzen("*");
            refresh();
        });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK, 0), "mulVK", () -> {
            rechner.operatorSetzen("*");
            refresh();
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), "divPad", () -> {
            rechner.operatorSetzen("/");
            refresh();
        });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "divVK", () -> {
            rechner.operatorSetzen("/");
            refresh();
        });

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "modVK", () -> {
            rechner.operatorSetzen("%");
            refresh();
        });

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
                rechner.setzeAusdruckAusVerlaufErgebnis(resultPart);
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
        speichereVerlauf();
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
            } else
            {
                lbl.setText(text);
            }

            lbl.setBorder(pad);

            Color bg = theme().historyBackground();
            Color fg = theme().historyForeground();
            Color selectedBg = theme().historySelectionBackground();

            if (isSelected)
            {
                lbl.setBackground(selectedBg);
                lbl.setForeground(fg);
            } else
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

    private void ladeVerlauf()
    {
        try
        {
            if (!Files.exists(HISTORY_FILE)) return;

            List<String> zeilen = Files.readAllLines(HISTORY_FILE, StandardCharsets.UTF_8);
            for (String zeile : zeilen)
            {
                if (zeile == null || zeile.isBlank()) continue;
                allHistoryModel.addElement(zeile);
            }

            applyHistoryFilter();
        } catch (IOException ignored)
        {
        }
    }

    private void speichereVerlauf()
    {
        List<String> zeilen = new ArrayList<>();
        for (int i = 0; i < allHistoryModel.size(); i++)
        {
            zeilen.add(allHistoryModel.getElementAt(i));
        }

        try
        {
            Files.write(
                    HISTORY_FILE,
                    zeilen,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException ignored)
        {
        }
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
        actions.put(",", () -> {
            rechner.eingabeKomma();
            refresh();
        });

        actions.put("+", () -> {
            rechner.operatorSetzen("+");
            refresh();
        });
        actions.put("-", () -> {
            rechner.operatorSetzen("-");
            refresh();
        });
        actions.put("×", () -> {
            rechner.operatorSetzen("×");
            refresh();
        });
        actions.put("÷", () -> {
            rechner.operatorSetzen("÷");
            refresh();
        });

        actions.put("=", this::evaluate);

        actions.put("±", () -> {
            rechner.wechselVorzeichen();
            refresh();
        });

        actions.put("C", () -> {
            rechner.allesLoeschen();
            refresh();
        });
        actions.put("CE", () -> {
            rechner.ce();
            refresh();
        });
        actions.put("←", () -> {
            rechner.loeschen();
            refresh();
        });

        actions.put("mod", () -> {
            rechner.operatorSetzen("%");
            refresh();
        });

        actions.put("x²", () -> {
            rechner.quadriere();
            refresh();
        });
        actions.put("√x", () -> {
            rechner.wurzel();
            refresh();
        });
        actions.put("1/x", () -> {
            rechner.reziprok();
            refresh();
        });

        actions.put("(", () -> {
            rechner.klammerAuf();
            refresh();
        });
        actions.put(")", () -> {
            rechner.klammerZu();
            refresh();
        });

        actions.put("n!", () -> {
            rechner.fakultaet();
            refresh();
        });

        actions.put("10ˣ", () -> {
            rechner.zehnHoch();
            refresh();
        });
        actions.put("xʸ", () -> {
            rechner.potenz();
            refresh();
        });

        actions.put("ln", () -> {
            rechner.ln();
            refresh();
        });
        actions.put("log", () -> {
            rechner.log();
            refresh();
        });

        actions.put("sin", () -> {
            rechner.sin();
            refresh();
        });
        actions.put("cos", () -> {
            rechner.cos();
            refresh();
        });
        actions.put("tan", () -> {
            rechner.tan();
            refresh();
        });

        actions.put("π", () -> {
            rechner.pi();
            refresh();
        });
        actions.put("e", () -> {
            rechner.e();
            refresh();
        });

        actions.put("exp", () -> {
            rechner.exp();
            refresh();
        });
        actions.put("|x|", () -> {
            rechner.betrag();
            refresh();
        });

        actions.put("MC", () -> {
            rechner.speicherLoeschen();
            refreshWithExtraInfo("M = 0");
        });
        actions.put("MR", () -> {
            rechner.speicherAbrufen();
            refresh();
        });
        actions.put("M+", () -> refreshWithExtraInfo("M = " + rechner.speicherAddieren()));
        actions.put("M-", () -> refreshWithExtraInfo("M = " + rechner.speicherSubtrahieren()));

        actions.put("Ans", () -> {
            rechner.ans();
            refresh();
        });

        actions.put("asin", () -> {
            rechner.arcsin();
            refresh();
        });
        actions.put("acos", () -> {
            rechner.arccos();
            refresh();
        });
        actions.put("atan", () -> {
            rechner.arctan();
            refresh();
        });

        actions.put("sinh", () -> {
            rechner.sinusHyperbolicus();
            refresh();
        });
        actions.put("cosh", () -> {
            rechner.cosinusHyperbolicus();
            refresh();
        });
        actions.put("tanh", () -> {
            rechner.tangensHyperbolicus();
            refresh();
        });

        actions.put("floor", () -> {
            rechner.abrunden();
            refresh();
        });
        actions.put("ceil", () -> {
            rechner.aufrunden();
            refresh();
        });
        actions.put("round", () -> {
            rechner.runden();
            refresh();
        });

        actions.put("rand", () -> {
            rechner.zufall();
            refresh();
        });
    }
}
