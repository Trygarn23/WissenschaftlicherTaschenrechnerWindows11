package ui.shell;

import ui.history.HistoryPanel;
import ui.shortcuts.Tastenkuerzel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class KeyboardShortcutBinder
{
    /** Zeichen, die im Suchfeld getippt und an den Rechner weitergereicht werden. */
    private static final Map<Character, String> SUCHFELD_ZEICHEN = Map.of(
            ',', ",",
            '.', ",",
            '+', "+",
            '-', "-",
            '*', "×",
            '/', "÷",
            '%', "mod",
            '\n', "=",
            '=', "="
    );

    private final JRootPane rootPane;
    private final HistoryPanel historyPanel;
    private final ShellActionRegistry aktionen;
    private final Runnable closeAction;
    private final BooleanSupplier calculatorShortcutsEnabled;

    public KeyboardShortcutBinder(
            JRootPane rootPane,
            HistoryPanel historyPanel,
            ShellActionRegistry aktionen,
            Runnable closeAction,
            BooleanSupplier calculatorShortcutsEnabled)
    {
        this.rootPane = rootPane;
        this.historyPanel = historyPanel;
        this.aktionen = aktionen;
        this.closeAction = closeAction;
        this.calculatorShortcutsEnabled = calculatorShortcutsEnabled;
    }

    public void setupKeyboard()
    {
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        for (int i = 0; i <= 9; i++)
        {
            String ziffer = String.valueOf(i);
            bind(im, am, KeyEvent.VK_0 + i, "digitTop" + i, () -> aktionen.ausfuehren(ziffer));
            bind(im, am, KeyEvent.VK_NUMPAD0 + i, "digitPad" + i, () -> aktionen.ausfuehren(ziffer));
        }

        for (Tastenkuerzel kuerzel : Tastenkuerzel.values())
        {
            for (int tastenCode : kuerzel.getTastenCodes())
            {
                bind(im, am, tastenCode, kuerzel.name() + tastenCode, () -> aktionen.ausfuehren(kuerzel.getAktion()));
            }
        }

        bind(im, am, KeyEvent.VK_ESCAPE, "escapePress", closeAction);
    }

    private void bind(InputMap im, ActionMap am, int tastenCode, String name, Runnable action)
    {
        im.put(KeyStroke.getKeyStroke(tastenCode, 0), name);
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
                String aktion = Character.isDigit(ch) ? String.valueOf(ch) : SUCHFELD_ZEICHEN.get(ch);
                if (aktion != null)
                {
                    aktionen.ausfuehren(aktion);
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                {
                    aktionen.ausfuehren(Tastenkuerzel.ZURUECK.getAktion());
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
