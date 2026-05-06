package ui.shell;

import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class DisplayPanel extends JPanel
{
    private final JTextPane recDisplay = new JTextPane();
    private final JTextPane mainDisplay = new JTextPane();
    private final JLabel statusLabel = new JLabel("Modus: Standard | Winkel: DEG | Speicher: leer");

    private Consumer<String> pasteListener;

    public DisplayPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setMinimumSize(new Dimension(0, 112));
        setPreferredSize(new Dimension(0, 148));

        configurePane(recDisplay);
        configurePane(mainDisplay);
        configureStatusLabel();
        configureClipboardActions();
        setToolTipText("Display anklicken: Strg+C kopiert, Strg+V fügt einen Ausdruck ein");
        recDisplay.setToolTipText(getToolTipText());
        mainDisplay.setToolTipText(getToolTipText());

        mainDisplay.setText("0");

        JPanel displayArea = new JPanel(new BorderLayout(0, 4));
        displayArea.setOpaque(true);
        displayArea.setBorder(new EmptyBorder(10, 12, 8, 12));
        displayArea.add(recDisplay, BorderLayout.NORTH);
        displayArea.add(mainDisplay, BorderLayout.CENTER);
        displayArea.add(statusLabel, BorderLayout.SOUTH);

        add(displayArea, BorderLayout.CENTER);
    }

    private void configurePane(JTextPane pane)
    {
        pane.setEditable(false);
        pane.setOpaque(true);
        pane.setBorder(null);
        pane.setFocusable(true);
        pane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(0, doc.getLength(), right, false);
    }

    private void configureStatusLabel()
    {
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        statusLabel.setFocusable(false);
        statusLabel.setToolTipText("Aktueller Modus, Winkelmodus und Speicherstatus");
    }

    private void configureClipboardActions()
    {
        bindClipboardActions(this);
        bindClipboardActions(recDisplay);
        bindClipboardActions(mainDisplay);
    }

    private void bindClipboardActions(JComponent component)
    {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "displayCopy");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "displayPaste");

        actionMap.put("displayCopy", new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                copyDisplayText();
            }
        });

        actionMap.put("displayPaste", new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                pasteDisplayText();
            }
        });
    }

    public void setMainText(String text)
    {
        mainDisplay.setText(text);
    }

    public void setSecondaryText(String text)
    {
        recDisplay.setText(text);
    }

    public void setStatusText(String text)
    {
        statusLabel.setText(text);
    }

    public void setPasteListener(Consumer<String> pasteListener)
    {
        this.pasteListener = pasteListener;
    }

    public String getMainText()
    {
        return mainDisplay.getText();
    }

    public String getSecondaryText()
    {
        return recDisplay.getText();
    }

    public String getStatusText()
    {
        return statusLabel.getText();
    }

    public void applyTheme(AppTheme theme)
    {
        Component displayArea = getComponent(0);
        displayArea.setBackground(theme.displayBackground());

        recDisplay.setBackground(theme.displayBackground());
        recDisplay.setForeground(theme.secondaryDisplayForeground());
        recDisplay.setFont(theme.secondaryDisplayFont());

        mainDisplay.setBackground(theme.displayBackground());
        mainDisplay.setForeground(theme.displayForeground());
        mainDisplay.setFont(theme.displayFont());

        statusLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));
    }

    private void copyDisplayText()
    {
        String selectedText = mainDisplay.getSelectedText();
        String text = selectedText != null && !selectedText.isBlank()
                ? selectedText
                : mainDisplay.getText();

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }

    private void pasteDisplayText()
    {
        if (pasteListener == null)
        {
            return;
        }

        try
        {
            Object data = Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);

            if (data instanceof String text && !text.isBlank())
            {
                pasteListener.accept(text);
            }
        }
        catch (Exception ignored)
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
