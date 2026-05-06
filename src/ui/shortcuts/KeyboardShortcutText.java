package ui.shortcuts;

public final class KeyboardShortcutText
{
    public static final String DIGIT_PREFIX = " oder Num ";
    public static final String COMMA = ", oder .";
    public static final String PLUS = "+ oder Num +";
    public static final String MINUS = "- oder Num -";
    public static final String MULTIPLY = "* oder Num *";
    public static final String DIVIDE = "/ oder Num /";
    public static final String MODULO = "% oder P";
    public static final String ENTER = "Enter";
    public static final String BACKSPACE = "Backspace";
    public static final String ESCAPE = "Esc";
    public static final String COPY = "Strg+C";
    public static final String PASTE = "Strg+V";

    private KeyboardShortcutText()
    {
    }

    public static String digit(String digit)
    {
        return digit + DIGIT_PREFIX + digit;
    }
}
