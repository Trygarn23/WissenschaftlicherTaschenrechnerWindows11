package modes.matrix.ui;

import common.formatting.ZahlenEingabe;
import modes.matrix.formatting.MatrixCsv;
import modes.matrix.formatting.MatrixFormatter;
import modes.matrix.logic.MatrixRechnerService;
import modes.matrix.model.InverseErgebnis;
import modes.matrix.model.LgsLoesung;
import modes.matrix.model.Matrix;
import modes.matrix.model.RechenSchritt;
import common.state.RechnerModus;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MatrixPanel extends JPanel implements ModePanel
{
    private final MatrixRechnerService service = new MatrixRechnerService();
    private final MatrixFormatter formatter = new MatrixFormatter();

    private final MatrixEingabeGitter matrixA = new MatrixEingabeGitter("Matrix A", "A");
    private final MatrixEingabeGitter matrixB = new MatrixEingabeGitter("Matrix B", "B");
    private final JTextField skalarField = new JTextField("2");
    private final JCheckBox schritteBox = new JCheckBox("Rechenschritte zeigen");
    private final JTextArea resultArea = new JTextArea("Bereit");
    private final JLabel statusLabel = new JLabel("Matrixmodus bereit");
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel, resultArea);
    private final List<JButton> buttons = new ArrayList<>();

    private AppTheme theme;

    public MatrixPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        add(buildInputArea(), BorderLayout.CENTER);
        add(buildResultArea(), BorderLayout.EAST);
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.MATRIX;
    }

    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());
        applyThemeRecursively(this);
        resultArea.setBackground(theme.displayBackground());
        resultArea.setForeground(theme.displayForeground());
        resultArea.setCaretColor(theme.displayForeground());
        statusAnzeige.setTheme(theme);
        matrixA.applyTheme(theme);
        matrixB.applyTheme(theme);

        skalarField.setFont(AppFonts.normal(15));
        ModernButtonStyler.styleInput(skalarField, theme);
        skalarField.setCaretColor(theme.displayForeground());

        schritteBox.setOpaque(false);
        schritteBox.setFont(AppFonts.normal(13));
        schritteBox.setForeground(theme.displayForeground());

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }
    }

    private JPanel buildInputArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel matrices = new JPanel(new GridLayout(1, 2, 12, 0));
        matrices.setOpaque(false);
        matrices.add(matrixA);
        matrices.add(matrixB);

        JPanel controls = new JPanel(new GridLayout(0, 4, 8, 8));
        controls.setOpaque(false);
        controls.add(createButton("A + B", () -> showMatrix(service.addiere(matrixA.lese(), matrixB.lese()), "Addition")));
        controls.add(createButton("A - B", () -> showMatrix(service.subtrahiere(matrixA.lese(), matrixB.lese()), "Subtraktion")));
        controls.add(createButton("A × B", () -> showMatrix(service.multipliziere(matrixA.lese(), matrixB.lese()), "Multiplikation")));
        controls.add(createButton("k × A", () -> showMatrix(service.skalarMultiplizieren(matrixA.lese(), ZahlenEingabe.lese(skalarField.getText())), "Skalarmultiplikation")));
        controls.add(createButton("A^T", () -> showMatrix(service.transponiere(matrixA.lese()), "Transponieren A")));
        controls.add(createButton("B^T", () -> showMatrix(service.transponiere(matrixB.lese()), "Transponieren B")));
        controls.add(createButton("spur A", () -> showScalar(service.spur(matrixA.lese()), "Spur A")));
        controls.add(createButton("spur B", () -> showScalar(service.spur(matrixB.lese()), "Spur B")));
        controls.add(createButton("rang A", () -> showScalar(service.rang(matrixA.lese()), "Rang A")));
        controls.add(createButton("rang B", () -> showScalar(service.rang(matrixB.lese()), "Rang B")));
        controls.add(createButton("det A", () -> showScalar(service.determinante(matrixA.lese()), "Determinante A")));
        controls.add(createButton("det B", () -> showScalar(service.determinante(matrixB.lese()), "Determinante B")));
        controls.add(createButton("A⁻¹", () -> showInverse(matrixA, "Inverse A")));
        controls.add(createButton("B⁻¹", () -> showInverse(matrixB, "Inverse B")));
        controls.add(createButton("Ax = b", this::showGleichungssystem));
        controls.add(schritteBox);
        controls.add(createButton("A → CSV", () -> exportCsv(matrixA, "matrix-a.csv")));
        controls.add(createButton("CSV → A", () -> importCsv(matrixA, "A")));
        controls.add(createButton("B → CSV", () -> exportCsv(matrixB, "matrix-b.csv")));
        controls.add(createButton("CSV → B", () -> importCsv(matrixB, "B")));
        controls.add(wrapScalarInput());
        controls.add(createButton("Leeren", this::clearMatrices));

        schritteBox.setFocusable(false);
        schritteBox.setToolTipText("Zeigt bei Inverse und Ax = b jeden Gauß-Jordan-Schritt");

        panel.add(matrices, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultArea()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(330, 0));

        JLabel title = new JLabel("Ergebnis");
        title.setFont(AppFonts.fett(24));

        resultArea.setEditable(false);
        resultArea.setFont(AppFonts.festeBreite(18));
        resultArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        statusLabel.setFont(AppFonts.normal(14));

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel wrapScalarInput()
    {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("k"), BorderLayout.WEST);
        panel.add(skalarField, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(e -> runSafely(action));
        buttons.add(button);
        return button;
    }

    private void runSafely(Runnable action)
    {
        try
        {
            action.run();
        }
        catch (IllegalArgumentException | ArithmeticException e)
        {
            resultArea.setText("Fehler");
            statusAnzeige.zeigeFehler(e.getMessage(), "Ungültige Matrixeingabe");
        }
    }

    private void showMatrix(Matrix matrix, String status)
    {
        resultArea.setText(formatter.formatiere(matrix));
        statusAnzeige.zeigeErfolg(status + " erfolgreich");
    }

    private void showScalar(double value, String status)
    {
        resultArea.setText(formatter.formatiereDouble(value));
        statusAnzeige.zeigeErfolg(status + " berechnet");
    }

    private void showInverse(MatrixEingabeGitter gitter, String status)
    {
        InverseErgebnis ergebnis = service.invertiere(gitter.lese());
        showMitSchritten(formatter.formatiere(ergebnis.inverse()), ergebnis.schritte());
        statusAnzeige.zeigeErfolg(status + " berechnet");
    }

    private void showGleichungssystem()
    {
        LgsLoesung loesung = service.loese(matrixA.lese(), matrixB.lese());
        String text = loesung.art() == LgsLoesung.Art.EINDEUTIG
                ? formatter.formatiereLoesung(loesung.loesung())
                : "Stufenform (A|b):" + System.lineSeparator() + formatter.formatiere(loesung.stufenform());
        showMitSchritten(text, loesung.schritte());
        statusAnzeige.zeigeErfolg(loesung.meldung());
    }

    private void showMitSchritten(String ergebnis, List<RechenSchritt> schritte)
    {
        String text = ergebnis;
        if (schritteBox.isSelected() && !schritte.isEmpty())
        {
            text += System.lineSeparator() + System.lineSeparator() + formatter.formatiereSchritte(schritte);
        }
        resultArea.setText(text);
        resultArea.setCaretPosition(0);
    }

    private void exportCsv(MatrixEingabeGitter gitter, String vorschlag)
    {
        String csv = MatrixCsv.schreibe(gitter.lese());
        JFileChooser chooser = csvChooser("Matrix als CSV exportieren");
        chooser.setSelectedFile(new File(vorschlag));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        Path file = chooser.getSelectedFile().toPath();
        if (!file.getFileName().toString().toLowerCase().endsWith(".csv"))
        {
            file = file.resolveSibling(file.getFileName() + ".csv");
        }

        try
        {
            // BOM, damit Excel UTF-8 erkennt (wie beim Verlauf-Export).
            Files.writeString(file, "﻿" + csv, StandardCharsets.UTF_8);
            statusAnzeige.zeigeErfolg("Gespeichert: " + file.getFileName());
        }
        catch (IOException e)
        {
            statusAnzeige.zeigeFehler("Datei konnte nicht gespeichert werden.");
        }
    }

    private void importCsv(MatrixEingabeGitter gitter, String name)
    {
        JFileChooser chooser = csvChooser("CSV in Matrix " + name + " laden");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        try
        {
            gitter.setze(MatrixCsv.lese(Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8)));
            statusAnzeige.zeigeErfolg("Matrix " + name + " geladen");
        }
        catch (IOException e)
        {
            statusAnzeige.zeigeFehler("Datei konnte nicht gelesen werden (UTF-8 erwartet).");
        }
    }

    private JFileChooser csvChooser(String titel)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(titel);
        chooser.setFileFilter(new FileNameExtensionFilter("CSV-Datei", "csv"));
        return chooser;
    }

    private void clearMatrices()
    {
        matrixA.leeren();
        matrixB.leeren();
        skalarField.setText("2");
        resultArea.setText("Bereit");
        statusAnzeige.zeigeErfolg("Matrixmodus bereit");
    }

    private void applyThemeRecursively(Component component)
    {
        if (component instanceof JLabel label)
        {
            label.setForeground(theme.displayForeground());
        }
        else if (component instanceof JPanel panel && panel != this)
        {
            panel.setBackground(theme.panelBackground());
        }

        if (component instanceof Container container)
        {
            for (Component child : container.getComponents())
            {
                applyThemeRecursively(child);
            }
        }
    }
}
