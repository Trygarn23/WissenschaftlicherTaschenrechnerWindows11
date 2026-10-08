package modes.programmierer.ui;

import common.formatting.ZahlenEingabe;
import modes.programmierer.formatting.ProgrammiererFormatter;
import modes.programmierer.logic.Ieee754Zerlegung;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Inhalt des IEEE-754-Dialogs: eigenes Eingabefeld, darunter float- und double-Zerlegung. */
class ProgrammiererIeeePanel extends JPanel
{
    private final ProgrammiererFormatter formatter = new ProgrammiererFormatter();
    private final JTextField eingabeFeld = new JTextField(16);
    private final JButton zerlegenButton = new JButton("Zerlegen");
    private final JTextArea ergebnisArea = new JTextArea(9, 82);
    private final JLabel zahlLabel = new JLabel("Zahl:");
    private final JLabel statusLabel = new JLabel("Zahl eingeben, auch NaN, Infinity oder -0");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);

    ProgrammiererIeeePanel(String startwert, AppTheme theme)
    {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        zerlegenButton.setToolTipText("Zerlegt die Zahl in Vorzeichen, Exponent und Mantisse");
        zerlegenButton.addActionListener(e -> zerlege());
        eingabeFeld.addActionListener(e -> zerlege());
        eingabeFeld.setText(startwert);

        JPanel eingabeZeile = new JPanel(new BorderLayout(8, 0));
        eingabeZeile.setOpaque(false);
        eingabeZeile.add(zahlLabel, BorderLayout.WEST);
        eingabeZeile.add(eingabeFeld, BorderLayout.CENTER);
        eingabeZeile.add(zerlegenButton, BorderLayout.EAST);

        ergebnisArea.setEditable(false);
        ergebnisArea.setFont(AppFonts.festeBreite(13));

        add(eingabeZeile, BorderLayout.NORTH);
        add(new JScrollPane(ergebnisArea), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        applyTheme(theme);
        zerlege();
    }

    private void zerlege()
    {
        try
        {
            double wert = ZahlenEingabe.lese(eingabeFeld.getText());
            ergebnisArea.setText(formatter.formatIeee754(Ieee754Zerlegung.alsFloat(wert)) + "\n\n"
                    + formatter.formatIeee754(Ieee754Zerlegung.alsDouble(wert)));
            ergebnisArea.setCaretPosition(0);
            statusAnzeige.zeigeErfolg("Zerlegt");
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void applyTheme(AppTheme theme)
    {
        setBackground(theme.panelBackground());
        ModernButtonStyler.styleInput(eingabeFeld, theme);
        eingabeFeld.setCaretColor(theme.displayForeground());
        ModernButtonStyler.styleButton(zerlegenButton, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        ergebnisArea.setBackground(theme.displayBackground());
        ergebnisArea.setForeground(theme.displayForeground());
        statusLabel.setFont(AppFonts.normal(13));
        statusAnzeige.setTheme(theme);
        zahlLabel.setForeground(theme.displayForeground());
    }
}
