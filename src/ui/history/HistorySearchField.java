package ui.history;

import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Suchfeld des Verlaufs mit grauem Platzhaltertext, solange nichts eingegeben ist. */
class HistorySearchField extends JTextField
{
    static final String PLACEHOLDER = "Suche...";

    private AppTheme theme;

    HistorySearchField(Runnable onChange)
    {
        setFont(AppFonts.normal(14));
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        setOpaque(true);
        setToolTipText("Sucht in Ausdruck, Ergebnis und Modus; mit Punkt oder Doppelpunkt auch nach Datum/Uhrzeit");
        showPlaceholder();

        addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (PLACEHOLDER.equals(getText()))
                {
                    setText("");
                    setForeground(textColor());
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (getText().isBlank())
                {
                    showPlaceholder();
                }
            }
        });

        getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                onChange.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                onChange.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                onChange.run();
            }
        });
    }

    void clear()
    {
        if (isFocusOwner())
        {
            setText("");
            setForeground(textColor());
        }
        else
        {
            showPlaceholder();
        }
    }

    void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        ModernButtonStyler.styleInput(this, theme);
        setCaretColor(theme.historyForeground());
        setForeground(PLACEHOLDER.equals(getText()) ? theme.placeholderForeground() : theme.historyForeground());
    }

    private void showPlaceholder()
    {
        setText(PLACEHOLDER);
        setForeground(theme != null ? theme.placeholderForeground() : Color.GRAY);
    }

    private Color textColor()
    {
        return theme != null ? theme.historyForeground() : Color.WHITE;
    }
}
