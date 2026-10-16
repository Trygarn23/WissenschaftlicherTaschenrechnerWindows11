# Changelog

Rückwirkend aus dem Git-Verlauf zusammengefasst. Es gibt keine Versionsnummern-Tags, deshalb ist nach Datum gruppiert.
Die Version im UI (Einstellungen → „Version“) steht in `AppSettings.VERSION`, aktuell `0.5.0-local`.

## Unveröffentlicht

Alles ab 2026-10-08 (aktueller Stand, Version `0.5.0-local`).

### Neu
- Neue Modi und Werkzeuge (Bruch, Datum, Finanz, Gleichung, Logik, Netzwerk, Vektor), Gleichungssysteme und kompakte Gleichungseingabe.
- Graph: erweiterte Analyse, logische Operatorsymbole werden gezeichnet.

### Geändert
- Parser-Umbau, Tastatur, Einstellungs-Layout und Modus-Hervorhebung überarbeitet.
- Todo-Liste stark erweitert.

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
