# Wissenschaftlicher Taschenrechner

## Motivation

Dieses Projekt ist ein selbst entwickelter wissenschaftlicher Taschenrechner in Java mit grafischer Oberfläche (Swing). Entstanden ist das Ganze aus purer Neugier und ein bisschen Langeweile. Nicht, weil es von der Schule verlangt wurde, sondern weil ich ausprobieren wollte, wie weit man über den Unterricht hinausgehen kann.

## Features

### Standardmodus

- Grundrechenarten
- Prozentfunktion
- Vorzeichenwechsel
- Quadrat, Wurzel und Kehrwert
- Klammern, Operatorprioritäten und Ergebnis-History über den gemeinsamen Parser
- Eingabe über Buttons und Tastatur

### Wissenschaftlicher Modus

- Trigonometrische Funktionen mit DEG/RAD-Unterstützung
- Inverse und hyperbolische trigonometrische Funktionen
- Logarithmen, Exponentialfunktion, Betrag, Rundungsfunktionen und Zufallszahl
- Konstanten `π`, `e` und `Ans`
- Wissenschaftliche Schreibweise für sehr große und sehr kleine Werte
- Einstellbare Präzision über die Settings

### Programmierermodus

- Ganzzahlrechner für `BIN`, `OCT`, `DEC` und `HEX`
- Wortbreiten `BYTE`, `WORD`, `DWORD` und `QWORD`
- Signed/Unsigned-Umschaltung
- Bitoperationen `AND`, `OR`, `XOR`, `NOT`
- Shifts `<<`, `>>` und `>>>`
- Parallele Anzeige der Zahl in mehreren Basen
- Tastatursteuerung und basisabhängige Button-Aktivierung

### Graphmodus

- Funktionsplotter für Ausdrücke mit der Variable `x`
- Theme-fähige Zeichenfläche mit Achsen, Raster und Beschriftung
- Zoom per Buttons und Mausrad
- Verschieben per Drag
- Doppelklick zum Zurücksetzen der Ansicht
- Wertetabelle für `f(x)`, `f'(x)` und `f''(x)`
- Numerische Kurvendiskussion im sichtbaren Bereich:
  - y-Achsen-Schnittpunkt
  - Nullstellen
  - Extremstellen
  - Wendestellen
  - Schnittpunkte zweier Funktionen
- Mehrere Funktionen als einfacher Desmos-artiger Einstieg

### Komplexmodus

- Eingabe zweier komplexer Zahlen über Real- und Imaginärteil
- Addition, Subtraktion, Multiplikation und Division
- Konjugation
- Betrag und Phase
- Kartesische und polare Darstellung
- Ergebnis kann in die Zwischenablage kopiert werden

### Themes

- Mehrere Themes, unter anderem Dark, Light, Neon, Matrix, Win95 und Win11
- Theme-Auswahl über die obere Aktionsleiste
- Theme-Anbindung für die Hauptmodi

### Settings

- Einstellungsdialog über den Button oben rechts
- Persistente Einstellungen:
  - Theme
  - Startmodus
  - Winkelmodus
  - Verlauf-Verhalten
  - Präzision
  - Zahlenformat
  - Fenstergröße

### History

- Verlauf für Standard- und wissenschaftliche Berechnungen
- Suchfeld und Clear-Funktion
- Doppelklick auf Verlaufseinträge übernimmt das Ergebnis
- Verlauf kann über die Settings deaktiviert werden

## Voraussetzungen

- Java Development Kit, empfohlen: JDK 25
- IntelliJ IDEA ist für die Arbeit am Projekt vorgesehen
- Es gibt aktuell kein Maven- oder Gradle-Build-System
- Tests verwenden JUnit 5.14.0

## Startanleitung

In IntelliJ IDEA:

1. Projektordner öffnen.
2. Ein JDK konfigurieren.
3. `src/Main.java` starten.

Per Konsole unter Windows PowerShell, wenn `javac` und `java` im PATH liegen:

```powershell
javac -encoding UTF-8 -d out\app (Get-ChildItem src -Recurse -Filter *.java).FullName
java -cp out\app Main
```

Falls `javac` nicht im PATH liegt, muss der vollständige Pfad zum JDK verwendet werden.

## Testanleitung

Das Projekt hat derzeit kein Build-Tool, daher laufen die Tests über einen lokalen JUnit-Classpath oder direkt über IntelliJ.

Empfohlen in IntelliJ:

1. JUnit 5.14.0 als Test-Library einbinden.
2. Den Ordner `Tests` als Testquelle markieren.
3. Alle Tests ausführen.

Manuell per Konsole ist ebenfalls möglich, erfordert aber einen vollständigen JUnit-5-Classpath. Die Tests liegen im Ordner `Tests`.

## Projektstruktur

```text
src/
  Main.java
  common/
    formatting/
    history/
    logic/
    parser/
    state/
  modes/
    standard/
    wissenschaftlich/
    programmierer/
    graph/
    komplex/
  ui/
    settings/
    shell/
    shortcuts/
    theme/
    tooltips/

Tests/
docs/
todo.md
```

Die Modi sind weitgehend getrennt aufgebaut. Gemeinsame Logik wie Parser, Verlauf, Formatierung und Rechnerzustand liegt unter `common`.

## Bekannte Einschränkungen

- Es gibt noch kein Maven- oder Gradle-Build-System.
- Der Graphmodus arbeitet numerisch, nicht symbolisch. Kurvendiskussion und Ableitungen sind Näherungen im sichtbaren Bereich.
- Der Komplexmodus nutzt Eingabefelder, keinen eigenen komplexen Ausdruckparser.
- Graph- und Komplexmodus schreiben aktuell nicht in die allgemeine History.
- Einige UI-Prüfungen bleiben manuell, zum Beispiel Verhalten bei hoher DPI, sehr kleinen Fenstern und allen Themes.
- Die Projektstruktur der Tests ist noch nicht die übliche Maven/Gradle-Struktur `src/test/java`.

## Roadmap

Die aktuelle Roadmap und offene Detailaufgaben stehen in [`todo.md`](todo.md). Dort sind auch bewusst spätere Themen wie Parser-Modularisierung, Verlauf-Ausbau, benutzerdefinierte Themes, weitere Modi und Build-System dokumentiert.

## Hinweis zur Nutzung

Dieses Projekt ist öffentlich einsehbar und dient Lern- und Übungszwecken. Der Quellcode darf ohne ausdrückliche Zustimmung nicht kopiert, weiterverwendet oder als eigenes Projekt ausgegeben werden.
