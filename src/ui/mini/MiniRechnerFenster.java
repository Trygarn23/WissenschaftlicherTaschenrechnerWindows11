package ui.mini;

import common.state.WinkelModus;
import ui.theme.AppTheme;

import javax.swing.JFrame;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Kleiner Rechner, der über anderen Fenstern bleibt (z. B. neben der IDE).
 * Eigenes JFrame statt Dialog, damit er beim Minimieren des Hauptfensters nicht mit verschwindet.
 */
public final class MiniRechnerFenster extends JFrame
{
    private static MiniRechnerFenster offen;

    private final MiniRechnerPanel panel;

    private MiniRechnerFenster(Frame owner, AppTheme theme, WinkelModus winkel)
    {
        super("Mini-Rechner");
        if (owner != null)
        {
            setIconImages(owner.getIconImages());
        }
        panel = new MiniRechnerPanel(theme, winkel, this::setAlwaysOnTop);
        setContentPane(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setSize(320, 170);
        setLocationRelativeTo(owner);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
                panel.fokussiereEingabe();
            }

            @Override
            public void windowClosed(WindowEvent e)
            {
                offen = null;
            }
        });
    }

    /** Öffnet den Mini-Rechner oder holt das schon offene Fenster nach vorn. */
    public static MiniRechnerFenster zeige(Frame owner, AppTheme theme, WinkelModus winkel)
    {
        if (offen == null)
        {
            offen = new MiniRechnerFenster(owner, theme, winkel);
        }
        else
        {
            offen.applyTheme(theme);
        }
        offen.setVisible(true);
        offen.toFront();
        offen.panel.fokussiereEingabe();
        return offen;
    }

    public void applyTheme(AppTheme theme)
    {
        panel.applyTheme(theme);
    }
}
