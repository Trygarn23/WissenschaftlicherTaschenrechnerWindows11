package ui.shell;

import common.state.RechnerModus;
import modes.bruch.ui.BruchPanel;
import modes.datum.ui.DatumPanel;
import modes.finanz.ui.FinanzPanel;
import modes.gleichung.ui.GleichungPanel;
import modes.graph.ui.GraphPanel;
import modes.komplex.ui.KomplexPanel;
import modes.logik.ui.LogikPanel;
import modes.matrix.ui.MatrixPanel;
import modes.netzwerk.ui.NetzwerkPanel;
import modes.programmierer.ui.ProgrammiererPanel;
import modes.standard.ui.StandardPanel;
import modes.statistik.ui.StatistikPanel;
import modes.vektor.ui.VektorPanel;
import modes.wissenschaftlich.ui.WissenschaftlichPanel;

import javax.swing.JPanel;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Baut ein Panel pro Modus. Steht hier statt in {@code TaschenrechnerUI}, damit die Shell nicht jeden Modus
 * einzeln importieren muss – ein neuer Modus = eine Zeile hier plus ein Eintrag in {@link RechnerModus}.
 */
final class ModusPanels
{
    private ModusPanels()
    {
    }

    /**
     * @param funktionsMenue  was im wissenschaftlichen f(x)-Menü gewählt wurde
     * @param uebernehmen     Wert aus einem Hilfsmodus (z. B. Brüche) in den Ausdrucksrechner übernehmen
     */
    static Map<RechnerModus, JPanel> erstelle(Consumer<String> funktionsMenue, Consumer<String> uebernehmen)
    {
        Map<RechnerModus, JPanel> panels = new EnumMap<>(RechnerModus.class);
        panels.put(RechnerModus.STANDARD, new StandardPanel());

        WissenschaftlichPanel wissenschaftlich = new WissenschaftlichPanel();
        wissenschaftlich.setFunctionSelectionListener(funktionsMenue::accept);
        panels.put(RechnerModus.WISSENSCHAFTLICH, wissenschaftlich);

        panels.put(RechnerModus.PROGRAMMIERER, new ProgrammiererPanel());
        panels.put(RechnerModus.GRAPH, new GraphPanel());
        panels.put(RechnerModus.KOMPLEX, new KomplexPanel());
        panels.put(RechnerModus.MATRIX, new MatrixPanel());
        panels.put(RechnerModus.STATISTIK, new StatistikPanel());
        panels.put(RechnerModus.GLEICHUNG, new GleichungPanel());

        BruchPanel bruch = new BruchPanel();
        bruch.setErgebnisUebernehmenListener(uebernehmen);
        panels.put(RechnerModus.BRUCH, bruch);

        panels.put(RechnerModus.VEKTOR, new VektorPanel());
        panels.put(RechnerModus.FINANZ, new FinanzPanel());
        panels.put(RechnerModus.NETZWERK, new NetzwerkPanel());
        panels.put(RechnerModus.LOGIK, new LogikPanel());
        panels.put(RechnerModus.DATUM, new DatumPanel());
        return panels;
    }
}
