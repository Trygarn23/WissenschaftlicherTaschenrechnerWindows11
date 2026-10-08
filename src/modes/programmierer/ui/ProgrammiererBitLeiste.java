package modes.programmierer.ui;

import modes.programmierer.logic.ProgrammiererLogik;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.themes.DarkTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.IntConsumer;

/** 64 klickbare Bits in zwei Zeilen à 32, gruppiert zu je 4 mit Bitnummer darunter. */
class ProgrammiererBitLeiste extends JPanel
{
    private static final int BITS = 64;

    private final JButton[] bitButtons = new JButton[BITS];
    private final JPanel[] gruppen = new JPanel[BITS / 4];
    private final JLabel[] nummern = new JLabel[BITS / 4];
    private final JPanel obereZeile;
    private AppTheme theme = new DarkTheme();

    ProgrammiererBitLeiste(IntConsumer bitListener)
    {
        setLayout(new GridLayout(2, 1, 0, 4));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 6, 0));

        obereZeile = baueZeile(63, bitListener);
        add(obereZeile);
        add(baueZeile(31, bitListener));
    }

    void refresh(ProgrammiererLogik logik)
    {
        int aktiveBits = logik.getWortbreite().getBits();
        obereZeile.setVisible(aktiveBits > 32);

        for (int gruppe = 0; gruppe < gruppen.length; gruppe++)
        {
            gruppen[gruppe].setVisible(gruppe * 4 < aktiveBits);
        }

        for (int bit = 0; bit < BITS; bit++)
        {
            boolean gesetzt = bit < aktiveBits && logik.istBitGesetzt(bit);
            JButton button = bitButtons[bit];
            button.setText(gesetzt ? "1" : "0");
            button.setBackground(gesetzt ? theme.operatorButtonBackground() : theme.numberButtonBackground());
            button.setForeground(gesetzt ? theme.operatorButtonForeground() : theme.numberButtonForeground());
        }
    }

    void applyTheme(AppTheme theme, ProgrammiererLogik logik)
    {
        this.theme = theme;
        for (JLabel nummer : nummern)
        {
            nummer.setForeground(theme.secondaryDisplayForeground());
        }
        refresh(logik);
    }

    private JPanel baueZeile(int hoechstesBit, IntConsumer bitListener)
    {
        JPanel zeile = new JPanel(new GridLayout(1, 8, 8, 0));
        zeile.setOpaque(false);

        for (int gruppenStart = hoechstesBit; gruppenStart > hoechstesBit - 32; gruppenStart -= 4)
        {
            zeile.add(baueGruppe(gruppenStart, bitListener));
        }

        return zeile;
    }

    private JPanel baueGruppe(int hoechstesBit, IntConsumer bitListener)
    {
        int niedrigstesBit = hoechstesBit - 3;

        JPanel bits = new JPanel(new GridLayout(1, 4, 1, 0));
        bits.setOpaque(false);
        for (int bit = hoechstesBit; bit >= niedrigstesBit; bit--)
        {
            bits.add(baueBitButton(bit, bitListener));
        }

        JLabel nummer = new JLabel(String.valueOf(niedrigstesBit), SwingConstants.RIGHT);
        nummer.setFont(AppFonts.normal(10));
        nummern[niedrigstesBit / 4] = nummer;

        JPanel gruppe = new JPanel(new BorderLayout(0, 1));
        gruppe.setOpaque(false);
        gruppe.add(bits, BorderLayout.CENTER);
        gruppe.add(nummer, BorderLayout.SOUTH);
        gruppen[niedrigstesBit / 4] = gruppe;
        return gruppe;
    }

    private JButton baueBitButton(int bit, IntConsumer bitListener)
    {
        JButton button = new JButton("0");
        button.setFont(AppFonts.festeBreite(13));
        button.setMargin(new Insets(1, 0, 1, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Bit " + bit + " kippen");
        button.getAccessibleContext().setAccessibleName("Bit " + bit);
        button.addActionListener(e -> bitListener.accept(bit));
        bitButtons[bit] = button;
        return button;
    }
}
