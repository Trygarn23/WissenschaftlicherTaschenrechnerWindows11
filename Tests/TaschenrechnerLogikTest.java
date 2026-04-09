import logic.TaschenrechnerLogik;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaschenrechnerLogikTest {

    private TaschenrechnerLogik logik;

    @BeforeEach
    void setUp() {
        logik = new TaschenrechnerLogik();
    }

    @Test
    void starts_with_zero_display() {
        assertEquals("0", logik.formatLiveAnzeige());
        assertEquals("", logik.getVerlauf());
    }

    @Test
    void typing_digits_builds_expression_and_live_display_formats() {
        logik.eingabeZahl("1");
        logik.eingabeZahl("0");
        logik.eingabeZahl("0");
        logik.eingabeZahl("0");
        assertEquals("1.000", logik.formatLiveAnzeige());
    }

    @Test
    void comma_input_is_prevented_twice_in_same_number() {
        logik.eingabeZahl("1");
        logik.eingabeKomma();
        logik.eingabeZahl("2");
        logik.eingabeKomma(); // should do nothing
        assertEquals("1,2", logik.formatLiveAnzeige());
    }

    @Test
    void operator_normalization_from_ui_symbols() {
        logik.eingabeZahl("6");
        logik.operatorSetzen("×");
        logik.eingabeZahl("7");

        String res = logik.berechne();
        assertEquals("42", res);
        assertTrue(logik.getVerlauf().contains("6×7") || logik.getVerlauf().contains("6*7"),
                "Verlauf should contain original expression or normalized one.");
    }

    @Test
    void calculates_simple_expression_and_updates_history() {
        logik.eingabeZahl("2");
        logik.operatorSetzen("+");
        logik.eingabeZahl("3");

        assertEquals("5", logik.berechne());
        assertEquals("2+3 = 5", logik.getVerlauf());
    }

    @Test
    void equals_then_new_digit_resets_expression() {
        logik.eingabeZahl("2");
        logik.operatorSetzen("+");
        logik.eingabeZahl("3");
        assertEquals("5", logik.berechne());

        logik.eingabeZahl("7"); // should start new expression
        assertEquals("7", logik.formatLiveAnzeige());
    }

    @Test
    void ans_inserts_previous_result() {
        logik.eingabeZahl("2");
        logik.operatorSetzen("+");
        logik.eingabeZahl("3");
        assertEquals("5", logik.berechne());

        logik.operatorSetzen("*");
        logik.ans();

        String res = logik.berechne();
        assertEquals("25", res);
    }

    @Test
    void toggle_sign_on_empty_starts_negative_number() {
        assertEquals("-", logik.wechselVorzeichen());
        logik.eingabeZahl("5");
        assertEquals("-5", logik.formatLiveAnzeige());
    }

    @Test
    void power_operator() {
        logik.eingabeZahl("2");
        logik.potenz();
        logik.eingabeZahl("3");
        assertEquals("8", logik.berechne());
    }

    @Test
    void sqrt_of_negative_is_error() {
        logik.eingabeZahl("-");
        logik.eingabeZahl("9");
        assertEquals("Fehler", logik.wurzel());
    }

    @Test
    void reciprocal_of_zero_is_error() {
        logik.eingabeZahl("0");
        assertEquals("Fehler", logik.reziprok());
    }

    @Test
    void ln_of_non_positive_is_error() {
        logik.eingabeZahl("0");
        assertEquals("Fehler", logik.ln());
    }

    @Test
    void factorial_only_for_non_negative_integers() {
        logik.eingabeZahl("5");
        assertEquals("120", logik.fakultaet());

        logik.allesLoeschen();
        logik.eingabeZahl("2");
        logik.eingabeKomma();
        logik.eingabeZahl("5");
        assertEquals("Fehler", logik.fakultaet());
    }

    @Test
    void trig_depends_on_mode_deg_vs_rad() {
        // DEG: sin(90) = 1
        logik.eingabeZahl("9");
        logik.eingabeZahl("0");
        assertNotEquals("Fehler", logik.sin());
        assertEquals("1", logik.formatLiveAnzeige());

        // RAD: erst pi/2 ausrechnen, dann sin(Ergebnis)
        logik.allesLoeschen();
        logik.toggleWinkelModus(); // RAD

        logik.pi();
        logik.operatorSetzen("/");
        logik.eingabeZahl("2");

        assertNotEquals("Fehler", logik.berechne()); // ergibt ~1,570796...
        assertEquals("1", logik.sin());              // sin(pi/2) = 1
        assertEquals("1", logik.formatLiveAnzeige());
    }

    @Test
    void memory_add_recall_clear() {
        logik.eingabeZahl("1");
        logik.eingabeZahl("0");
        assertEquals("10", logik.formatLiveAnzeige());

        assertEquals("10", logik.memoryAdd());
        logik.allesLoeschen();

        logik.memoryRecall();
        assertEquals("10", logik.formatLiveAnzeige());

        assertEquals("0", logik.memoryClear());
        logik.allesLoeschen();
        logik.memoryRecall();
        assertEquals("0", logik.formatLiveAnzeige());
    }

    @Test
    void set_expression_from_history_result_handles_grouping_and_spaces() {
        logik.setAusdruckVonHistoryResult("  1.234,5  ");
        assertEquals("1.234,5", logik.formatLiveAnzeige());
        assertEquals("1.234,5", logik.berechne()); // berechne() gibt formatiert zurück
    }
}
