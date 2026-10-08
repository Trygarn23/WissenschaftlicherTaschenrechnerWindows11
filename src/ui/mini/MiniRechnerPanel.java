package ui.mini;

import common.logic.BerechnungsErgebnis;
import common.logic.RechnerService;
import common.state.WinkelModus;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/** Inhalt des Mini-Rechners: Ausdruck tippen, Vorschau sehen, Enter rechnet. Mit eigenem RechnerService. */
public class MiniRechnerPanel extends JPanel
{
    private final RechnerService rechner = new RechnerService();
    private final JTextField eingabe = new JTextField();
    private final JLabel zeile = new JLabel(" ");
    private final JButton kopieren = new JButton("Kopieren");
    private final JToggleButton immerOben = new JToggleButton("Immer oben", true);
    private AppTheme theme;
    private String letztesErgebnis = "";
    private boolean fehler;

    /** @param immerObenGeaendert bekommt den neuen Zustand des Pin-Umschalters. */
    public MiniRechnerPanel(AppTheme theme, WinkelModus winkel, Consumer<Boolean> immerObenGeaendert)
    {
        super(new BorderLayout(0, 6));
        rechner.setWinkelModus(winkel);

        eingabe.setFont(AppFonts.normal(18));
        zeile.setFont(AppFonts.normal(13));
        kopieren.setFocusable(false);
        immerOben.setFocusable(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttons.setOpaque(false);
        buttons.add(kopieren);
        buttons.add(immerOben);

        JPanel unten = new JPanel(new BorderLayout(6, 0));
        unten.setOpaque(false);
        unten.add(zeile, BorderLayout.CENTER);
        unten.add(buttons, BorderLayout.SOUTH);

        add(eingabe, BorderLayout.NORTH);
        add(unten, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        kopieren.addActionListener(e -> kopiereErgebnis());
        immerOben.addActionListener(e -> immerObenGeaendert.accept(immerOben.isSelected()));
        eingabe.addActionListener(e -> rechne());
        eingabe.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "mini.leeren");
        eingabe.getActionMap().put("mini.leeren", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                eingabe.setText("");
            }
        });
        eingabe.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                zeigeVorschau();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                zeigeVorschau();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                zeigeVorschau();
            }
        });

        applyTheme(theme);
    }

    public void rechne()
    {
        if (eingabe.getText().isBlank())
        {
            return;
        }
        rechner.setAusdruckText(eingabe.getText().strip());
        BerechnungsErgebnis ergebnis = rechner.berechneDetailliert();
        if (ergebnis.isErfolgreich())
        {
            letztesErgebnis = ergebnis.getAnzeigeText();
            // Parserfertige Form ins Feld, damit man direkt weiterrechnen kann.
            eingabe.setText(rechner.getAusdruckText());
            zeigeText(ergebnis.getVerlaufText(), false);
        }
        else
        {
            zeigeText(ergebnis.getFehlerMeldung(), true);
        }
    }

    public void fokussiereEingabe()
    {
        eingabe.requestFocusInWindow();
    }

    public void setWinkelModus(WinkelModus winkel)
    {
        rechner.setWinkelModus(winkel);
        zeigeVorschau();
    }

    public String getLetztesErgebnis()
    {
        return letztesErgebnis;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.panelBackground());
        ModernButtonStyler.styleInput(eingabe, theme);
        eingabe.setCaretColor(theme.displayForeground());
        ModernButtonStyler.styleButton(kopieren, theme, theme.functionButtonBackground(), theme.functionButtonForeground());
        immerOben.setFont(theme.buttonFont());
        immerOben.setBackground(theme.toggleButtonBackground());
        immerOben.setForeground(theme.toggleButtonForeground());
        immerOben.setFocusPainted(false);
        zeigeText(zeile.getText(), fehler);
    }

    private void zeigeVorschau()
    {
        rechner.setAusdruckText(eingabe.getText().strip());
        zeigeText(rechner.liveVorschau().orElse(" "), false);
    }

    private void zeigeText(String text, boolean istFehler)
    {
        fehler = istFehler;
        zeile.setText(text);
        zeile.setForeground(istFehler ? theme.dangerBackground() : theme.secondaryDisplayForeground());
    }

    private void kopiereErgebnis()
    {
        if (!letztesErgebnis.isEmpty())
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(letztesErgebnis), null);
        }
    }
}
