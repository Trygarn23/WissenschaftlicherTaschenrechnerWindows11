package ui.shell;

import modes.wissenschaftlich.logic.WissenschaftlichRechnerService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ShellActionRegistry
{
    private final WissenschaftlichRechnerService rechner;
    private final Runnable refresh;
    private final Consumer<String> refreshWithExtraInfo;
    private final Runnable evaluate;
    private final Map<String, Runnable> actions = new HashMap<>();

    public ShellActionRegistry(
            WissenschaftlichRechnerService rechner,
            Runnable refresh,
            Consumer<String> refreshWithExtraInfo,
            Runnable evaluate)
    {
        this.rechner = rechner;
        this.refresh = refresh;
        this.refreshWithExtraInfo = refreshWithExtraInfo;
        this.evaluate = evaluate;
        initActions();
    }

    private void initActions()
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

        actions.put("n!", () -> {
            rechner.fakultaet();
            refresh.run();
        });

        actions.put("10ˣ", () -> {
            rechner.zehnHoch();
            refresh.run();
        });
        actions.put("xʸ", () -> {
            rechner.potenz();
            refresh.run();
        });

        actions.put("ln", () -> {
            rechner.ln();
            refresh.run();
        });
        actions.put("log", () -> {
            rechner.log();
            refresh.run();
        });

        actions.put("sin", () -> {
            rechner.sin();
            refresh.run();
        });
        actions.put("cos", () -> {
            rechner.cos();
            refresh.run();
        });
        actions.put("tan", () -> {
            rechner.tan();
            refresh.run();
        });

        actions.put("π", () -> {
            rechner.pi();
            refresh.run();
        });
        actions.put("e", () -> {
            rechner.e();
            refresh.run();
        });

        actions.put("exp", () -> {
            rechner.exp();
            refresh.run();
        });
        actions.put("|x|", () -> {
            rechner.betrag();
            refresh.run();
        });

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

        actions.put("Ans", () -> {
            rechner.ans();
            refresh.run();
        });

        actions.put("asin", () -> {
            rechner.arcsin();
            refresh.run();
        });
        actions.put("acos", () -> {
            rechner.arccos();
            refresh.run();
        });
        actions.put("atan", () -> {
            rechner.arctan();
            refresh.run();
        });

        actions.put("sinh", () -> {
            rechner.sinusHyperbolicus();
            refresh.run();
        });
        actions.put("cosh", () -> {
            rechner.cosinusHyperbolicus();
            refresh.run();
        });
        actions.put("tanh", () -> {
            rechner.tangensHyperbolicus();
            refresh.run();
        });

        actions.put("floor", () -> {
            rechner.abrunden();
            refresh.run();
        });
        actions.put("ceil", () -> {
            rechner.aufrunden();
            refresh.run();
        });
        actions.put("round", () -> {
            rechner.runden();
            refresh.run();
        });

        actions.put("rand", () -> {
            rechner.zufall();
            refresh.run();
        });
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
