package ui;

import Theme.AppTheme;
import Theme.ThemeManager;
import Theme.ThemeType;
import logic.TaschenrechnerLogik;
import ui.modes.GraphPlaceholderPanel;
import ui.modes.KomplexPlaceholderPanel;
import ui.modes.ProgrammiererHostPanel;
import ui.modes.RechnerModus;
import ui.modes.StandardPanel;
import ui.modes.WissenschaftlichPanel;
import ui.shell.DisplayPanel;
import ui.shell.GlobalActionBarPanel;
import ui.shell.HistoryPanel;
import ui.shell.ModeBarPanel;
import ui.shell.ModeContentHostPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaschenrechnerUI extends JFrame
{
    private static final Path HISTORY_FILE =
            Paths.get(System.getProperty("user.home"), ".wissenschaftlicher_taschenrechner_history.txt");

    private final ThemeManager themeManager = new ThemeManager();
    private final TaschenrechnerLogik rechner = new TaschenrechnerLogik();

    private RechnerModus aktuellerModus = RechnerModus.STANDARD;

    private final GlobalActionBarPanel globalActionBarPanel = new GlobalActionBarPanel();
    private final ModeBarPanel modeBarPanel = new ModeBarPanel();
    private final DisplayPanel displayPanel = new DisplayPanel();
    private final ModeContentHostPanel modeContentHostPanel = new ModeContentHostPanel();
    private final HistoryPanel historyPanel = new HistoryPanel();

    private final Map<RechnerModus, JPanel> modePanels = new EnumMap<>(RechnerModus.class);
    private final Map<String, Runnable> actions = new HashMap<>();

    public TaschenrechnerUI()
    {
        configureFrame();
        buildLayout();
        initModeContent();
        initActions();
        wireShellEvents();

        setupKeyboard();
        setupSearchFieldKeyForwarding();
        ladeVerlauf();

        refresh();
        applyCurrentTheme();

        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
    }

    private void configureFrame()
    {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 860);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(980, 700));
    }

    private AppTheme theme()
    {
        return themeManager.getCurrentTheme();
    }

    private void buildLayout()
    {
        JPanel contentPane = new JPanel(new BorderLayout(12, 12));
        contentPane.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(contentPane);

        JPanel topArea = new JPanel();
        topArea.setOpaque(false);
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.add(globalActionBarPanel);
        topArea.add(Box.createVerticalStrut(10));
        topArea.add(modeBarPanel);
        topArea.add(Box.createVerticalStrut(12));
        topArea.add(displayPanel);

        JPanel centerArea = new JPanel(new BorderLayout(12, 0));
        centerArea.setOpaque(false);
        centerArea.add(modeContentHostPanel, BorderLayout.CENTER);
        centerArea.add(historyPanel, BorderLayout.EAST);

        contentPane.add(topArea, BorderLayout.NORTH);
        contentPane.add(centerArea, BorderLayout.CENTER);
    }

    private void initModeContent()
    {
        registerMode(RechnerModus.STANDARD, new StandardPanel());
        registerMode(RechnerModus.WISSENSCHAFTLICH, new WissenschaftlichPanel());
        registerMode(RechnerModus.PROGRAMMIERER, new ProgrammiererHostPanel());
        registerMode(RechnerModus.GRAPH, new GraphPlaceholderPanel());
        registerMode(RechnerModus.KOMPLEX, new KomplexPlaceholderPanel());

        setAktuellerModus(aktuellerModus);
    }

    private void registerMode(RechnerModus modus, JPanel panel)
    {
        modePanels.put(modus, panel);

        if (modus == RechnerModus.STANDARD || modus == RechnerModus.WISSENSCHAFTLICH)
        {
            attachCalculatorButtonActions(panel);
        }

        modeContentHostPanel.registerMode(modus, panel);
    }

    private void wireShellEvents()
    {
        modeBarPanel.setModeListener(this::setAktuellerModus);

        globalActionBarPanel.setAngleModeListener(e -> {
            rechner.winkelModusUmschalten();
            globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
            refreshWithExtraInfo(rechner.getWinkelModus().name());
        });

        globalActionBarPanel.setThemeToggleListener(e -> toggleTheme());

        historyPanel.setClearHistoryListener(e -> speichereVerlauf());

        historyPanel.setEntryDoubleClickListener(this::useHistoryEntryResult);
    }

    private void setAktuellerModus(RechnerModus modus)
    {
        aktuellerModus = modus;
        modeContentHostPanel.showMode(modus);
        modeBarPanel.setSelectedMode(modus, theme());
        defocusSearchIfNeeded();
    }

    private void useHistoryEntryResult(String entry)
    {
        if (entry == null || entry.isBlank())
        {
            return;
        }

        int eq = entry.lastIndexOf('=');
        if (eq < 0)
        {
            return;
        }

        String resultPart = entry.substring(eq + 1).trim();
        rechner.setzeAusdruckAusVerlaufErgebnis(resultPart);
        refresh();
    }

    private void refresh()
    {
        displayPanel.setMainText(rechner.formatiereLiveAnzeige());
        displayPanel.setSecondaryText(rechner.getVerlauf());
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
    }

    private void refreshWithExtraInfo(String info)
    {
        displayPanel.setMainText(rechner.formatiereLiveAnzeige());
        String verlauf = rechner.getVerlauf();
        displayPanel.setSecondaryText(info + (verlauf.isEmpty() ? "" : " | " + verlauf));
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
    }

    private void handleButton(JButton sourceBtn)
    {
        defocusSearchIfNeeded();

        String text = sourceBtn.getText();

        if (text == null || text.isBlank())
        {
            return;
        }

        if (text.matches("\\d"))
        {
            rechner.eingabeZahl(text);
            refresh();
            return;
        }

        actions.getOrDefault(text, Toolkit.getDefaultToolkit()::beep).run();
    }

    private void toggleTheme()
    {
        ThemeType nextType =
                themeManager.getCurrentThemeType() == ThemeType.DARK
                        ? ThemeType.LIGHT
                        : ThemeType.DARK;

        themeManager.setTheme(nextType);
        applyCurrentTheme();
    }

    private void applyCurrentTheme()
    {
        getContentPane().setBackground(theme().windowBackground());

        globalActionBarPanel.applyTheme(theme());
        globalActionBarPanel.setThemeButtonText(theme().getDisplayName());
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());

        modeBarPanel.setSelectedMode(aktuellerModus, theme());
        displayPanel.applyTheme(theme());
        historyPanel.applyTheme(theme());

        for (Map.Entry<RechnerModus, JPanel> entry : modePanels.entrySet())
        {
            if (entry.getKey() == RechnerModus.PROGRAMMIERER)
            {
                continue;
            }

            applyThemeRecursively(entry.getValue());
        }

        repaint();
        revalidate();
    }

    private void applyThemeRecursively(Component component)
    {
        if (component instanceof JButton button)
        {
            styleButton(button, button.getText());
        }
        else if (component instanceof JLabel label)
        {
            label.setForeground(theme().displayForeground());
        }
        else if (component instanceof JPanel panel)
        {
            panel.setBackground(theme().panelBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeRecursively(child);
            }
        }
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

        if (text != null && text.matches("\\d"))
        {
            bg = theme().numberButtonBackground();
            fg = theme().numberButtonForeground();
        }
        else if (text != null && "+-×÷".contains(text))
        {
            bg = theme().operatorButtonBackground();
            fg = theme().operatorButtonForeground();
        }
        else if ("C".equals(text) || "CE".equals(text) || "←".equals(text))
        {
            bg = theme().specialButtonBackground();
            fg = theme().specialButtonForeground();
        }
        else
        {
            bg = theme().functionButtonBackground();
            fg = theme().functionButtonForeground();
        }

        btn.setBackground(bg);
        btn.setForeground(fg);
    }

    private void attachCalculatorButtonActions(Component component)
    {
        if (component instanceof JButton button)
        {
            button.addActionListener(e -> handleButton(button));
            return;
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                attachCalculatorButtonActions(child);
            }
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

    private void evaluate()
    {
        String res = rechner.berechne();
        displayPanel.setMainText(res);

        String verlauf = rechner.getVerlauf();
        displayPanel.setSecondaryText(verlauf);

        if (!"Fehler".equals(res))
        {
            addHistoryEntry(verlauf);
        }
    }

    private void addHistoryEntry(String entry)
    {
        if (entry == null || entry.isBlank())
        {
            return;
        }

        historyPanel.addEntry(entry);
        speichereVerlauf();
    }

    private void setupSearchFieldKeyForwarding()
    {
        historyPanel.addSearchFieldKeyListener(new KeyAdapter()
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
                    if (historyPanel.hasSearchSelection() || historyPanel.getSearchCaretPosition() > 0)
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
                    historyPanel.clearSearch();
                    getRootPane().requestFocusInWindow();
                }
            }
        });
    }

    private void ladeVerlauf()
    {
        try
        {
            if (!Files.exists(HISTORY_FILE))
            {
                return;
            }

            List<String> zeilen = Files.readAllLines(HISTORY_FILE, StandardCharsets.UTF_8);
            historyPanel.setAllEntries(zeilen);
        }
        catch (IOException ignored)
        {
        }
    }

    private void speichereVerlauf()
    {
        try
        {
            Files.write(
                    HISTORY_FILE,
                    historyPanel.getAllEntries(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        catch (IOException ignored)
        {
        }
    }

    private boolean keyboardBlockedBySearch()
    {
        return historyPanel.isSearchFocusOwner();
    }

    private void defocusSearchIfNeeded()
    {
        if (historyPanel.isSearchFocusOwner())
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