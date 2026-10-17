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
- Ins Display klicken oder ←/→, Pos1/Ende nutzen, um mitten im Ausdruck weiterzutippen (Entf löscht rechts)
- Zusammengehörige Klammern werden farbig markiert, Parserfehler zeigen die fehlerhafte Stelle

### Wissenschaftlicher Modus

- Trigonometrische Funktionen mit DEG/RAD-Unterstützung
- Inverse und hyperbolische trigonometrische Funktionen
- Logarithmen, Exponentialfunktion, Betrag, Rundungsfunktionen und Zufallszahl
- Konstanten `π`, `e` und `Ans`
- Wissenschaftliche Schreibweise für sehr große und sehr kleine Werte
- Einstellbare Präzision über die Settings
- f(x)-Menü mit `nCr`, `nPr`, `ggT`, `kgV` und `n!` (Argumente mit `;` trennen, z. B. `nCr(5;2)`; `;` auch per Shift+Komma)
- `S↔D` zeigt das Ergebnis exakt (z. B. `3/4`, `√2/2`, `3π/4`), „Mehr Stellen“ mit voller Genauigkeit
- Variablen A–F über `STO`/`RCL`, bleiben nach einem Neustart erhalten
- Funktionen per Rechtsklick im f(x)-Menü anheften
- Eigene Funktionen in `x` über die Befehlssuche (Strg+K) anlegen und löschen

### Programmierermodus

- Ganzzahlrechner für `BIN`, `OCT`, `DEC` und `HEX`
- Wortbreiten `BYTE`, `WORD`, `DWORD` und `QWORD`
- Signed/Unsigned-Umschaltung direkt neben den Wortbreiten
- Grundrechenarten `+`, `-`, `×`
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
- „Statistik…“ im Mehr-Menü zeigt die Lieblingsfunktion

### Einheiten

- Ausfahrbares Einheiten-Panel (Strg+8), Umrechnungen als Favorit merkbar
- „Ergebnis umrechnen“ in der Befehlssuche übernimmt das aktuelle Ergebnis

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

Änderungen stehen im [`CHANGELOG.md`](CHANGELOG.md).

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

### Namensregeln

- **UI-Klassen auf Englisch** (`applyTheme`, `buildSidebar`, `createButton`), weil Swing selbst englisch ist.
- **Logik und Model auf Deutsch** (`berechne`, `formatiereZahl`, `RechnerZustand`).
- Innerhalb einer Klasse nicht mischen: die privaten Hilfsmethoden einer Klasse sind alle in einer Sprache.
- Texte für die Oberfläche immer mit echten Umlauten („öffnen“, nicht „oeffnen“).
- Fehlermeldungen sind ganze Sätze mit Punkt und sagen, was zu tun ist („Bitte eine Zahl eingeben.“). Mathematisch heißt es „nicht definiert“, nicht „nicht erlaubt“.

### Lösch-Tasten

| Taste | Bedeutung | Tastatur |
|---|---|---|
| `←` | letztes Zeichen löschen | Backspace |
| `CE` | nur die aktuelle Eingabe löschen | Esc |
| `C` | alles löschen | – |
| `CLR` | nur im Programmierer: alles löschen, weil `C` dort eine Hex-Ziffer ist | Esc |
| „Leeren“ | in Formular-Modi (Matrix, Statistik, Logik) alle Eingabefelder leeren | – |

Auf Buttons steht immer das Symbol (`←`), „Backspace“ nur in Tastenkürzel-Übersichten.

### Gespeicherte Dateien

Settings, Session, Verlauf und das eigene Theme liegen unter `~/.wissenschaftlicher_taschenrechner/`.
Ältere Versionen haben die Dateien direkt im Benutzerordner abgelegt, die werden beim Start einmal dorthin verschoben.

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
