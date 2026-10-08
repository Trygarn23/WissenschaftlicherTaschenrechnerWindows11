package modes.graph.ui;

import common.formatting.ZahlenEingabe;
import common.state.RechnerModus;
import common.state.WinkelModus;
import modes.graph.formatting.FunktionslistenFormat;
import modes.graph.formatting.GraphFormatter;
import modes.graph.logic.FlaechenRechner;
import modes.graph.logic.GraphEvaluator;
import modes.graph.logic.GraphIntersectionService;
import modes.graph.logic.KurvendiskussionService;
import modes.graph.model.Flaeche;
import modes.graph.model.GraphPunkt;
import modes.graph.model.GraphState;
import modes.graph.model.KurvendiskussionResult;
import modes.graph.model.Tangente;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** Graph-Modus: verbindet Funktionsliste, Steuerleiste, Parameter-Regler, Analyse-Ausgabe und Zeichenfläche. */
public class GraphPanel extends JPanel implements ModePanel
{
    private final GraphState state = new GraphState();
    private final GraphEvaluator evaluator = new GraphEvaluator();
    private final KurvendiskussionService kurvendiskussionService = new KurvendiskussionService(evaluator);
    private final GraphIntersectionService intersectionService = new GraphIntersectionService(evaluator);
    private final FlaechenRechner flaechenRechner = new FlaechenRechner(evaluator);
    private final GraphCanvasPanel canvasPanel = new GraphCanvasPanel(state, evaluator);
    private final GraphFormatter formatter = new GraphFormatter();

    private final JLabel statusLabel = new JLabel("Bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final FunktionslistePanel funktionsliste;
    private final GraphSteuerleiste steuerleiste;
    private final ParameterReglerPanel parameterRegler = new ParameterReglerPanel(this::parameterGeaendert);
    private final AnalyseAusgabePanel analyseAusgabe;

    private AppTheme theme;
    private WinkelModus winkelModus = WinkelModus.DEG;
    private Tangente tangente;
    private Flaeche flaeche;

    public GraphPanel()
    {
        setLayout(new BorderLayout(12, 0));
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        funktionsliste = new FunktionslistePanel(state, this::addFunction, this::plot, this::selectFunction, this::removeFunction);
        steuerleiste = new GraphSteuerleiste(state, this::plot, this::ansichtGeaendert,
                this::oeffneFunktionsliste, this::speichereFunktionsliste, this::speichereAlsPng);
        analyseAusgabe = new AnalyseAusgabePanel(evaluator, this::berechneFlaeche, this::entferneFlaeche);

        evaluator.setFunktionen(state.getFunktionen());
        canvasPanel.setViewportChangedListener(this::updateAnalysis);
        canvasPanel.setFunctionSelectionListener(this::selectFunction);
        canvasPanel.setPointSelectionListener(this::useAnalysisPoint);
        canvasPanel.setTangentenListener(this::setzeTangente, this::entferneTangente);

        add(buildSidebar(), BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);
        plot();
    }

    public void setWinkelModus(WinkelModus winkelModus)
    {
        this.winkelModus = winkelModus;
        canvasPanel.setWinkelModus(winkelModus);
        plot();
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.GRAPH;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        canvasPanel.applyTheme(theme);
        funktionsliste.applyTheme(theme);
        steuerleiste.applyTheme(theme);
        analyseAusgabe.applyTheme(theme);
        applyThemeToChildren(this);
        statusAnzeige.setTheme(theme);
        funktionsliste.aktualisiereAuswahl();

        repaint();
    }

    private JPanel buildSidebar()
    {
        JPanel sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(300, 0));

        funktionsliste.add(statusLabel, BorderLayout.SOUTH);

        JPanel north = new JPanel(new BorderLayout(0, 10));
        north.setOpaque(false);
        north.add(funktionsliste, BorderLayout.NORTH);
        north.add(steuerleiste, BorderLayout.CENTER);
        north.add(parameterRegler, BorderLayout.SOUTH);

        sidebar.add(north, BorderLayout.NORTH);
        sidebar.add(analyseAusgabe, BorderLayout.CENTER);
        return sidebar;
    }

    private void addFunction()
    {
        funktionsliste.uebernehmeInState();
        state.fuegeFunktionHinzu("x");
        neuAufbauen();
        funktionsliste.fokussiereAktive();
    }

    private void removeFunction(int index)
    {
        funktionsliste.uebernehmeInState();
        if (!state.entferneFunktion(index))
        {
            statusAnzeige.zeigeFehler("Eine Funktion muss bleiben");
            return;
        }

        // Indizes verschieben sich, Tangente und Fläche würden sonst an der falschen Kurve hängen.
        tangente = null;
        flaeche = null;
        neuAufbauen();
    }

    private void neuAufbauen()
    {
        funktionsliste.baueNeu();
        if (theme != null)
        {
            applyTheme(theme);
        }
        plot();
    }

    private void selectFunction(int index)
    {
        funktionsliste.uebernehmeInState();
        state.setAktiveFunktion(index);
        funktionsliste.aktualisiereAuswahl();
        analyseAusgabe.setFunktionsName(state.getAktiveFunktion().getName());
        plot();
    }

    private void useAnalysisPoint(GraphPunkt punkt)
    {
        analyseAusgabe.setTabellenMitte(punkt.getX());
        statusAnzeige.zeigeErfolg("Punkt " + formatter.formatierePunkt(punkt) + " in die Wertetabelle übernommen");
    }

    private void ansichtGeaendert()
    {
        updateAnalysis();
        canvasPanel.repaint();
    }

    private void plot()
    {
        syncFunctionsFromUi();

        analyseAusgabe.setFunktionsName(state.getAktiveFunktion().getName());
        funktionsliste.aktualisiereAuswahl();
        String ausdruck = state.getAktiveFunktion().getAusdruck();
        if (ausdruck.isBlank())
        {
            statusAnzeige.zeigeFehler("Bitte Funktion eingeben");
            analyseAusgabe.zeigeText("Gib mir eine Funktion, ich mal dir was.");
            canvasPanel.setKurvendiskussionResult(null);
            canvasPanel.repaint();
            return;
        }

        if (!evaluator.istGueltig(ausdruck, winkelModus))
        {
            statusAnzeige.zeigeFehler("Ausdruck kann nicht gezeichnet werden");
            analyseAusgabe.zeigeText("Kurvendiskussion nicht möglich.");
            canvasPanel.setKurvendiskussionResult(null);
            canvasPanel.repaint();
            return;
        }

        statusAnzeige.zeigeErfolg("Zeichne " + state.getAktiveFunktion().getName() + "(x) = " + ausdruck);
        analyseAusgabe.aktualisiereTabelle(ausdruck, winkelModus);
        updateAnalysis();
        canvasPanel.pulseRefresh();
        canvasPanel.repaint();
    }

    private void syncFunctionsFromUi()
    {
        funktionsliste.uebernehmeInState();
        evaluator.setFunktionen(state.getFunktionen());
        if (parameterRegler.zeige(GraphEvaluator.findeParameter(state.getFunktionen())) && theme != null)
        {
            applyTheme(theme);
        }
        evaluator.setParameter(parameterRegler.werte());
    }

    /** Live-Update beim Ziehen eines Reglers: nur neu rechnen und zeichnen, keine Statusmeldung. */
    private void parameterGeaendert()
    {
        evaluator.setParameter(parameterRegler.werte());
        analyseAusgabe.aktualisiereTabelle(state.getAktiveFunktion().getAusdruck(), winkelModus);
        updateAnalysis();
        canvasPanel.repaint();
    }

    private void updateAnalysis()
    {
        KurvendiskussionResult result = kurvendiskussionService.analysiere(
                state.getAktiveFunktion().getAusdruck(),
                state.getXMin(),
                state.getXMax(),
                winkelModus
        );

        StringBuilder text = new StringBuilder(formatter.formatiereKurvendiskussion(result, intersections()));
        aktualisiereTangenteUndFlaeche();
        if (tangente != null)
        {
            text.append("\n").append(formatter.formatiereTangente(tangente));
        }
        if (flaeche != null)
        {
            text.append("\n").append(formatter.formatiereFlaeche(flaeche));
        }

        analyseAusgabe.zeigeText(text.toString());
        canvasPanel.setKurvendiskussionResult(result);
    }

    /** Tangente und Fläche hängen an Funktion und Stelle; bei geänderten Ausdrücken/Parametern neu rechnen. */
    private void aktualisiereTangenteUndFlaeche()
    {
        if (tangente != null)
        {
            tangente = evaluator.tangente(tangente.funktionIndex(),
                    state.getFunktion(tangente.funktionIndex()).getAusdruck(), tangente.x0(), winkelModus);
        }
        if (flaeche != null)
        {
            try
            {
                flaeche = flaechenRechner.berechne(flaeche.funktionIndex(),
                        state.getFunktion(flaeche.funktionIndex()).getAusdruck(), flaeche.a(), flaeche.b(), winkelModus);
            }
            catch (IllegalArgumentException e)
            {
                flaeche = null;
                statusAnzeige.zeigeFehler(e.getMessage());
            }
        }
        canvasPanel.setTangente(tangente);
        canvasPanel.setFlaeche(flaeche);
    }

    private void setzeTangente(int funktionIndex, double x)
    {
        Tangente neu = evaluator.tangente(funktionIndex, state.getFunktion(funktionIndex).getAusdruck(), x, winkelModus);
        if (neu == null)
        {
            statusAnzeige.zeigeFehler("An dieser Stelle gibt es keine Tangente");
            return;
        }
        tangente = neu;
        updateAnalysis();
        statusAnzeige.zeigeErfolg(formatter.formatiereTangente(neu));
    }

    private void entferneTangente()
    {
        tangente = null;
        updateAnalysis();
    }

    private void berechneFlaeche(String textA, String textB)
    {
        try
        {
            syncFunctionsFromUi();
            flaeche = flaechenRechner.berechne(state.getAktiveFunktionIndex(), state.getAktiveFunktion().getAusdruck(),
                    ZahlenEingabe.lese(textA), ZahlenEingabe.lese(textB), winkelModus);
            updateAnalysis();
            statusAnzeige.zeigeErfolg("Integral = " + formatter.formatiereZahl(flaeche.integral()));
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void entferneFlaeche()
    {
        flaeche = null;
        updateAnalysis();
    }

    private void oeffneFunktionsliste()
    {
        JFileChooser chooser = dateiDialog("Funktionslisten (*.txt)", "txt");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        try
        {
            String text = Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8);
            state.ersetzeFunktionen(FunktionslistenFormat.lese(text));
            tangente = null;
            flaeche = null;
            neuAufbauen();
            statusAnzeige.zeigeErfolg(state.getFunktionen().size() + " Funktionen geladen");
        }
        catch (IOException e)
        {
            statusAnzeige.zeigeFehler("Datei konnte nicht gelesen werden");
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void speichereFunktionsliste()
    {
        File datei = waehleSpeicherort("Funktionslisten (*.txt)", "txt");
        if (datei == null)
        {
            return;
        }

        try
        {
            funktionsliste.uebernehmeInState();
            Files.writeString(datei.toPath(), FunktionslistenFormat.schreibe(state.getFunktionen()), StandardCharsets.UTF_8);
            statusAnzeige.zeigeErfolg("Gespeichert: " + datei.getName());
        }
        catch (IOException e)
        {
            statusAnzeige.zeigeFehler("Datei konnte nicht gespeichert werden");
        }
    }

    private void speichereAlsPng()
    {
        File datei = waehleSpeicherort("PNG-Bilder (*.png)", "png");
        if (datei == null)
        {
            return;
        }

        try
        {
            canvasPanel.speichereAlsPng(datei);
            statusAnzeige.zeigeErfolg("Bild gespeichert: " + datei.getName());
        }
        catch (IOException | IllegalStateException e)
        {
            statusAnzeige.zeigeFehler("Bild konnte nicht gespeichert werden");
        }
    }

    /** Fragt nach dem Speicherort und hängt die Endung an, falls sie fehlt; {@code null} bei Abbruch. */
    private File waehleSpeicherort(String beschreibung, String endung)
    {
        JFileChooser chooser = dateiDialog(beschreibung, endung);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return null;
        }
        File datei = chooser.getSelectedFile();
        return datei.getName().toLowerCase(java.util.Locale.ROOT).endsWith("." + endung) ? datei : new File(datei.getPath() + "." + endung);
    }

    private static JFileChooser dateiDialog(String beschreibung, String endung)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(beschreibung, endung));
        return chooser;
    }

    private List<GraphPunkt> intersections()
    {
        if (state.getFunktionen().size() < 2 || !state.getAktiveFunktion().isSichtbar())
        {
            return List.of();
        }

        List<GraphPunkt> punkte = new ArrayList<>();
        for (int index = 0; index < state.getFunktionen().size(); index++)
        {
            if (index == state.getAktiveFunktionIndex() || !state.getFunktion(index).isSichtbar())
            {
                continue;
            }

            // Eine ungültige Nebenfunktion liefert einfach keine Schnittpunkte (NaN), statt alles zu blockieren.
            punkte.addAll(intersectionService.findeSchnittpunkte(
                    state.getAktiveFunktion().getAusdruck(),
                    state.getFunktion(index).getAusdruck(),
                    state.getXMin(),
                    state.getXMax(),
                    winkelModus
            ));
        }
        return punkte;
    }

    private void applyThemeToChildren(Component component)
    {
        if (theme == null)
        {
            return;
        }

        if (component instanceof JLabel label)
        {
            if (!Boolean.TRUE.equals(label.getClientProperty("graphSwatch")))
            {
                label.setForeground(theme.displayForeground());
            }
        }
        else if (component instanceof JButton button)
        {
            if (!Boolean.TRUE.equals(button.getClientProperty("graphSwatch")))
            {
                ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
                if (Boolean.TRUE.equals(button.getClientProperty("compactGraphControl")))
                {
                    styleCompactGraphButton(button);
                }
            }
        }
        else if (component instanceof JPanel panel && panel != this)
        {
            panel.setBackground(theme.panelBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeToChildren(child);
            }
        }
    }

    private void styleCompactGraphButton(JButton button)
    {
        button.setFont(theme.buttonFont().deriveFont(12f));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.cardBorder(), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
}
