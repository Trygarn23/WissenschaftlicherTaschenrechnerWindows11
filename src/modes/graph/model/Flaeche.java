package modes.graph.model;

/** Ergebnis der Flächenberechnung zwischen Kurve und x-Achse auf [a; b]. */
public record Flaeche(int funktionIndex, double a, double b, double integral, double flaecheninhalt)
{
}
