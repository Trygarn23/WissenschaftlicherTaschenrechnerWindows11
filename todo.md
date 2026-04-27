# Taschenrechner Projekt

## Simple
- [ ] Tooltips für Buttons hinzufügen.
- [ ] Tastenkürzel in Tooltips anzeigen.
- [ ] Copy/Paste fürs Display ergänzen.
- [ ] Statusanzeige für Winkelmodus, Speicherstatus und Modus ergänzen.
- [ ] Resize-Verhalten verbessern.
- [ ] Display überarbeiten.
---
## Standardmodus

- [x] `StandardPanel` als reine UI-Klasse behalten.
- [x] `%`-Button korrekt mit Prozentfunktion verdrahten.
- [ ] Standardmodus auf `common.logic.RechnerService` umstellen.
- [ ] Prüfen, ob `StandardActionFactory` nach dem Action-Refactoring sinnvoll ist.
- [ ] Standard nicht mehr indirekt über `WissenschaftlichRechnerService` betreiben.

---
## Wissenschaftlich
- [ ] Wissenschaftliche Notation für große Zahlen ergänzen.
- [ ] Wissenschaftliche Notation für kleine Zahlen ergänzen.
- [ ] Einstellbare Präzision hinzufügen.
- [ ] Live-Formatierung für längere Ausdrücke verbessern.
- [ ] Bessere Fehlermeldungen statt nur Fehler einführen.
- [ ] Wissenschaftliche Schreibweise wie `1,2e-5` unterstützen.
- [ ] Domain-Prüfungen für `ln`, `sqrt`, `asin`, `acos` verbessern.
- [ ] Funktionsregistrierung zentralisieren.
- [ ] `WissenschaftlichOperationen` einführen.
- [x] `sin`, `cos`, `tan`, `asin`, `acos`, `atan` nach `WissenschaftlichOperationen` verschieben.
- [x] `sinh`, `cosh`, `tanh` nach `WissenschaftlichOperationen` verschieben.
- [x] `ln`, `log`, `exp`, `abs`, `floor`, `ceil`, `round`, `rand` nach `WissenschaftlichOperationen` verschieben.
- [ ] ! `fakultaet()` nach `WissenschaftlichOperationen` verschieben oder später als Parser-Funktion ergänzen.
- [x] Wissenschaftliches `f(x)`-Popup ans Theme-System anbinden.
- [ ] Wissenschaftliche Funktionsbuttons nicht mehr direkt in `TaschenrechnerUI` verdrahten.
---

## Programmierer
- [x] PRG-Modus UI-Grundgerüst bauen.
- [x] BIN OCT DEC HEX Umschaltung implementieren.
- [x] Ganzzahlmodus für PRG bauen.
- [x] Zahl parallel in BIN OCT DEC HEX anzeigen.
- [x] Wortbreiten-Grundgerüst mit BYTE WORD DWORD QWORD anlegen.
- [x] PRG-Code in eigenes Package auslagern.
- [ ] Bit-Operationen AND OR XOR NOT vervollständigen.
- [ ] Shift-Operationen links und rechts finalisieren.
- [ ] Signed/Unsigned-Umschaltung ergänzen.
- [ ] `unsigned` im UI sichtbar machen.
- [ ] `ProgrammiererLogik.maskiere()` abhängig von `unsigned` machen.
- [ ] `shiftRight()` in arithmetischen und logischen Right Shift trennen.
- [ ] Buttons je nach Basis deaktivieren.
- [ ] A-F-Buttons bei BIN/OCT/DEC deaktivieren.
- [ ] Ungültige Ziffern nicht nur logisch ignorieren, sondern UI-seitig deaktivieren.
- [ ] Logischen Right Shift ergänzen.
- [ ] PRG-Tests ergänzen.
- [ ] Operatoren für PRG-Modus im Parser ergänzen.
- [ ] Formatter für BIN OCT HEX ergänzen.
- [ ] `formatBinary()` aus `ProgrammiererPanel` in `ProgrammiererFormatter` verschieben.
- [ ] `ProgrammiererPanel` erst nach Funktionsabschluss in kleinere Panels splitten.
- [ ] `ProgrammiererHostPanel` nur behalten, wenn dort zusätzliche Host-Funktion entsteht.
---

## Graph
- [ ] `GraphPlaceholderPanel` vorerst behalten.
- [ ] Kein leeres `logic`/`model`-Package erzwingen.
- [ ] Parser erst um Variablenunterstützung für `x` erweitern, wenn `common.parser` stabil ist.
- [ ] Parser um Variablenunterstützung für `x` erweitern.
- [ ] Danach `GraphState`, `GraphPanel`, `FunktionsDefinition` und `Wertetabelle` planen.
- [ ] Graph-Modus UI bauen.
- [ ] Zeichenfläche für Funktionsgraphen implementieren.
- [ ] Achsen und Skalierung zeichnen.
- [ ] Zoom in und Zoom out ergänzen.
- [ ] Wertetabelle für `f(x)` anzeigen.
- [ ] Nullstellen grob markieren.
---

## Komplex
- [ ] `KomplexPlaceholderPanel` vorerst behalten.
- [ ] Kein leeres `logic`/`model`-Package erzwingen.
- [ ] Später `KomplexeZahl` als erstes echtes Modell einführen.
- [ ] Danach `KomplexParser`, `KomplexFormatter` und `KomplexRechnerService` planen.
- [ ] Komplex-Modus UI bauen.
- [ ] Klasse `KomplexeZahl` erstellen.
- [ ] Addition für komplexe Zahlen implementieren.
- [ ] Subtraktion für komplexe Zahlen implementieren.
- [ ] Multiplikation für komplexe Zahlen implementieren.
- [ ] Division für komplexe Zahlen implementieren.
- [ ] Betrag und Phase berechnen.
- [ ] Konjugation ergänzen.
- [ ] Polarform und kartesische Form umrechnen.
- [ ] Formatter für komplexe Zahlen ergänzen.
---

## Verlauf / History
- [ ] Verlauf mit Zeitstempel erweitern.
- [ ] Verlauf nach Modus kennzeichnen.
- [ ] Favoriten im Verlauf ermöglichen.
- [ ] Verlauf exportieren.
- [ ] Verlauf erst nach Einführung einer strukturierten `VerlaufEintrag`-Klasse erweitern.
---

## Refactoring
- [x] Parser-Tests vorhanden.
- [x] Logik-Tests vorhanden.
- [ ] Parser-Tests prüfen und fehlende Edgecases ergänzen.
- [ ] Logik-Tests prüfen und fehlende Edgecases ergänzen.
- [x] Theme-System in echte Themes umbauen.
- [x] Dark Theme verbessern.
- [x] Light Theme verbessern.
- [x] Win95 Theme hinzufügen.
- [x] Win11 Theme hinzufügen.
- [x] Neon Theme hinzufügen.
- [x] Matrix Theme hinzufügen.
- [x] Aktives Theme persistent speichern.
- [ ] ! Themes für Programmiermodus übernehmen.
- [ ] Layouts überarbeiten für:
    - [ ] Standard.
    - [ ] Wissenschaftlich.
    - [ ] Programmierer.
---
## Spätere Features
- [ ] Session speichern/laden erst nach sauberem `RechnerZustand`.
- [ ] Einheitenumrechnung als eigenes Feature planen, nicht in den bestehenden Rechnerkern drücken.
- [ ] Einheitenumrechnung hinzufügen.
- [ ] Konstantenbibliothek hinzufügen.
- [ ] Benutzerdefinierte Funktionen unterstützen.
- [ ] Schritt-für-Schritt-Auswertung bauen.
- [ ] Statistikmodus erst planen, wenn `common.logic`, `common.parser` und `common.state` stabil sind.
- [ ] Matrixmodus erst planen, wenn `common.logic`, `common.parser` und `common.state` stabil sind.
- [ ] Statistikmodus hinzufügen.
- [ ] Matrixmodus hinzufügen.
---
## Legende
- [x] fertig
- [ ] offen
- [ ] ! in Arbeit