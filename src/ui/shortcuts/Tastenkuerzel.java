package ui.shortcuts;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

/**
 * Einzige Quelle für die Tastenkürzel der Rechner-Buttons.
 * {@code aktion} ist gleichzeitig Button-Text und Name in der {@code ShellActionRegistry},
 * deshalb nutzen Tastaturbindung und Tooltips dieselbe Tabelle und können nicht auseinanderlaufen.
 */
public enum Tastenkuerzel
{
    KOMMA(",", ", oder .", KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD, KeyEvent.VK_DECIMAL),
    PLUS("+", "+ oder Num +", KeyEvent.VK_PLUS, KeyEvent.VK_ADD),
    MINUS("-", "- oder Num -", KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT),
    MAL("×", "* oder Num *", KeyEvent.VK_ASTERISK, KeyEvent.VK_MULTIPLY),
    GETEILT("÷", "/ oder Num /", KeyEvent.VK_SLASH, KeyEvent.VK_DIVIDE),
    MODULO("mod", "% oder P", KeyEvent.VK_P),
    GLEICH("=", "Enter", KeyEvent.VK_ENTER),
    ZURUECK("←", "Backspace", KeyEvent.VK_BACK_SPACE);

    /** Wird nicht von der Shell gebunden, sondern vom Programmierer-Panel selbst. */
    public static final String PROGRAMMIERER_CLR_TEXT = "Esc";

    private final String aktion;
    private final String anzeigeText;
    private final int[] tastenCodes;

    Tastenkuerzel(String aktion, String anzeigeText, int... tastenCodes)
    {
        this.aktion = aktion;
        this.anzeigeText = anzeigeText;
        this.tastenCodes = tastenCodes;
    }

    public String getAktion()
    {
        return aktion;
    }

    public String getAnzeigeText()
    {
        return anzeigeText;
    }

    public int[] getTastenCodes()
    {
        return tastenCodes.clone();
    }

    public static Optional<Tastenkuerzel> fuerAktion(String aktion)
    {
        return Arrays.stream(values()).filter(kuerzel -> kuerzel.aktion.equals(aktion)).findFirst();
    }

    public static String ziffernText(String ziffer)
    {
        return ziffer + " oder Num " + ziffer;
    }
}
