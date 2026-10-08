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
- [ ] High-DPI-Checkliste für Windows-Skalierung 100 %, 125%, 150%, 200% erstellen.
- [x] Statusanzeige bei Moduswechsel sofort aktualisieren.
- [x] Statusanzeige bei Speicheränderung sofort aktualisieren.
- [x] Statusanzeige bei Winkelmoduswechsel sofort aktualisieren.
- [x] History/Suche bei ausgeblendeten Modi nicht per Tastatur fokussierbar machen.
- [x] `ESC`-Verhalten festlegen: Suche defokussieren, Eingabe löschen oder Fenster schließen. → Suche verlassen, sonst wie `CE`; Fenster schließt Esc nicht mehr.
- [ ] Einheitliche Benennung im UI festlegen: `CLR`, `C`, `CE`, `←`, `Backspace`.
- [ ] Tastaturbedienung für jeden Modus vereinheitlichen.
- [ ] Fokusreihenfolge pro Modus festlegen.
- [ ] Accessibility verbessern: Kontrast, Screenreader-Namen, Tooltips, Fokusrahmen.
- [ ] Fehlertexte vereinheitlichen: kurz, fachlich korrekt, hilfreich.
  - [x] Zahleneingaben in Komplex, Matrix, Statistik und Einheiten über `ZahlenEingabe` mit deutscher Meldung.
  - [x] Umlaute in Fehlermeldungen und Einheitennamen korrigiert (z. B. „benötigt“, „Länge“, „Fuß“).
- [ ] In-App-Hilfe planen, aber ohne nerviges Tutorial-Gedöns.
- [ ] Performance bei langen Ausdrücken, vielen History-Einträgen und großen Matrizen messen.
- [ ] Letzte Eingabe nach einem Absturz wieder anbieten, damit nicht alles einfach weg ist.
- [x] Einmal Rückgängig anbieten, wenn man aus Versehen den Ausdruck gelöscht hat. → Strg+Z nach C/CE, solange noch nichts Neues getippt wurde.
- [ ] Klammerpaare beim Tippen sichtbar zusammengehörig markieren.
- [x] Stern-Button im Verlauf zeigt nur ein Kästchen → Stern wird jetzt selbst gemalt (`StarIcon`), klappt in jeder Schrift und jedem Theme.
- [x] Suchfeld im Verlauf ist zu schmal, da steht nur „Such“ → Suchfeld hat jetzt eine eigene Zeile über den Buttons.
- [x] Live-Vorschau im Display: Ergebnis schon beim Tippen grau anzeigen, z. B. „= 42“ → über `vorschauWert()` mit `OptionalDouble`, weil `aktuellerWertOder0()` „ist 0“ und „geht nicht“ nicht unterscheiden kann.
- [x] Anzeigen, wie viele Klammern noch offen sind, z. B. „2 offen“ → als kleiner Zwischenschritt, bevor die Klammerpaare markiert werden.
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
- [ ] Nützliche Einheitenumrechnungen direkt aus einem Ergebnis starten.
- [ ] Häufig genutzte Funktionen als Favoriten anheften.
- [ ] Ergebnis bei Bedarf mit mehr Nachkommastellen anschauen, ohne die Einstellung dauerhaft umzubauen.
- [ ] Wissenschaftliches `f(x)`-Popup optisch in allen Themes angleichen.
- [ ] Wissenschaftliches Panel bei kleiner Fenstergröße stabilisieren.
- [ ] S⇔D-Taste wie beim Casio: Ergebnis exakt anzeigen, z. B. 0,333… → 1/3 oder 1,414… → √2.
- [ ] `nCr` und `nPr` ergänzen.
- [ ] ggT, kgV und Primfaktorzerlegung ergänzen.
- [ ] Variablen A–F zum Speichern (STO/RCL), weil ein M-Speicher auf Dauer echt wenig ist.
- [ ] `2nd`-Taste wie beim TI überlegen: sin ↔ asin, x² ↔ √x usw. → könnte das `f(x)`-Popup ersetzen?

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
- [ ] Bit-Leiste mit 64 klickbaren Kästchen, ein Klick kippt das Bit.
- [ ] Wert zusätzlich als ASCII-/Unicode-Zeichen anzeigen.
- [ ] IEEE-754-Ansicht: Wie sieht die Zahl intern als `float`/`double` aus? → Vorzeichen, Exponent, Mantisse.

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
- [x] Mauszeiger im Graphen zeigt die aktuellen x- und y-Werte. → 0.5 Sekunden kein Bewegen → Hover Feld?
- [ ] Funktionslisten speichern und später wieder öffnen.
- [x] Wichtigen Punkt im Graphen anklicken und seine Werte übernehmen. → Rechtsclick
- [x] Mehrere Funktionen hinzufügbar machen
- [x] Kurvendiskussion für Graphen durch anclicken machen, Automatisch erster Graph
- [x] GraphenBuchstaben ineinander nutzen können: f(x) = 2x ; g(x) = x^2 + f(x) ; ...
- [x] Scrolling bzw UI etwas überarbeiten → Zeichnen knöpfe etwas kleiner und generell etwas verbessern
- [ ] Schieberegler für Parameter: f(x) = a·x², a am Regler ziehen und die Kurve wackelt live mit.
- [ ] Tangente an einem angeklickten Punkt einzeichnen.
- [ ] Fläche unter der Kurve berechnen (Integral, numerisch reicht) und schraffiert anzeigen.

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
- [ ] Gaußsche Zahlenebene: z1, z2 und das Ergebnis als Pfeile zeichnen.

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
- [ ] Bei Gauß-Jordan auf Wunsch die einzelnen Rechenschritte mitzeigen.
- [x] Rang, Transponieren und Spur umsetzen.
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
- [ ] Statistikdaten als Tabelle wieder herauskopieren.
- [ ] Auffällige Ausreißer in Statistikdaten sichtbar markieren.
- [ ] Regression mit Gleichung und Gütemaß verständlich anzeigen.
- [ ] Normalverteilung und Binomialverteilung als kleine Rechner → typischer Abi-Kram.

---

## Gleichungsmodus
- [ ] Gleichungsmodus als ruhigen Helfer planen: lineare und quadratische Gleichungen zuerst, kein vollwertiges CAS.
- [ ] Eingabeform festlegen: klassische Form `ax + b = c`, Koeffizientenfelder oder beides.
- [ ] Lineare Gleichungen mit einer Variable lösen.
- [ ] Quadratische Gleichungen mit reellen und komplexen Lösungen lösen.
- [ ] Ergebnis mit kurzem Rechenweg anzeigen, aber nicht den Bildschirm volltexten.
- [ ] Fehlerfälle menschlich formulieren: keine Lösung, unendlich viele Lösungen, ungültige Eingabe.
- [ ] Gleichungsmodus vom normalen Parser wiederverwenden, ohne Parser-Sonderfälle quer durchs Projekt zu ziehen.
- [ ] Unit Tests nachziehen: lineare Gleichungen, quadratische Gleichungen, Sonderfälle.

---

## Bruchmodus
- [ ] Bruchmodus planen für exakte Rechnungen mit Brüchen statt gerundeten Dezimalzahlen.
- [ ] Bruchmodell bauen: Zähler, Nenner, Kürzen, Vorzeichen normalisieren.
- [ ] Grundrechenarten für Brüche implementieren: Addition, Subtraktion, Multiplikation, Division.
- [ ] Gemischte Zahlen optional planen, aber nicht direkt erzwingen.
- [ ] Dezimalzahl in Bruch umwandeln und Bruch als Dezimalzahl anzeigen.
- [ ] Bruchmodus mit Standard/Wissenschaftlich verbinden: Ergebnis übernehmen, ohne beide Modi zu vermischen.
- [ ] Unit Tests nachziehen: Kürzen, negative Brüche, Nenner 0, große Zahlen.

---

## Vektor-/Geometriemodus
- [ ] Vektormodus klein starten: 2D- und 3D-Vektoren eingeben und anzeigen.
- [ ] Vektoraddition, Subtraktion und Skalierung implementieren.
- [ ] Skalarprodukt, Betrag und Winkel zwischen zwei Vektoren berechnen.
- [ ] Kreuzprodukt nur für 3D ergänzen.
- [ ] Einfache Geometrie-Helfer planen: Abstand zweier Punkte, Mittelpunkt, Steigung.
- [ ] UI nicht überfrachten: Eingabefelder und Ergebnisbereich reichen am Anfang.
- [ ] Unit Tests nachziehen: 2D/3D-Rechnungen, Nullvektor, Rundung.

---

## Finanzmodus
- [ ] Finanzmodus als Alltagsrechner planen: Prozent, Rabatt, Steuer, Trinkgeld, Zinsen.
- [ ] Einfache Zinsrechnung implementieren: Kapital, Zinssatz, Laufzeit, Endbetrag.
- [ ] Prozentrechner mit klaren Fragen bauen: "Wie viel sind x Prozent von y?" und "x ist wie viel Prozent von y?".
- [ ] Rabatt-/Mehrwertsteuer-Helfer ergänzen.
- [ ] Monatsrate/Kreditrechner optional planen, aber erst nach den einfachen Fällen.
- [ ] Ergebnisse nachvollziehbar anzeigen, damit es nicht wie eine schwarze Box wirkt.
- [ ] Unit Tests nachziehen: Prozentfälle, Zinsen, Rundung auf Geldbeträge.

---

## IT-/Netzwerkmodus
- [ ] Subnetzrechner für IPv4: IP + CIDR rein, Netzadresse, Broadcast und Anzahl Hosts raus.
- [ ] Subnetting: Netz in x gleich große Teilnetze aufteilen → Lernfeld Netzwerke / AP1 lässt grüßen.
- [ ] Subnetzmaske zwischen `/24` und `255.255.255.0` hin und her umrechnen.
- [ ] IP-Adresse binär anzeigen, damit man sieht, wo Netz- und Hostteil anfangen.
- [ ] IPv6 erstmal nur kürzen/ausschreiben, alles andere später.
- [ ] Unit Tests nachziehen: typische Prüfungsaufgaben, `/31`, `/32`, ungültige IPs.

---

## Logikmodus
- [ ] Ausdruck wie `A ∧ (B ∨ ¬C)` eingeben und die Wahrheitstabelle ausspucken lassen.
- [ ] Eingabe auch mit `&&`, `||`, `!` bzw. AND/OR/NOT erlauben, damit man nicht nach Sonderzeichen suchen muss.
- [ ] KV-Diagramm optional planen, aber erst wenn die Wahrheitstabelle sauber läuft.
- [ ] Unit Tests nachziehen: Klammern, Vorrang, Variablen A–D.

---

## Datums-/Zeitrechner
- [ ] Tage zwischen zwei Daten ausrechnen.
- [ ] Datum + n Tage / Wochen / Monate.
- [ ] Wochentag zu einem Datum anzeigen.
- [ ] Arbeitstage zählen, Wochenende raus → Feiertage optional später.
- [ ] Stunden und Minuten zusammenrechnen, z. B. für das Berichtsheft.
- [ ] Unit Tests nachziehen: Schaltjahre, Monatsende, Jahreswechsel.

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
- [x] Favoriten im Verlauf ermöglichen.
- [x] Verlauf exportieren. → „Mehr“-Menü im Verlauf, exportiert die gerade angezeigten Einträge.
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
- [x] Favoriten im UI anzeigen.
- [x] Favoriten persistent speichern.
- [x] Verlaufseinträge löschen: einzeln.
- [x] Verlaufseinträge löschen: alle.
- [x] Verlaufseinträge löschen: nur aktueller Modus. → gelöst als „Angezeigte Einträge löschen“: Filter auf den Modus stellen, dann löschen.
- [x] Vor dem endgültigen Löschen kurz nachfragen. → bei mehreren Einträgen; ein einzelner geht ohne Rückfrage, dafür gibt es Rückgängig.
- [x] Gerade gelöschte Verlaufseinträge für diesen Moment zurückholen. → „Löschen rückgängig“, gilt bis zum nächsten neuen Eintrag.
- [ ] Gleiche Rechnungen im Verlauf auf Wunsch zusammenfassen.
- [x] Verlauf nach Modus filtern.
- [x] Verlauf nach Favoriten filtern.
- [x] Verlauf exportieren als `.txt`.
- [x] Verlauf exportieren als `.csv`. → Semikolon-getrennt, mit BOM, damit Excel die Umlaute erkennt.
- [ ] Verlauf exportieren als `.json` optional planen.
- [ ] Verlauf importieren optional planen.
- [x] Doppelklick-Verhalten bei strukturierten Einträgen neu implementieren.
- [x] History bei Standard/Wissenschaftlich sichtbar lassen.
- [x] History bei Programmierer/Graph/Komplex bewusst ausblenden oder modusspezifisch machen.
- [x] Unit Tests nachziehen: Verlaufsladen alter Dateien absichern.
- [x] Unit Tests nachgezogen: Verlaufsspeichern strukturierter Einträge absichern.
- [x] Unit Tests nachziehen: Favoriten absichern.
- [x] Unit Tests nachziehen: Export absichern.

---

## UI/UX
- [x] Freundliches modernes Theme `Azubi Modern` ergänzen.
- [x] Gemeinsamen `ModernButtonStyler` für rundere Buttons, Hover, Pressed und Fokus einführen.
- [x] Display, History und Settings auf moderne Card-/Input-Rollen umstellen.
- [x] Graph, Matrix, Statistik, Komplex und Einheiten-SidePanel mit modernen Button-/Input-Rollen angleichen.
- [x] Dezente humorvolle Leer-/Hinweistexte ergänzen, ohne die Bedienung zu stören.
- [x] Obere Modusleiste auf Hauptmodi reduzieren: Standard, Wissenschaftlich, PRG, Graph, Komplex.
- [x] Weitere-Modi-Menü für Matrix, Statistik und Einheiten ergänzen.
- [x] Separaten Einheiten-Button aus der oberen Aktionsleiste entfernen.
- [ ] Modernisierung manuell in allen Themes und Modi in IntelliJ durchklicken.
- [ ] Kleine UI-Politur: Scrollbereiche und Tabellen optisch weiter angleichen.
- [ ] Kompakte Ansicht für kleine Fenster anbieten.
- [ ] Verlauf ein-/ausklappbar machen, die Tasten sind eh riesig und der Verlauf winzig.
- [ ] Befehlssuche mit Strg+K: Modus, Theme, Funktion oder Konstante tippen, Enter, fertig → Aktionen haben jetzt eh eindeutige Namen.
- [x] Moduswechsel per Strg+1 bis Strg+9. → Strg+1–7 Modi, Strg+8 Einheiten, F1 bzw. Button „Tastenkürzel“ zeigt alle Kürzel an.
- [ ] Zuletzt genutzte Modi in der Modusleiste weiter nach vorne holen.
- [ ] Im Display irgendwo in den Ausdruck klicken und mittendrin weitertippen, statt nur hinten anhängen/löschen.
- [ ] Mini-Rechner: kleines Fenster, das immer im Vordergrund bleibt → z. B. neben IntelliJ.

---

## Animationen
- [x] Kleine Swing-Animationsbasis mit `javax.swing.Timer` ergänzen.
- [x] Button-Hover, Pressed und Klick-Feedback sanft animieren.
- [x] Display-Ergebnis und Fehlerzustände kurz visuell hervorheben.
- [x] Moduswechsel über aktiven Button und Fade-Overlay weicher wirken lassen.
- [x] History-Einträge und Favorit-Umschaltung mit kurzem Feedback versehen.
- [x] Graph-, Matrix- und Statistik-Ergebnisbereiche dezent hervorheben.
- [ ] Option zum Reduzieren von Animationen prüfen.
- [ ] Einstellung „weniger Bewegung“ wirklich umsetzen, sobald klar ist, welche Animationen bleiben dürfen.
- [ ] Animationen manuell auf langsamen Geräten prüfen.

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
- [x] `WissenschaftlichRechnerService`-Adapter entfernt (wurde nur noch vom eigenen Test genutzt).
- [x] `ShellActionRegistry` weiter beobachten: Wird sie zu groß? → Nein, Aktionen sind jetzt Einzeiler über `mitRefresh(...)`.
- [x] Persistence-Orchestrierung aus `TaschenrechnerUI` in einen Shell-Service auslagern.
- [ ] `GraphPanel`, `MatrixPanel` und `AusdruckEditor` in weiteren sicheren Schritten verkleinern.
- [ ] Gemeinsame Theme-Hilfen für einfache Mode-Panels prüfen, ohne Spezialpanels zu verbiegen.
- [ ] Optional `StandardActionFactory` nur einführen, falls Standardaktionen wachsen.
- [ ] Optional `WissenschaftlichActionFactory` einführen, falls wissenschaftliche Actions wachsen.
- [x] `KeyboardShortcutBinder` mit Tooltips synchron halten → gemeinsame Tabelle `Tastenkuerzel`, abgesichert per Test.
- [x] `ButtonTooltips` und `ShellActionRegistry` auf gemeinsame Action-Namen vereinheitlichen (Action-Name = Button-Text, `ShellActionRegistry.ausfuehren(...)`).
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
- [x] Unit Tests nachziehen: `BerechnungsService` stärker über Ergebnisobjekte statt Strings absichern.
- [x] Fehlerbehandlung vereinheitlichen: nur erwartete Exceptions fangen, IO-Fehler loggen (`DateiPersistenz`).
- [x] Einheitliches `ModePanel`-Konzept einführen: Jeder Modus bekommt klare Methoden für Modus, Theme, Sichtbarkeit und Winkelmodus.

### Großes MVP-Refactoring
- [x] Das große MVP-Refactoring planen, ohne die jetzige Modul-Struktur über den Haufen zu werfen.
- [ ] Die einzelnen Rechner-Modi bleiben ihre eigenen kleinen Welten und bekommen nur intern eine klare MVP-Aufteilung.
- [ ] Die Rollen simpel halten: Model kennt die Daten, View zeigt den Kram an und der Presenter kümmert sich um den Ablauf.
- [ ] Den Komplexmodus als erstes Versuchskaninchen umbauen, weil dort Model, State, Service und Formatter schon vorhanden sind.
- [ ] Nach dem ersten Umbau ehrlich prüfen: Ist der Code wirklich einfacher geworden oder haben wir nur mehr Dateien gebaut?
- [ ] Nur weitermachen, wenn der MVP-Aufbau beim Komplexmodus übersichtlicher und leichter testbar ist.
- [ ] Swing-Panels nach und nach abspecken: anzeigen, Eingaben annehmen und Klicks weitergeben sollte dort möglichst reichen.
- [ ] Berechnungen, Zustandsänderungen und längere Abläufe aus den Panels in den Presenter oder passende Services verschieben.
- [ ] Presenter ohne `JButton`, `JPanel` und sonstiges Swing-Zeug halten, damit man sie ohne echtes Fenster testen kann.
- [ ] Services und States von außen übergeben, statt sie irgendwo versteckt im Panel mit `new` zu erstellen.
- [ ] Keine Monster-Presenter bauen, die am Ende wieder alles können und nur anders heißen.
- [ ] Keine leeren Interfaces oder Mini-Klassen nur deshalb anlegen, weil MVP auf dem Papier danach aussieht.
- [ ] Standard und Wissenschaftlich gemeinsam betrachten, weil beide viel Rechnerlogik und dasselbe Display teilen.
- [ ] Danach Matrix, Statistik, Graph und PRG Stück für Stück umbauen – nicht alles in einem riesigen Rundumschlag.
- [ ] `TaschenrechnerUI` am Ende möglichst nur noch die Bauteile zusammenstecken lassen.
- [ ] Moduswechsel, Settings, Session und History in eine kleine Shell-Steuerung verschieben, wenn es dadurch wirklich ruhiger wird.
- [ ] Für jeden Presenter verständliche Unit Tests schreiben; die vorhandenen Paneltests bleiben als Sicherheitsnetz bestehen.
- [ ] Alten Misch-Code erst entfernen, wenn der jeweilige Modus nach dem Umbau genauso funktioniert wie vorher.
- [ ] Eine kurze Architektur-Seite schreiben: Wo gehört neuer Code hin und wie sieht ein einfacher MVP-Modus bei uns aus?

- [ ] Gemeinsames `ModeState`-Konzept entwerfen, ohne Spezialzustände wie Graph/Komplex/PRG in `RechnerZustand` zu quetschen.
- [x] Theme-Duplikation reduzieren → `BasisTheme`, feste Themes setzen nur noch ihre Farben.
- [x] Gemeinsame Button-Rollenzuordnung in `CalculatorButtonStyler` bündeln und für Standard/Wissenschaftlich/Shell nutzen.
- [ ] Theme-System um semantische Rollen erweitern: Display, Function, Operator, Danger, Accent, Disabled, Grid, Canvas.
- [x] Theme Default-Rollen für Disabled, Hover, Pressed, Danger, Grid, Canvas und Popup ergänzen.
- [x] Action-Bar-Popup und Graph-Canvas auf semantische Theme-Rollen umstellen.
- [ ] Benutzerdefinierte Themes erst nach Theme-Palette planen.
- [ ] Layouts auf gemeinsame Hilfsmethoden reduzieren.
- [ ] Große UI-Klassen verkleinern: `TaschenrechnerUI`, `ProgrammiererPanel`, `HistoryPanel`.
- [x] `HistoryPanel` bei strukturiertem Verlauf aufteilen.
- [x] `HistoryPanel` nach `ui.history` verschieben.
- [x] `HistoryEntryRenderer` aus `HistoryPanel` auslagern.
- [x] `TaschenrechnerUI` Theme-Rekursion in `ShellThemeApplier` auslagern.
- [x] Modus-Sichtbarkeitsregeln in `ModeVisibilityPolicy` auslagern.
- [x] Ausdruck-/Clipboard-Normalisierung aus `AusdruckEditor` auslagern.
- [x] `AusdruckEditor` Term- und Zahlenpositionslogik in package-private Helper auslagern.
- [x] Legacy-Verlaufstext-Mapping aus `HistoryPanel` nach `common.history` verschieben.
- [x] History-Suche in eine reine `HistoryFilter`-Hilfe auslagern.
- [x] Hardcoded Start-Hintergrund aus `ProgrammiererPanel` entfernen.
- [x] `ProgrammiererPanel` nach Funktionalitätsabschluss aufteilen.
- [ ] Build-System sauber entscheiden: Maven oder Gradle, danach Unit Tests mit einem Standardbefehl ausführbar machen.
- [ ] UI-Checkliste für alle Themes und Modi anlegen.
- [ ] Regressionstest-Suite vor großen Feature-Branches ausführen.

---

## Clean Code / Struktur
Kleine Aufräumrunde, damit man sich im Code auch in einem halben Jahr noch zurechtfindet.
Ergänzt die Punkte aus Refactoring und MVP oben.

### Alles nur einmal speichern
- [x] Das gewählte Theme wurde an drei Stellen gemerkt → jetzt nur noch in den Einstellungen, die doppelte `theme.txt` ist raus.
- [x] Alle Speicherorte der App an einer Stelle sammeln → `AppDateien`.
- [x] Eigener Ordner statt vieler einzelner Dateien im Benutzerordner → `~/.wissenschaftlicher_taschenrechner/`, alte Dateien ziehen beim ersten Start automatisch um.
- [x] Theme-Speicher testbar machen → hat sich erledigt, weil es ihn nicht mehr gibt.
- [x] Ein paar Tests schreiben noch in den echten Benutzerordner (über den Einstellungen-Dialog) → `SettingsDialog` bekommt den Speicherort jetzt von außen, Tests nutzen einen Testordner.

### Nicht alles doppelt schreiben
- [x] Zahlen sehen in Komplex, Matrix, Statistik und Einheiten jetzt gleich aus → eine gemeinsame `ZahlenAnzeige`.
- [x] Die Schriftart stand an fast 50 Stellen einzeln im Code → jetzt zentral über `AppFonts`.
- [x] Erfolg- und Fehlermeldungen in der Statuszeile funktionieren in allen Modi gleich → `StatusAnzeige`. Nebenbei behoben: Nach einem Fehler blieb die Zeile rot, auch wenn danach alles geklappt hat.
- [x] Theme-Färbung in Graph und Komplex mit der allgemeinen zusammenlegen → angeschaut, bewusst so gelassen: Der Graph hat zu viele Sonderfälle, das würde mehr verbiegen als helfen.

### Leichter lesbar
- [x] Methoden wie `setStatus(text, true)` versteht man nur mit Reinschauen → jetzt sprechend: `zeigeErfolg(...)` und `zeigeFehler(...)`.
- [x] Prüfen, ob `RechnerService` nur Sachen durchreicht → bleibt, weil er als zentrale Anlaufstelle für Standard und Wissenschaftlich gewollt ist. Steht jetzt auch so in der Klasse.
- [x] Namensregel aufschreiben: Oberfläche englisch, Rechenlogik deutsch → steht jetzt in der README.
- [x] Letzte Texte ohne Umlaute gefixt: „Einstellungen öffnen“, „Theme auswählen“.

### Kleinere Klassen
- [x] Die Graph-Zeichenfläche war ein Riesenteil → Zeichnen ist jetzt im `GraphZeichner`, das Panel kümmert sich nur noch um Maus und Menüs (von ~660 auf ~380 Zeilen).
- [x] Der Graph hat Fehler an zehn Stellen pauschal abgefangen → ungültige Stellen liefern jetzt einfach „keinen Wert“, die ganzen Fangnetze sind weg.
- [x] Programmierer-Logik aufteilen? → angeschaut, bleibt zusammen: viele kleine Methoden, die alle zum selben Rechner gehören. Aufteilen wäre nur mehr Dateien.

### Tests
- [x] Keine Extra-Methoden nur für Tests mehr im echten Code → die Tests suchen sich Buttons und Listen jetzt selbst raus, wie ein Mensch, der draufklickt.
- [x] Testklassen ordentlich in Ordner einsortieren, so wie im Programmcode → alle 43 umgezogen.

---

## Settings
- [x] Setting Menü hinzufügen
- [x] Setting Button richtig anzeigen
- [x] Setting MenüButtons im Untermenü Clickable machen
- [x] Funktionalität geben
- [x] Settings-Dialog: Änderungen optional mit Speichern/Abbrechen statt Sofortübernahme anbieten.
- [ ] Settings-Datei versionieren, falls später neue Felder dazukommen.
- [ ] Einstellungen mit einem Klick auf einen sicheren Standard zurücksetzen.
- [ ] Bei neuen Einstellungen kurz erklären, was sie verändern.
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
- [ ] Start ohne IntelliJ für andere Menschen wirklich testen.
- [ ] Portable Version mit allen nötigen Dateien vorbereiten.
- [ ] Kleine Release-Checkliste schreiben: testen, Version erhöhen, Changelog, Paket bauen.
- [ ] Druck-/Report-Ansicht optional planen.
- [ ] Lokale Projektdateien für komplexere Arbeiten planen, z. B. Graphen + Tabellen + Notizen.
- [ ] Lokalisierung Deutsch/Englisch optional planen.
- [ ] Dark/Light-Systemtheme automatisch übernehmen optional planen.
- [ ] Auto-Update oder Release-Paket optional planen.
- [ ] `.jar`-Build oder Installer optional planen.
- [ ] GitHub Releases vorbereiten.
- [ ] Changelog führen.
- [x] Version im UI anzeigen.
- [ ] Prüfungsmodus: nur Standard + Wissenschaftlich, Verlauf und Graph gesperrt, und man sieht direkt, dass er an ist.
- [ ] Rechner-Statistik als Spaß-Feature: „Deine Lieblingsfunktion: sin (42×)“ xD
- [ ] GitHub Actions: Tests bei jedem Push automatisch laufen lassen → geht erst nach Maven/Gradle.

## Nächste Runde (für volles Limit)
Große Brocken zuerst, kleine als Lückenfüller.

### Refactoring (größte Klassen zuerst)
- [ ] `HistoryPanel` (~600 Zeilen) aufteilen: Filter-/Suchleiste, Liste, Export-/Lösch-Aktionen in eigene Teile.
- [ ] `GraphPanel` (~580 Zeilen) aufteilen: Funktionsliste, Steuerleiste (Zoom/Bereich), Analyse-Ausgabe getrennt.
- [ ] `AusdruckEditor` (~510 Zeilen) prüfen: Cursor/Klammer-Logik von Undo/Redo trennen.
- [ ] `TaschenrechnerUI` (~430 Zeilen) weiter entschlacken: Aufbau des Fensters vs. Verdrahtung der Aktionen.
- [ ] `StatistikPanel` (~400 Zeilen): Eingabetabelle und Ergebnisanzeige trennen.
- [ ] `SettingsDialog` (~340 Zeilen): je Einstellungsbereich ein eigenes kleines Panel.
- [ ] `MatrixPanel` (~340 Zeilen): Matrix-Eingabegitter als eigene Komponente.
- [ ] Nach jedem Aufteilen: alle Tests grün, Zeilenzahl vorher/nachher hier notieren.
- [ ] Toten Code suchen (unbenutzte Methoden/Klassen/Imports) und löschen.
- [ ] Magische Zahlen (Pixelgrößen, Abstände, Grenzwerte) in benannte Konstanten.
- [ ] Alle `catch (Exception e)` durchgehen: konkrete Ausnahme oder bewusst kommentieren.

### Features
- [ ] Prüfungsmodus umsetzen (siehe Spätere Features) inkl. sichtbarem Hinweis in der Statuszeile.
- [ ] Graph als PNG exportieren.
- [ ] Matrix als CSV exportieren und importieren.
- [ ] Statistik-Daten aus CSV importieren.
- [ ] Verlauf zusätzlich als JSON exportieren.
- [ ] Einheiten: Favoriten-Umrechnungen merken.
- [ ] Wissenschaftlich: Ergebnis als Bruch anzeigen (z. B. 0,75 → 3/4), wo sinnvoll.
- [ ] Wissenschaftlich: Ans-Taste / letztes Ergebnis in neuen Ausdruck übernehmen (falls nicht vorhanden).
- [ ] Gleichungsmodus: lineare und quadratische Gleichungen lösen (Grundversion).
- [ ] Bruchmodus: Grundrechenarten mit Kürzen (Grundversion).
- [ ] Datumsrechner: Tage zwischen zwei Daten, Datum ± Tage.
- [ ] Rechner-Statistik („Lieblingsfunktion“) aus dem Verlauf berechnen.
- [ ] Lokalisierung DE/EN: alle UI-Texte an einer Stelle sammeln (erst sammeln, dann übersetzen).

### Tests
- [ ] Tests für die neuen Exporte (PNG nur „Datei entsteht“, CSV Inhalt prüfen).
- [ ] Parser-Grenzfälle: sehr lange Ausdrücke, verschachtelte Klammern, leere Eingabe, nur Operatoren.
- [ ] Programmierer: Überlauf an jeder Wortbreite testen.
- [ ] Session laden mit kaputter/alter Datei als Test absichern.
- [ ] Testabdeckung grob schätzen: welche Klasse in `src/common` hat noch gar keinen Test?

### Build / Release
- [ ] Maven oder Gradle entscheiden und einführen (Voraussetzung für GitHub Actions).
- [ ] Ausführbare `.jar` bauen und auf einem zweiten Rechner ohne IntelliJ starten.
- [ ] `CHANGELOG.md` anlegen, rückwirkend aus der Git-Historie füllen.
- [ ] Release-Checkliste in `docs/` schreiben.
- [ ] README: Screenshots aller Modi ergänzen.

### Skills / Werkzeuge (global in `~/.claude/skills`)
- [x] `todo-abarbeiten`: nächsten offenen Punkt nehmen, umsetzen, testen, abhaken, Commit vorschlagen.
- [x] `klasse-aufteilen`: große Klasse sicher in kleinere zerlegen, Tests vorher/nachher.
- [x] `ihk-lernzettel`: aus eigenem Code Lernzettel + Prüfungsfragen für AP1/AP2 machen.
- [ ] Skills ausprobieren und Beschreibungen nachschärfen, wenn sie nicht von selbst anspringen.
- [ ] Skill `java-test-schreiben` (JUnit-Stil dieses Projekts) überlegen.

---

## Legende
- [x] fertig
- [ ] offen
- [ ] ! in Arbeit

## Befehle
git add .
git commit -m ""
git push
