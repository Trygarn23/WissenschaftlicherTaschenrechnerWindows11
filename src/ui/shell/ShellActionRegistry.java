package ui.shell;

import common.logic.RechnerService;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;

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
    private final Map<String, Runnable> actions = new HashMap<>();

    public ShellActionRegistry(
            RechnerService rechner,
            WissenschaftlichOperationen wissenschaftlichOperationen,
            Runnable refresh,
            Consumer<String> refreshWithExtraInfo,
            Runnable evaluate)
    {
        this.rechner = rechner;
        this.wissenschaftlichOperationen = wissenschaftlichOperationen;
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

    private void initCommonActions()
    {
        actions.put(",", () -> {
            rechner.eingabeKomma();
            refresh.run();
        });

        actions.put("+", () -> {
            rechner.operatorSetzen("+");
            refresh.run();
        });
        actions.put("-", () -> {
            rechner.operatorSetzen("-");
            refresh.run();
        });
        actions.put("×", () -> {
            rechner.operatorSetzen("×");
            refresh.run();
        });
        actions.put("÷", () -> {
            rechner.operatorSetzen("÷");
            refresh.run();
        });

        actions.put("=", evaluate);

        actions.put("±", () -> {
            rechner.wechselVorzeichen();
            refresh.run();
        });

        actions.put("C", () -> {
            rechner.allesLoeschen();
            refresh.run();
        });
        actions.put("CE", () -> {
            rechner.ce();
            refresh.run();
        });
        actions.put("←", () -> {
            rechner.loeschen();
            refresh.run();
        });

        actions.put("%", () -> {
            rechner.prozent();
            refresh.run();
        });
        actions.put("mod", () -> {
            rechner.operatorSetzen("%");
            refresh.run();
        });

        actions.put("x²", () -> {
            rechner.quadriere();
            refresh.run();
        });
        actions.put("√x", () -> {
            rechner.wurzel();
            refresh.run();
        });
        actions.put("1/x", () -> {
            rechner.reziprok();
            refresh.run();
        });

        actions.put("(", () -> {
            rechner.klammerAuf();
            refresh.run();
        });
        actions.put(")", () -> {
            rechner.klammerZu();
            refresh.run();
        });

        actions.put("xʸ", () -> {
            rechner.potenz();
            refresh.run();
        });

        actions.put("Ans", () -> {
            rechner.ans();
            refresh.run();
        });
    }

    private void initScientificActions()
    {
        actions.put("n!", () -> {
            wissenschaftlichOperationen.fakultaet();
            refresh.run();
        });

        actions.put("10ˣ", () -> {
            wissenschaftlichOperationen.zehnHoch();
            refresh.run();
        });

        actions.put("ln", () -> {
            wissenschaftlichOperationen.ln();
            refresh.run();
        });
        actions.put("log", () -> {
            wissenschaftlichOperationen.log();
            refresh.run();
        });

        actions.put("sin", () -> {
            wissenschaftlichOperationen.sin();
            refresh.run();
        });
        actions.put("cos", () -> {
            wissenschaftlichOperationen.cos();
            refresh.run();
        });
        actions.put("tan", () -> {
            wissenschaftlichOperationen.tan();
            refresh.run();
        });

        actions.put("asin", () -> {
            wissenschaftlichOperationen.arcsin();
            refresh.run();
        });
        actions.put("acos", () -> {
            wissenschaftlichOperationen.arccos();
            refresh.run();
        });
        actions.put("atan", () -> {
            wissenschaftlichOperationen.arctan();
            refresh.run();
        });

        actions.put("sinh", () -> {
            wissenschaftlichOperationen.sinusHyperbolicus();
            refresh.run();
        });
        actions.put("cosh", () -> {
            wissenschaftlichOperationen.cosinusHyperbolicus();
            refresh.run();
        });
        actions.put("tanh", () -> {
            wissenschaftlichOperationen.tangensHyperbolicus();
            refresh.run();
        });

        actions.put("π", () -> {
            wissenschaftlichOperationen.pi();
            refresh.run();
        });
        actions.put("e", () -> {
            wissenschaftlichOperationen.e();
            refresh.run();
        });

        actions.put("exp", () -> {
            wissenschaftlichOperationen.exp();
            refresh.run();
        });
        actions.put("|x|", () -> {
            wissenschaftlichOperationen.betrag();
            refresh.run();
        });

        actions.put("floor", () -> {
            wissenschaftlichOperationen.abrunden();
            refresh.run();
        });
        actions.put("ceil", () -> {
            wissenschaftlichOperationen.aufrunden();
            refresh.run();
        });
        actions.put("round", () -> {
            wissenschaftlichOperationen.runden();
            refresh.run();
        });

        actions.put("rand", () -> {
            wissenschaftlichOperationen.zufall();
            refresh.run();
        });
    }

    private void initMemoryActions()
    {
        actions.put("MC", () -> {
            rechner.speicherLoeschen();
            refreshWithExtraInfo.accept("M = 0");
        });
        actions.put("MR", () -> {
            rechner.speicherAbrufen();
            refresh.run();
        });
        actions.put("M+", () -> refreshWithExtraInfo.accept("M = " + rechner.speicherAddieren()));
        actions.put("M-", () -> refreshWithExtraInfo.accept("M = " + rechner.speicherSubtrahieren()));
    }

    public void handleButton(JButton sourceBtn)
    {
        String text = sourceBtn.getText();
        if (text == null) return;

        if (text.matches("\\d"))
        {
            rechner.eingabeZahl(text);
            refresh.run();
            return;
        }

        Runnable action = actions.get(text);
        if (action != null)
        {
            action.run();
        }
        else
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void handleScientificMenuAction(String functionName)
    {
        Runnable action = actions.get(functionName);
        if (action != null)
        {
            action.run();
        }
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
