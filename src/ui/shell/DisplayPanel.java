package ui.shell;

import common.logic.KlammerPaare;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.animation.AnimationSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class DisplayPanel extends JPanel
{
    private static final int MAIN_TEXT_COMPACT_LIMIT = 18;
    private static final int MAIN_TEXT_TINY_LIMIT = 32;
    private static final float TINY_FONT_MIN = 22f;
    private static final float TINY_FONT_SHRINK = 20f;
    private static final float COMPACT_FONT_MIN = 28f;
    private static final float COMPACT_FONT_SHRINK = 12f;
    private static final int MIN_HEIGHT = 112;
    private static final int PREFERRED_HEIGHT = 148;
    private static final double ERROR_HIGHLIGHT_BLEND = 0.35;
    private static final int SUCCESS_PULSE_MS = 220;
    private static final int ERROR_PULSE_MS = 240;

    private final JTextPane recDisplay = new JTextPane();
    private final JTextPane mainDisplay = new JTextPane();
    private final JLabel statusLabel = new JLabel("Modus: Standard | Winkel: DEG | Speicher: leer");
    private final JPanel displayArea = new JPanel(new BorderLayout(0, 4));

    private static final Color ERROR_COLOR = new Color(220, 50, 50);

    private Consumer<String> pasteListener;
    private IntConsumer cursorListener;
    private AppTheme currentTheme;
    private boolean cursorVisible;

    public DisplayPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setMinimumSize(new Dimension(0, MIN_HEIGHT));
        setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));

        configurePane(recDisplay);
        configurePane(mainDisplay);
        configureStatusLabel();
        configureClipboardActions();
        setToolTipText("Display anklicken: Strg+C kopiert, Strg+V fügt einen Ausdruck ein");
        recDisplay.setToolTipText(getToolTipText());
        mainDisplay.setToolTipText(getToolTipText());

        mainDisplay.setText("0");
        configureCursor();

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

        // Nicht editierbare Textpanes behalten sonst ihre Editor-Aktionen und
        // schlucken Enter/Backspace, bevor die Rechnertasten am RootPane greifen.
        for (int key : new int[]{KeyEvent.VK_ENTER, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                KeyEvent.VK_HOME, KeyEvent.VK_END, KeyEvent.VK_DELETE})
        {
            pane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(key, 0), "rechnerTaste");
        }

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(0, doc.getLength(), right, false);
    }

    /**
     * Das Display ist nicht editierbar, deshalb zeigt Swing keinen Caret. Dieser hier ist sichtbar,
     * sobald der Cursor mitten im Ausdruck steht; ein Klick meldet die Stelle an den Rechner.
     */
    private void configureCursor()
    {
        mainDisplay.setCaret(new DefaultCaret()
        {
            @Override
            public boolean isVisible()
            {
                return cursorVisible;
            }
        });
        mainDisplay.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                // Mit der Maus markieren bleibt zum Kopieren erhalten.
                if (cursorListener != null && SwingUtilities.isLeftMouseButton(e)
                        && mainDisplay.getSelectionStart() == mainDisplay.getSelectionEnd())
                {
                    cursorListener.accept(mainDisplay.getCaretPosition());
                }
            }
        });
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
        setMainText(text, -1);
    }

    /** @param cursorIndex Stelle im angezeigten Text für den Eingabecursor, -1 = am Ende (dann unsichtbar) */
    public void setMainText(String text, int cursorIndex)
    {
        mainDisplay.setText(text);
        // Sonst gehen alte Klammerfarben und Fehlermarkierungen auf den neuen Text über.
        mainDisplay.getStyledDocument().setCharacterAttributes(0, mainDisplay.getDocument().getLength(), SimpleAttributeSet.EMPTY, true);
        highlightBrackets();
        cursorVisible = cursorIndex >= 0 && cursorIndex < mainDisplay.getDocument().getLength();
        mainDisplay.setCaretPosition(cursorVisible ? cursorIndex : mainDisplay.getDocument().getLength());
        mainDisplay.repaint();
        updateMainDisplayFont();
    }

    /** Hinterlegt das Zeichen an der Fehlerstelle rot; eine Stelle hinter dem Ende markiert das letzte Zeichen. */
    public void markError(int index)
    {
        int length = mainDisplay.getDocument().getLength();
        if (index < 0 || length == 0)
        {
            return;
        }

        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setUnderline(style, true);
        StyleConstants.setForeground(style, ERROR_COLOR);
        if (currentTheme != null)
        {
            StyleConstants.setBackground(style, currentTheme.blend(currentTheme.displayBackground(), ERROR_COLOR, ERROR_HIGHLIGHT_BLEND));
        }
        mainDisplay.getStyledDocument().setCharacterAttributes(Math.min(index, length - 1), 1, style, false);
    }

    /** Wird mit der angeklickten Stelle im angezeigten Text aufgerufen. */
    public void setCursorListener(IntConsumer cursorListener)
    {
        this.cursorListener = cursorListener;
    }

    /** Färbt zusammengehörige Klammern je Verschachtelungstiefe gleich ein. */
    private void highlightBrackets()
    {
        if (currentTheme == null)
        {
            return;
        }

        Color[] colors = {currentTheme.graphYAchseColor(), currentTheme.graphWendestelleColor(), currentTheme.graphExtremumColor()};
        StyledDocument doc = mainDisplay.getStyledDocument();
        int[] depths = KlammerPaare.tiefen(mainDisplay.getText());
        for (int i = 0; i < depths.length && i < doc.getLength(); i++)
        {
            if (depths[i] < 0)
            {
                continue;
            }
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setForeground(style, colors[depths[i] % colors.length]);
            StyleConstants.setBold(style, true);
            doc.setCharacterAttributes(i, 1, style, false);
        }
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
        this.currentTheme = theme;

        displayArea.setBackground(theme.displayBackground());
        displayArea.setBorder(ModernButtonStyler.cardBorder(theme));

        recDisplay.setBackground(theme.displayBackground());
        recDisplay.setForeground(theme.secondaryDisplayForeground());
        recDisplay.setFont(theme.secondaryDisplayFont());
        // Die rechtsbündige Textpane meldet nach dem ersten Layout die Höhe 0, dann wäre die zweite Zeile
        // (Vorschau, offene Klammern, Fehlermeldung) unsichtbar. Deshalb feste Höhe aus der Schrift.
        recDisplay.setPreferredSize(new Dimension(0, recDisplay.getFontMetrics(recDisplay.getFont()).getHeight()));

        mainDisplay.setBackground(theme.displayBackground());
        mainDisplay.setForeground(theme.displayForeground());
        mainDisplay.setCaretColor(theme.displayForeground());

        statusLabel.setForeground(theme.secondaryDisplayForeground());
        statusLabel.setFont(theme.secondaryDisplayFont().deriveFont(Font.PLAIN, 13f));
        updateMainDisplayFont();
        highlightBrackets();
    }

    public void pulseSuccess()
    {
        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(displayArea, currentTheme.successPulseColor(), SUCCESS_PULSE_MS);
        }
    }

    public void pulseError()
    {
        if (currentTheme != null)
        {
            AnimationSupport.pulseBackground(displayArea, currentTheme.errorPulseColor(), ERROR_PULSE_MS);
        }
    }

    public int getMainFontSize()
    {
        return mainDisplay.getFont().getSize();
    }

    private void updateMainDisplayFont()
    {
        if (currentTheme == null)
        {
            return;
        }

        Font displayFont = currentTheme.displayFont();
        int length = mainDisplay.getText() == null ? 0 : mainDisplay.getText().length();

        if (length > MAIN_TEXT_TINY_LIMIT)
        {
            mainDisplay.setFont(displayFont.deriveFont(Math.max(TINY_FONT_MIN, displayFont.getSize2D() - TINY_FONT_SHRINK)));
        }
        else if (length > MAIN_TEXT_COMPACT_LIMIT)
        {
            mainDisplay.setFont(displayFont.deriveFont(Math.max(COMPACT_FONT_MIN, displayFont.getSize2D() - COMPACT_FONT_SHRINK)));
        }
        else
        {
            mainDisplay.setFont(displayFont);
        }
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
        catch (UnsupportedFlavorException | IOException | IllegalStateException ignored)
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
