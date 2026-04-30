import common.formatting.ZahlenFormatter;
import common.logic.AusdruckEditor;
import common.state.RechnerZustand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AusdruckEditorTest
{
    private RechnerZustand zustand;
    private AusdruckEditor editor;

    @BeforeEach
    void setUp()
    {
        zustand = new RechnerZustand();
        editor = new AusdruckEditor(zustand, new ZahlenFormatter());
    }

    @Test
    void konstanteEinsetzen_ShouldInsertMultiplication_WhenExpressionAlreadyEndsWithValue()
    {
        // Arrange
        editor.eingabeZahl("2");

        // Act
        String expression = editor.konstanteEinsetzen(Math.PI);

        // Assert
        assertTrue(expression.startsWith("2*"));
        assertTrue(expression.contains("3,141592653589793"));
    }

    @Test
    void funktionEinfuegenOderUmklammern_ShouldWrapLastTerm_WhenExpressionEndsWithNumber()
    {
        // Arrange
        editor.eingabeZahl("9");
        editor.eingabeZahl("0");

        // Act
        String expression = editor.funktionEinfuegenOderUmklammern("sin");

        // Assert
        assertEquals("sin(90)", expression);
    }

    @Test
    void funktionEinfuegenOderUmklammern_ShouldOpenFunction_WhenExpressionIsEmpty()
    {
        // Arrange

        // Act
        String expression = editor.funktionEinfuegenOderUmklammern("sin");

        // Assert
        assertEquals("sin(", expression);
    }

    @Test
    void funktionOhneArgumenteEinfuegen_ShouldInsertFunctionCall_WhenExpressionIsEmpty()
    {
        // Arrange

        // Act
        String expression = editor.funktionOhneArgumenteEinfuegen("rand");

        // Assert
        assertEquals("rand()", expression);
    }

    @Test
    void praefixOperatorEinfuegenOderUmklammern_ShouldWrapLastTerm_WhenExpressionEndsWithNumber()
    {
        // Arrange
        editor.eingabeZahl("2");

        // Act
        String expression = editor.praefixOperatorEinfuegenOderUmklammern("10^(");

        // Assert
        assertEquals("10^(2)", expression);
    }

    @Test
    void fakultaet_ShouldReplaceLastNumberWithFactorial_WhenLastNumberIsValidInteger()
    {
        // Arrange
        editor.eingabeZahl("5");

        // Act
        String expression = editor.fakultaet();

        // Assert
        assertEquals("120", expression);
        assertEquals("120", zustand.getAusdruckText());
    }

    @Test
    void fakultaet_ShouldReturnFehler_WhenLastNumberIsDecimal()
    {
        // Arrange
        editor.eingabeZahl("2");
        editor.eingabeKomma();
        editor.eingabeZahl("5");

        // Act
        String expression = editor.fakultaet();

        // Assert
        assertEquals("Fehler", expression);
        assertEquals("", zustand.getAusdruckText());
        assertTrue(zustand.isGleichGedrueckt());
    }

    @Test
    void allesLoeschen_ShouldClearExpressionHistoryAndEqualsState()
    {
        // Arrange
        editor.eingabeZahl("1");
        zustand.setVerlaufText("1+1 = 2");
        zustand.setGleichGedrueckt(true);

        // Act
        String expression = editor.allesLoeschen();

        // Assert
        assertEquals("0", expression);
        assertEquals("", zustand.getAusdruckText());
        assertEquals("", zustand.getVerlaufText());
        assertFalse(zustand.isGleichGedrueckt());
    }
}
