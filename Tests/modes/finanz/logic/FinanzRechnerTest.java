package modes.finanz.logic;

import modes.finanz.model.FinanzErgebnis;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FinanzRechnerTest
{
    private static BigDecimal bd(String wert)
    {
        return new BigDecimal(wert);
    }

    private static void assertBetrag(String erwartet, FinanzErgebnis ergebnis)
    {
        assertEquals(bd(erwartet), ergebnis.wert());
    }

    @Test
    void runde_ShouldRoundHalfUpToCents()
    {
        // Act & Assert
        assertEquals(bd("2.35"), FinanzRechner.runde(bd("2.345")));
        assertEquals(bd("-2.35"), FinanzRechner.runde(bd("-2.345")));
        assertEquals(bd("0.01"), FinanzRechner.runde(bd("0.005")));
        assertEquals(bd("10.00"), FinanzRechner.runde(bd("10")));
    }

    @Test
    void prozentwert_ShouldCalculateAndExplain()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.prozentwert(bd("19"), bd("250"));

        // Assert
        assertBetrag("47.50", ergebnis);
        assertEquals("47,50 €", ergebnis.anzeige());
        assertEquals(List.of("19 % von 250,00 € = 250,00 € × 0,19 = 47,50 €"), ergebnis.rechenweg());
    }

    @Test
    void prozentsatz_ShouldReturnPercentAndRejectZeroBase()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.prozentsatz(bd("47.5"), bd("250"));
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> FinanzRechner.prozentsatz(bd("5"), BigDecimal.ZERO));

        // Assert
        assertBetrag("19.00", ergebnis);
        assertEquals("19 %", ergebnis.anzeige());
        assertEquals("Der Grundwert darf nicht 0 sein.", fehler.getMessage());
    }

    @Test
    void prozentsatz_ShouldRoundRepeatingDecimals()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.prozentsatz(bd("1"), bd("3"));

        // Assert
        assertBetrag("33.33", ergebnis);
        assertEquals("33,33 %", ergebnis.anzeige());
    }

    @Test
    void grundwert_ShouldFindBaseAndRejectZeroPercent()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.grundwert(bd("47.5"), bd("19"));

        // Assert
        assertBetrag("250.00", ergebnis);
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.grundwert(bd("5"), BigDecimal.ZERO));
    }

    @Test
    void veraenderung_ShouldShowIncreaseAndDecreaseWithSign()
    {
        // Act
        FinanzErgebnis plus = FinanzRechner.veraenderung(bd("80"), bd("100"));
        FinanzErgebnis minus = FinanzRechner.veraenderung(bd("100"), bd("80"));

        // Assert
        assertEquals("+25 %", plus.anzeige());
        assertEquals("-20 %", minus.anzeige());
        assertEquals(2, plus.rechenweg().size());
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.veraenderung(BigDecimal.ZERO, bd("5")));
    }

    @Test
    void veraenderung_ShouldUseAbsoluteBaseForNegativeStart()
    {
        // Act: von -50 auf -25 ist eine Zunahme
        FinanzErgebnis ergebnis = FinanzRechner.veraenderung(bd("-50"), bd("-25"));

        // Assert
        assertBetrag("50.00", ergebnis);
    }

    @Test
    void bruttoAusNetto_ShouldAddTaxRoundedToCents()
    {
        // Act
        FinanzErgebnis normal = FinanzRechner.bruttoAusNetto(bd("100"), bd("19"));
        FinanzErgebnis ermaessigt = FinanzRechner.bruttoAusNetto(bd("9.99"), bd("7"));

        // Assert
        assertBetrag("119.00", normal);
        assertEquals("MwSt: 100,00 € × 19 % = 19,00 €", normal.rechenweg().get(0));
        assertBetrag("10.69", ermaessigt);
    }

    @Test
    void nettoAusBrutto_ShouldRemoveTax()
    {
        // Act
        FinanzErgebnis normal = FinanzRechner.nettoAusBrutto(bd("119"), bd("19"));
        FinanzErgebnis ermaessigt = FinanzRechner.nettoAusBrutto(bd("10.70"), bd("7"));
        FinanzErgebnis krumm = FinanzRechner.nettoAusBrutto(bd("10"), bd("19"));

        // Assert
        assertBetrag("100.00", normal);
        assertEquals("Enthaltene MwSt: 119,00 € − 100,00 € = 19,00 €", normal.rechenweg().get(1));
        assertBetrag("10.00", ermaessigt);
        assertBetrag("8.40", krumm);
    }

    @Test
    void steuer_ShouldRejectNegativeInput()
    {
        // Act
        IllegalArgumentException fehler = assertThrows(IllegalArgumentException.class,
                () -> FinanzRechner.bruttoAusNetto(bd("-1"), bd("19")));

        // Assert
        assertEquals("Der Nettobetrag darf nicht negativ sein.", fehler.getMessage());
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.nettoAusBrutto(bd("10"), bd("-7")));
    }

    @Test
    void rabatt_ShouldSubtractRoundedDiscountAndCheckRange()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.rabatt(bd("59.99"), bd("20"));

        // Assert
        assertBetrag("47.99", ergebnis);
        assertEquals("Rabatt: 59,99 € × 20 % = 12,00 €", ergebnis.rechenweg().get(0));
        assertBetrag("0.00", FinanzRechner.rabatt(bd("10"), bd("100")));
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.rabatt(bd("10"), bd("101")));
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.rabatt(bd("10"), bd("-1")));
    }

    @Test
    void trinkgeld_ShouldSplitAndRoundPerPerson()
    {
        // Act
        FinanzErgebnis geteilt = FinanzRechner.trinkgeld(bd("84.5"), bd("10"), bd("2"));
        FinanzErgebnis allein = FinanzRechner.trinkgeld(bd("84.5"), bd("10"), bd("1"));

        // Assert
        assertBetrag("46.48", geteilt);
        assertEquals("46,48 € pro Person", geteilt.anzeige());
        assertEquals(3, geteilt.rechenweg().size());
        assertBetrag("92.95", allein);
        assertEquals(2, allein.rechenweg().size());
    }

    @Test
    void trinkgeld_ShouldRejectInvalidPersonCount()
    {
        // Act
        IllegalArgumentException halb = assertThrows(IllegalArgumentException.class,
                () -> FinanzRechner.trinkgeld(bd("10"), bd("10"), bd("1.5")));
        IllegalArgumentException null_ = assertThrows(IllegalArgumentException.class,
                () -> FinanzRechner.trinkgeld(bd("10"), bd("10"), BigDecimal.ZERO));

        // Assert
        assertEquals("Die Anzahl der Personen muss eine ganze Zahl sein.", halb.getMessage());
        assertEquals("Es muss mindestens eine Person bezahlen.", null_.getMessage());
    }

    @Test
    void einfacheZinsen_ShouldAddLinearInterest()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.einfacheZinsen(bd("1000"), bd("3"), bd("5"));
        FinanzErgebnis halbesJahr = FinanzRechner.einfacheZinsen(bd("1000"), bd("3"), bd("0.5"));

        // Assert
        assertBetrag("1150.00", ergebnis);
        assertEquals("Zinsen: 1.000,00 € × 3 % × 5 Jahre = 150,00 €", ergebnis.rechenweg().get(0));
        assertBetrag("1015.00", halbesJahr);
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.einfacheZinsen(bd("1000"), bd("3"), BigDecimal.ZERO));
    }

    @Test
    void zinseszins_ShouldCompoundYearlyAndMonthly()
    {
        // Act
        FinanzErgebnis jaehrlich = FinanzRechner.zinseszins(bd("1000"), bd("3"), bd("5"), 1);
        FinanzErgebnis monatlich = FinanzRechner.zinseszins(bd("1000"), bd("3"), bd("5"), 12);

        // Assert
        assertBetrag("1159.27", jaehrlich);
        assertEquals("Zinsen: 1.159,27 € − 1.000,00 € = 159,27 €", jaehrlich.rechenweg().get(2));
        assertBetrag("1161.62", monatlich);
    }

    @Test
    void zinseszins_ShouldHandleZeroRateAndFractionalYears()
    {
        // Act
        FinanzErgebnis ohneZins = FinanzRechner.zinseszins(bd("1000"), BigDecimal.ZERO, bd("10"), 1);
        FinanzErgebnis anderthalbJahre = FinanzRechner.zinseszins(bd("1000"), bd("12"), bd("1.5"), 12);

        // Assert
        assertBetrag("1000.00", ohneZins);
        assertBetrag("1196.15", anderthalbJahre);
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.zinseszins(bd("1000"), bd("3"), bd("1.5"), 1));
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.zinseszins(bd("-1"), bd("3"), bd("1"), 1));
    }

    @Test
    void kreditrate_ShouldCalculateAnnuity()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.kreditrate(bd("10000"), bd("5"), bd("48"));

        // Assert
        assertBetrag("230.29", ergebnis);
        assertEquals("230,29 € pro Monat", ergebnis.anzeige());
        assertEquals("Zinsen gesamt: 11.053,92 € − 10.000,00 € = 1.053,92 €", ergebnis.rechenweg().get(2));
    }

    @Test
    void kreditrate_ShouldDivideEvenlyWithoutInterest()
    {
        // Act
        FinanzErgebnis ergebnis = FinanzRechner.kreditrate(bd("1200"), BigDecimal.ZERO, bd("12"));

        // Assert
        assertBetrag("100.00", ergebnis);
        assertEquals("Ohne Zinsen: 1.200,00 € ÷ 12 Monate = 100,00 €", ergebnis.rechenweg().get(0));
        assertTrue(ergebnis.rechenweg().get(2).endsWith("= 0,00 €"));
    }

    @Test
    void kreditrate_ShouldRejectInvalidInput()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.kreditrate(BigDecimal.ZERO, bd("5"), bd("12")));
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.kreditrate(bd("1000"), bd("5"), bd("0")));
        assertThrows(IllegalArgumentException.class, () -> FinanzRechner.kreditrate(bd("1000"), bd("5"), bd("12.5")));
        IllegalArgumentException riesig = assertThrows(IllegalArgumentException.class,
                () -> FinanzRechner.kreditrate(bd("1000"), bd("5"), bd("1000000000")));
        assertEquals("Die Laufzeit in Monaten ist zu groß.", riesig.getMessage());
    }

    @Test
    void finanzRechnung_ShouldCalculateAllDefaultsWithoutError()
    {
        for (FinanzRechnung rechnung : FinanzRechnung.values())
        {
            // Arrange
            List<BigDecimal> werte = rechnung.vorgaben().stream()
                    .map(text -> new BigDecimal(text.replace(',', '.')))
                    .toList();

            // Act
            FinanzErgebnis ergebnis = rechnung.berechne(werte);

            // Assert
            assertEquals(rechnung.felder().size(), werte.size(), rechnung.name());
            assertFalse(ergebnis.rechenweg().isEmpty(), rechnung.name());
        }
    }

    @Test
    void finanzRechnung_ShouldRejectWrongNumberOfValues()
    {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> FinanzRechnung.PROZENTWERT.berechne(List.of(BigDecimal.ONE)));
    }
}
