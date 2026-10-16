package ui.shell;

import common.parser.EigeneFunktionen;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;

/** Einfache Dialoge zum Anlegen und Löschen eigener Funktionen f(x). */
final class EigeneFunktionenDialog
{
    private EigeneFunktionenDialog()
    {
    }

    static void anlegen(Component parent, EigeneFunktionen funktionen)
    {
        JTextField nameField = new JTextField(12);
        JTextField ausdruckField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Name (nur Buchstaben, z. B. quad)"));
        panel.add(nameField);
        panel.add(new JLabel("Ausdruck in x (z. B. x^2+1)"));
        panel.add(ausdruckField);

        while (JOptionPane.showConfirmDialog(parent, panel, "Eigene Funktion anlegen",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
        {
            try
            {
                funktionen.fuegeHinzu(nameField.getText(), ausdruckField.getText());
                return;
            }
            catch (IllegalArgumentException e)
            {
                JOptionPane.showMessageDialog(parent, e.getMessage(), "Eigene Funktion", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    static void loeschen(Component parent, EigeneFunktionen funktionen)
    {
        Object[] namen = funktionen.alle().entrySet().stream()
                .map(e -> e.getKey() + "(x) = " + e.getValue())
                .toArray();
        if (namen.length == 0)
        {
            JOptionPane.showMessageDialog(parent, "Es gibt noch keine eigenen Funktionen.", "Eigene Funktion löschen",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object auswahl = JOptionPane.showInputDialog(parent, "Welche Funktion löschen?", "Eigene Funktion löschen",
                JOptionPane.QUESTION_MESSAGE, null, namen, namen[0]);
        if (auswahl != null)
        {
            funktionen.entferne(auswahl.toString().substring(0, auswahl.toString().indexOf('(')));
        }
    }
}
