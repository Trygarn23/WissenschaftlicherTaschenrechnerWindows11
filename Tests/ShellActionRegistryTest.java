import common.logic.RechnerService;
import modes.wissenschaftlich.logic.WissenschaftlichOperationen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.shell.ShellActionRegistry;

import javax.swing.*;
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
}
