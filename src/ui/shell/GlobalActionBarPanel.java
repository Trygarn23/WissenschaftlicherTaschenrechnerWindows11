package ui.shell;

import ui.theme.AppTheme;
import ui.theme.ThemeType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GlobalActionBarPanel extends JPanel
{
    private final JLabel titleLabel = new JLabel("Taschenrechner");
    private final JButton angleModeButton = new JButton("DEG");
    private final JButton themeButton = new JButton("ui/Theme");
    private final JButton settingsButton = new JButton("⚙");

    private final JPopupMenu themePopupMenu = new JPopupMenu();
    private final Map<ThemeType, JButton> themeOptionButtons = new LinkedHashMap<>();

    private Consumer<ThemeType> themeSelectionListener;

    public GlobalActionBarPanel()
    {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsPanel.setOpaque(false);

        angleModeButton.setFocusable(false);
        themeButton.setFocusable(false);
        settingsButton.setFocusable(false);
        settingsButton.setToolTipText("Einstellungen öffnen");

        buildThemePopup();

        themeButton.addActionListener(e -> themePopupMenu.show(themeButton, 0, themeButton.getHeight()));

        actionsPanel.add(angleModeButton);
        actionsPanel.add(themeButton);
        actionsPanel.add(settingsButton);

        add(titleLabel, BorderLayout.WEST);
        add(actionsPanel, BorderLayout.EAST);
    }

    private void buildThemePopup()
    {
        JPanel popupContent = new JPanel(new BorderLayout(0, 10));
        popupContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        popupContent.setBackground(new Color(28, 28, 28));

        JLabel popupTitle = new JLabel("Theme auswählen");
        popupTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        popupTitle.setForeground(Color.WHITE);

        JPanel themeGrid = new JPanel(new GridLayout(0, 2, 8, 8));
        themeGrid.setOpaque(false);

        addThemeOption(themeGrid, ThemeType.DARK, "Dark");
        addThemeOption(themeGrid, ThemeType.LIGHT, "Light");
        addThemeOption(themeGrid, ThemeType.NEON, "Neon");
        addThemeOption(themeGrid, ThemeType.MATRIX, "Matrix");
        addThemeOption(themeGrid, ThemeType.WIN95, "Win95");
        addThemeOption(themeGrid, ThemeType.WIN11, "Win11");

        popupContent.add(popupTitle, BorderLayout.NORTH);
        popupContent.add(themeGrid, BorderLayout.CENTER);

        themePopupMenu.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55), 1));
        themePopupMenu.add(popupContent);
    }

    private void addThemeOption(JPanel parent, ThemeType themeType, String label)
    {
        JButton optionButton = new JButton(label);
        optionButton.setFocusable(false);
        optionButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optionButton.setBackground(new Color(55, 55, 55));
        optionButton.setForeground(Color.WHITE);
        optionButton.setBorderPainted(false);
        optionButton.setOpaque(true);
        optionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        optionButton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        optionButton.addActionListener(e -> {
            themePopupMenu.setVisible(false);
            if (themeSelectionListener != null)
            {
                themeSelectionListener.accept(themeType);
            }
        });

        themeOptionButtons.put(themeType, optionButton);
        parent.add(optionButton);
    }

    public void setAngleModeText(String text)
    {
        angleModeButton.setText(text);
    }

    public void setThemeButtonText(String text)
    {
        themeButton.setText("Theme: " + text);
    }

    public void setAngleModeListener(java.awt.event.ActionListener listener)
    {
        angleModeButton.addActionListener(listener);
    }

    public void setThemeSelectionListener(Consumer<ThemeType> listener)
    {
        this.themeSelectionListener = listener;
    }

    public void setSettingsListener(java.awt.event.ActionListener listener)
    {
        settingsButton.addActionListener(listener);
    }

    public void highlightSelectedTheme(ThemeType selectedTheme)
    {
        for (Map.Entry<ThemeType, JButton> entry : themeOptionButtons.entrySet())
        {
            boolean selected = entry.getKey() == selectedTheme;
            JButton button = entry.getValue();

            if (selected)
            {
                button.setBackground(new Color(24, 153, 219));
                button.setForeground(Color.WHITE);
            } else
            {
                button.setBackground(new Color(55, 55, 55));
                button.setForeground(Color.WHITE);
            }
        }
    }

    public void applyTheme(AppTheme theme)
    {
        setBackground(theme.windowBackground());

        titleLabel.setForeground(theme.displayForeground());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        styleActionButton(angleModeButton, theme);
        styleActionButton(themeButton, theme);
        styleIconButton(settingsButton, theme);
    }

    private void styleActionButton(JButton button, AppTheme theme)
    {
        button.setFont(theme.buttonFont());
        button.setBackground(theme.toggleButtonBackground());
        button.setForeground(theme.toggleButtonForeground());
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    private void styleIconButton(JButton button, AppTheme theme)
    {
        styleActionButton(button, theme);
        button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setPreferredSize(new Dimension(42, 42));
        button.setMinimumSize(new Dimension(42, 42));
    }
}
