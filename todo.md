# Taschenrechner Projekt

## Simple
- [x] Tooltips für Buttons hinzufügen.
- [x] Tastenkürzel in Tooltips anzeigen.
- [x] Copy/Paste fürs Display ergänzen.
- [x] Statusanzeige für Winkelmodus, Speicherstatus und Modus ergänzen.
- [x] Resize-Verhalten verbessern.
- [x] Display überarbeiten.
- [x] Tooltips textlich vereinheitlichen und auf gleiche Sprache / gleiche Schreibweise angleichen.
- [x] Tooltips für Sonderfunktionen fachlich genauer formulieren, z. B. `ans`, `mod`, `10ˣ`, `n!`, `rand`.
- [x] Tooltips für Programmierermodus ergänzen: Signed/Unsigned, `>>>`, `<<`, `>>`, AND, OR, XOR, NOT.
- [x] Unit Tests nachgezogen: Jeder sichtbare Rechnerbutton soll einen Tooltip haben.
- [x] Tastenkürzel-Dokumentation zentralisieren, damit Tooltip-Text und `KeyboardShortcutBinder` nicht auseinanderlaufen.
- [x] Unit Tests nachgezogen: Copy/Paste-Verhalten für ungültige Eingaben absichern.
- [x] Copy/Paste-Verhalten bei leerem Clipboard absichern.
- [x] Unit Tests nachgezogen: Copy/Paste-Verhalten bei Text mit Leerzeichen, Tausenderpunkten und Komma absichern.
- [x] Unit Tests nachgezogen: Copy/Paste-Verhalten bei wissenschaftlicher Schreibweise absichern, z. B. `1,2e-5`.
- [x] Display-Schriftgröße bei sehr langen Ausdrücken dynamisch weiter verbessern.
- [ ] Display bei sehr kleinen Fenstergrößen stabilisieren.
- [ ] Display bei hoher DPI / Windows-Skalierung stabilisieren.
- [ ] High-DPI-Checkliste für Windows-Skalierung 100%, 125%, 150%, 200% erstellen.
- [x] Statusanzeige bei Moduswechsel sofort aktualisieren.
- [x] Statusanzeige bei Speicheränderung sofort aktualisieren.
- [x] Statusanzeige bei Winkelmoduswechsel sofort aktualisieren.
- [x] History/Suche bei ausgeblendeten Modi nicht per Tastatur fokussierbar machen.
- [ ] `ESC`-Verhalten festlegen: Suche defokussieren, Eingabe löschen oder Fenster schließen.
- [ ] Einheitliche Benennung im UI festlegen: `CLR`, `C`, `CE`, `←`, `Backspace`.
- [ ] Kleine UI-Politur: konsistente Innenabstände zwischen Display, Modebar, Buttons und History.
- [ ] Kleine UI-Politur: Button-Hover-Zustände in allen Themes angleichen.
- [ ] Kleine UI-Politur: aktive Mode-Bar-Auswahl in allen Themes angleichen.
- [ ] Kleine UI-Politur: Fokusrahmen / Tastaturfokus sichtbar aber nicht störend gestalten.
- [ ] Tastaturbedienung für jeden Modus vereinheitlichen.
- [ ] Fokusreihenfolge pro Modus festlegen.
- [ ] Accessibility verbessern: Kontrast, Screenreader-Namen, Tooltips, Fokusrahmen.
- [ ] Fehlertexte vereinheitlichen: kurz, fachlich korrekt, hilfreich.
- [ ] In-App-Hilfe planen, aber ohne nerviges Tutorial-Gedöns.
- [ ] Performance bei langen Ausdrücken, vielen History-Einträgen und großen Matrizen messen.
- [x] Fenstergröße nach Neustart optional speichern.
- [x] Letzten aktiven Modus optional speichern.
- [x] Letzten Winkelmodus optional speichern.

---

## Standardmodus

- [x] `StandardPanel` als reine UI-Klasse behalten.
- [x] `%`-Button korrekt mit Prozentfunktion verdrahten.
- [x] Standardmodus auf `common.logic.RechnerService` umstellen.
- [x] Entscheidung zu `StandardActionFactory` nach dem Action-Refactoring festhalten. Ergebnis: aktuell nicht nötig.
- [x] Standard nicht mehr indirekt über `WissenschaftlichRechnerService` betreiben.
- [x] Standardmodus-Regressionstest für alle Standardbuttons ergänzen.
- [x] Unit Tests nachgezogen: Verhalten von `←` nach Ergebnis absichern.
- [x] Unit Tests nachgezogen: Verhalten von neuer Zahl nach `=` absichern.
- [x] Unit Tests nachgezogen: Verhalten von Operator nach `=` absichern.
- [x] Mehrfachoperatoren absichern, z. B. `2++3`, `2×÷3`.
- [x] Kommaeingabe mehrfach verhindern.
- [x] Unit Tests nachgezogen: `±` mit leerem Ausdruck, Zahl, negativer Zahl und nach Operator absichern.
- [x] `1/x` bei 0 mit sinnvoller Fehlermeldung behandeln.
- [x] `√x` bei negativer Zahl mit sinnvoller Fehlermeldung behandeln.
- [x] Unit Tests nachgezogen: `x²` bei sehr großen Zahlen absichern.
- [x] Standardmodus-Buttonlayout bei kleiner Fenstergröße stabilisieren.
- [x] Standardmodus-Buttonlayout bei sehr breitem Fenster stabilisieren.
- [x] Standardmodus: Alle Buttons über Maus und Tastatur erreichbar machen.
- [x] Entscheidung zu eigener `StandardActionFactory` festhalten. Ergebnis: noch nicht nötig, Standardaktionen bleiben überschaubar.

---

## Wissenschaftlich
- [x] Wissenschaftliche Notation für große Zahlen ergänzen.
- [x] Wissenschaftliche Notation für kleine Zahlen ergänzen.
- [x] Einstellbare Präzision hinzufügen.
- [x] Live-Formatierung für längere Ausdrücke verbessern.
- [x] Bessere Fehlermeldungen statt nur Fehler einführen.
- [x] Wissenschaftliche Schreibweise wie `1,2e-5` unterstützen.
- [x] Domain-Prüfungen für `ln`, `sqrt`, `asin`, `acos` verbessern.
- [x] Funktionsregistrierung zentralisieren. Ergebnis: vor Graph bewusst nicht weiter abstrahieren; Registry ist in `docs/parser-roadmap.md` geplant.
- [x] `WissenschaftlichOperationen` einführen.
- [x] `sin`, `cos`, `tan`, `asin`, `acos`, `atan` nach `WissenschaftlichOperationen` verschieben.
- [x] `sinh`, `cosh`, `tanh` nach `WissenschaftlichOperationen` verschieben.
- [x] `ln`, `log`, `exp`, `abs`, `floor`, `ceil`, `round`, `rand` nach `WissenschaftlichOperationen` verschieben.
- [x] `fakultaet()` nach `WissenschaftlichOperationen` verschieben oder später als Parser-Funktion ergänzen.
- [x] Wissenschaftliches `f(x)`-Popup ans Theme-System anbinden.
- [x] Wissenschaftliche Funktionsbuttons nicht mehr direkt in `TaschenrechnerUI` verdrahten.
- [x] Präzision als Einstellung im UI anbieten.
- [x] Präzision persistent speichern.
- [x] Präzision für normale, wissenschaftliche und sehr kleine Werte einheitlich anwenden.
- [x] Domain-Prüfung für `ln(x)` bei `x <= 0` ergänzen.
- [x] Domain-Prüfung für `log(x)` bei `x <= 0` ergänzen.
- [x] Domain-Prüfung für `sqrt(x)` bei `x < 0` ergänzen.
- [x] Domain-Prüfung für `asin(x)` bei `x < -1 || x > 1` ergänzen.
- [x] Domain-Prüfung für `acos(x)` bei `x < -1 || x > 1` ergänzen.
- [x] Domain-Prüfung für `tan(90°)` bzw. Polstellen verbessern.
- [x] Domain-Fehlermeldungen nutzerfreundlich formulieren.
- [x] Fehlermeldungen testbar über `BerechnungsFehler` / `ParserFehler` halten.
- [x] `FunktionsRegistry` für Parserfunktionen planen.
- [x] `OperatorRegistry` für Operatorprioritäten planen.
- [x] Entscheidung festhalten, ob `fakultaet()` langfristig in den Parser gehört.
- [x] Fakultät für große Werte begrenzen und Fehlermeldung klar anzeigen.
- [x] Fakultät nur für ganze nichtnegative Zahlen erlauben.
- [x] Unit Tests nachgezogen: `rand()` im Bereich `[0, 1)` absichern.
- [x] Unit Tests nachgezogen: `π` und `e` als Unicode-Eingabe absichern.
- [x] `pi` und `π` konsistent behandeln.
- [x] Unit Tests nachgezogen: `ans` in wissenschaftlichen Funktionen absichern, z. B. `sin(ans)`.
- [x] Unit Tests nachgezogen: DEG/RAD-Verhalten für `sin`, `cos`, `tan` absichern.
- [x] Unit Tests nachgezogen: DEG/RAD-Verhalten für `asin`, `acos`, `atan` absichern.
- [x] Unit Tests nachgezogen: `sinh`, `cosh`, `tanh` unabhängig vom Winkelmodus absichern.
- [x] Unit Tests nachgezogen: `10ˣ` mit leerem Ausdruck, Zahl und Klammer absichern.
- [x] Unit Tests nachgezogen: `exp`, `ln`, `log` mit Ausdruck und letzter Zahl absichern.
- [x] Wissenschaftliches `f(x)`-Popup per Tastatur erreichbar machen.
- [ ] Wissenschaftliches `f(x)`-Popup optisch in allen Themes angleichen.
- [ ] Wissenschaftliches Panel bei kleiner Fenstergröße stabilisieren.

---

## Programmierer
- [x] PRG-Modus UI-Grundgerüst bauen.
- [x] BIN OCT DEC HEX Umschaltung implementieren.
- [x] Ganzzahlmodus für PRG bauen.
- [x] Zahl parallel in BIN OCT DEC HEX anzeigen.
- [x] Wortbreiten-Grundgerüst mit BYTE WORD DWORD QWORD anlegen.
- [x] PRG-Code in eigenes Package auslagern.
- [x] Bit-Operationen AND OR XOR NOT vervollständigen.
- [x] Shift-Operationen links und rechts finalisieren.
- [x] Signed/Unsigned-Umschaltung ergänzen.
- [x] `unsigned` im UI sichtbar machen.
- [x] `ProgrammiererLogik.maskiere()` abhängig von `unsigned` machen.
- [x] `shiftRight()` in arithmetischen und logischen Right Shift trennen.
- [x] Buttons je nach Basis deaktivieren.
- [x] A-F-Buttons bei BIN/OCT/DEC deaktivieren.
- [x] Ungültige Ziffern nicht nur logisch ignorieren, sondern UI-seitig deaktivieren.
- [x] Logischen Right Shift ergänzen.
- [x] Unit Tests nachgezogen: PRG-Grundverhalten absichern.
- [x] Formatter für BIN OCT HEX ergänzen.
- [x] `formatBinary()` aus `ProgrammiererPanel` in `ProgrammiererFormatter` verschieben.
- [x] `ProgrammiererPanel` erst nach Funktionsabschluss in kleinere Panels splitten.
- [x] `ProgrammiererHostPanel` nur behalten, wenn dort zusätzliche Host-Funktion entsteht.
- [x] History/Suche im Programmiermodus ausblenden.
- [x] Entscheidung zu `Operatoren für PRG-Modus im Parser ergänzen` festhalten: vermutlich ersetzen durch eigenen PRG-Parser nur bei Bedarf.
- [x] PRG-Modus bewusst vom normalen `AusdruckParser` getrennt halten.
- [x] Unit Tests nachgezogen: BYTE signed `FF` ergibt `-1`.
- [x] Unit Tests nachgezogen: BYTE unsigned `FF` ergibt `255`.
- [x] Unit Tests nachgezogen: WORD signed `FFFF` ergibt `-1`.
- [x] Unit Tests nachgezogen: DWORD signed `FFFFFFFF` ergibt `-1`.
- [x] Unit Tests nachgezogen: QWORD unsigned bei großen Werten absichern.
- [x] Unit Tests nachgezogen: arithmetischen Right Shift bei negativen Werten absichern.
- [x] Unit Tests nachgezogen: logischen Right Shift bei gesetztem Vorzeichenbit absichern.
- [x] Unit Tests nachgezogen: Shift Left mit Maskierung pro Wortbreite absichern.
- [x] Unit Tests nachgezogen: NOT mit BYTE, WORD, DWORD, QWORD absichern.
- [x] Unit Tests nachgezogen: AND/OR/XOR über unterschiedliche Basen absichern.
- [x] Unit Tests nachgezogen: Basiswechsel nach Operation absichern.
- [x] Unit Tests nachgezogen: Wortbreitenwechsel nach Operation absichern.
- [x] Unit Tests nachgezogen: Signed/Unsigned-Wechsel nach Operation absichern.
- [x] Unit Tests nachgezogen: Backspace nach Basiswechsel absichern.
- [x] Unit Tests nachgezogen: Backspace nach Ergebnis absichern.
- [x] `CLR` setzt pending operation zurück.
- [x] `=` ohne pending operation macht nichts und bleibt stabil.
- [x] Unit Tests nachgezogen: Mehrfachoperationen absichern, z. B. `A AND F OR 1`.
- [x] Entscheidung zu führenden Nullen festhalten: bewusst entfernen oder optional anzeigen.
- [x] Optional: BIN-Anzeige auf Wortbreite auffüllen, z. B. BYTE immer 8 Bit.
- [x] Optional: HEX-Anzeige auf Wortbreite auffüllen, z. B. BYTE immer 2 Stellen.
- [x] Optional: Gruppierung für HEX ergänzen, z. B. `FFFF FFFF`.
- [x] Entscheidung zu OCT-Gruppierung festhalten.
- [x] `SIGNED/UNSIGNED`-Button optisch deutlicher machen.
- [x] `SIGNED/UNSIGNED`-Button Tooltip ergänzen.
- [x] `>>>`, `>>`, `<<` Tooltips ergänzen.
- [x] PRG-Statusanzeige ergänzen: Basis, Wortbreite, Signed/Unsigned.
- [x] PRG-Modus mit Tastatursteuerung versehen.
- [x] PRG-Tastatur: A-F nur in HEX akzeptieren.
- [x] PRG-Tastatur: 2-9 je nach Basis blockieren.
- [x] PRG-Tastatur: `&`, `|`, `^`, `~` optional als Shortcuts planen.
- [x] PRG-Tastatur: Shift-Shortcuts planen.
- [x] PRG-Tastatur darf globale Standard-/Wissenschaftlich-Shortcuts im PRG-Modus nicht auslösen.
- [x] PRG-Eingabelänge je nach Basis und Wortbreite begrenzen.
- [x] `ProgrammiererFormatterTest` ergänzen.
- [x] Unit Tests nachgezogen: `ProgrammiererPanel` Button-Aktivierung absichern.
- [x] `ProgrammiererPanel` in `ProgrammiererDisplayPanel`, `ProgrammiererTastenPanel`, `ProgrammiererOptionsPanel` splitten.
- [x] `ProgrammiererButtonStyler` als Styling-Zentrale behalten, falls Styling weiter wächst.
- [x] `ProgrammiererHostPanel` entweder mit echter Host-Funktion füllen oder entfernen.
- [x] Länge anpassen oder begrenzen, damit die Anzeige nicht fehlschlägt
- [x] Programmierermodus optisch vollständig ans Theme-System anbinden, nicht nur Textfarben.
- [x] Hardcoded PRG-Farben aus `ProgrammiererButtonStyler` in eine theme-fähige Palette überführen.
- [x] PRG-Display-Hintergrund pro Theme angleichen.
- [x] PRG-Basis-/Wortbreitenbuttons pro Theme angleichen.
- [x] PRG-Ziffern-, Operator-, Bit- und Sonderbuttons pro Theme angleichen.
- [x] PRG-Disabled-Zustände pro Theme lesbar machen.
- [x] PRG-Hover-/Pressed-Zustände pro Theme angleichen.
- [x] Unit Tests nachziehen: Themewechsel verändert den Programmierermodus sichtbar.

---

## Graph
- [x] `GraphPlaceholderPanel` durch echtes `GraphPanel` ersetzen.
- [x] Kein leeres `logic`/`model`-Package erzwingen.
- [x] Parser erst um Variablenunterstützung für `x` erweitern, wenn `common.parser` stabil ist.
- [x] Parser um Variablenunterstützung für `x` erweitern.
- [x] Danach `GraphState`, `GraphPanel`, `FunktionsDefinition` und `Wertetabelle` planen.
- [x] Graph-Modus UI bauen.
- [x] Zeichenfläche für Funktionsgraphen implementieren.
- [x] Achsen und Skalierung zeichnen.
- [x] Zoom in und Zoom out ergänzen.
- [x] Wertetabelle für `f(x)` anzeigen.
- [x] Wertetabelle für `f'(x)` und `f''(x)` anzeigen.
- [x] Kurvendiskussion unten links im Graphmodus anzeigen.
- [x] Ableitung `f'(x)` numerisch bilden.
- [x] Zweite Ableitung `f''(x)` numerisch bilden.
- [x] Nullstellen über Kurvendiskussion berechnen.
- [x] Extremstellen über `f'(x) = 0` berechnen.
- [x] Wendestellen über `f''(x) = 0` berechnen.
- [x] Schnittpunkt mit der Y-Achse berechnen.
- [x] Kurvendiskussion klar als numerische Näherung kennzeichnen.
- [x] Nullstellen grob markieren.
- [x] Wendestellen grob markieren.
- [x] Schnittpunkt mit der Y-Achse markieren.
- [x] Parser-Variable `x` ohne Konflikt mit Multiplikationszeichen `×` planen.
- [x] AusdruckParser um Variablenwerte erweitern, ohne Standardberechnung zu brechen.
- [x] Neue Parser-API planen, z. B. `auswerten(expr, ans, winkelModus, variablen)`.
- [x] Graph-Funktionen mit DEG/RAD-Verhalten definieren.
- [x] Graph-Funktionen mit `ans` definieren oder bewusst verbieten.
- [x] Graph-State planen: aktueller Ausdruck, x-Min, x-Max, y-Min, y-Max, Zoom, Schrittweite.
- [x] Graph-State nicht in `RechnerZustand` mischen.
- [x] `FunktionsDefinition` planen: Name, Ausdruck, Farbe, sichtbar.
- [x] Mehrere Funktionen im Graphmodus optional planen.
- [x] Zeichenfläche mit Anti-Aliasing implementieren.
- [x] Achsenbeschriftung implementieren.
- [x] Rasterlinien implementieren.
- [x] Ursprung und Skalierung visuell stabil halten.
- [x] Zoom per Buttons implementieren.
- [x] Zoom per Mausrad optional planen.
- [x] Pan/Verschieben per Drag optional planen.
- [x] Graph per Doppelklick auf die Standardansicht zurücksetzen.
- [x] Wertetabelle mit einstellbarer Schrittweite planen.
- [x] Polstellen / Definitionslücken erkennen oder zumindest nicht verbinden.
- [x] Sehr große Werte im Graphen begrenzen.
- [x] Parserfehler im Graphmodus nutzerfreundlich anzeigen.
- [x] Graphmodus erst nach Parser-Unit-Tests für Variablen starten.
- [x] Unit Tests nachgezogen: Graph-Funktionsauswertung mit `x` absichern.
- [x] Manuelle UI-Checkliste für Graph vorbereiten.
- [x] Kollisionschecker für zwei Graphen bauen.
- [x] Gute GUI für mehrere Funktionen bauen. Desmos vorbild?
- [x] Tokenizer für Brüche und so anpassen, dass er (..)/(..) versteht oder ../(..).
- [ ] Graph-Kurvendiskussion später mit symbolischen Ergebnissen anreichern, numerische Näherung bleibt Fallback.
- [ ] Graphmodus später für Statistik-Regressionen wiederverwenden.

---

## Komplex
- [x] `KomplexPlaceholderPanel` durch echtes `KomplexPanel` ersetzen.
- [x] Kein leeres `logic`/`model`-Package erzwingen.
- [x] Später `KomplexeZahl` als erstes echtes Modell einführen.
- [x] Danach `KomplexParser`, `KomplexFormatter` und `KomplexRechnerService` planen.
- [x] Komplex-Modus UI bauen.
- [x] Klasse `KomplexeZahl` erstellen.
- [x] Addition für komplexe Zahlen implementieren.
- [x] Subtraktion für komplexe Zahlen implementieren.
- [x] Multiplikation für komplexe Zahlen implementieren.
- [x] Division für komplexe Zahlen implementieren.
- [x] Betrag und Phase berechnen.
- [x] Konjugation ergänzen.
- [x] Polarform und kartesische Form umrechnen.
- [x] Formatter für komplexe Zahlen ergänzen.
- [x] `KomplexeZahl` immutable machen.
- [x] `KomplexeZahl` mit `real` und `imaginaer` als double starten.
- [x] Unit Tests nachgezogen: `KomplexeZahl` Grundrechenarten absichern.
- [x] Division durch `0 + 0i` sauber als Fehler behandeln.
- [x] Betrag über `Math.hypot(real, imag)` berechnen.
- [x] Phase über `Math.atan2(imaginaer, real)` berechnen.
- [x] Polarform mit DEG/RAD-Verhalten planen.
- [x] Formatter-Optionen planen: `a + bi`, `a - bi`, Polarform.
- [x] Parser-Syntax festlegen: zunächst Eingabefelder, Parser später optional.
- [x] Komplexmodus zunächst ohne normalen `AusdruckParser` bauen.
- [x] `KomplexParser` erst nach festgelegter Syntax bauen.
- [x] Komplexmodus nicht mit Standard-Rechnerzustand vermischen.
- [x] Eigenen `KomplexState` planen.
- [x] Komplexmodus-UI mit Real-/Imaginär-Eingabe planen.
- [x] Umschaltung kartesisch/polar planen.
- [x] Kopieren des Ergebnisses als Text unterstützen.
- [x] Unit Tests nachgezogen: Rundung und Formatierung absichern.
- [x] Unit Tests nachgezogen: Sonderfälle absichern: rein reell, rein imaginär, null.

---

## Matrixmodus
- [x] Matrixmodus als eigenen Modus planen, nicht als Erweiterung des normalen Ausdruckparsers.
- [x] Package-Struktur planen: `modes.matrix.model`, `modes.matrix.logic`, `modes.matrix.ui`, `modes.matrix.formatting`.
- [x] Immutable `Matrix`-Modell planen: Zeilen, Spalten, Werte, Dimensionvalidierung.
- [x] Matrix-Erstellung im UI planen: Größenwahl, editierbares Raster, Beispielwerte, Clear.
- [x] Matrix-Grundoperationen planen: Addition, Subtraktion, Skalarmultiplikation.
- [x] Matrixmultiplikation mit Dimensionsprüfung planen.
- [x] Determinante für 2x2 und 3x3 starten, größere Matrizen später über Gauß.
- [ ] Inverse Matrix über Gauß-Jordan planen.
- [ ] Rang, Transponieren und Spur planen.
- [ ] Lineare Gleichungssysteme `Ax = b` als späteres Overkill-Feature planen.
- [x] Matrixformatierung planen: kompakte Anzeige, Copy/Paste als Tabellenformat, CSV-kompatibel.
- [x] Unit Tests nachziehen: Matrixmodus mit Dimensionsfehlern, Rundung, singulären Matrizen und großen Werten absichern.

---

## Statistikmodus
- [x] Statistikmodus als eigenen State planen: Datenliste, Sortierung, Klassen, optional Gewichte.
- [x] Eingabe per Textfeld, Tabelle und Paste aus Tabellenkalkulation planen.
- [x] Kennzahlen planen: Summe, Mittelwert, Median, Modus, Minimum, Maximum.
- [x] Streuung planen: Varianz, Standardabweichung, Spannweite, Quartile.
- [x] Regressionsfunktionen planen: linear, quadratisch optional später.
- [x] Diagramme planen: Histogramm, Boxplot, Streudiagramm.
- [x] Statistikmodus sauber von Graphmodus trennen.

---

## Einheiten / Konstanten
- [x] Einheitenumrechnung als eigener Modus oder Sidepanel entscheiden.
- [x] Einheitenmodell planen: Kategorie, Einheit, Symbol, Faktor, Offset.
- [x] Temperatur separat behandeln, weil Celsius/Fahrenheit nicht nur Faktor sind.
- [x] Einheitenumrechnung als ausfahrbares SidePanel bauen.
- [x] Einheiten-SidePanel ans Theme-System anbinden.
- [x] SidePanel mit Swing-Animation oeffnen und schliessen.
- [x] Unit Tests nachziehen: Einheitenumrechnung und SidePanel absichern.
- [ ] Konstantenbibliothek mit Kategorien planen: Mathematik, Physik, Informatik, Chemie.
- [ ] Konstanten suchbar machen und in Standard/Wissenschaftlich einfügbar machen.
- [ ] Favorisierte Konstanten persistent speichern.
- [ ] Eigene Konstanten des Nutzers planen.

---

## CAS-light / Lernmodus
- [ ] Schritt-für-Schritt-Auswertung erst nach Parser-Modularisierung starten.
- [ ] Token- und Parserfehler mit Position im Ausdruck anzeigen.
- [ ] Einfache Umformungen planen: Klammern auflösen, Potenzregeln, Bruchvereinfachung.
- [ ] Ableitungsregeln symbolisch für einfache Funktionen planen.
- [ ] Benutzerdefinierte Funktionen mit Namen und Ausdruck speichern.
- [ ] Benutzerdefinierte Funktionen validieren.
- [ ] Benutzerdefinierte Funktionen im Parser registrieren.
- [ ] Lernmodus planen: Rechenweg anzeigen, aber normale Rechnerbedienung nicht verlangsamen.

---

## Verlauf / History
- [x] Verlauf mit Zeitstempel erweitern.
- [x] Verlauf nach Modus kennzeichnen.
- [ ] Favoriten im Verlauf ermöglichen.
- [ ] Verlauf exportieren.
- [x] Verlauf erst nach Einführung einer strukturierten `VerlaufEintrag`-Klasse erweitern.
- [x] `VerlaufEintrag` als Modell einführen.
- [x] `VerlaufEintrag` Felder planen: Ausdruck, Ergebnis, Modus, Zeitstempel, Favorit.
- [x] Bestehende String-History migrieren oder kompatibel einlesen.
- [x] Repository-Format festlegen: Text, CSV, JSON oder eigenes Format.
- [x] `DateiVerlaufRepository` auf strukturierte Einträge vorbereiten.
- [x] `VerlaufService` von `List<String>` auf `List<VerlaufEintrag>` umstellen.
- [x] `HistoryPanel` auf strukturierte Anzeige vorbereiten.
- [x] History-Suche über Ausdruck und Ergebnis ermöglichen.
- [x] History-Suche über Modus ermöglichen.
- [ ] History-Suche über Datum optional planen.
- [ ] Favoriten im UI anzeigen.
- [ ] Favoriten persistent speichern.
- [ ] Verlaufseinträge löschen: einzeln.
- [ ] Verlaufseinträge löschen: alle.
- [ ] Verlaufseinträge löschen: nur aktueller Modus.
- [ ] Verlauf nach Modus filtern.
- [ ] Verlauf nach Favoriten filtern.
- [ ] Verlauf exportieren als `.txt`.
- [ ] Verlauf exportieren als `.csv`.
- [ ] Verlauf exportieren als `.json` optional planen.
- [ ] Verlauf importieren optional planen.
- [ ] Doppelklick-Verhalten bei strukturierten Einträgen neu implementieren.
- [ ] History bei Standard/Wissenschaftlich sichtbar lassen.
- [ ] History bei Programmierer/Graph/Komplex bewusst ausblenden oder modusspezifisch machen.
- [x] Unit Tests nachziehen: Verlaufsladen alter Dateien absichern.
- [x] Unit Tests nachgezogen: Verlaufsspeichern strukturierter Einträge absichern.
- [ ] Unit Tests nachziehen: Favoriten absichern.
- [ ] Unit Tests nachziehen: Export absichern.

---

## Refactoring
- [x] Parser-Unit-Tests vorhanden.
- [x] Logik-Unit-Tests vorhanden.
- [x] Unit Tests nachgezogen: Parser-Edge-Cases ergänzen.
- [x] Unit Tests nachgezogen: Logik-Edge-Cases ergänzen.
- [x] Theme-System in echte Themes umbauen.
- [x] Dark Theme verbessern.
- [x] Light Theme verbessern.
- [x] Win95 Theme hinzufügen.
- [x] Win11 Theme hinzufügen.
- [x] Neon Theme hinzufügen.
- [x] Matrix Theme hinzufügen.
- [x] Nutzer eigene Theme kreation überlassen → Eigenes Menü, mit sowas wie einem Farbkreis
- [x] Aktives Theme persistent speichern.
- [x] Themes für Programmiermodus vollständig übernehmen.
- [ ] Layouts überarbeiten für:
  - [ ] Standard.
  - [ ] Wissenschaftlich.
  - [x] Programmierer.
- [x] `WissenschaftlichRechnerService` langfristig entfernen oder als Deprecated-Adapter markieren.
- [ ] `WissenschaftlichRechnerService`-Adapter aus Tests entfernen, sobald kein Kompatibilitätsbedarf mehr besteht.
- [ ] `ShellActionRegistry` weiter beobachten: Wird sie zu groß?
- [ ] Persistence-Orchestrierung aus `TaschenrechnerUI` in einen Shell-Service auslagern.
- [ ] `GraphPanel`, `MatrixPanel` und `AusdruckEditor` in weiteren sicheren Schritten verkleinern.
- [ ] Gemeinsame Theme-Hilfen für einfache Mode-Panels prüfen, ohne Spezialpanels zu verbiegen.
- [ ] Optional `StandardActionFactory` nur einführen, falls Standardaktionen wachsen.
- [ ] Optional `WissenschaftlichActionFactory` einführen, falls wissenschaftliche Actions wachsen.
- [ ] `KeyboardShortcutBinder` mit Tooltips synchron halten.
- [ ] `ButtonTooltips` und `ShellActionRegistry` auf gemeinsame Action-Namen vereinheitlichen.
- [x] Package-Namen vereinheitlichen: überall lowercase, z. B. `ui.theme`.
- [ ] Unit Tests nachziehen: Teststruktur langfristig in Standardstruktur überführen, z. B. `src/test/java`.
- [x] README aktualisieren: Projektstruktur, Modi, Tastenkürzel, Build/Test-Anleitung.
- [ ] README Screenshots ergänzen.
- [x] Parser weiter modularisieren: Tokenizer.
- [x] Parser weiter modularisieren: PostfixKonverter.
- [x] Parser weiter modularisieren: PostfixAuswerter.
- [ ] Parser weiter modularisieren: OperatorRegistry.
- [ ] Parser weiter modularisieren: FunktionsRegistry.
- [ ] Parser weiter modularisieren, bevor CAS-/Matrix-/Statistikfeatures auf ihn aufbauen.
- [x] `RechnerZustand` stärker kapseln und direkte `StringBuilder`-Zugriffe reduzieren.
- [ ] Unit Tests nachziehen: `BerechnungsService` stärker über Ergebnisobjekte statt Strings absichern.
- [ ] Fehlerbehandlung vereinheitlichen.
- [ ] Einheitliches `ModePanel`-Konzept planen: Jeder Modus bekommt klare Methoden für Theme, Fokus, Reset und optionale History.
- [ ] Gemeinsames `ModeState`-Konzept entwerfen, ohne Spezialzustände wie Graph/Komplex/PRG in `RechnerZustand` zu quetschen.
- [ ] Theme-Duplikation reduzieren, z. B. über `ThemePalette` oder Basisklasse.
- [ ] Theme-System um semantische Rollen erweitern: Display, Function, Operator, Danger, Accent, Disabled, Grid, Canvas.
- [ ] Benutzerdefinierte Themes erst nach Theme-Palette planen.
- [ ] Layouts auf gemeinsame Hilfsmethoden reduzieren.
- [ ] Große UI-Klassen verkleinern: `TaschenrechnerUI`, `ProgrammiererPanel`, `HistoryPanel`.
- [x] `HistoryPanel` bei strukturiertem Verlauf aufteilen.
- [x] `HistoryPanel` nach `ui.history` verschieben.
- [x] `HistoryEntryRenderer` aus `HistoryPanel` auslagern.
- [x] `TaschenrechnerUI` Theme-Rekursion in `ShellThemeApplier` auslagern.
- [x] Modus-Sichtbarkeitsregeln in `ModeVisibilityPolicy` auslagern.
- [x] Ausdruck-/Clipboard-Normalisierung aus `AusdruckEditor` auslagern.
- [x] Legacy-Verlaufstext-Mapping aus `HistoryPanel` nach `common.history` verschieben.
- [x] Hardcoded Start-Hintergrund aus `ProgrammiererPanel` entfernen.
- [x] `ProgrammiererPanel` nach Funktionalitätsabschluss aufteilen.
- [ ] Build-System sauber entscheiden: Maven oder Gradle, danach Unit Tests mit einem Standardbefehl ausführbar machen.
- [ ] UI-Checkliste für alle Themes und Modi anlegen.
- [ ] Regressionstest-Suite vor großen Feature-Branches ausführen.

---

## Settings
- [x] Setting Menü hinzufügen
- [x] Setting Button richtig anzeigen
- [x] Setting MenüButtons im Untermenü Clickable machen
- [x] Funktionalität geben
- [x] Settings-Dialog: Änderungen optional mit Speichern/Abbrechen statt Sofortübernahme anbieten.
- [ ] Settings-Datei versionieren, falls später neue Felder dazukommen.
- [x] Einstellungen-Dialog planen.
- [x] Einstellungen persistent speichern.
- [x] Einstellungen für Präzision ergänzen.
- [x] Einstellungen für Theme ergänzen.
- [x] Einstellungen für Startmodus ergänzen.
- [x] Einstellungen für Winkelmodus ergänzen.
- [x] Einstellungen für History-Verhalten ergänzen.
- [x] Einstellungen für Zahlenformat ergänzen.

## Spätere Features
- [x] Session speichern/laden erst nach sauberem `RechnerZustand`.
- [x] Session speichern: Modus, Ausdruck, Verlauf, Winkelmodus, Speicher, Theme.
- [x] Session laden mit Kompatibilitätsprüfung.
- [x] Session-Datei robust gegen Fehler lesen.
- [x] Sessionmodell planen: aktiver Modus, Ausdruck, Verlauf, Settings, modusspezifische States.
- [x] Session-Dateiformat versionieren.
- [x] Session laden mit Migrations-/Kompatibilitätsprüfung.
- [ ] Export/Screenshot des Rechners optional planen.
- [ ] Export planen: Verlauf als TXT/CSV/JSON, Graph als PNG, Matrix als CSV.
- [ ] Druck-/Report-Ansicht optional planen.
- [ ] Lokale Projektdateien für komplexere Arbeiten planen, z. B. Graphen + Tabellen + Notizen.
- [ ] Lokalisierung Deutsch/Englisch optional planen.
- [ ] Dark/Light-Systemtheme automatisch übernehmen optional planen.
- [ ] Auto-Update oder Release-Paket optional planen.
- [ ] `.jar`-Build oder Installer optional planen.
- [ ] GitHub Releases vorbereiten.
- [ ] Changelog führen.
- [x] Version im UI anzeigen.

## Legende
- [x] fertig
- [ ] offen
- [ ] ! in Arbeit

## Befehle
git add .
git commit -m ""
git push
