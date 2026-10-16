package modes.wissenschaftlich.logic;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ExakteFormTest
{
    @Test
    void finde_ShouldReturnFraction_WhenValueHasSmallDenominator()
    {
        // Act
        Optional<String> dreiViertel = ExakteForm.finde(0.75);
        Optional<String> drittel = ExakteForm.finde(1.0 / 3);
        Optional<String> negativ = ExakteForm.finde(-2.5);
        Optional<String> rundungsfehler = ExakteForm.finde(0.1 + 0.2);

        // Assert
        assertEquals(Optional.of("3/4"), dreiViertel);
        assertEquals(Optional.of("1/3"), drittel);
        assertEquals(Optional.of("-5/2"), negativ);
        assertEquals(Optional.of("3/10"), rundungsfehler);
    }

    @Test
    void finde_ShouldReturnSquareRoot_WhenSquareIsSimpleFraction()
    {
        // Act
        Optional<String> wurzelZwei = ExakteForm.finde(Math.sqrt(2));
        Optional<String> halbeWurzelZwei = ExakteForm.finde(Math.sqrt(2) / 2);
        Optional<String> wurzelAchtzehn = ExakteForm.finde(Math.sqrt(18));
        Optional<String> negativ = ExakteForm.finde(-Math.sqrt(3) / 2);

        // Assert
        assertEquals(Optional.of("√2"), wurzelZwei);
        assertEquals(Optional.of("√2/2"), halbeWurzelZwei);
        assertEquals(Optional.of("3√2"), wurzelAchtzehn);
        assertEquals(Optional.of("-√3/2"), negativ);
    }

    @Test
    void finde_ShouldReturnMultipleOfPi_WhenValueIsSimpleFractionOfPi()
    {
        // Act
        Optional<String> pi = ExakteForm.finde(Math.PI);
        Optional<String> dreiViertelPi = ExakteForm.finde(3 * Math.PI / 4);
        Optional<String> minusHalbePi = ExakteForm.finde(-Math.PI / 2);

        // Assert
        assertEquals(Optional.of("π"), pi);
        assertEquals(Optional.of("3π/4"), dreiViertelPi);
        assertEquals(Optional.of("-π/2"), minusHalbePi);
    }

    @Test
    void finde_ShouldReturnEmpty_WhenNoSimpleExactFormExists()
    {
        // Act & Assert
        assertTrue(ExakteForm.finde(Math.E).isEmpty());
        assertTrue(ExakteForm.finde(1.23456789).isEmpty());
        assertTrue(ExakteForm.finde(Double.NaN).isEmpty());
    }
}
