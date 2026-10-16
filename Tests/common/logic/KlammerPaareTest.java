package common.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KlammerPaareTest
{
    @Test
    void tiefen_ShouldGiveBothBracketsOfAPairTheSameDepth_WhenBracketsAreNested()
    {
        // Act
        int[] tiefen = KlammerPaare.tiefen("(1+(2))");

        // Assert
        assertArrayEquals(new int[]{0, -1, -1, 1, -1, 1, 0}, tiefen);
    }

    @Test
    void tiefen_ShouldIgnoreOpenBracket_WhenItIsNotClosedYet()
    {
        // Act
        int[] tiefen = KlammerPaare.tiefen("sin((2)");

        // Assert
        assertEquals(-1, tiefen[3]);
        assertEquals(1, tiefen[4]);
        assertEquals(1, tiefen[6]);
    }

    @Test
    void tiefen_ShouldIgnoreClosingBracket_WhenThereIsNoOpenBracket()
    {
        // Act
        int[] tiefen = KlammerPaare.tiefen("2)+(3)");

        // Assert
        assertArrayEquals(new int[]{-1, -1, -1, 0, -1, 0}, tiefen);
    }

    @Test
    void tiefen_ShouldReturnEmptyArray_WhenTextIsNull()
    {
        // Act & Assert
        assertEquals(0, KlammerPaare.tiefen(null).length);
    }
}
