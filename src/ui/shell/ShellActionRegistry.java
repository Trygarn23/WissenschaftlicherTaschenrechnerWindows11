package ui.shell;

import common.logic.RechnerService;
import modes.wissenschaftlich.logic.ExakteForm;
import modes.wissenschaftlich.logic.VariablenSpeicher;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import ui.shortcuts.Tastenkuerzel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ShellActionRegistry
{
    private final RechnerService rechner;
    private final WissenschaftlichOperationen wissenschaftlichOperationen;
    private final Runnable refresh;
    private final Consumer<String> refreshWithExtraInfo;
    private final Runnable evaluate;
    private final VariablenSpeicher variablen;
    private final Map<String, Runnable> actions = new HashMap<>();
    /** Ausdruck, zu dem S⇔D gerade die exakte Form zeigt; der nächste Druck schaltet zurück auf dezimal. */
    private String exaktAngezeigtFuer;

    /** Variablen A–F nur im Speicher, z. B. für Tests. */
    public ShellActionRegistry(
            RechnerService rechner,
            WissenschaftlichOperationen wissenschaftlichOperationen,
            Runnable refresh,
            Consumer<String> refreshWithExtraInfo,
            Runnable evaluate)
    {
        this(rechner, wissenschaftlichOperationen, refresh, refreshWithExtraInfo, evaluate, new VariablenSpeicher(null));
    }

    public ShellActionRegistry(
            RechnerService rechner,
            WissenschaftlichOperationen wissenschaftlichOperationen,
            Runnable refresh,
            Consumer<String> refreshWithExtraInfo,
            Runnable evaluate,
            VariablenSpeicher variablen)
    {
        this.rechner = rechner;
        this.wissenschaftlichOperationen = wissenschaftlichOperationen;
        this.variablen = variablen;
        this.refresh = refresh;
        this.refreshWithExtraInfo = refreshWithExtraInfo;
        this.evaluate = evaluate;
        initActions();
    }

    private void initActions()
    {
        initCommonActions();
        initScientificActions();
        initMemoryActions();
    }

    /** Registriert eine Aktion, nach der die Anzeige automatisch aktualisiert wird. */
    private void mitRefresh(String taste, Runnable aktion)
    {
        actions.put(taste, () -> {
            aktion.run();
            refresh.run();
        });
    }

    private void initCommonActions()
    {
        for (int ziffer = 0; ziffer <= 9; ziffer++)
        {
            String text = String.valueOf(ziffer);
            mitRefresh(text, () -> rechner.eingabeZahl(text));
        }

        mitRefresh(",", rechner::eingabeKomma);

        for (String operator : new String[]{"+", "-", "×", "÷"})
        {
            mitRefresh(operator, () -> rechner.operatorSetzen(operator));
        }
        mitRefresh("mod", () -> rechner.operatorSetzen("%"));

        actions.put("=", evaluate);

        mitRefresh("±", rechner::wechselVorzeichen);
        mitRefresh("C", rechner::allesLoeschen);
        mitRefresh("CE", rechner::ce);
        mitRefresh("←", rechner::loeschen);
        mitRefresh(Tastenkuerzel.RUECKGAENGIG_AKTION, rechner::rueckgaengig);
        mitRefresh("%", rechner::prozent);
        mitRefresh("x²", rechner::quadriere);
        mitRefresh("√x", rechner::wurzel);
        mitRefresh("1/x", rechner::reziprok);
        mitRefresh("(", rechner::klammerAuf);
        mitRefresh(")", rechner::klammerZu);
        mitRefresh("xʸ", rechner::potenz);
        mitRefresh("Ans", rechner::ans);
    }

    private void initScientificActions()
    {
        WissenschaftlichOperationen w = wissenschaftlichOperationen;
        mitRefresh("n!", w::fakultaet);
        mitRefresh("10ˣ", w::zehnHoch);
        mitRefresh("ln", w::ln);
        mitRefresh("log", w::log);
        mitRefresh("sin", w::sin);
        mitRefresh("cos", w::cos);
        mitRefresh("tan", w::tan);
        mitRefresh("asin", w::arcsin);
        mitRefresh("acos", w::arccos);
        mitRefresh("atan", w::arctan);
        mitRefresh("sinh", w::sinusHyperbolicus);
        mitRefresh("cosh", w::cosinusHyperbolicus);
        mitRefresh("tanh", w::tangensHyperbolicus);
        mitRefresh("π", w::pi);
        mitRefresh("e", w::e);
        mitRefresh("exp", w::exp);
        mitRefresh("|x|", w::betrag);
        mitRefresh("floor", w::abrunden);
        mitRefresh("ceil", w::aufrunden);
        mitRefresh("round", w::runden);
        mitRefresh("rand", w::zufall);
        for (String funktion : new String[]{"nCr", "nPr", "ggT", "kgV"})
        {
            mitRefresh(funktion, () -> w.zweiArgumente(funktion));
        }
        mitRefresh(";", w::trennzeichen);

        actions.put("S⇔D", this::exaktUmschalten);
        actions.put("Mehr Stellen", () -> refreshWithExtraInfo.accept("= " + rechner.formatiereVolleGenauigkeit(rechner.aktuellerWertOder0())));
    }

    private void exaktUmschalten()
    {
        String ausdruck = rechner.getAusdruckText();
        if (ausdruck.equals(exaktAngezeigtFuer))
        {
            exaktAngezeigtFuer = null;
            refresh.run();
            return;
        }
        exaktAngezeigtFuer = ausdruck;
        refreshWithExtraInfo.accept(ExakteForm.finde(rechner.aktuellerWertOder0())
                .map(form -> "= " + form)
                .orElse("Keine exakte Form gefunden"));
    }

    private void initMemoryActions()
    {
        actions.put("MC", () -> {
            rechner.speicherLoeschen();
            refreshWithExtraInfo.accept("M = 0");
        });
        mitRefresh("MR", rechner::speicherAbrufen);
        actions.put("M+", () -> refreshWithExtraInfo.accept("M = " + rechner.speicherAddieren()));
        actions.put("M-", () -> refreshWithExtraInfo.accept("M = " + rechner.speicherSubtrahieren()));

        for (String name : VariablenSpeicher.NAMEN)
        {
            actions.put("STO " + name, () -> {
                double wert = rechner.aktuellerWertOder0();
                variablen.speichere(name, wert);
                refreshWithExtraInfo.accept(name + " = " + rechner.formatiereZahl(wert));
            });
            actions.put("RCL " + name, () -> variablen.wert(name).ifPresentOrElse(
                    wert -> {
                        rechner.zahlEinsetzen(wert);
                        refresh.run();
                    },
                    () -> refreshWithExtraInfo.accept(name + " ist leer")));
        }
    }

    /**
     * Führt die Aktion mit diesem Namen aus. Der Name entspricht dem Button-Text,
     * damit Buttons, Tastenkürzel und Tooltips dieselben Namen verwenden.
     *
     * @return {@code false}, wenn es keine Aktion mit diesem Namen gibt
     */
    public boolean ausfuehren(String aktion)
    {
        Runnable action = aktion == null ? null : actions.get(aktion);
        if (action == null)
        {
            return false;
        }
        action.run();
        return true;
    }

    public boolean kennt(String aktion)
    {
        return actions.containsKey(aktion);
    }

    public void handleButton(JButton sourceBtn)
    {
        String text = sourceBtn.getText();
        if (text != null && !ausfuehren(text))
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void handleScientificMenuAction(String functionName)
    {
        ausfuehren(functionName);
    }

    public void attachCalculatorButtonActions(Component component)
    {
        if (component instanceof JButton button)
        {
            button.addActionListener(e -> handleButton(button));
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                attachCalculatorButtonActions(child);
            }
        }
    }
}
