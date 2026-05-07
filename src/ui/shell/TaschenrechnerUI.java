package ui.shell;

import common.history.DateiVerlaufRepository;
import common.history.VerlaufService;
import common.state.RechnerModus;
import modes.graph.ui.GraphPlaceholderPanel;
import modes.komplex.ui.KomplexPlaceholderPanel;
import modes.programmierer.ui.ProgrammiererPanel;
import modes.standard.ui.StandardPanel;
import common.logic.BerechnungsErgebnis;
import common.logic.RechnerService;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import modes.wissenschaftlich.ui.WissenschaftlichPanel;
import ui.theme.AppTheme;
import ui.settings.AppSettings;
import ui.theme.ThemeManager;
import ui.theme.ThemeType;
import ui.settings.SettingsDialog;
import ui.settings.SettingsPersistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.Map;

public class TaschenrechnerUI extends JFrame
{
    private final ThemeManager themeManager = new ThemeManager();
    private final SettingsPersistence settingsPersistence = new SettingsPersistence();
    private AppSettings appSettings = settingsPersistence.lade();
    private final RechnerService rechner = new RechnerService();
    private final WissenschaftlichOperationen wissenschaftlichOperationen = new WissenschaftlichOperationen(rechner.getAusdruckEditor());
    private final VerlaufService verlaufService = new VerlaufService(new DateiVerlaufRepository());

    private RechnerModus aktuellerModus = appSettings.getStartModus();

    private final GlobalActionBarPanel globalActionBarPanel = new GlobalActionBarPanel();
    private final ModeBarPanel modeBarPanel = new ModeBarPanel();
    private final DisplayPanel displayPanel = new DisplayPanel();
    private final ModeContentHostPanel modeContentHostPanel = new ModeContentHostPanel();
    private final HistoryPanel historyPanel = new HistoryPanel();
    private final Map<RechnerModus, JPanel> modePanels = new EnumMap<>(RechnerModus.class);

    private ShellActionRegistry shellActionRegistry;
    private KeyboardShortcutBinder keyboardShortcutBinder;

    public TaschenrechnerUI()
    {
        applySettingsToServices();
        themeManager.setTheme(appSettings.getThemeType());
        configureFrame();
        buildLayout();

        shellActionRegistry = new ShellActionRegistry(rechner, wissenschaftlichOperationen, this::refresh, this::refreshWithExtraInfo, this::evaluate);

        initModeContent();
        wireShellEvents();

        keyboardShortcutBinder = new KeyboardShortcutBinder(
                getRootPane(),
                historyPanel,
                rechner,
                this::refresh,
                this::evaluate,
                this::dispose,
                this::sindStandardShortcutsAktiv
        );
        keyboardShortcutBinder.setupKeyboard();
        keyboardShortcutBinder.setupSearchFieldKeyForwarding();

        ladeVerlauf();
        refresh();
        applyCurrentTheme();

        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
    }

    private void configureFrame()
    {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(appSettings.getFensterBreite(), appSettings.getFensterHoehe());
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(980, 700));
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                speichereFenstergroesse();
            }
        });
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

        WissenschaftlichPanel wissenschaftlichPanel = new WissenschaftlichPanel();
        wissenschaftlichPanel.setFunctionSelectionListener(shellActionRegistry::handleScientificMenuAction);

        registerMode(RechnerModus.WISSENSCHAFTLICH, wissenschaftlichPanel);
        registerMode(RechnerModus.PROGRAMMIERER, new ProgrammiererPanel());
        registerMode(RechnerModus.GRAPH, new GraphPlaceholderPanel());
        registerMode(RechnerModus.KOMPLEX, new KomplexPlaceholderPanel());

        setAktuellerModus(aktuellerModus);
    }

    private void registerMode(RechnerModus modus, JPanel panel)
    {
        modePanels.put(modus, panel);

        if (modus == RechnerModus.STANDARD || modus == RechnerModus.WISSENSCHAFTLICH)
        {
            shellActionRegistry.attachCalculatorButtonActions(panel);
        }

        modeContentHostPanel.registerMode(modus, panel);
    }

    private void wireShellEvents()
    {
        modeBarPanel.setModeListener(this::setAktuellerModus);

        globalActionBarPanel.setAngleModeListener(e -> {
            rechner.winkelModusUmschalten();
            appSettings.setWinkelModus(rechner.getWinkelModus());
            settingsPersistence.speichere(appSettings);
            globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
            refreshWithExtraInfo(rechner.getWinkelModus().name());
        });

        globalActionBarPanel.setThemeSelectionListener(this::setTheme);
        globalActionBarPanel.setSettingsListener(e -> SettingsDialog.showDialog(this, theme(), appSettings, this::applySettings));
        historyPanel.setClearHistoryListener(e -> speichereVerlauf());
        historyPanel.setEntryDoubleClickListener(this::useHistoryEntryResult);
        displayPanel.setPasteListener(this::pasteDisplayText);
    }

    private void setAktuellerModus(RechnerModus modus)
    {
        aktuellerModus = modus;
        appSettings.setStartModus(modus);
        settingsPersistence.speichere(appSettings);

        modeBarPanel.setSelectedMode(modus, themeManager.getCurrentTheme());
        modeContentHostPanel.showMode(modus);

        displayPanel.setVisible(sollGlobalesDisplayAnzeigen(modus));
        historyPanel.setVisible(sollHistoryAnzeigen(modus));
        if (!sollHistoryAnzeigen(modus) && keyboardShortcutBinder != null)
        {
            keyboardShortcutBinder.defocusSearchIfNeeded();
        }

        updateStatus();
        revalidate();
        repaint();
    }

    private boolean sollHistoryAnzeigen(RechnerModus modus)
    {
        return switch (modus)
        {
            case STANDARD, WISSENSCHAFTLICH -> true;
            case PROGRAMMIERER, GRAPH, KOMPLEX -> false;
        };
    }

    private boolean sollGlobalesDisplayAnzeigen(RechnerModus modus)
    {
        return modus != RechnerModus.PROGRAMMIERER;
    }

    private boolean sindStandardShortcutsAktiv()
    {
        return aktuellerModus == RechnerModus.STANDARD || aktuellerModus == RechnerModus.WISSENSCHAFTLICH;
    }

    private void useHistoryEntryResult(String entry)
    {
        if (entry == null || entry.isBlank()) return;

        int eq = entry.lastIndexOf('=');
        if (eq < 0) return;

        String resultPart = entry.substring(eq + 1).trim();
        rechner.setzeAusdruckAusVerlaufErgebnis(resultPart);
        refresh();
    }

    private void refresh()
    {
        displayPanel.setMainText(rechner.formatiereLiveAnzeige());
        displayPanel.setSecondaryText(rechner.getVerlauf());
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
        updateStatus();
    }

    private void refreshWithExtraInfo(String info)
    {
        displayPanel.setMainText(rechner.formatiereLiveAnzeige());
        String verlauf = rechner.getVerlauf();
        displayPanel.setSecondaryText(info + (verlauf.isEmpty() ? "" : " | " + verlauf));
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
        updateStatus();
    }

    private void updateStatus()
    {
        String speicherText = rechner.hatSpeicherWert() ? "M belegt" : "Speicher leer";
        displayPanel.setStatusText(
                "Modus: " + aktuellerModus.getLabel()
                        + " | Winkel: " + rechner.getWinkelModus().name()
                        + " | " + speicherText
        );
    }

    private void pasteDisplayText(String text)
    {
        if (aktuellerModus != RechnerModus.STANDARD && aktuellerModus != RechnerModus.WISSENSCHAFTLICH)
        {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        rechner.setzeAusdruckAusZwischenablage(text);
        refresh();
    }

    private void setTheme(ThemeType themeType)
    {
        AppSettings updatedSettings = appSettings.copy();
        updatedSettings.setThemeType(themeType);
        applySettings(updatedSettings);
    }

    private void applySettings(AppSettings settings)
    {
        boolean historySettingChanged = appSettings.isHistoryEnabled() != settings.isHistoryEnabled();
        appSettings = settings.copy();
        settingsPersistence.speichere(appSettings);
        applySettingsToServices();
        themeManager.setTheme(appSettings.getThemeType());
        if (historySettingChanged)
        {
            ladeVerlauf();
        }
        applyCurrentTheme();
        refresh();
    }

    private void applySettingsToServices()
    {
        rechner.setWinkelModus(appSettings.getWinkelModus());
        rechner.setNachkommastellen(appSettings.getNachkommastellen());
        rechner.setZahlenFormatModus(appSettings.getZahlenFormatModus());
    }

    private void applyCurrentTheme()
    {
        getContentPane().setBackground(theme().windowBackground());

        globalActionBarPanel.applyTheme(theme());
        globalActionBarPanel.setThemeButtonText(theme().getDisplayName());
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
        globalActionBarPanel.highlightSelectedTheme(themeManager.getCurrentThemeType());

        modeBarPanel.setSelectedMode(aktuellerModus, theme());
        displayPanel.applyTheme(theme());
        historyPanel.applyTheme(theme());

        for (Map.Entry<RechnerModus, JPanel> entry : modePanels.entrySet())
        {
            applyThemeRecursively(entry.getValue());
        }

        repaint();
        revalidate();
    }

    private void applyThemeRecursively(Component component)
    {
        if (component instanceof ProgrammiererPanel programmiererPanel)
        {
            programmiererPanel.applyTheme(theme());
            return;
        }

        if (component instanceof WissenschaftlichPanel wissenschaftlichPanel)
        {
            wissenschaftlichPanel.applyTheme(theme());
        }

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
        btn.setFocusable(Boolean.TRUE.equals(btn.getClientProperty("keyboardFocusable")));

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

    private void evaluate()
    {
        BerechnungsErgebnis ergebnis = rechner.berechneDetailliert();
        displayPanel.setMainText(ergebnis.getAnzeigeText());

        if (ergebnis.isErfolgreich())
        {
            displayPanel.setSecondaryText(ergebnis.getVerlaufText());
            addHistoryEntry(ergebnis.getVerlaufText());
            updateStatus();
            return;
        }

        displayPanel.setSecondaryText(ergebnis.getFehlerMeldung());
        updateStatus();
    }

    private void addHistoryEntry(String entry)
    {
        if (entry == null || entry.isBlank()) return;

        historyPanel.addEntry(entry);
        speichereVerlauf();
    }

    private void ladeVerlauf()
    {
        historyPanel.setAllEntries(appSettings.isHistoryEnabled() ? verlaufService.ladeEintraege() : java.util.List.of());
    }

    private void speichereVerlauf()
    {
        if (appSettings.isHistoryEnabled())
        {
            verlaufService.speichereEintraege(historyPanel.getAllEntries());
        }
    }

    private void speichereFenstergroesse()
    {
        appSettings.setFensterBreite(getWidth());
        appSettings.setFensterHoehe(getHeight());
        settingsPersistence.speichere(appSettings);
    }
}
