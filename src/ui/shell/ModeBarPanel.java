package ui.shell;

import common.state.RechnerModus;
import ui.shortcuts.Tastenkuerzel;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModeBarPanel extends JPanel
{
    private static final List<RechnerModus> DIREKTE_MODI = List.of(
            RechnerModus.STANDARD,
            RechnerModus.WISSENSCHAFTLICH,
            RechnerModus.PROGRAMMIERER,
            RechnerModus.GRAPH,
            RechnerModus.KOMPLEX
    );
    /** Alles, was nicht direkt in der Leiste steht, landet im „Weitere…“-Menü. */
    private static final List<RechnerModus> WEITERE_MODI = Arrays.stream(RechnerModus.values())
            .filter(modus -> !DIREKTE_MODI.contains(modus))
            .toList();

    private final Map<RechnerModus, JButton> buttons = new EnumMap<>(RechnerModus.class);
    private final Map<RechnerModus, JButton> weitereButtons = new EnumMap<>(RechnerModus.class);
    private final JButton weitereButton = new JButton("Weitere...");
    private final JButton einheitenButton = new JButton("Einheiten");
    private final JPopupMenu weitereMenu = new JPopupMenu();
    private final JButton zuletztButton = new JButton();
    private RechnerModus zuletztModus;

    private Consumer<RechnerModus> modeListener;
    private Runnable unitsListener;

    public ModeBarPanel()
    {
        setLayout(new GridLayout(1, 0, 8, 0));
        setOpaque(true);

        for (RechnerModus modus : DIREKTE_MODI)
        {
            JButton button = createModeButton(modus);
            buttons.put(modus, button);
            add(button);
        }

        buildWeitereMenu();
        configureWeitereButton();
        add(weitereButton);

        zuletztButton.setFocusable(false);
        zuletztButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        zuletztButton.addActionListener(e -> {
            if (zuletztModus != null)
            {
                openMode(zuletztModus);
            }
        });
    }

    private JButton createModeButton(RechnerModus modus)
    {
        JButton button = new JButton(modus.getLabel());
        button.setFocusable(false);
        button.setToolTipText("Wechselt zu " + modus.getLabel()
                + (Tastenkuerzel.hatStrgZahl(modus) ? " (" + Tastenkuerzel.strgText(Tastenkuerzel.modusNummer(modus)) + ")" : ""));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> openMode(modus));
        return button;
    }

    private void buildWeitereMenu()
    {
        JPanel menuContent = new JPanel(new GridLayout(0, 2, 6, 6));
        menuContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));

        for (RechnerModus modus : WEITERE_MODI)
        {
            JButton button = createModeButton(modus);
            weitereButtons.put(modus, button);
            menuContent.add(button);
        }

        einheitenButton.setFocusable(false);
        einheitenButton.setToolTipText("Einheiten ein-/ausblenden (" + Tastenkuerzel.strgText(Tastenkuerzel.einheitenNummer()) + ")");
        einheitenButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        einheitenButton.addActionListener(e -> {
            weitereMenu.setVisible(false);
            if (unitsListener != null)
            {
                unitsListener.run();
            }
        });
        menuContent.add(einheitenButton);

        weitereMenu.add(menuContent);
    }

    private void configureWeitereButton()
    {
        weitereButton.setFocusable(false);
        weitereButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        weitereButton.setToolTipText("Weitere Modi und Werkzeuge");
        weitereButton.addActionListener(e -> weitereMenu.show(weitereButton, 0, weitereButton.getHeight()));
        weitereButton.setComponentPopupMenu(weitereMenu); // Rechtsklick öffnet das Menü auch
    }

    private void openMode(RechnerModus modus)
    {
        weitereMenu.setVisible(false);
        if (modeListener != null)
        {
            modeListener.accept(modus);
        }
    }

    public void setModeListener(Consumer<RechnerModus> modeListener)
    {
        this.modeListener = modeListener;
    }

    public void setUnitsListener(Runnable unitsListener)
    {
        this.unitsListener = unitsListener;
    }

    public void setSelectedMode(RechnerModus aktuellerModus, AppTheme theme)
    {
        setBackground(theme.modeBarBackground());
        weitereMenu.setBorder(javax.swing.BorderFactory.createLineBorder(theme.modeBorder(), 1));

        for (Map.Entry<RechnerModus, JButton> entry : buttons.entrySet())
        {
            styleModeButton(entry.getValue(), entry.getKey() == aktuellerModus, theme);
        }

        boolean weitererModusAktiv = WEITERE_MODI.contains(aktuellerModus);
        if (weitererModusAktiv)
        {
            merkeZuletztGenutzt(aktuellerModus);
        }
        if (zuletztModus != null)
        {
            styleModeButton(zuletztButton, zuletztModus == aktuellerModus, theme);
        }
        ModernButtonStyler.styleButton(
                weitereButton,
                theme,
                theme.modeButtonInactiveBackground(),
                theme.modeButtonForeground(false)
        );

        for (Map.Entry<RechnerModus, JButton> entry : weitereButtons.entrySet())
        {
            boolean aktiv = entry.getKey() != null && entry.getKey() == aktuellerModus;
            styleMenuButton(entry.getValue(), aktiv, theme);
        }
        styleMenuButton(einheitenButton, false, theme);
    }

    /**
     * Der zuletzt genutzte Modus aus „Weitere…“ bekommt einen eigenen Knopf in der Leiste,
     * damit man für Matrix und Co. nicht jedes Mal das Menü aufklappen muss.
     */
    private void merkeZuletztGenutzt(RechnerModus modus)
    {
        zuletztModus = modus;
        zuletztButton.setText(modus.getLabel());
        zuletztButton.setToolTipText("Zuletzt genutzt: " + modus.getLabel());
        if (zuletztButton.getParent() == null)
        {
            add(zuletztButton, getComponentZOrder(weitereButton));
            revalidate();
        }
    }

    /** Im Prüfungsmodus sind nur Standard und Wissenschaftlich anklickbar. */
    public void setPruefungsModus(boolean aktiv)
    {
        for (Map.Entry<RechnerModus, JButton> entry : buttons.entrySet())
        {
            entry.getValue().setEnabled(PruefungsModus.istModusErlaubt(aktiv, entry.getKey()));
        }
        weitereButton.setEnabled(!aktiv);
        zuletztButton.setEnabled(!aktiv);
    }

    private void styleModeButton(JButton button, boolean aktiv, AppTheme theme)
    {
        ModernButtonStyler.styleButton(
                button,
                theme,
                aktiv ? theme.modeButtonActiveBackground() : theme.modeButtonInactiveBackground(),
                theme.modeButtonForeground(aktiv)
        );
    }

    private void styleMenuButton(JButton button, boolean aktiv, AppTheme theme)
    {
        ModernButtonStyler.styleButton(
                button,
                theme,
                aktiv ? theme.menuActiveBackground() : theme.popupOptionBackground(),
                aktiv ? theme.popupSelectedForeground() : theme.popupOptionForeground()
        );
    }
}
