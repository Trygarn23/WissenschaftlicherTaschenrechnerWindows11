package ui.shell;

import common.state.RechnerModus;
import ui.history.HistoryPanel;
import ui.shortcuts.Tastenkuerzel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

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
    private final BooleanSupplier calculatorShortcutsEnabled;

    public KeyboardShortcutBinder(
            JRootPane rootPane,
            HistoryPanel historyPanel,
            ShellActionRegistry aktionen,
            BooleanSupplier calculatorShortcutsEnabled)
    {
        this.rootPane = rootPane;
        this.historyPanel = historyPanel;
        this.aktionen = aktionen;
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

        bind(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "rueckgaengig",
                () -> aktionen.ausfuehren(Tastenkuerzel.RUECKGAENGIG_AKTION));
    }

    /**
     * Strg+1 … Strg+8 und F1 funktionieren in jedem Modus, auch wenn die Rechnertasten gerade aus sind
     * oder das Suchfeld den Fokus hat.
     */
    public void setupGlobaleTasten(Consumer<RechnerModus> modusWechsel, Runnable einheitenUmschalten, Runnable hilfeOeffnen)
    {
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        for (RechnerModus modus : RechnerModus.values())
        {
            if (!Tastenkuerzel.hatStrgZahl(modus)) continue;
            bindStrgZahl(im, am, Tastenkuerzel.modusNummer(modus), "modus" + modus.name(), () -> modusWechsel.accept(modus));
        }
        bindStrgZahl(im, am, Tastenkuerzel.einheitenNummer(), "einheitenUmschalten", einheitenUmschalten);
        bindImmer(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "tastenkuerzelHilfe", hilfeOeffnen);
    }

    /** Werkzeug-Tasten wie Strg+K: funktionieren in jedem Modus, auch wenn das Suchfeld den Fokus hat. */
    public void bindeWerkzeugTaste(KeyStroke taste, String name, Runnable aktion)
    {
        bindImmer(rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), rootPane.getActionMap(), taste, name, aktion);
    }

    private void bindStrgZahl(InputMap im, ActionMap am, int nummer, String name, Runnable action)
    {
        bindImmer(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_0 + nummer, InputEvent.CTRL_DOWN_MASK), name, action);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + nummer, InputEvent.CTRL_DOWN_MASK), name);
    }

    private void bindImmer(InputMap im, ActionMap am, KeyStroke ks, String name, Runnable action)
    {
        im.put(ks, name);
        am.put(name, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                action.run();
            }
        });
    }

    private void bind(InputMap im, ActionMap am, int tastenCode, String name, Runnable action)
    {
        bind(im, am, KeyStroke.getKeyStroke(tastenCode, 0), name, action);
    }

    private void bind(InputMap im, ActionMap am, KeyStroke ks, String name, Runnable action)
    {
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
