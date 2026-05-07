import modes.komplex.model.KomplexeZahl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KomplexeZahlTest
{
    @Test
    void operations_ShouldCalculateBasicComplexArithmetic()
    {
        // Arrange
        KomplexeZahl a = new KomplexeZahl(3, 2);
        KomplexeZahl b = new KomplexeZahl(1, -4);

        // Act
        KomplexeZahl sum = a.addiere(b);
        KomplexeZahl diff = a.subtrahiere(b);
        KomplexeZahl product = a.multipliziere(b);
        KomplexeZahl quotient = a.dividiere(b);

        // Assert
        assertComplex(4, -2, sum);
        assertComplex(2, 6, diff);
        assertComplex(11, -10, product);
        assertComplex(-5.0 / 17.0, 14.0 / 17.0, quotient);
    }

    @Test
    void dividiere_ShouldThrow_WhenDivisorIsZero()
    {
        // Arrange
        KomplexeZahl a = new KomplexeZahl(1, 2);

        // Act & Assert
        assertThrows(ArithmeticException.class, () -> a.dividiere(new KomplexeZahl(0, 0)));
    }

    @Test
    void polarAndConjugate_ShouldUseMathHypotAndAtan2Semantics()
    {
        // Arrange
        KomplexeZahl z = new KomplexeZahl(3, 4);

        // Act
        KomplexeZahl conjugate = z.konjugiert();
        KomplexeZahl fromPolar = KomplexeZahl.ausPolar(5, Math.atan2(4, 3));

        // Assert
        assertEquals(5, z.betrag(), 1e-10);
        assertEquals(Math.atan2(4, 3), z.phaseRad(), 1e-10);
        assertComplex(3, -4, conjugate);
        assertComplex(3, 4, fromPolar);
    }

    private void assertComplex(double real, double imag, KomplexeZahl actual)
    {
        assertEquals(real, actual.getReal(), 1e-10);
        assertEquals(imag, actual.getImaginaer(), 1e-10);
    }
}
