# Changelog

Rückwirkend aus dem Git-Verlauf zusammengefasst. Es gibt keine Versionsnummern-Tags, deshalb ist nach Datum gruppiert.
Die Version im UI (Einstellungen → „Version“) steht in `AppSettings.VERSION`, aktuell `0.5.0-local`.

## Unveröffentlicht

Alles ab 2026-10-08 (aktueller Stand, Version `0.5.0-local`).

### Neu
- Neue Modi und Werkzeuge (Bruch, Datum, Finanz, Gleichung, Logik, Netzwerk, Vektor), Gleichungssysteme und kompakte Gleichungseingabe.
- Graph: erweiterte Analyse, logische Operatorsymbole werden gezeichnet.
- Wissenschaftlich: `nCr`, `nPr`, `ggT`, `kgV`, `n!` im f(x)-Menü (Trennzeichen `;`, auch per Shift+Komma).
- Wissenschaftlich: `S↔D` zeigt Ergebnisse exakt (Bruch, Wurzel, Vielfache von π), „Mehr Stellen“ zeigt die volle Genauigkeit.
- Variablen A–F mit STO/RCL, bleiben über Neustarts erhalten.
- Display: mitten im Ausdruck weitertippen (Klick, ←/→), Klammerpaare farbig, Parserfehler mit markierter Stelle.
- Funktionen im f(x)-Menü per Rechtsklick anheften; eigene Funktionen anlegen und löschen (Befehlssuche).
- Einheiten: Favoriten-Umrechnungen und „Ergebnis umrechnen“ aus der Befehlssuche.
- Verlauf: „Statistik…“ mit Lieblingsfunktion.
- Tastenkürzel-Übersicht um Strg+K, Strg+Shift+K, ←/→, Pos1/Ende/Entf und einen Programmierer-Abschnitt ergänzt.
- Vor dem Löschen einer eigenen Funktion, die noch benutzt wird, kommt eine Warnung.
- Doku: Architektur-Seite, Release-, UI- und High-DPI-Checkliste, Testabdeckung.

### Geändert
- Parser-Umbau, Tastatur, Einstellungs-Layout und Modus-Hervorhebung überarbeitet.
- Todo-Liste stark erweitert.
- Konstanten-Favoriten als Stern statt Checkbox.
- Aufgeräumt: toter Code und unbenutzte Imports entfernt, magische Zahlen benannt, `AusdruckEditor`, `TaschenrechnerUI`, `GraphPanel` und `MatrixPanel` verkleinert, gemeinsame Formular-/Theme-Hilfe `ModusFormular` für die einfachen Modi.

### Behoben
- Parser: `a x` wird als `a·x` gelesen statt als unbekannter Name `ax`.
- Graph: Funktionen wie `1/x`, die bei x = 0 nicht definiert sind, galten als ungültig.
- Mini-Rechner übernimmt einen Theme-Wechsel sofort.
- Kaputte Settings-/Session-Datei (abgeschnittenes Unicode-Escape) hat das Laden abstürzen lassen.
- Die zweite Display-Zeile (Live-Vorschau, offene Klammern, Fehlermeldung) hatte die Höhe 0 und war unsichtbar.
- `S↔D` statt `S⇔D`, weil Segoe UI kein ⇔ hat.

## 2026-10-04

### Neu
- Verlauf: Einträge löschen (mit Rückfrage und Rückgängig), nach Modus und Favoriten filtern, Export als TXT und CSV.
- Live-Vorschau mit offenen Klammern im Display.
- Strg+Z holt gelöschte Eingabe zurück.

### Geändert
- Esc löscht die Eingabe, statt das Fenster zu schließen.
- Verlauf-Stern wird selbst gemalt, Suchfeld verbreitert.

### Behoben
- Einstellungen-Test griff auf den echten Benutzerordner zu.

## 2026-10-03

### Neu
- Tastenkürzel für Modi, F1-Übersicht.
- Speicherorte gebündelt über `AppDateien`, Fehler beim Speichern/Laden werden geloggt.

### Geändert
- Clean-Code-Aufräumarbeiten: Themes über `BasisTheme`, Theme-Ordner kleingeschrieben, `ShellActionRegistry` verkleinert, Aktionen/Tooltips/Kürzel über gemeinsame Namen verbunden.
- Graphmodus aufgeräumt, Formatierung in `GraphFormatter`, Maussteuerung aufgeteilt.
- Fehlerbehandlung vereinheitlicht, deutsche Fehlermeldungen bei Zahleneingaben.
- Alten `WissenschaftlichRechnerService` entfernt.

### Behoben
- Umlaute in Meldungen korrigiert.

## 2026-09-02

### Neu
- Graph: Funktionen können sich untereinander verknüpfen.

### Geändert
- Graph-UI verbessert, Fokus auf Kurvendiskussion klickbar.

## 2026-05-25

### Geändert
- Architektur, Themes, Verlauf und Matrixmodus verbessert.

## 2026-05-13

### Neu
- Statistikmodus mit Auswertung, Diagrammen und Tests.
- Prototyp Längenrechner (Einheiten).

### Geändert
- Parser-Tokenisierung in eigenen Tokenizer ausgelagert; Parser, Shell und History weiter modularisiert.

## 2026-05-12

### Neu
- Session (Fenster-/Modus-Zustand wird gespeichert).

### Geändert
- Settings verbessert, History neu gebaut.

## 2026-05-07

### Neu
- Settings-Dialog, eigenes Fenster, klickbare Elemente, Versionsnummer.
- Graphmodus (Parser mit `x`, Zoom, Pan, Ableitungstabelle, Analysemarker, zweite Funktion, Schnittpunkte) inkl. Tests.
- Komplexmodus mit eigenem Modell, Service, Formatter, UI und Tests.
- Großes Testpaket, Fenstergrößen werden gespeichert.

### Geändert
- Programmierermodus verbessert und umgelagert, Tastatureingaben, UI-Limits.

## 2026-05-06

### Neu
- Fehlermeldungen, Display-Rework, Tooltips.

## April 2026 (2026-04-09 bis 2026-04-30)

### Neu
- Programmierermodus (GUI und Logik), Themes Win95, Win11, Neon, Matrix.

### Geändert
- Großes Refactoring in mehreren Teilen: Logik und UI getrennt, UI-Komponenten auf einzelne Klassen verteilt, Themes herausgezogen.

### Behoben
- Themes und History-Leiste im Programmierermodus.

## 2026-03-14 und 2026-03-15

### Neu
- Modi (Standard wieder eingeführt), weitere Funktionen und Buttons.
- Auto-Formatierung für Potenz und Kehrwert.

### Geändert
- Funktionsnamen ins Deutsche übersetzt, Klassen weiter aufgeteilt.

### Behoben
- Tokenizer-Fix, Autoklammern bei sin/cos/tan, Kehrwert.

## Januar 2026 (Projektstart, 2026-01-08 bis 2026-01-22)

### Neu
- Basis-Taschenrechner-UI, Grundfunktionen, Tastatureingabe, Klammerlogik.
- Fakultät, Potenzen, Logarithmen, Exponentialfunktion, Trigonometrie, Klick-Eingabe in der GUI.
- Memory-Buttons, Verlauf mit Suchfunktion.
- README mit Projektbeschreibung.

### Geändert
- Mehrere UI-Verbesserungen und Animationen, Auslagerung von Keys, Refactoring des Projekts.

### Behoben
- Parserverhalten, UI-Korrekturen, Tastaturfixes.
