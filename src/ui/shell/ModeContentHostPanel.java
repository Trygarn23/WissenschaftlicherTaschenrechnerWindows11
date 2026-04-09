package ui.shell;

import ui.modes.RechnerModus;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class ModeContentHostPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final Map<RechnerModus, JPanel> modePanels = new EnumMap<>(RechnerModus.class);

    public ModeContentHostPanel() {
        setLayout(cardLayout);
        setOpaque(false);
    }

    public void registerMode(RechnerModus modus, JPanel panel) {
        modePanels.put(modus, panel);
        add(panel, modus.name());
    }

    public void showMode(RechnerModus modus) {
        cardLayout.show(this, modus.name());
    }

    public JPanel getModePanel(RechnerModus modus) {
        return modePanels.get(modus);
    }
}