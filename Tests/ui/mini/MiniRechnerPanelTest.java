package ui.mini;

import common.state.WinkelModus;
import org.junit.jupiter.api.Test;
import testhilfen.SwingSuche;
import ui.theme.themes.DarkTheme;
import ui.theme.themes.LightTheme;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MiniRechnerPanelTest
{
    @Test
    void tippen_ShouldShowLivePreview()
    {
        // Arrange
        MiniRechnerPanel panel = new MiniRechnerPanel(new DarkTheme(), WinkelModus.DEG, an -> { });

        // Act
        eingabe(panel).setText("2*(3+4");

        // Assert
        assertEquals("= 14", zeile(panel).getText());
    }

    @Test
    void rechne_ShouldReplaceExpressionWithResult()
    {
        // Arrange
        MiniRechnerPanel panel = new MiniRechnerPanel(new DarkTheme(), WinkelModus.DEG, an -> { });
        eingabe(panel).setText("2*(3+4)");

        // Act
        panel.rechne();

        // Assert
        assertEquals("14", eingabe(panel).getText());
        assertEquals("14", panel.getLetztesErgebnis());
        assertEquals("2*(3+4) = 14", zeile(panel).getText());
    }

    @Test
    void rechne_ShouldUseAngleModeFromConstructor()
    {
        // Arrange
        MiniRechnerPanel grad = new MiniRechnerPanel(new DarkTheme(), WinkelModus.DEG, an -> { });
        MiniRechnerPanel bogen = new MiniRechnerPanel(new DarkTheme(), WinkelModus.RAD, an -> { });
        eingabe(grad).setText("sin(90)");
        eingabe(bogen).setText("cos(0)");

        // Act
        grad.rechne();
        bogen.rechne();

        // Assert
        assertEquals("1", grad.getLetztesErgebnis());
        assertEquals("1", bogen.getLetztesErgebnis());
    }

    @Test
    void rechne_ShouldShowErrorAndKeepExpression_WhenInvalid()
    {
        // Arrange
        DarkTheme theme = new DarkTheme();
        MiniRechnerPanel panel = new MiniRechnerPanel(theme, WinkelModus.DEG, an -> { });
        eingabe(panel).setText("5/0");

        // Act
        panel.rechne();

        // Assert
        assertEquals("5/0", eingabe(panel).getText());
        assertEquals("Division durch 0 ist nicht definiert.", zeile(panel).getText());
        assertEquals(theme.dangerBackground(), zeile(panel).getForeground());
        assertEquals("", panel.getLetztesErgebnis());
    }

    @Test
    void immerOben_ShouldReportToggleState()
    {
        // Arrange
        List<Boolean> zustaende = new ArrayList<>();
        MiniRechnerPanel panel = new MiniRechnerPanel(new LightTheme(), WinkelModus.DEG, zustaende::add);

        // Act
        SwingSuche.finde(panel, JToggleButton.class, b -> "Immer oben".equals(b.getText())).doClick();

        // Assert
        assertEquals(List.of(false), zustaende);
    }

    private static JTextField eingabe(MiniRechnerPanel panel)
    {
        return SwingSuche.finde(panel, JTextField.class);
    }

    private static JLabel zeile(MiniRechnerPanel panel)
    {
        return SwingSuche.alle(panel, JLabel.class).getFirst();
    }
}
