package modes.wissenschaftlich.ui;

import common.konstanten.KonstantenFavoriten;
import common.persistence.AppDateien;
import common.state.RechnerModus;
import modes.wissenschaftlich.logic.VariablenSpeicher;
import ui.shell.ModePanel;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.CalculatorButtonStyler;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WissenschaftlichPanel extends JPanel implements ModePanel
{
    private static final String[] BUTTONS = {
            "f(x)", "floor", "ceil", "mod", "rand",
            "MC", "MR", "M+", "M-", "Ans",
            "π", "e", "sin", "cos", "tan",
            "x²", "1/x", "|x|", "CE", "C",
            "√x", "(", ")", "←", "÷",
            "xʸ", "7", "8", "9", "×",
            "10ˣ", "4", "5", "6", "-",
            "log", "1", "2", "3", "+",
            "ln", "±", "0", ",", "="
    };

    /** Gruppen des f(x)-Popups in Anzeigereihenfolge; die Favoriten folgen derselben Reihenfolge. */
    private static final Map<String, String[]> FX_GROUPS = new LinkedHashMap<>();

    static
    {
        FX_GROUPS.put("Trigonometrie", new String[] {
                "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh"});
        FX_GROUPS.put("Kombinatorik & Zahlen", new String[] {
                "nCr", "nPr", ";", "ggT", "kgV", "n!"});
        FX_GROUPS.put("Ergebnis & Speicher", new String[] {
                "S↔D", "Mehr Stellen", "STO", "RCL"});
    }

    private Consumer<String> functionSelectionListener;
    private JPopupMenu functionPopupMenu;
    private JPanel functionPopupContent;
    private JLabel functionPopupTitleLabel;
    private final List<JButton> functionPopupButtons = new ArrayList<>();
    private final List<JLabel> functionGroupLabels = new ArrayList<>();

    /** Per Rechtsklick angeheftete Funktionen; die Namensliste ist dieselbe Logik wie bei Konstanten-Favoriten. */
    private final KonstantenFavoriten favoriten;
    private final JPanel favoritesRow = new JPanel(new GridLayout(1, 0, 6, 6));
    private AppTheme theme;

    public WissenschaftlichPanel()
    {
        this(new KonstantenFavoriten(AppDateien.funktionsFavoriten()));
    }

    public WissenschaftlichPanel(KonstantenFavoriten favoriten)
    {
        this.favoriten = favoriten;
        setLayout(new GridLayout(9, 5, 8, 8));
        setOpaque(true);

        for (String text : BUTTONS)
        {
            if ("f(x)".equals(text))
            {
                add(createFunctionMenuButton());
            }
            else
            {
                add(createButton(text));
            }
        }
    }

    public void setFunctionSelectionListener(Consumer<String> listener)
    {
        this.functionSelectionListener = listener;
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.WISSENSCHAFTLICH;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.panelBackground());
        CalculatorButtonStyler.stylePanel(this, theme);

        if (functionPopupMenu != null)
        {
            functionPopupMenu.setBorder(BorderFactory.createLineBorder(theme.modeBorder(), 1));
        }

        if (functionPopupContent != null)
        {
            functionPopupContent.setBackground(theme.panelBackground());
        }

        if (functionPopupTitleLabel != null)
        {
            functionPopupTitleLabel.setFont(theme.buttonFont().deriveFont(Font.BOLD));
            functionPopupTitleLabel.setForeground(theme.displayForeground());
        }

        for (JLabel label : functionGroupLabels)
        {
            label.setFont(theme.buttonFont().deriveFont(Font.BOLD));
            label.setForeground(theme.displayForeground());
        }

        for (JButton button : functionPopupButtons)
        {
            styleFunctionButton(button);
        }
        for (Component button : favoritesRow.getComponents())
        {
            styleFunctionButton((JButton) button);
        }
    }

    private void styleFunctionButton(JButton button)
    {
        if (theme == null)
        {
            return;
        }
        button.setFont(theme.buttonFont());
        button.setBackground(theme.functionButtonBackground());
        button.setForeground(theme.functionButtonForeground());
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private JButton createButton(String text)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        ButtonTooltips.apply(button, text);
        return button;
    }

    private JButton createFunctionMenuButton()
    {
        JButton triggerButton = new JButton("f(x) ▼");
        triggerButton.setFocusable(true);
        triggerButton.setMnemonic(KeyEvent.VK_F);
        triggerButton.putClientProperty("keyboardFocusable", Boolean.TRUE);
        triggerButton.getAccessibleContext().setAccessibleName("Funktionsmenü");
        ButtonTooltips.apply(triggerButton, "f(x)");

        functionPopupMenu = new JPopupMenu();

        functionPopupContent = new JPanel(new BorderLayout(0, 8));
        functionPopupContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        functionPopupTitleLabel = new JLabel("Funktionen (Rechtsklick: anheften)");
        functionPopupTitleLabel.setFont(AppFonts.fett(14));

        JPanel groups = new JPanel();
        groups.setLayout(new BoxLayout(groups, BoxLayout.Y_AXIS));
        groups.setOpaque(false);

        for (Map.Entry<String, String[]> group : FX_GROUPS.entrySet())
        {
            JLabel label = new JLabel(group.getKey());
            label.setFont(AppFonts.fett(12));
            label.setBorder(new EmptyBorder(4, 0, 4, 0));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            functionGroupLabels.add(label);
            groups.add(label);

            JPanel grid = new JPanel(new GridLayout(0, 3, 6, 6));
            grid.setOpaque(false);
            grid.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (String functionName : group.getValue())
            {
                JButton fnButton = createPopupFunctionButton(functionName);
                functionPopupButtons.add(fnButton);
                grid.add(fnButton);
            }
            groups.add(grid);
        }

        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.setOpaque(false);
        favoritesRow.setOpaque(false);
        north.add(functionPopupTitleLabel, BorderLayout.NORTH);
        north.add(favoritesRow, BorderLayout.CENTER);
        aktualisiereFavoriten();

        functionPopupContent.add(north, BorderLayout.NORTH);
        functionPopupContent.add(groups, BorderLayout.CENTER);
        functionPopupMenu.add(functionPopupContent);

        triggerButton.addActionListener(e -> functionPopupMenu.show(triggerButton, 0, triggerButton.getHeight()));

        return triggerButton;
    }

    private JButton createPopupFunctionButton(String functionName)
    {
        JButton fnButton = new JButton(functionName);
        fnButton.setFocusable(false);
        fnButton.setFont(AppFonts.normal(13));
        fnButton.setBorderPainted(false);
        fnButton.setOpaque(true);
        ButtonTooltips.apply(fnButton, functionName);

        fnButton.addActionListener(e -> {
            functionPopupMenu.setVisible(false);
            if ("STO".equals(functionName) || "RCL".equals(functionName))
            {
                showVariableMenu((JComponent) functionPopupMenu.getInvoker(), functionName);
            }
            else if (functionSelectionListener != null)
            {
                functionSelectionListener.accept(functionName);
            }
        });
        fnButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (SwingUtilities.isRightMouseButton(e))
                {
                    favoriten.umschalten(functionName);
                    aktualisiereFavoriten();
                }
            }
        });
        styleFunctionButton(fnButton);
        return fnButton;
    }

    /** Kleines Auswahlmenü A–F; gemeldet wird z. B. „STO A“ oder „RCL C“. */
    private void showVariableMenu(JComponent anchor, String command)
    {
        JPopupMenu variableMenu = new JPopupMenu();
        for (String name : VariablenSpeicher.NAMEN)
        {
            JMenuItem item = new JMenuItem(command + " " + name);
            item.addActionListener(e -> {
                if (functionSelectionListener != null)
                {
                    functionSelectionListener.accept(command + " " + name);
                }
            });
            if (theme != null)
            {
                item.setFont(theme.buttonFont());
                item.setBackground(theme.functionButtonBackground());
                item.setForeground(theme.functionButtonForeground());
            }
            variableMenu.add(item);
        }
        if (theme != null)
        {
            variableMenu.setBackground(theme.panelBackground());
            variableMenu.setBorder(BorderFactory.createLineBorder(theme.modeBorder(), 1));
        }
        variableMenu.show(anchor, 0, anchor.getHeight());
    }

    /** Zeile „Favoriten“ oben im Popup, in der Reihenfolge des Popups. */
    private void aktualisiereFavoriten()
    {
        favoritesRow.removeAll();
        for (String[] group : FX_GROUPS.values())
        {
            for (String functionName : group)
            {
                if (favoriten.istFavorit(functionName))
                {
                    favoritesRow.add(createPopupFunctionButton(functionName));
                }
            }
        }
        favoritesRow.setVisible(favoritesRow.getComponentCount() > 0);
        favoritesRow.revalidate();
        if (functionPopupMenu != null && functionPopupMenu.isVisible())
        {
            functionPopupMenu.pack();
        }
    }
}
