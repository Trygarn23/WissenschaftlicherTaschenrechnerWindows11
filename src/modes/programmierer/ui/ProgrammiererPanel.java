package modes.programmierer.ui;

import modes.programmierer.formatting.ProgrammiererFormatter;
import modes.programmierer.logic.ProgrammiererLogik;
import modes.programmierer.model.Basis;
import modes.programmierer.model.Wortbreite;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ProgrammiererPanel extends JPanel
{
    private final ProgrammiererLogik logik = new ProgrammiererLogik();
    private final ProgrammiererFormatter formatter = new ProgrammiererFormatter();
    private final ProgrammiererDisplayPanel displayPanel = new ProgrammiererDisplayPanel();
    private final ProgrammiererOptionsPanel optionsPanel = new ProgrammiererOptionsPanel(this::setBasis, this::setWortbreite);
    private final ProgrammiererTastenPanel tastenPanel = new ProgrammiererTastenPanel(this::handleButton);

    public ProgrammiererPanel()
    {
        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBorder(new EmptyBorder(6, 0, 0, 0));

        optionsPanel.setTastenPanel(tastenPanel);

        add(displayPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
        setupKeyboard();

        refreshAnzeige();
    }

    private void setBasis(Basis basis)
    {
        logik.setBasis(basis);
        refreshAnzeige();
    }

    private void setWortbreite(Wortbreite wortbreite)
    {
        logik.setWortbreite(wortbreite);
        refreshAnzeige();
    }

    private void handleButton(String text)
    {
        switch (text)
        {
            case "CLR" -> logik.clear();
            case "←" -> logik.backspace();
            case "NOT" -> logik.not();
            case "<<" -> logik.shiftLeft();
            case ">>" -> logik.shiftRightArithmetic();
            case ">>>" -> logik.shiftRightLogical();
            case "AND" -> logik.and();
            case "OR" -> logik.or();
            case "XOR" -> logik.xor();
            case "+" -> logik.plus();
            case "-" -> logik.minus();
            case "=" -> logik.berechne();
            case "±" -> logik.vorzeichenWechseln();
            case "SIGNED", "UNSIGNED" -> logik.toggleUnsigned();
            default -> logik.digitEingeben(text);
        }

        refreshAnzeige();
    }

    private void setupKeyboard()
    {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        for (int i = 0; i <= 9; i++)
        {
            String digit = String.valueOf(i);
            bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), "digitTop" + i, digit);
            bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), "digitPad" + i, digit);
        }

        for (int i = 0; i < 6; i++)
        {
            String hex = String.valueOf((char) ('A' + i));
            bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_A + i, 0), "hex" + hex, hex);
        }

        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace", "←");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear", "CLR");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "equals", "=");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "plusPad", "+");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "plus", "+");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "minusPad", "-");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minus", "-");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.SHIFT_DOWN_MASK), "notShortcut", "NOT");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.SHIFT_DOWN_MASK), "andShortcut", "AND");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, InputEvent.ALT_GRAPH_DOWN_MASK), "orShortcut", "OR");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_CIRCUMFLEX, 0), "xorShortcut", "XOR");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), "shiftLeftShortcut", "<<");
        bindKey(inputMap, actionMap, KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0), "shiftRightShortcut", ">>");
    }

    private void bindKey(InputMap inputMap, ActionMap actionMap, KeyStroke keyStroke, String actionName, String buttonText)
    {
        if (keyStroke == null)
        {
            return;
        }

        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if (!isShowing())
                {
                    return;
                }

                handleButton(buttonText);
            }
        });
    }

    private void refreshAnzeige()
    {
        displayPanel.refresh(logik, formatter);
        optionsPanel.refresh(logik.getBasis(), logik.getWortbreite());
        tastenPanel.refresh(logik.getBasis(), logik.isUnsigned());
    }

    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.panelBackground());
        displayPanel.applyTheme(theme);
        optionsPanel.applyTheme(theme);
        tastenPanel.applyTheme(theme);

        revalidate();
        repaint();
    }
}
