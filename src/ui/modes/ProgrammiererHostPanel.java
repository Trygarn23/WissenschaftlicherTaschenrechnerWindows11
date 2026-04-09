package ui.modes;

import ProgrammierRechner.ProgrammiererPanel;

import javax.swing.*;
import java.awt.*;

public class ProgrammiererHostPanel extends JPanel
{
    public ProgrammiererHostPanel()
    {
        setLayout(new BorderLayout());
        add(new ProgrammiererPanel(), BorderLayout.CENTER);
    }
}