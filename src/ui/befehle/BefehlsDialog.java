package ui.befehle;

import ui.theme.AppTheme;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/** Befehlssuche (Strg+K) im Stil einer Befehlspalette: oben mittig, ohne Rahmen. */
public final class BefehlsDialog extends JDialog
{
    private static final int BREITE = 520;
    private static final int ABSTAND_OBEN = 60;

    private BefehlsDialog(Frame owner, AppTheme theme, List<Befehl> befehle)
    {
        super(owner, "Befehlssuche", true);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        BefehlsPanel panel = new BefehlsPanel(theme, befehle, this::dispose);
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
        addWindowFocusListener(new WindowAdapter()
        {
            @Override
            public void windowLostFocus(WindowEvent e)
            {
                dispose();
            }
        });

        pack();
        setSize(BREITE, getHeight());
        if (owner != null)
        {
            setLocation(owner.getX() + (owner.getWidth() - BREITE) / 2, owner.getY() + ABSTAND_OBEN);
        }
        else
        {
            setLocationRelativeTo(null);
        }
    }

    public static void showDialog(Frame owner, AppTheme theme, List<Befehl> befehle)
    {
        new BefehlsDialog(owner, theme, befehle).setVisible(true);
    }
}
