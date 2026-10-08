package ui.konstanten;

import common.konstanten.EigeneKonstanten;
import common.konstanten.KonstantenFavoriten;
import ui.theme.AppTheme;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

/** Modaler Dialog zum Suchen und Einfügen von Konstanten. Nach dem Einfügen schließt er sich. */
public final class KonstantenDialog extends JDialog
{
    private KonstantenDialog(Frame owner, AppTheme theme, KonstantenFavoriten favoriten, EigeneKonstanten eigene, Consumer<String> einfuegen)
    {
        super(owner, "Konstanten", true);

        KonstantenPanel panel = new KonstantenPanel(theme, favoriten, eigene, wert ->
        {
            einfuegen.accept(wert);
            dispose();
        });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(panel);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
                panel.fokussiereSuche();
            }
        });
        setSize(780, 580);
        setLocationRelativeTo(owner);
    }

    public static void showDialog(Frame owner, AppTheme theme, KonstantenFavoriten favoriten, EigeneKonstanten eigene, Consumer<String> einfuegen)
    {
        new KonstantenDialog(owner, theme, favoriten, eigene, einfuegen).setVisible(true);
    }
}
