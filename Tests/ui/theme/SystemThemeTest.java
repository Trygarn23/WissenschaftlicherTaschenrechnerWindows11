package ui.theme;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SystemThemeTest
{
    @Test
    void leseAusgabe_ShouldReadLightAndDarkFromRegOutput()
    {
        // Arrange
        String kopf = "\r\nHKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\r\n";

        // Act & Assert
        assertEquals(Optional.of(true), SystemTheme.leseAusgabe(kopf + "    AppsUseLightTheme    REG_DWORD    0x1\r\n"));
        assertEquals(Optional.of(false), SystemTheme.leseAusgabe(kopf + "    AppsUseLightTheme    REG_DWORD    0x0\r\n"));
    }

    @Test
    void leseAusgabe_ShouldGiveNoAnswerForErrorsOrUnknownValues()
    {
        // Act & Assert
        assertEquals(Optional.empty(), SystemTheme.leseAusgabe("FEHLER: Der angegebene Registrierungsschlüssel wurde nicht gefunden."));
        assertEquals(Optional.empty(), SystemTheme.leseAusgabe("    AppsUseLightTheme    REG_DWORD    0x7"));
        assertEquals(Optional.empty(), SystemTheme.leseAusgabe(""));
    }
}
