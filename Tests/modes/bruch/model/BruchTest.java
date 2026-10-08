package modes.bruch.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BruchTest
{
    @Test
    void konstruktor_ShouldKuerzenUndVorzeichenNormalisieren()
    {
        // Act & Assert
        assertEquals(new Bruch(1, 2), new Bruch(2, 4));
        assertEquals("-1/2", new Bruch(1, -2).toString());
        assertEquals("1/2", new Bruch(-3, -6).toString());
        assertEquals("0", new Bruch(0, -7).toString());
        assertEquals(1, new Bruch(0, -7).nenner());
        assertEquals("5", new Bruch(10, 2).toString());
    }

    @Test
    void konstruktor_ShouldRejectNennerNull()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class, () -> new Bruch(1, 0));

        // Assert
        assertEquals("Der Nenner darf nicht 0 sein.", fehler.getMessage());
    }

    @Test
    void grundrechenarten_ShouldRechnenExakt()
    {
        // Arrange
        Bruch a = new Bruch(3, 4);
        Bruch b = new Bruch(1, 6);

        // Act & Assert
        assertEquals(new Bruch(11, 12), a.plus(b));
        assertEquals(new Bruch(7, 12), a.minus(b));
        assertEquals(new Bruch(1, 8), a.mal(b));
        assertEquals(new Bruch(9, 2), a.durch(b));
        assertEquals(new Bruch(-1, 1), new Bruch(1, 3).minus(new Bruch(4, 3)));
        assertEquals(Bruch.ganz(1), new Bruch(1, 3).plus(new Bruch(1, 3)).plus(new Bruch(1, 3)));
    }

    @Test
    void grundrechenarten_ShouldHandleNegativeBrueche()
    {
        // Arrange
        Bruch a = new Bruch(-1, 2);
        Bruch b = new Bruch(1, -3);

        // Act & Assert
        assertEquals(new Bruch(-5, 6), a.plus(b));
        assertEquals(new Bruch(-1, 6), a.minus(b));
        assertEquals(new Bruch(1, 6), a.mal(b));
        assertEquals(new Bruch(3, 2), a.durch(b));
    }

    @Test
    void durch_ShouldRejectDivisionDurchNull()
    {
        // Act
        ArithmeticException fehler = assertThrows(ArithmeticException.class, () -> new Bruch(1, 2).durch(Bruch.NULL));

        // Assert
        assertEquals("Division durch 0 ist nicht erlaubt.", fehler.getMessage());
    }

    @Test
    void grosseZahlen_ShouldKuerzenStattUeberlaufen()
    {
        // Arrange
        long gross = 3_000_000_000L;
        Bruch a = new Bruch(gross, gross + 1);
        Bruch b = new Bruch(gross + 1, gross);

        // Act & Assert
        assertEquals(Bruch.ganz(1), a.mal(b));
        assertEquals(new Bruch(2, Long.MAX_VALUE), new Bruch(1, Long.MAX_VALUE).plus(new Bruch(1, Long.MAX_VALUE)));
    }

    @Test
    void grosseZahlen_ShouldMeldeUeberlaufAufDeutsch()
    {
        // Arrange
        Bruch a = new Bruch(1, Long.MAX_VALUE);
        Bruch b = new Bruch(1, Long.MAX_VALUE - 1);

        // Act
        ArithmeticException plus = assertThrows(ArithmeticException.class, () -> a.plus(b));
        ArithmeticException mal = assertThrows(ArithmeticException.class, () -> Bruch.ganz(Long.MAX_VALUE).mal(Bruch.ganz(2)));
        ArithmeticException vorzeichen = assertThrows(ArithmeticException.class, () -> new Bruch(Long.MIN_VALUE, -1));

        // Assert
        assertEquals("Die Zahlen sind zu groß für eine exakte Bruchrechnung.", plus.getMessage());
        assertEquals(plus.getMessage(), mal.getMessage());
        assertEquals(plus.getMessage(), vorzeichen.getMessage());
    }

    @Test
    void alsGemischteZahl_ShouldZeigeGanzeUndRest()
    {
        // Act & Assert
        assertEquals("2 1/3", new Bruch(7, 3).alsGemischteZahl());
        assertEquals("-2 1/3", new Bruch(-7, 3).alsGemischteZahl());
        assertEquals("2/3", new Bruch(2, 3).alsGemischteZahl());
        assertEquals("4", Bruch.ganz(4).alsGemischteZahl());
    }

    @Test
    void alsDouble_ShouldLiefereDezimalwert()
    {
        // Act & Assert
        assertEquals(0.75, new Bruch(3, 4).alsDouble());
        assertEquals(-2.5, new Bruch(-5, 2).alsDouble());
    }

    @Test
    void ausDezimal_ShouldFindeExaktePassendeBrueche()
    {
        // Act & Assert
        assertEquals(Optional.of(new Bruch(3, 4)), Bruch.ausDezimal(0.75, 10_000));
        assertEquals(Optional.of(new Bruch(1, 3)), Bruch.ausDezimal(0.333333333333, 10_000));
        assertEquals(Optional.of(new Bruch(-2, 3)), Bruch.ausDezimal(-2.0 / 3, 10_000));
        assertEquals(Optional.of(Bruch.ganz(5)), Bruch.ausDezimal(5.0, 10_000));
        assertEquals(Optional.of(Bruch.ganz(-3)), Bruch.ausDezimal(-3.0, 10_000));
        assertEquals(Optional.of(Bruch.NULL), Bruch.ausDezimal(0.0, 10_000));
    }

    @Test
    void ausDezimal_ShouldBleibeLeerOhnePassendenBruch()
    {
        // Act & Assert
        assertTrue(Bruch.ausDezimal(Math.PI, 10_000).isEmpty());
        assertTrue(Bruch.ausDezimal(Math.sqrt(2), 10_000).isEmpty());
        assertTrue(Bruch.ausDezimal(Double.NaN, 10_000).isEmpty());
        assertTrue(Bruch.ausDezimal(Double.POSITIVE_INFINITY, 10_000).isEmpty());
        assertTrue(Bruch.ausDezimal(1e300, 10_000).isEmpty());
    }

    @Test
    void parse_ShouldVerstehenAlleSchreibweisen()
    {
        // Act & Assert
        assertEquals(new Bruch(3, 4), Bruch.parse("3/4"));
        assertEquals(new Bruch(-3, 4), Bruch.parse(" -3 / 4 "));
        assertEquals(new Bruch(-3, 4), Bruch.parse("3/-4"));
        assertEquals(new Bruch(7, 3), Bruch.parse("2 1/3"));
        assertEquals(new Bruch(-7, 3), Bruch.parse("-2 1/3"));
        assertEquals(new Bruch(3, 4), Bruch.parse("0,75"));
        assertEquals(new Bruch(1, 10), Bruch.parse("0.1"));
        assertEquals(new Bruch(3, 4), Bruch.parse("1,5/2"));
        assertEquals(Bruch.ganz(5), Bruch.parse("5"));
        assertEquals(Bruch.ganz(100), Bruch.parse("100"));
    }

    @Test
    void parse_ShouldErklaereFehleingabenAufDeutsch()
    {
        // Act
        IllegalArgumentException leer = assertThrows(IllegalArgumentException.class, () -> Bruch.parse("  "));
        IllegalArgumentException text = assertThrows(IllegalArgumentException.class, () -> Bruch.parse("abc"));
        IllegalArgumentException doppelt = assertThrows(IllegalArgumentException.class, () -> Bruch.parse("1/2/3"));
        IllegalArgumentException nenner = assertThrows(IllegalArgumentException.class, () -> Bruch.parse("3/0"));
        IllegalArgumentException gemischtNenner = assertThrows(IllegalArgumentException.class, () -> Bruch.parse("2 1/0"));

        // Assert
        assertEquals("Bitte einen Bruch eingeben.", leer.getMessage());
        assertEquals("„abc“ ist kein gültiger Bruch.", text.getMessage());
        assertEquals("„1/2/3“ ist kein gültiger Bruch.", doppelt.getMessage());
        assertEquals("Der Nenner darf nicht 0 sein.", nenner.getMessage());
        assertEquals("Der Nenner darf nicht 0 sein.", gemischtNenner.getMessage());
    }
}
