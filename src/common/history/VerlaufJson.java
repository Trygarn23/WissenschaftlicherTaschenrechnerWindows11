package common.history;

import common.state.RechnerModus;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verlauf als JSON schreiben und wieder einlesen.
 * Kein JSON-Framework, weil das Projekt ohne Abhängigkeiten auskommt – das Format ist flach genug für einen kleinen eigenen Leser.
 */
public final class VerlaufJson
{
    private VerlaufJson()
    {
    }

    public static String schreibe(List<VerlaufEintrag> eintraege)
    {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < eintraege.size(); i++)
        {
            VerlaufEintrag eintrag = eintraege.get(i);
            json.append("  {")
                    .append("\"zeitpunkt\": ").append(text(eintrag.getZeitpunkt().toString())).append(", ")
                    .append("\"modus\": ").append(text(eintrag.getModus().name())).append(", ")
                    .append("\"ausdruck\": ").append(text(eintrag.getAusdruck())).append(", ")
                    .append("\"ergebnis\": ").append(text(eintrag.getErgebnis())).append(", ")
                    .append("\"favorit\": ").append(eintrag.isFavorit())
                    .append('}')
                    .append(i < eintraege.size() - 1 ? ",\n" : "\n");
        }
        return json.append("]\n").toString();
    }

    /**
     * Liest eine mit {@link #schreibe} erzeugte Datei. Unbekannte Felder werden ignoriert,
     * fehlende bekommen sinnvolle Standardwerte. Kaputtes JSON → {@link IllegalArgumentException} mit deutscher Meldung.
     */
    public static List<VerlaufEintrag> lese(String json)
    {
        Leser leser = new Leser(json == null ? "" : json);
        List<VerlaufEintrag> eintraege = new ArrayList<>();
        for (Object element : leser.leseListe())
        {
            if (element instanceof Map<?, ?> objekt)
            {
                eintraege.add(alsEintrag(objekt));
            }
        }
        return eintraege;
    }

    private static VerlaufEintrag alsEintrag(Map<?, ?> objekt)
    {
        String ausdruck = objekt.get("ausdruck") instanceof String s ? s : "";
        String ergebnis = objekt.get("ergebnis") instanceof String s ? s : "";
        boolean favorit = Boolean.TRUE.equals(objekt.get("favorit"));
        return new VerlaufEintrag(ausdruck, ergebnis, modus(objekt.get("modus")), zeitpunkt(objekt.get("zeitpunkt")), favorit);
    }

    private static RechnerModus modus(Object wert)
    {
        try
        {
            return wert instanceof String s ? RechnerModus.valueOf(s) : RechnerModus.STANDARD;
        }
        catch (IllegalArgumentException e)
        {
            return RechnerModus.STANDARD;
        }
    }

    private static LocalDateTime zeitpunkt(Object wert)
    {
        try
        {
            return wert instanceof String s ? LocalDateTime.parse(s) : LocalDateTime.now();
        }
        catch (DateTimeParseException e)
        {
            return LocalDateTime.now();
        }
    }

    private static String text(String wert)
    {
        StringBuilder out = new StringBuilder("\"");
        for (char c : wert.toCharArray())
        {
            switch (c)
            {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default ->
                {
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
                }
            }
        }
        return out.append('"').toString();
    }

    /** Rekursiver Abstieg über das kleine JSON-Subset: Listen, Objekte, Texte, Zahlen, true/false/null. */
    private static final class Leser
    {
        private final String json;
        private int pos;

        Leser(String json)
        {
            this.json = json;
        }

        List<Object> leseListe()
        {
            if (json.startsWith("﻿")) pos++;
            Object wert = wert();
            leerzeichen();
            if (pos != json.length()) fehler("Unerwarteter Text nach dem Ende");
            if (!(wert instanceof List<?> liste))
            {
                throw new IllegalArgumentException("Die Datei enthält keine Liste von Verlaufseinträgen.");
            }
            return new ArrayList<>(liste);
        }

        private Object wert()
        {
            leerzeichen();
            if (pos >= json.length()) fehler("Datei endet zu früh");
            char c = json.charAt(pos);
            return switch (c)
            {
                case '[' -> liste();
                case '{' -> objekt();
                case '"' -> zeichenkette();
                case 't' -> wort("true", Boolean.TRUE);
                case 'f' -> wort("false", Boolean.FALSE);
                case 'n' -> wort("null", null);
                default -> zahl();
            };
        }

        private List<Object> liste()
        {
            List<Object> liste = new ArrayList<>();
            pos++;
            leerzeichen();
            if (naechstes(']')) return liste;
            do
            {
                liste.add(wert());
                leerzeichen();
            }
            while (naechstes(','));
            erwarte(']');
            return liste;
        }

        private Map<String, Object> objekt()
        {
            Map<String, Object> objekt = new LinkedHashMap<>();
            pos++;
            leerzeichen();
            if (naechstes('}')) return objekt;
            do
            {
                leerzeichen();
                String name = zeichenkette();
                erwarte(':');
                objekt.put(name, wert());
                leerzeichen();
            }
            while (naechstes(','));
            erwarte('}');
            return objekt;
        }

        private String zeichenkette()
        {
            erwarte('"');
            StringBuilder text = new StringBuilder();
            while (pos < json.length())
            {
                char c = json.charAt(pos++);
                if (c == '"') return text.toString();
                if (c != '\\')
                {
                    text.append(c);
                    continue;
                }
                if (pos >= json.length()) break;
                char escape = json.charAt(pos++);
                switch (escape)
                {
                    case 'n' -> text.append('\n');
                    case 'r' -> text.append('\r');
                    case 't' -> text.append('\t');
                    case 'b' -> text.append('\b');
                    case 'f' -> text.append('\f');
                    case 'u' -> text.append(unicodeZeichen());
                    default -> text.append(escape);
                }
            }
            fehler("Text ohne schließendes Anführungszeichen");
            return null;
        }

        private char unicodeZeichen()
        {
            if (pos + 4 > json.length()) fehler("Unvollständiges \\u-Zeichen");
            try
            {
                char zeichen = (char) Integer.parseInt(json.substring(pos, pos + 4), 16);
                pos += 4;
                return zeichen;
            }
            catch (NumberFormatException e)
            {
                fehler("Ungültiges \\u-Zeichen");
                return 0;
            }
        }

        private Object zahl()
        {
            int start = pos;
            while (pos < json.length() && "+-0123456789.eE".indexOf(json.charAt(pos)) >= 0) pos++;
            if (start == pos) fehler("Unerwartetes Zeichen „" + json.charAt(pos) + "“");
            try
            {
                return Double.parseDouble(json.substring(start, pos));
            }
            catch (NumberFormatException e)
            {
                fehler("Ungültige Zahl");
                return null;
            }
        }

        private Object wort(String wort, Object wert)
        {
            if (!json.startsWith(wort, pos)) fehler("Unerwartetes Wort");
            pos += wort.length();
            return wert;
        }

        private boolean naechstes(char c)
        {
            if (pos < json.length() && json.charAt(pos) == c)
            {
                pos++;
                return true;
            }
            return false;
        }

        private void erwarte(char c)
        {
            leerzeichen();
            if (!naechstes(c)) fehler("„" + c + "“ erwartet");
        }

        private void leerzeichen()
        {
            while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        }

        private void fehler(String grund)
        {
            throw new IllegalArgumentException("Die Datei ist kein gültiger Verlauf: " + grund + " (Stelle " + pos + ").");
        }
    }
}
