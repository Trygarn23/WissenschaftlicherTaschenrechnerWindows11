package modes.matrix.model;

/** Ein Schritt im Gauß-Jordan-Verfahren, z. B. „Z2 ← Z2 − 3·Z1“, mit der Matrix danach. */
public record RechenSchritt(String beschreibung, Matrix zwischenstand)
{
}
