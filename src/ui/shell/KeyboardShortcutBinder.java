package ui.shell;

import common.logic.RechnerService;
import ui.history.HistoryPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.BooleanSupplier;

public class KeyboardShortcutBinder
{
    private final JRootPane rootPane;
    private final HistoryPanel historyPanel;
    private final RechnerService rechner;
    private final Runnable refresh;
    private final Runnable evaluate;
    private final Runnable closeAction;
    private final BooleanSupplier calculatorShortcutsEnabled;

    public KeyboardShortcutBinder(
            JRootPane rootPane,
            HistoryPanel historyPanel,
            RechnerService rechner,
            Runnable refresh,
            Runnable evaluate,
            Runnable closeAction,
            BooleanSupplier calculatorShortcutsEnabled)
    {
        this.rootPane = rootPane;
        this.historyPanel = historyPanel;
        this.rechner = rechner;
        this.refresh = refresh;
        this.evaluate = evaluate;
        this.closeAction = closeAction;
        this.calculatorShortcutsEnabled = calculatorShortcutsEnabled;
    }

    public void setupKeyboard()
    {
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        for (int i = 0; i <= 9; i++)
        {
            final String num = String.valueOf(i);

            bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), "digitTop" + i, () -> {
                rechner.eingabeZahl(num);
                refresh.run();
            });

            bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), "digitPad" + i, () -> {
                rechner.eingabeZahl(num);
                refresh.run();
            });
        }

        Runnable commaAction = () -> {
            rechner.eingabeKomma();
            refresh.run();
        };

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), "commaVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0), "periodVK", commaAction);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), "decimalVK", commaAction);

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "plusPad", () -> operator("+"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "plusVK", () -> operator("+"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "minusPad", () -> operator("-"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minusVK", () -> operator("-"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "mulPad", () -> operator("*"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK, 0), "mulVK", () -> operator("*"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), "divPad", () -> operator("/"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "divVK", () -> operator("/"));
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "modVK", () -> operator("%"));

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterMain", evaluate);
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspacePress", () -> {
            rechner.loeschen();
            refresh.run();
        });
        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapePress", closeAction);
    }

    private void operator(String operator)
    {
        rechner.operatorSetzen(operator);
        refresh.run();
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
                if (!calculatorShortcutsEnabled.getAsBoolean())
                {
                    return;
                }

                if (keyboardBlockedBySearch())
                {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                action.run();
            }
        });
    }

    public void setupSearchFieldKeyForwarding()
    {
        historyPanel.addSearchFieldKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                char ch = e.getKeyChar();

                if (Character.isDigit(ch))
                {
                    rechner.eingabeZahl(String.valueOf(ch));
                    refresh.run();
                    e.consume();
                }
                else if (ch == ',' || ch == '.')
                {
                    rechner.eingabeKomma();
                    refresh.run();
                    e.consume();
                }
                else if ("+-*/%".indexOf(ch) >= 0)
                {
                    rechner.operatorSetzen(String.valueOf(ch));
                    refresh.run();
                    e.consume();
                }
                else if (ch == '\n' || ch == '=')
                {
                    evaluate.run();
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                {
                    rechner.loeschen();
                    refresh.run();
                    e.consume();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    defocusSearchIfNeeded();
                    e.consume();
                }
            }
        });
    }

    private boolean keyboardBlockedBySearch()
    {
        return historyPanel.isSearchFocusOwner();
    }

    public void defocusSearchIfNeeded()
    {
        if (historyPanel.isSearchFocusOwner())
        {
            rootPane.requestFocusInWindow();
        }
    }
}
