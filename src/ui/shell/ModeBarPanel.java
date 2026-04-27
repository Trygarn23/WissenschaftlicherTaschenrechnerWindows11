package ui.shell;

import ui.theme.AppTheme;
import common.state.RechnerModus;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModeBarPanel extends JPanel {
    private final Map<RechnerModus, JButton> buttons = new EnumMap<>(RechnerModus.class);
    private Consumer<RechnerModus> modeListener;

    public ModeBarPanel() {
        setLayout(new GridLayout(1, 5, 8, 0));
        setOpaque(true);

        for (RechnerModus modus : RechnerModus.values()) {
            JButton button = createModeButton(modus);
            buttons.put(modus, button);
            add(button);
        }
    }

    private JButton createModeButton(RechnerModus modus) {
        JButton button = new JButton(modus.getLabel());
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));

        button.addActionListener(e -> {
            if (modeListener != null) {
                modeListener.accept(modus);
            }
        });

        return button;
    }

    public void setModeListener(Consumer<RechnerModus> modeListener) {
        this.modeListener = modeListener;
    }

    public void setSelectedMode(RechnerModus aktuellerModus, AppTheme theme) {
        for (Map.Entry<RechnerModus, JButton> entry : buttons.entrySet()) {
            boolean aktiv = entry.getKey() == aktuellerModus;
            JButton button = entry.getValue();

            button.setBackground(
                    aktiv
                            ? theme.modeButtonActiveBackground()
                            : theme.modeButtonInactiveBackground()
            );
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme.modeBorder(), 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));
        }

        setBackground(theme.modeBarBackground());
    }
}