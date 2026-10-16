package ui.shell;

import common.logic.RechnerService;
import modes.standard.ui.StandardPanel;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.shell.ShellActionRegistry;
import ui.shortcuts.Tastenkuerzel;
import ui.tooltips.ButtonTooltips;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ShellActionRegistryTest
{
    private RechnerService rechner;
    private AtomicInteger refreshCount;
    private AtomicReference<String> extraInfo;
    private ShellActionRegistry registry;

    @BeforeEach
    void setUp()
    {
        rechner = new RechnerService();
        WissenschaftlichOperationen wissenschaftlich = new WissenschaftlichOperationen(rechner.getAusdruckEditor());
        refreshCount = new AtomicInteger(0);
        extraInfo = new AtomicReference<>("");
        registry = new ShellActionRegistry(
                rechner,
                wissenschaftlich,
                refreshCount::incrementAndGet,
                extraInfo::set,
                () -> {
                    rechner.berechne();
                    refreshCount.incrementAndGet();
                });
    }

    @Test
    void tastenkuerzel_ShouldOnlyPointToExistingActionsWithTooltip()
    {
        // Arrange & Act & Assert
        for (Tastenkuerzel kuerzel : Tastenkuerzel.values())
        {
            assertTrue(registry.kennt(kuerzel.getAktion()), "Keine Aktion für " + kuerzel);
            assertTrue(ButtonTooltips.textFor(kuerzel.getAktion()).endsWith("(Taste: " + kuerzel.getAnzeigeText() + ")"),
                    "Tooltip zeigt Tastenkürzel nicht an: " + kuerzel);
        }
    }

    @Test
    void ausfuehren_ShouldHandleDigitsAndReportUnknownActions()
    {
        // Act
        boolean ziffer = registry.ausfuehren("7");
        boolean unbekannt = registry.ausfuehren("gibtsNicht");

        // Assert
        assertTrue(ziffer);
        assertFalse(unbekannt);
        assertEquals("7", rechner.formatiereLiveAnzeige());
        assertEquals(1, refreshCount.get());
    }

    @Test
    void handleButton_ShouldRouteStandardPercentButtonToPercentFunction()
    {
        // Arrange
        JButton fiveButton = new JButton("5");
        JButton zeroButton = new JButton("0");
        JButton percentButton = new JButton("%");

        // Act
        registry.handleButton(fiveButton);
        registry.handleButton(zeroButton);
        registry.handleButton(percentButton);

        // Assert
        assertEquals("0,5", rechner.formatiereLiveAnzeige());
        assertEquals(3, refreshCount.get());
    }

    @Test
    void handleButton_ShouldRouteModuloButtonToModuloOperator()
    {
        // Arrange
        JButton tenButton = new JButton("1");
        JButton zeroButton = new JButton("0");
        JButton modButton = new JButton("mod");
        JButton fourButton = new JButton("4");
        JButton equalsButton = new JButton("=");

        // Act
        registry.handleButton(tenButton);
        registry.handleButton(zeroButton);
        registry.handleButton(modButton);
        registry.handleButton(fourButton);
        registry.handleButton(equalsButton);

        // Assert
        assertEquals("2", rechner.formatiereLiveAnzeige());
    }

    @Test
    void handleScientificMenuAction_ShouldRouteFunctionSelectionToScientificOperations()
    {
        // Arrange
        registry.handleButton(new JButton("9"));
        registry.handleButton(new JButton("0"));

        // Act
        registry.handleScientificMenuAction("sin");
        String result = rechner.berechne();

        // Assert
        assertEquals("1", result);
    }

    @Test
    void memoryActions_ShouldExposeMemoryFeedback_WhenMemoryButtonsAreUsed()
    {
        // Arrange
        registry.handleButton(new JButton("1"));
        registry.handleButton(new JButton("0"));

        // Act
        registry.handleButton(new JButton("M+"));
        registry.handleButton(new JButton("C"));
        registry.handleButton(new JButton("MR"));

        // Assert
        assertEquals("M = 10", extraInfo.get());
        assertEquals("10", rechner.formatiereLiveAnzeige());
    }

    @Test
    void attachCalculatorButtonActions_ShouldAttachActionToEveryStandardButton()
    {
        // Arrange
        StandardPanel panel = new StandardPanel();

        // Act
        registry.attachCalculatorButtonActions(panel);

        // Assert
        assertAllStandardButtonsHaveActions(panel);
    }

    @Test
    void sdTaste_ShouldToggleBetweenExactFormAndDecimal_WhenPressedTwice()
    {
        // Arrange
        rechner.setAusdruckText("3÷4");
        rechner.berechne();

        // Act
        registry.ausfuehren("S⇔D");
        String exakt = extraInfo.get();
        int refreshVorher = refreshCount.get();
        registry.ausfuehren("S⇔D");

        // Assert
        assertEquals("= 3/4", exakt);
        assertEquals(refreshVorher + 1, refreshCount.get());
    }

    @Test
    void stoUndRcl_ShouldStoreResultAndInsertItAsNumber()
    {
        // Arrange
        rechner.setAusdruckText("2+3");
        rechner.berechne();

        // Act
        registry.ausfuehren("STO A");
        String gespeichert = extraInfo.get();
        registry.ausfuehren("C");
        registry.ausfuehren("2");
        registry.ausfuehren("+");
        registry.ausfuehren("RCL A");

        // Assert
        assertEquals("A = 5", gespeichert);
        assertEquals("2+5", rechner.formatiereLiveAnzeige());
        assertEquals("7", rechner.berechne());
    }

    @Test
    void rcl_ShouldShowHint_WhenVariableIsEmpty()
    {
        // Act
        registry.ausfuehren("RCL F");

        // Assert
        assertEquals("F ist leer", extraInfo.get());
        assertEquals("0", rechner.formatiereLiveAnzeige());
    }

    @Test
    void mehrStellen_ShouldShowFullPrecision_WithoutChangingSetting()
    {
        // Arrange
        rechner.setNachkommastellen(2);
        rechner.setAusdruckText("1÷3");
        rechner.berechne();

        // Act
        registry.ausfuehren("Mehr Stellen");

        // Assert
        assertEquals("= 0,3333333333333333", extraInfo.get());
        assertEquals(2, rechner.getNachkommastellen());
    }

    @Test
    void neueFunktionsmenueAktionen_ShouldExistAndHaveTooltips()
    {
        // Act & Assert
        for (String aktion : new String[]{"S⇔D", "Mehr Stellen", "STO A", "RCL F"})
        {
            assertTrue(registry.kennt(aktion), "Keine Aktion für " + aktion);
        }
        for (String taste : new String[]{"S⇔D", "STO", "RCL", "Mehr Stellen"})
        {
            assertNotNull(ButtonTooltips.textFor(taste), "Kein Tooltip für " + taste);
        }
    }

    private void assertAllStandardButtonsHaveActions(Container container)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton button)
            {
                assertTrue(button.getActionListeners().length > 0, "Missing action for button: " + button.getText());
            }

            if (component instanceof Container child)
            {
                assertAllStandardButtonsHaveActions(child);
            }
        }
    }

    @Test
    void ausfuehren_ShouldEditAtCursor_WhenCursorWasMovedLeft()
    {
        // Arrange
        rechner.setAusdruckText("12");

        // Act
        registry.ausfuehren(ShellActionRegistry.CURSOR_LINKS);
        registry.ausfuehren("5");
        registry.ausfuehren("←");
        registry.ausfuehren("+");

        // Assert
        assertEquals("1+2", rechner.getAusdruckText());
    }

    @Test
    void ausfuehren_ShouldClearWholeExpression_WhenCIsPressedWithCursorInside()
    {
        // Arrange
        rechner.setAusdruckText("12+3");
        registry.ausfuehren(ShellActionRegistry.CURSOR_LINKS);
        registry.ausfuehren(ShellActionRegistry.CURSOR_LINKS);

        // Act
        registry.ausfuehren("C");
        registry.ausfuehren("7");

        // Assert
        assertEquals("7", rechner.getAusdruckText());
        assertEquals(-1, rechner.cursorAnzeigeIndex());
    }
}
