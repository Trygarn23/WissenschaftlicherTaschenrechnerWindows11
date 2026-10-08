package ui.settings;

import common.formatting.ZahlenFormatModus;
import common.state.RechnerModus;
import common.state.WinkelModus;
import ui.animation.AnimationSupport;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ThemeType;
import ui.theme.custom.CustomThemeColors;
import ui.theme.custom.CustomThemePersistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class SettingsDialog extends JDialog
{
    private final AppTheme theme;
    private final SettingsRows rows;
    private AppSettings workingSettings;
    private AppSettings appliedSettings;
    private final Consumer<AppSettings> settingsListener;
    private final Runnable sessionSaveListener;
    private final Runnable sessionLoadListener;
    private final CustomThemePersistence customThemePersistence;
    private CustomThemeColors customThemeColors;
    private CustomThemeColors appliedCustomThemeColors;

    SettingsDialog(
            Frame owner,
            AppTheme theme,
            AppSettings settings,
            Consumer<AppSettings> settingsListener,
            Runnable sessionSaveListener,
            Runnable sessionLoadListener,
            CustomThemePersistence customThemePersistence)
    {
        super(owner, "Einstellungen", false);
        this.theme = theme;
        this.rows = new SettingsRows(theme);
        this.workingSettings = settings.copy();
        this.appliedSettings = settings.copy();
        this.settingsListener = settingsListener;
        this.sessionSaveListener = sessionSaveListener;
        this.sessionLoadListener = sessionLoadListener;
        this.customThemePersistence = customThemePersistence;
        this.customThemeColors = customThemePersistence.lade();
        this.appliedCustomThemeColors = customThemeColors;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        rebuildContent();
        setMinimumSize(new Dimension(600, 520));
        setSize(new Dimension(640, 720));
        setLocationRelativeTo(owner);
    }

    public static void showDialog(Frame owner, AppTheme theme, AppSettings settings, Consumer<AppSettings> settingsListener)
    {
        showDialog(owner, theme, settings, settingsListener, null, null);
    }

    public static void showDialog(
            Frame owner,
            AppTheme theme,
            AppSettings settings,
            Consumer<AppSettings> settingsListener,
            Runnable sessionSaveListener,
            Runnable sessionLoadListener)
    {
        new SettingsDialog(owner, theme, settings, settingsListener, sessionSaveListener, sessionLoadListener,
                new CustomThemePersistence()).setVisible(true);
    }

    private void rebuildContent()
    {
        setContentPane(createContent());
        revalidate();
        repaint();
    }

    private JPanel createContent()
    {
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setBackground(theme.windowBackground());

        JLabel title = new JLabel("Einstellungen");
        title.setFont(AppFonts.fett(22));
        title.setForeground(theme.displayForeground());

        JLabel hint = new JLabel("Optik ist Geschmackssache. Außer Neon. Neon ist eine Entscheidung.");
        hint.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));
        hint.setForeground(theme.secondaryDisplayForeground());

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(hint, BorderLayout.SOUTH);

        JPanel settingsList = new JPanel();
        settingsList.setLayout(new BoxLayout(settingsList, BoxLayout.Y_AXIS));
        settingsList.setOpaque(false);
        addSection(settingsList, "Aussehen", createAussehen());
        addSection(settingsList, "Rechnen", createRechnen());
        addSection(settingsList, "Verlauf und Session", createVerlauf());
        addSection(settingsList, "Schule", createSchule());
        settingsList.add(rows.value("Version", AppSettings.VERSION));

        // Oben ausrichten, sonst zieht das Scrollfenster die Zeilen auf volle Höhe.
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(settingsList, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(top);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        content.add(header, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        content.add(createFooter(), BorderLayout.SOUTH);
        return content;
    }

    private void addSection(JPanel list, String title, JComponent... rowsInSection)
    {
        JLabel label = rows.sectionTitle(title);
        label.setAlignmentX(LEFT_ALIGNMENT);
        list.add(label);
        list.add(Box.createVerticalStrut(6));
        for (JComponent row : rowsInSection)
        {
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
            list.add(row);
            list.add(Box.createVerticalStrut(8));
        }
    }

    private JComponent[] createAussehen()
    {
        return new JComponent[]{
                rows.combo("Theme", "Farbschema der ganzen App.", ThemeType.values(), workingSettings.getThemeType(),
                        workingSettings::setThemeType),
                rows.check("Hell/Dunkel vom System", "Beim Start Light oder Dark passend zu Windows wählen.",
                        workingSettings.isThemeVomSystem(), workingSettings::setThemeVomSystem),
                CustomThemeSection.create(this, theme, () -> customThemeColors, this::changeCustomColors),
                rows.check("Weniger Bewegung", "Keine Übergänge und kein Aufblinken – angenehmer bei Reizempfindlichkeit oder langsamen Rechnern.",
                        workingSettings.isWenigerBewegung(), workingSettings::setWenigerBewegung)
        };
    }

    private JComponent[] createRechnen()
    {
        return new JComponent[]{
                rows.combo("Startmodus", "Mit diesem Modus startet die App.", RechnerModus.values(), workingSettings.getStartModus(),
                        workingSettings::setStartModus),
                rows.combo("Winkelmodus", "DEG = Grad (sin 90 = 1), RAD = Bogenmaß (sin π/2 = 1).", WinkelModus.values(),
                        workingSettings.getWinkelModus(), workingSettings::setWinkelModus),
                rows.spinner("Präzision", "Wie viele Nachkommastellen höchstens angezeigt werden.",
                        workingSettings.getNachkommastellen(), 2, 15, workingSettings::setNachkommastellen),
                rows.combo("Zahlenformat", "AUTO wählt selbst, ob normal oder wissenschaftlich (1,2e-5) angezeigt wird.",
                        ZahlenFormatModus.values(), workingSettings.getZahlenFormatModus(), workingSettings::setZahlenFormatModus)
        };
    }

    private JComponent[] createVerlauf()
    {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(rows.button("Session speichern", () -> runIfSet(sessionSaveListener)));
        actions.add(rows.button("Session laden", () -> runIfSet(sessionLoadListener)));

        return new JComponent[]{
                rows.check("Verlauf speichern", "Rechnungen bleiben nach dem Neustart erhalten.",
                        workingSettings.isHistoryEnabled(), workingSettings::setHistoryEnabled),
                rows.row("Session", "Aktuellen Stand (Modus, Ausdruck, Speicher) sichern oder zurückholen.", actions)
        };
    }

    private JComponent[] createSchule()
    {
        return new JComponent[]{
                rows.check("Prüfungsmodus", "Nur Standard und Wissenschaftlich, kein Verlauf. Oben steht dann gut sichtbar „PRÜFUNGSMODUS“.",
                        workingSettings.isPruefungsModus(), workingSettings::setPruefungsModus)
        };
    }

    private void changeCustomColors(UnaryOperator<CustomThemeColors> change)
    {
        customThemeColors = change.apply(customThemeColors);
        workingSettings.setThemeType(ThemeType.CUSTOM);
    }

    private JPanel createFooter()
    {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(rows.button("Alles auf Standard", this::resetToDefaults));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(rows.button("Zurücksetzen", this::resetToAppliedValues));
        right.add(rows.button("Abbrechen", this::dispose));
        JButton applyButton = rows.button("Anwenden", this::publish);
        applyButton.addActionListener(e -> AnimationSupport.pulseBackground(applyButton, theme.successPulseColor(), 180));
        right.add(applyButton);
        right.add(rows.button("Speichern", () -> {
            publish();
            dispose();
        }));

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private static void runIfSet(Runnable listener)
    {
        if (listener != null)
        {
            listener.run();
        }
    }

    private void publish()
    {
        customThemePersistence.speichere(customThemeColors);
        appliedSettings = workingSettings.copy();
        appliedCustomThemeColors = customThemeColors;
        settingsListener.accept(appliedSettings.copy());
    }

    /** Verwirft nur die noch nicht angewendeten Änderungen im Dialog. */
    private void resetToAppliedValues()
    {
        workingSettings = appliedSettings.copy();
        customThemeColors = appliedCustomThemeColors;
        rebuildContent();
    }

    /** Sicherer Weg zurück, wenn man sich verstellt hat: alles wie beim ersten Start. Greift erst mit Anwenden/Speichern. */
    private void resetToDefaults()
    {
        workingSettings = workingSettings.standardMitFenster();
        rebuildContent();
    }
}
