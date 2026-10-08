package ui.shell;

import common.history.VerlaufEintrag;
import common.konstanten.EigeneKonstanten;
import common.konstanten.KonstantenFavoriten;
import common.logic.BerechnungsErgebnis;
import common.logic.RechnerService;
import common.persistence.AppDateien;
import common.state.RechnerModus;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import ui.animation.AnimationSupport;
import ui.befehle.BefehlsDialog;
import ui.mini.MiniRechnerFenster;
import ui.history.HistoryPanel;
import ui.konstanten.KonstantenDialog;
import ui.settings.AppSettings;
import ui.settings.SettingsDialog;
import ui.shortcuts.TastenkuerzelDialog;
import ui.theme.AppTheme;
import ui.theme.SystemTheme;
import ui.theme.ThemeManager;
import ui.theme.ThemeType;
import ui.units.EinheitenSidePanelHost;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class TaschenrechnerUI extends JFrame
{
    private final ThemeManager themeManager = new ThemeManager();
    private final ShellPersistenceService persistenceService = new ShellPersistenceService();
    private AppSettings appSettings = persistenceService.ladeSettings();
    private final RechnerService rechner = new RechnerService();
    private final WissenschaftlichOperationen wissenschaftlichOperationen = new WissenschaftlichOperationen(rechner.getAusdruckEditor());
    private final LetzteEingabeSicherung letzteEingabe = new LetzteEingabeSicherung(AppDateien.wiederherstellung());
    private final KonstantenFavoriten konstantenFavoriten = new KonstantenFavoriten(AppDateien.konstantenFavoriten());
    private final EigeneKonstanten eigeneKonstanten = new EigeneKonstanten(AppDateien.eigeneKonstanten());

    private RechnerModus aktuellerModus = appSettings.getStartModus();
    /** Wohin Ergebnisse aus Hilfsmodi (z. B. Brüche) übernommen werden: der zuletzt benutzte Ausdrucksrechner. */
    private RechnerModus letzterAusdrucksModus = RechnerModus.STANDARD;

    private final GlobalActionBarPanel globalActionBarPanel = new GlobalActionBarPanel();
    private final ModeBarPanel modeBarPanel = new ModeBarPanel();
    private final DisplayPanel displayPanel = new DisplayPanel();
    private final ModeContentHostPanel modeContentHostPanel = new ModeContentHostPanel();
    private final HistoryPanel historyPanel = new HistoryPanel();
    private final EinheitenSidePanelHost einheitenSidePanelHost = new EinheitenSidePanelHost();
    private Map<RechnerModus, JPanel> modePanels;

    private ShellActionRegistry shellActionRegistry;
    private KeyboardShortcutBinder keyboardShortcutBinder;

    public TaschenrechnerUI()
    {
        applySettingsToServices();
        themeManager.setTheme(effektiverThemeTyp());
        configureFrame();
        buildLayout();

        shellActionRegistry = new ShellActionRegistry(rechner, wissenschaftlichOperationen, this::refresh, this::refreshWithExtraInfo, this::evaluate);

        initModeContent();
        wireShellEvents();

        keyboardShortcutBinder = new KeyboardShortcutBinder(
                getRootPane(),
                historyPanel,
                shellActionRegistry,
                this::sindStandardShortcutsAktiv
        );
        keyboardShortcutBinder.setupKeyboard();
        keyboardShortcutBinder.setupSearchFieldKeyForwarding();
        keyboardShortcutBinder.setupGlobaleTasten(this::setAktuellerModus, this::toggleEinheiten, this::zeigeTastenkuerzel);
        keyboardShortcutBinder.bindeWerkzeugTaste(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK),
                "befehlssuche", this::zeigeBefehlssuche);
        keyboardShortcutBinder.bindeWerkzeugTaste(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
                "konstanten", this::zeigeKonstanten);

        ladeVerlauf();
        applyPruefungsModus();
        refresh();
        stelleLetzteEingabeWiederHer();
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
                letzteEingabe.loesche();
            }
        });
    }

    private AppTheme theme()
    {
        return themeManager.getCurrentTheme();
    }

    /** Mit „Hell/Dunkel vom System“ entscheidet Windows über Light oder Dark, sonst das gewählte Theme. */
    private ThemeType effektiverThemeTyp()
    {
        return appSettings.isThemeVomSystem()
                ? SystemTheme.passendesTheme(appSettings.getThemeType())
                : appSettings.getThemeType();
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
        modePanels = ModusPanels.erstelle(shellActionRegistry::handleScientificMenuAction, this::uebernimmInAusdrucksRechner);
        for (Map.Entry<RechnerModus, JPanel> entry : modePanels.entrySet())
        {
            if (ModeVisibilityPolicy.sindStandardShortcutsAktiv(entry.getKey()))
            {
                shellActionRegistry.attachCalculatorButtonActions(entry.getValue());
            }
            modeContentHostPanel.registerMode(entry.getKey(), entry.getValue());
        }

        setAktuellerModus(PruefungsModus.erlaubterModus(appSettings.isPruefungsModus(), aktuellerModus));
    }

    private void wireShellEvents()
    {
        modeBarPanel.setModeListener(this::setAktuellerModus);
        modeBarPanel.setUnitsListener(this::toggleEinheiten);

        globalActionBarPanel.setAngleModeListener(e -> {
            rechner.winkelModusUmschalten();
            appSettings.setWinkelModus(rechner.getWinkelModus());
            persistenceService.speichereSettings(appSettings);
            aktualisiereWinkelmodusInModi();
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
        globalActionBarPanel.setShortcutsListener(e -> zeigeTastenkuerzel());
        globalActionBarPanel.setBefehleListener(e -> zeigeBefehlssuche());
        globalActionBarPanel.setKonstantenListener(e -> zeigeKonstanten());
        globalActionBarPanel.setMiniListener(e -> zeigeMiniRechner());
        historyPanel.setEntriesChangedListener(e -> speichereVerlauf());
        historyPanel.setFavoriteChangedListener(e -> speichereVerlauf());
        historyPanel.setEntryDoubleClickListener(this::useHistoryEntryResult);
        displayPanel.setPasteListener(this::pasteDisplayText);
    }

    private void setAktuellerModus(RechnerModus modus)
    {
        if (!PruefungsModus.istModusErlaubt(appSettings.isPruefungsModus(), modus))
        {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        aktuellerModus = modus;
        if (ModeVisibilityPolicy.sindStandardShortcutsAktiv(modus))
        {
            letzterAusdrucksModus = modus;
        }
        appSettings.setStartModus(modus);
        persistenceService.speichereSettings(appSettings);

        modeBarPanel.setSelectedMode(modus, themeManager.getCurrentTheme());
        modeContentHostPanel.showMode(modus, theme());

        ModePanel modePanel = (ModePanel) modePanels.get(modus);
        boolean zeigtHistory = modePanel.zeigtHistory() && PruefungsModus.sindWerkzeugeErlaubt(appSettings.isPruefungsModus());

        displayPanel.setVisible(modePanel.zeigtGlobalesDisplay());
        historyPanel.setVisible(zeigtHistory);
        if (!zeigtHistory && keyboardShortcutBinder != null)
        {
            keyboardShortcutBinder.defocusSearchIfNeeded();
        }

        updateStatus();
        revalidate();
        repaint();
    }

    private void toggleEinheiten()
    {
        if (werkzeugGesperrt())
        {
            return;
        }
        einheitenSidePanelHost.toggle();
    }

    private void zeigeTastenkuerzel()
    {
        TastenkuerzelDialog.showDialog(this, theme());
    }

    private void zeigeKonstanten()
    {
        if (werkzeugGesperrt())
        {
            return;
        }
        KonstantenDialog.showDialog(this, theme(), konstantenFavoriten, eigeneKonstanten, this::fuegeInAusdruckEin);
    }

    private void zeigeBefehlssuche()
    {
        ShellBefehle.Aktionen aktionen = new ShellBefehle.Aktionen(
                this::setAktuellerModus,
                this::setTheme,
                this::fuegeInAusdruckEin,
                () -> globalActionBarPanel.clickSettings(),
                this::zeigeTastenkuerzel,
                this::toggleEinheiten,
                this::zeigeKonstanten,
                this::zeigeMiniRechner,
                () -> historyPanel.setEingeklappt(!historyPanel.isEingeklappt()),
                globalActionBarPanel::clickAngleMode
        );
        BefehlsDialog.showDialog(this, theme(), ShellBefehle.erstelle(aktionen, eigeneKonstanten.alle(), appSettings.isPruefungsModus()));
    }

    private void zeigeMiniRechner()
    {
        if (werkzeugGesperrt())
        {
            return;
        }
        MiniRechnerFenster.zeige(this, theme(), rechner.getWinkelModus());
    }

    /** Im Prüfungsmodus gibt es keine Hilfswerkzeuge – kurzer Piep statt stillem Nichts. */
    private boolean werkzeugGesperrt()
    {
        if (PruefungsModus.sindWerkzeugeErlaubt(appSettings.isPruefungsModus()))
        {
            return false;
        }
        Toolkit.getDefaultToolkit().beep();
        return true;
    }

    /** Hängt z. B. eine Konstante an den Ausdruck; steht davor schon eine Zahl, kommt ein Malzeichen dazwischen. */
    private void fuegeInAusdruckEin(String text)
    {
        if (!ModeVisibilityPolicy.sindStandardShortcutsAktiv(aktuellerModus))
        {
            setAktuellerModus(letzterAusdrucksModus);
        }
        String ausdruck = rechner.getAusdruckText();
        boolean malNoetig = !ausdruck.isEmpty() && (Character.isLetterOrDigit(ausdruck.charAt(ausdruck.length() - 1))
                || ausdruck.endsWith(")") || ausdruck.endsWith("π"));
        rechner.setAusdruckText(ausdruck + (malNoetig ? "×" : "") + text);
        refresh();
    }

    private boolean sindStandardShortcutsAktiv()
    {
        return ((ModePanel) modePanels.get(aktuellerModus)).nutztStandardShortcuts();
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

    private void uebernimmInAusdrucksRechner(String wert)
    {
        rechner.setzeAusdruckAusVerlaufErgebnis(wert);
        setAktuellerModus(letzterAusdrucksModus);
        refresh();
    }

    private void stelleLetzteEingabeWiederHer()
    {
        letzteEingabe.ladeUebrigeEingabe().ifPresent(ausdruck -> {
            rechner.setAusdruckText(ausdruck);
            refreshWithExtraInfo("Letzte Eingabe wiederhergestellt");
        });
    }

    private void refresh()
    {
        displayPanel.setMainText(rechner.formatiereLiveAnzeige());
        displayPanel.setSecondaryText(rechner.zweiteZeile());
        aktualisiereKopfUndSicherung();
    }

    private void refreshWithExtraInfo(String info)
    {
        displayPanel.setMainText(rechner.formatiereLiveAnzeige());
        String verlauf = rechner.getVerlauf();
        displayPanel.setSecondaryText(info + (verlauf.isEmpty() ? "" : " | " + verlauf));
        aktualisiereKopfUndSicherung();
    }

    private void aktualisiereKopfUndSicherung()
    {
        globalActionBarPanel.setAngleModeText(rechner.getWinkelModus().name());
        letzteEingabe.merke(rechner.getAusdruckText());
        updateStatus();
    }

    private void updateStatus()
    {
        String speicherText = rechner.hatSpeicherWert() ? "M belegt" : "Speicher leer";
        displayPanel.setStatusText(
                (appSettings.isPruefungsModus() ? PruefungsModus.HINWEIS + " | " : "")
                        + "Modus: " + aktuellerModus.getLabel()
                        + " | Winkel: " + rechner.getWinkelModus().name()
                        + " | " + speicherText
        );
    }

    private void pasteDisplayText(String text)
    {
        if (!ModeVisibilityPolicy.sindStandardShortcutsAktiv(aktuellerModus))
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
        // Wer ausdrücklich ein Theme wählt, will nicht, dass Windows es beim nächsten Start überschreibt.
        updatedSettings.setThemeVomSystem(false);
        applySettings(updatedSettings);
    }

    private void applySettings(AppSettings settings)
    {
        boolean historySettingChanged = appSettings.isHistoryEnabled() != settings.isHistoryEnabled();
        appSettings = settings.copy();
        persistenceService.speichereSettings(appSettings);
        applySettingsToServices();
        themeManager.setTheme(effektiverThemeTyp());
        if (historySettingChanged)
        {
            ladeVerlauf();
        }
        applyPruefungsModus();
        applyCurrentTheme();
        refresh();
    }

    private void applySettingsToServices()
    {
        rechner.setWinkelModus(appSettings.getWinkelModus());
        rechner.setNachkommastellen(appSettings.getNachkommastellen());
        rechner.setZahlenFormatModus(appSettings.getZahlenFormatModus());
        AnimationSupport.setWenigerBewegung(appSettings.isWenigerBewegung());
        aktualisiereWinkelmodusInModi();
    }

    private void applyPruefungsModus()
    {
        boolean aktiv = appSettings.isPruefungsModus();
        setTitle(aktiv ? "Taschenrechner – " + PruefungsModus.HINWEIS : "Taschenrechner");
        modeBarPanel.setPruefungsModus(aktiv);
        globalActionBarPanel.setPruefungsModus(aktiv);
        if (aktiv && einheitenSidePanelHost.isGeoeffnet())
        {
            einheitenSidePanelHost.setGeoeffnet(false);
        }
        setAktuellerModus(PruefungsModus.erlaubterModus(aktiv, aktuellerModus));
    }

    private void aktualisiereWinkelmodusInModi()
    {
        if (modePanels == null)
        {
            return;
        }
        for (JPanel panel : modePanels.values())
        {
            ((ModePanel) panel).setWinkelModus(rechner.getWinkelModus());
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

        for (JPanel panel : modePanels.values())
        {
            ShellThemeApplier.applyThemeRecursively(panel, theme(), rechner.getWinkelModus());
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
        }
        else
        {
            displayPanel.setSecondaryText(ergebnis.getFehlerMeldung());
            displayPanel.pulseError();
        }
        letzteEingabe.merke(rechner.getAusdruckText());
        updateStatus();
    }

    private void addHistoryEntry(String entry)
    {
        // Im Prüfungsmodus wird nichts mitgeschrieben – auch nicht unsichtbar im Hintergrund.
        if (entry == null || entry.isBlank() || appSettings.isPruefungsModus()) return;

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
        setAktuellerModus(PruefungsModus.erlaubterModus(appSettings.isPruefungsModus(), session.aktiverModus()));
        applyCurrentTheme();
        refresh();
    }
}
