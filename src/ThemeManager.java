import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemeManager
{
    public static final Color DARK_BG = new Color(25, 25, 25);
    public static final Color PLACEHOLDER_FG = new Color(140, 140, 140);

    private static final String BASE_COLOR_KEY = "baseColor";

    public void styleButton(JButton btn, String text)
    {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);

        Color baseColor;
        Color textColor = Color.WHITE;

        if (text.matches("\\d"))
        {
            baseColor = new Color(45, 45, 45);
        }
        else if ("+-×÷".contains(text))
        {
            baseColor = new Color(173, 41, 99);
            textColor = Color.BLACK;
        }
        else if (text.equals("C") || text.equals("CE") || text.equals("←"))
        {
            baseColor = new Color(100, 60, 60);
        }
        else if (text.equals("Dark") || text.equals("Light"))
        {
            baseColor = new Color(70, 70, 120);
        }
        else if (text.equals("DEG") || text.equals("RAD"))
        {
            baseColor = new Color(80, 100, 140);
        }
        else
        {
            baseColor = new Color(60, 60, 60);
        }

        btn.setBackground(baseColor);
        btn.setForeground(textColor);
        btn.putClientProperty(BASE_COLOR_KEY, baseColor);

        btn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                btn.setBackground(dunkelColor(baseColor, 25));
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                btn.setBackground(helleColor(baseColor, 20));
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                btn.setBackground(helleColor(baseColor, 20));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                btn.setBackground((Color) btn.getClientProperty(BASE_COLOR_KEY));
            }
        });
    }

    public void resetButtonColors(Container buttonPanel)
    {
        for (Component c : buttonPanel.getComponents())
        {
            if (c instanceof JButton b)
            {
                Color base = (Color) b.getClientProperty(BASE_COLOR_KEY);
                if (base != null)
                {
                    b.setBackground(base);
                }
            }
        }
    }

    public void applyHistoryColors(
            boolean darkMode,
            JList<String> historyList,
            JScrollPane historyScroll,
            JButton clearHistoryBtn,
            JTextField historySearchField,
            String searchPlaceholder
    )
    {
        Color bg = darkMode ? DARK_BG : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        historyList.setBackground(bg);
        historyList.setForeground(fg);
        historyList.setSelectionBackground(helleColor(bg, 30));
        historyList.setSelectionForeground(fg);

        historyScroll.getViewport().setBackground(bg);

        clearHistoryBtn.setBackground(darkMode ? new Color(60, 60, 60) : new Color(220, 220, 220));
        clearHistoryBtn.setForeground(fg);
        clearHistoryBtn.setBorderPainted(false);
        clearHistoryBtn.setOpaque(true);
        clearHistoryBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        historySearchField.setBackground(darkMode ? new Color(35, 35, 35) : new Color(245, 245, 245));
        historySearchField.setCaretColor(fg);

        if (searchPlaceholder.equals(historySearchField.getText()))
        {
            historySearchField.setForeground(PLACEHOLDER_FG);
        }
        else
        {
            historySearchField.setForeground(fg);
        }
    }

    public Color helleColor(Color c, int amount)
    {
        return new Color(
                Math.min(255, c.getRed() + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount)
        );
    }

    public Color dunkelColor(Color c, int amount)
    {
        return new Color(
                Math.max(0, c.getRed() - amount),
                Math.max(0, c.getGreen() - amount),
                Math.max(0, c.getBlue() - amount)
        );
    }

    public Color lerp(Color a, Color b, float t)
    {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }
}