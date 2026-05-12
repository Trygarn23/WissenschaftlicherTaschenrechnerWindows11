package modes.programmierer.ui;

import modes.programmierer.formatting.ProgrammiererFormatter;
import modes.programmierer.logic.ProgrammiererLogik;
import modes.programmierer.model.Basis;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class ProgrammiererDisplayPanel extends JPanel
{
    private final JPanel displayPanel = new JPanel(new BorderLayout(0, 10));
    private final JLabel aktuelleBasisLabel = new JLabel("DEC: 0", SwingConstants.RIGHT);
    private final JLabel hexLabel = new JLabel("HEX: 0", SwingConstants.RIGHT);
    private final JLabel decLabel = new JLabel("DEC: 0", SwingConstants.RIGHT);
    private final JLabel octLabel = new JLabel("OCT: 0", SwingConstants.RIGHT);
    private final JLabel binLabel = new JLabel("BIN: 0", SwingConstants.RIGHT);
    private final JLabel statusLabel = new JLabel("Basis: DEC | Wortbreite: QWORD | SIGNED", SwingConstants.RIGHT);

    ProgrammiererDisplayPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 6, 0));

        displayPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        aktuelleBasisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 42));
        aktuelleBasisLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel conversions = new JPanel(new GridLayout(4, 1, 0, 6));
        conversions.setOpaque(false);

        styleSecondaryLabel(hexLabel);
        styleSecondaryLabel(decLabel);
        styleSecondaryLabel(octLabel);
        styleSecondaryLabel(binLabel);
        styleSecondaryLabel(statusLabel);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        conversions.add(hexLabel);
        conversions.add(decLabel);
        conversions.add(octLabel);
        conversions.add(binLabel);

        displayPanel.add(aktuelleBasisLabel, BorderLayout.NORTH);
        displayPanel.add(conversions, BorderLayout.CENTER);
        displayPanel.add(statusLabel, BorderLayout.SOUTH);

        add(displayPanel, BorderLayout.CENTER);
        applyTheme(new DarkTheme());
    }

    void refresh(ProgrammiererLogik logik, ProgrammiererFormatter formatter)
    {
        String op = logik.getPendingOperationText();

        aktuelleBasisLabel.setText(
                logik.getBasis().name() + ": "
                        + formatter.emptyAsZero(logik.getAktuelleEingabe())
                        + (op.isEmpty() ? "" : "   [" + op + "]")
        );

        hexLabel.setText("HEX: " + formatter.formatHex(logik.getAnzeige(Basis.HEX), logik.getWortbreite()));
        decLabel.setText("DEC: " + formatter.formatDec(logik.getAnzeige(Basis.DEC)));
        octLabel.setText("OCT: " + formatter.formatOct(logik.getAnzeige(Basis.OCT)));
        binLabel.setText("BIN: " + formatter.formatBinary(logik.getAnzeige(Basis.BIN), logik.getWortbreite()));
        statusLabel.setText("Basis: " + logik.getBasis().name()
                + " | Wortbreite: " + logik.getWortbreite().name()
                + " | " + (logik.isUnsigned() ? "UNSIGNED" : "SIGNED"));
    }

    void applyTheme(AppTheme theme)
    {
        displayPanel.setBackground(theme.displayBackground());
        aktuelleBasisLabel.setForeground(theme.displayForeground());
        aktuelleBasisLabel.setFont(theme.displayFont().deriveFont(Font.PLAIN, 42f));
        hexLabel.setForeground(theme.secondaryDisplayForeground());
        decLabel.setForeground(theme.secondaryDisplayForeground());
        octLabel.setForeground(theme.secondaryDisplayForeground());
        binLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));
    }

    private void styleSecondaryLabel(JLabel label)
    {
        label.setFont(new Font("Consolas", Font.PLAIN, 18));
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
    }
}
