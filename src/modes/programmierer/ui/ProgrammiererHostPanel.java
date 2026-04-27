package modes.programmierer.ui;

import modes.programmierer.ui.ProgrammiererPanel;

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