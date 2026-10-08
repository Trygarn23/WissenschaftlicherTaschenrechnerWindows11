package modes.netzwerk.ui;

import common.state.RechnerModus;
import modes.netzwerk.formatting.NetzwerkBericht;
import modes.netzwerk.logic.Ipv4Rechner;
import modes.netzwerk.logic.Ipv6Rechner;
import modes.netzwerk.model.Ipv4Netz;
import modes.netzwerk.model.Teilung;
import ui.shell.ModePanel;
import ui.shell.StatusAnzeige;
import ui.theme.AppFonts;
import ui.theme.AppTheme;
import ui.theme.ModernButtonStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NetzwerkPanel extends JPanel implements ModePanel
{
    private final JTextField adressField = new JTextField("192.168.1.10/26");
    private final JTextField maskenField = new JTextField();
    private final JTextField teilnetzField = new JTextField("4");
    private final JTextField ipv6Field = new JTextField("2001:0db8:0000:0000:0000:ff00:0042:8329");
    private final JLabel resultLabel = new JLabel(" ");
    private final JLabel detailLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel("Bereit");
    private final JTextArea ausgabe = new JTextArea();
    private final StatusAnzeige statusAnzeige = new StatusAnzeige(statusLabel);
    private final List<JButton> buttons = new ArrayList<>();
    private final List<JTextField> fields = List.of(adressField, maskenField, teilnetzField, ipv6Field);

    private AppTheme theme;

    public NetzwerkPanel()
    {
        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        add(buildInputPanel(), BorderLayout.WEST);
        add(buildResultPanel(), BorderLayout.CENTER);

        adressField.addActionListener(e -> berechneSubnetz());
        maskenField.addActionListener(e -> berechneSubnetz());
        teilnetzField.addActionListener(e -> berechneTeilnetze());
        ipv6Field.addActionListener(e -> kuerzeIpv6());
        berechneSubnetz();
    }

    @Override
    public RechnerModus getRechnerModus()
    {
        return RechnerModus.NETZWERK;
    }

    @Override
    public void applyTheme(AppTheme theme)
    {
        this.theme = theme;
        setBackground(theme.windowBackground());

        for (JTextField field : fields)
        {
            field.setFont(AppFonts.normal(15));
            ModernButtonStyler.styleInput(field, theme);
            field.setCaretColor(theme.displayForeground());
        }

        for (JButton button : buttons)
        {
            ModernButtonStyler.styleButton(button, theme, theme.toggleButtonBackground(), theme.toggleButtonForeground());
        }

        applyThemeToChildren(this);
        ausgabe.setBackground(theme.inputBackground());
        ausgabe.setForeground(theme.displayForeground());
        ausgabe.setCaretColor(theme.displayForeground());
        resultLabel.setForeground(theme.displayForeground());
        detailLabel.setForeground(theme.secondaryDisplayForeground());
        statusAnzeige.setTheme(theme);
    }

    private JPanel buildInputPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

        panel.add(section("Subnetz (IPv4)",
                wrapField("IP-Adresse, z. B. 192.168.1.10/26", adressField),
                wrapField("Maske (optional): /26, 26 oder 255.255.255.192", maskenField),
                buttonRow(createButton("Subnetz", this::berechneSubnetz), createButton("Maske umrechnen", this::rechneMaskeUm))));
        panel.add(section("Teilnetze",
                wrapField("Anzahl Teilnetze", teilnetzField),
                buttonRow(createButton("Teilnetze", this::berechneTeilnetze))));
        panel.add(section("IPv6",
                wrapField("IPv6-Adresse", ipv6Field),
                buttonRow(createButton("IPv6 kürzen", this::kuerzeIpv6), createButton("IPv6 ausschreiben", this::schreibeIpv6Aus))));
        panel.add(buttonRow(createButton("Kopieren", this::kopiere)));
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel section(String titel, JComponent... inhalte)
    {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        JLabel label = new JLabel(titel);
        label.setFont(AppFonts.fett(18));
        panel.add(label);
        for (JComponent inhalt : inhalte)
        {
            panel.add(inhalt);
        }
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel wrapField(String labelText, JTextField field)
    {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buttonRow(JButton... rowButtons)
    {
        JPanel panel = new JPanel(new GridLayout(1, 0, 8, 0));
        panel.setOpaque(false);
        for (JButton button : rowButtons)
        {
            panel.add(button);
        }
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel buildResultPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel title = new JLabel("Netzwerk");
        title.setFont(AppFonts.fett(28));

        JPanel kopf = new JPanel(new GridLayout(0, 1, 0, 6));
        kopf.setOpaque(false);
        resultLabel.setFont(AppFonts.normal(32));
        detailLabel.setFont(AppFonts.normal(16));
        statusLabel.setFont(AppFonts.normal(14));
        kopf.add(title);
        kopf.add(resultLabel);
        kopf.add(detailLabel);
        kopf.add(statusLabel);

        ausgabe.setEditable(false);
        ausgabe.setFont(AppFonts.festeBreite(14));
        ausgabe.setLineWrap(false);

        panel.add(kopf, BorderLayout.NORTH);
        panel.add(new JScrollPane(ausgabe), BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Runnable action)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(e -> action.run());
        buttons.add(button);
        return button;
    }

    private void berechneSubnetz()
    {
        fuehreAus("Subnetz berechnet", () ->
        {
            Ipv4Netz netz = Ipv4Rechner.leseNetz(adressField.getText(), maskenField.getText());
            zeige(Ipv4Rechner.alsText(netz.netzadresse()) + "/" + netz.praefix(),
                    "Broadcast " + Ipv4Rechner.alsText(netz.broadcast()) + " · " + NetzwerkBericht.zahl(netz.anzahlHosts()) + " Hosts");
            return NetzwerkBericht.subnetz(netz);
        });
    }

    private void berechneTeilnetze()
    {
        fuehreAus("Teilnetze berechnet", () ->
        {
            Ipv4Netz netz = Ipv4Rechner.leseNetz(adressField.getText(), maskenField.getText());
            Teilung teilung = Ipv4Rechner.teile(netz, leseAnzahl(teilnetzField.getText()));
            zeige(NetzwerkBericht.zahl(teilung.anzahl()) + " × /" + teilung.neuerPraefix(),
                    NetzwerkBericht.zahl(teilung.netze().getFirst().anzahlHosts()) + " nutzbare Hosts je Teilnetz");
            return NetzwerkBericht.teilnetze(teilung);
        });
    }

    private void rechneMaskeUm()
    {
        fuehreAus("Maske umgerechnet", () ->
        {
            String eingabe = maskenField.getText().isBlank() ? adressField.getText() : maskenField.getText();
            int slash = eingabe.indexOf('/');
            String maske = slash > 0 ? eingabe.substring(slash) : eingabe;
            int praefix = Ipv4Rechner.lesePraefix(maske);
            zeige("/" + praefix + " = " + Ipv4Rechner.alsText(Ipv4Netz.maske(praefix)), NetzwerkBericht.hinweis(praefix));
            return NetzwerkBericht.maske(praefix);
        });
    }

    private void kuerzeIpv6()
    {
        fuehreAus("IPv6 gekürzt", () -> ipv6(true));
    }

    private void schreibeIpv6Aus()
    {
        fuehreAus("IPv6 ausgeschrieben", () -> ipv6(false));
    }

    private String ipv6(boolean kurz)
    {
        String gekuerzt = Ipv6Rechner.kuerze(ipv6Field.getText());
        String ausgeschrieben = Ipv6Rechner.schreibeAus(ipv6Field.getText());
        zeige(kurz ? gekuerzt : ausgeschrieben, kurz ? ausgeschrieben : gekuerzt);
        return NetzwerkBericht.ipv6(gekuerzt, ausgeschrieben);
    }

    private static int leseAnzahl(String text)
    {
        String eingabe = text.strip();
        if (!eingabe.matches("\\d{1,10}") || Long.parseLong(eingabe) > Integer.MAX_VALUE)
        {
            throw new IllegalArgumentException("„" + eingabe + "“ ist keine gültige Anzahl Teilnetze.");
        }
        return Integer.parseInt(eingabe);
    }

    private void fuehreAus(String status, Supplier<String> berechnung)
    {
        try
        {
            ausgabe.setText(berechnung.get());
            ausgabe.setCaretPosition(0);
            statusAnzeige.zeigeErfolg(status);
        }
        catch (IllegalArgumentException e)
        {
            statusAnzeige.zeigeFehler(e.getMessage());
        }
    }

    private void zeige(String ergebnis, String detail)
    {
        resultLabel.setText(ergebnis);
        detailLabel.setText(detail.isEmpty() ? " " : detail);
    }

    private void kopiere()
    {
        String text = ausgabe.getText().isEmpty() ? resultLabel.getText() : ausgabe.getText();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        statusAnzeige.zeigeErfolg("Ergebnis kopiert");
    }

    private void applyThemeToChildren(Component component)
    {
        if (component instanceof JLabel label && label != resultLabel && label != detailLabel && label != statusLabel)
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
                applyThemeToChildren(child);
            }
        }
    }
}
