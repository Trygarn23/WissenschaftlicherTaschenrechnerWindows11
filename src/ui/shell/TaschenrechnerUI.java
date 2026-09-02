package ui.shell;

import common.history.VerlaufEintrag;
import common.state.RechnerModus;
import modes.graph.ui.GraphPanel;
import modes.komplex.ui.KomplexPanel;
import modes.matrix.ui.MatrixPanel;
import modes.programmierer.ui.ProgrammiererPanel;
import modes.statistik.ui.StatistikPanel;
import modes.standard.ui.StandardPanel;
import common.logic.BerechnungsErgebnis;
import common.logic.RechnerService;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import modes.wissenschaftlich.ui.WissenschaftlichPanel;
import ui.theme.AppTheme;
import ui.history.HistoryPanel;
import ui.settings.AppSettings;
import ui.theme.ThemeManager;
import ui.theme.ThemeType;
import ui.settings.SettingsDialog;
import ui.units.EinheitenSidePanelHost;

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
    private final ShellPersistenceService persistenceService = new ShellPersistenceService();
    private AppSettings appSettings = persistenceService.ladeSettings();
    private final RechnerService rechner = new RechnerService();
    private final WissenschaftlichOperationen wissenschaftlichOperationen = new WissenschaftlichOperationen(rechner.getAusdruckEditor());

    private RechnerModus aktuellerModus = appSettings.getStartModus();

    private final GlobalActionBarPanel globalActionBarPanel = new GlobalActionBarPanel();
    private final ModeBarPanel modeBarPanel = new ModeBarPanel();
    private final DisplayPanel displayPanel = new DisplayPanel();
    private final ModeContentHostPanel modeContentHostPanel = new ModeContentHostPanel();
    private final HistoryPanel historyPanel = new HistoryPanel();
    private final EinheitenSidePanelHost einheitenSidePanelHost = new EinheitenSidePanelHost();
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

        JPanel sideArea = new JPanel(new BorderLayout(10, 0));
        sideArea.setOpaque(false);
        sideArea.add(einheitenSidePanelHost, BorderLayout.WEST);
        sideArea.add(historyPanel, BorderLayout.EAST);
        centerArea.add(sideArea, BorderLayout.EAST);

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
        registerMode(RechnerModus.GRAPH, new GraphPanel());
        registerMode(RechnerModus.KOMPLEX, new KomplexPanel());
        registerMode(RechnerModus.MATRIX, new MatrixPanel());
        registerMode(RechnerModus.STATISTIK, new StatistikPanel());

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
        modeBarPanel.setUnitsListener(einheitenSidePanelHost::toggle);

        globalActionBarPanel.setAngleModeListener(e -> {
            rechner.winkelModusUmschalten();
            appSettings.setWinkelModus(rechner.getWinkelModus());
            persistenceService.speichereSettings(appSettings);
            aktualisiereGraphWinkelmodus();
            globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
            refreshWithExtraInfo(rechner.getWinkelModus().name());
        });

        globalActionBarPanel.setThemeSelectionListener(this::setTheme);
        globalActionBarPanel.setSettingsListener(e -> SettingsDialog.showDialog(
                this,
                theme(),
                appSettings,
                this::applySettings,
                this::speichereSession,
                this::ladeSession
        ));
        historyPanel.setClearHistoryListener(e -> speichereVerlauf());
        historyPanel.setFavoriteChangedListener(e -> speichereVerlauf());
        historyPanel.setEntryDoubleClickListener(this::useHistoryEntryResult);
        displayPanel.setPasteListener(this::pasteDisplayText);
    }

    private void setAktuellerModus(RechnerModus modus)
    {
        aktuellerModus = modus;
        appSettings.setStartModus(modus);
        persistenceService.speichereSettings(appSettings);

        modeBarPanel.setSelectedMode(modus, themeManager.getCurrentTheme());
        modeContentHostPanel.showMode(modus, theme());

        ModePanel modePanel = modePanels.get(modus) instanceof ModePanel panel ? panel : null;
        boolean zeigtDisplay = modePanel == null
                ? ModeVisibilityPolicy.sollGlobalesDisplayAnzeigen(modus)
                : modePanel.zeigtGlobalesDisplay();
        boolean zeigtHistory = modePanel == null
                ? ModeVisibilityPolicy.sollHistoryAnzeigen(modus)
                : modePanel.zeigtHistory();

        displayPanel.setVisible(zeigtDisplay);
        historyPanel.setVisible(zeigtHistory);
        if (!zeigtHistory && keyboardShortcutBinder != null)
        {
            keyboardShortcutBinder.defocusSearchIfNeeded();
        }

        updateStatus();
        revalidate();
        repaint();
    }

    private boolean sindStandardShortcutsAktiv()
    {
        ModePanel modePanel = modePanels.get(aktuellerModus) instanceof ModePanel panel ? panel : null;
        return modePanel == null
                ? ModeVisibilityPolicy.sindStandardShortcutsAktiv(aktuellerModus)
                : modePanel.nutztStandardShortcuts();
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
        persistenceService.speichereSettings(appSettings);
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
        aktualisiereGraphWinkelmodus();
    }

    private void aktualisiereGraphWinkelmodus()
    {
        JPanel graphPanel = modePanels.get(RechnerModus.GRAPH);
        if (graphPanel instanceof ModePanel panel)
        {
            panel.setWinkelModus(rechner.getWinkelModus());
        }
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
        einheitenSidePanelHost.applyTheme(theme());

        for (Map.Entry<RechnerModus, JPanel> entry : modePanels.entrySet())
        {
            ShellThemeApplier.applyThemeRecursively(entry.getValue(), theme(), rechner.getWinkelModus());
        }

        repaint();
        revalidate();
    }


    private void evaluate()
    {
        BerechnungsErgebnis ergebnis = rechner.berechneDetailliert();
        displayPanel.setMainText(ergebnis.getAnzeigeText());

        if (ergebnis.isErfolgreich())
        {
            displayPanel.setSecondaryText(ergebnis.getVerlaufText());
            displayPanel.pulseSuccess();
            addHistoryEntry(ergebnis.getVerlaufText());
            updateStatus();
            return;
        }

        displayPanel.setSecondaryText(ergebnis.getFehlerMeldung());
        displayPanel.pulseError();
        updateStatus();
    }

    private void addHistoryEntry(String entry)
    {
        if (entry == null || entry.isBlank()) return;

        VerlaufEintrag verlaufEintrag = persistenceService.erstelleVerlaufEintrag(entry, aktuellerModus);
        historyPanel.addStructuredEntry(verlaufEintrag);
        speichereVerlauf();
    }

    private void ladeVerlauf()
    {
        historyPanel.setAllStructuredEntries(persistenceService.ladeVerlauf(appSettings));
    }

    private void speichereVerlauf()
    {
        persistenceService.speichereVerlauf(appSettings, historyPanel.getAllStructuredEntries());
    }

    private void speichereFenstergroesse()
    {
        persistenceService.speichereFenstergroesse(appSettings, getWidth(), getHeight());
    }

    private void speichereSession()
    {
        persistenceService.speichereSession(new ShellSessionData(
                aktuellerModus,
                rechner.getAusdruckText(),
                rechner.getVerlauf(),
                historyPanel.getAllEntries(),
                rechner.getWinkelModus(),
                rechner.getSpeicherWert(),
                appSettings.getThemeType(),
                rechner.getZahlenFormatModus(),
                rechner.getNachkommastellen()
        ));
    }

    private void ladeSession()
    {
        ShellSessionData session = persistenceService.ladeSession();

        appSettings.setThemeType(session.themeType());
        appSettings.setStartModus(session.aktiverModus());
        appSettings.setWinkelModus(session.winkelModus());
        appSettings.setZahlenFormatModus(session.zahlenFormatModus());
        appSettings.setNachkommastellen(session.nachkommastellen());
        persistenceService.speichereSettings(appSettings);

        rechner.setWinkelModus(session.winkelModus());
        rechner.setZahlenFormatModus(session.zahlenFormatModus());
        rechner.setNachkommastellen(session.nachkommastellen());
        rechner.setAusdruckText(session.ausdruck());
        rechner.setVerlauf(session.verlauf());
        rechner.setSpeicherWert(session.speicherWert());

        historyPanel.setAllEntries(session.historyEintraege());
        themeManager.setTheme(session.themeType());
        setAktuellerModus(session.aktiverModus());
        applyCurrentTheme();
        refresh();
    }
}
