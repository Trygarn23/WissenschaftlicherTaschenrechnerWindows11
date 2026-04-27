import ui.shell.TaschenrechnerUI;

import javax.swing.*;
public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new TaschenrechnerUI().setVisible(true));
    }
}