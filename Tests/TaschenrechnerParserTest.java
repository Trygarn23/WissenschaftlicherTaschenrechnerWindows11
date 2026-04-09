import logic.TaschenrechnerParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TaschenrechnerParserTest {

    private static final double EPS = 1e-10;

    @ParameterizedTest
    @MethodSource("basicExpressions")
    void evaluates_basic_expressions(String expr, double expected) {
        double actual = TaschenrechnerParser.auswerten(expr, 0.0, TaschenrechnerParser.WinkelModus.DEG);
        assertEquals(expected, actual, EPS, "expr=" + expr);
    }

    static Stream<Object[]> basicExpressions() {
        return Stream.of(
                new Object[]{"1+2", 3.0},
                new Object[]{"2*3+4", 10.0},
                new Object[]{"2*(3+4)", 14.0},
                new Object[]{"10/4", 2.5},
                new Object[]{"2^3^2", 512.0},      // right associative: 2^(3^2)
                new Object[]{"-3+5", 2.0},         // unary number
                new Object[]{"-(3+5)", -8.0},      // unary minus token u-
                new Object[]{"5--3", 8.0},
                new Object[]{"5*-3", -15.0},
                new Object[]{"10%4", 2.0}
        );
    }

    @Test
    void supports_implicit_multiplication_number_parenthesis() {
        double actual = TaschenrechnerParser.auswerten("2(3+4)", 0.0, TaschenrechnerParser.WinkelModus.DEG);
        assertEquals(14.0, actual, EPS);
    }

    @Test
    void supports_implicit_multiplication_constant() {
        double actual = TaschenrechnerParser.auswerten("2pi", 0.0, TaschenrechnerParser.WinkelModus.DEG);
        assertEquals(2.0 * Math.PI, actual, EPS);
    }

    @Test
    void supports_constants_pi_and_e() {
        assertEquals(Math.PI, TaschenrechnerParser.auswerten("pi", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(Math.E, TaschenrechnerParser.auswerten("e", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
    }

    @Test
    void supports_ans_identifier() {
        double actual = TaschenrechnerParser.auswerten("ans+2", 5.0, TaschenrechnerParser.WinkelModus.DEG);
        assertEquals(7.0, actual, EPS);
    }

    @Test
    void supports_decimal_comma_and_dot() {
        assertEquals(3.5, TaschenrechnerParser.auswerten("3,5", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(3.5, TaschenrechnerParser.auswerten("3.5", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
    }

    @Test
    void adds_trailing_zero_if_expression_ends_with_separator() {
        assertEquals(1.2, TaschenrechnerParser.auswerten("1,2", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(1.2, TaschenrechnerParser.auswerten("1,2", 0.0, TaschenrechnerParser.WinkelModus.RAD), EPS);

        // ends with comma => should become "1,0"
        assertEquals(1.0, TaschenrechnerParser.auswerten("1,", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(1.0, TaschenrechnerParser.auswerten("1.", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
    }

    @Test
    void trig_in_deg_mode() {
        assertEquals(0.0, TaschenrechnerParser.auswerten("sin(0)", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(1.0, TaschenrechnerParser.auswerten("sin(90)", 0.0, TaschenrechnerParser.WinkelModus.DEG), 1e-9);
        assertEquals(0.0, TaschenrechnerParser.auswerten("cos(90)", 0.0, TaschenrechnerParser.WinkelModus.DEG), 1e-9);
    }

    @Test
    void trig_in_rad_mode() {
        assertEquals(1.0, TaschenrechnerParser.auswerten("sin(pi/2)", 0.0, TaschenrechnerParser.WinkelModus.RAD), 1e-9);
        assertEquals(0.0, TaschenrechnerParser.auswerten("cos(pi/2)", 0.0, TaschenrechnerParser.WinkelModus.RAD), 1e-9);
    }

    @Test
    void functions_ln_log_sqrt_abs_exp() {
        assertEquals(Math.log(2.0), TaschenrechnerParser.auswerten("ln(2)", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(Math.log10(1000.0), TaschenrechnerParser.auswerten("log(1000)", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(3.0, TaschenrechnerParser.auswerten("sqrt(9)", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(5.0, TaschenrechnerParser.auswerten("abs(-5)", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
        assertEquals(Math.exp(2.0), TaschenrechnerParser.auswerten("exp(2)", 0.0, TaschenrechnerParser.WinkelModus.DEG), EPS);
    }

    @Test
    void rejects_unbalanced_parentheses() {
        assertThrows(IllegalArgumentException.class,
                () -> TaschenrechnerParser.auswerten("(1+2", 0.0, TaschenrechnerParser.WinkelModus.DEG));
        assertThrows(IllegalArgumentException.class,
                () -> TaschenrechnerParser.auswerten("1+2)", 0.0, TaschenrechnerParser.WinkelModus.DEG));
    }

    @Test
    void rejects_unknown_tokens() {
        assertThrows(IllegalArgumentException.class,
                () -> TaschenrechnerParser.auswerten("1+a", 0.0, TaschenrechnerParser.WinkelModus.DEG));
        assertThrows(IllegalArgumentException.class,
                () -> TaschenrechnerParser.auswerten("foo(2)", 0.0, TaschenrechnerParser.WinkelModus.DEG));
    }
}
