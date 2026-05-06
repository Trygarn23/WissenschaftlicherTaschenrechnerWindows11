package modes.programmierer.ui;

import java.awt.Color;

final class ProgrammiererButtonStyler
{
    static final Color MODE_ACTIVE_BG = new Color(0, 145, 210);
    static final Color MODE_INACTIVE_BG = new Color(34, 39, 52);
    static final Color MODE_BORDER = new Color(58, 66, 84);
    static final Color DISABLED_BG = new Color(35, 35, 35);
    static final Color DISABLED_FG = new Color(105, 105, 105);

    private ProgrammiererButtonStyler()
    {
    }

    static Color buttonBackground(String text)
    {
        if (text.matches("\\d"))
        {
            return new Color(45, 45, 45);
        }

        if ("+-".contains(text))
        {
            return new Color(173, 41, 99);
        }

        if (text.equals("CLR") || text.equals("←"))
        {
            return new Color(100, 60, 60);
        }

        if (text.equals("NOT") || text.equals("AND") || text.equals("OR") || text.equals("XOR")
                || text.equals("<<") || text.equals(">>") || text.equals(">>>"))
        {
            return new Color(173, 41, 99);
        }

        if (text.equals("="))
        {
            return new Color(70, 70, 70);
        }

        return new Color(60, 60, 60);
    }

    static Color buttonForeground(String text)
    {
        if ("+-".contains(text) || text.equals("NOT") || text.equals("AND") || text.equals("OR")
                || text.equals("XOR") || text.equals("<<") || text.equals(">>") || text.equals(">>>"))
        {
            return Color.BLACK;
        }

        return Color.WHITE;
    }

    static Color brighten(Color color, int amount)
    {
        return new Color(
                Math.min(255, color.getRed() + amount),
                Math.min(255, color.getGreen() + amount),
                Math.min(255, color.getBlue() + amount)
        );
    }

    static Color darken(Color color, int amount)
    {
        return new Color(
                Math.max(0, color.getRed() - amount),
                Math.max(0, color.getGreen() - amount),
                Math.max(0, color.getBlue() - amount)
        );
    }
}
