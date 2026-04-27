package ui.shell;

import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class DisplayPanel extends JPanel
{
    private final JTextPane recDisplay = new JTextPane();
    private final JTextPane mainDisplay = new JTextPane();

    public DisplayPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(false);

        configurePane(recDisplay);
        configurePane(mainDisplay);

        mainDisplay.setText("0");

        add(recDisplay, BorderLayout.NORTH);
        add(mainDisplay, BorderLayout.CENTER);
    }

    private void configurePane(JTextPane pane)
    {
        pane.setEditable(false);
        pane.setOpaque(true);
        pane.setBorder(null);
        pane.setFocusable(false);

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(0, doc.getLength(), right, false);
    }

    public void setMainText(String text)
    {
        mainDisplay.setText(text);
    }

    public void setSecondaryText(String text)
    {
        recDisplay.setText(text);
    }

    public void applyTheme(AppTheme theme)
    {
        recDisplay.setBackground(theme.displayBackground());
        recDisplay.setForeground(theme.secondaryDisplayForeground());
        recDisplay.setFont(theme.secondaryDisplayFont());

        mainDisplay.setBackground(theme.displayBackground());
        mainDisplay.setForeground(theme.displayForeground());
        mainDisplay.setFont(theme.displayFont());
    }
}

