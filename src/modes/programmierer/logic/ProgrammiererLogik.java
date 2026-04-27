package modes.programmierer.logic;

import modes.programmierer.model.Basis;
import modes.programmierer.model.ProgrammiererState;
import modes.programmierer.model.Wortbreite;

public class ProgrammiererLogik
{
    private enum Operation
    {
        NONE,
        ADD,
        SUB,
        AND,
        OR,
        XOR
    }

    private final ProgrammiererState state = new ProgrammiererState();
    private final StringBuilder eingabe = new StringBuilder();

    private long linkerOperand = 0;
    private Operation pendingOperation = Operation.NONE;
    private boolean neuesEingabefeld = false;

    public void setBasis(Basis basis)
    {
        state.setBasis(basis);
        eingabeSetzenAusWert();
    }

    public Basis getBasis()
    {
        return state.getBasis();
    }

    public void setWortbreite(Wortbreite wortbreite)
    {
        state.setWortbreite(wortbreite);
        state.setWert(maskiere(state.getWert()));
        linkerOperand = maskiere(linkerOperand);
        eingabeSetzenAusWert();
    }

    public Wortbreite getWortbreite()
    {
        return state.getWortbreite();
    }

    public void clear()
    {
        eingabe.setLength(0);
        state.setWert(0);
        linkerOperand = 0;
        pendingOperation = Operation.NONE;
        neuesEingabefeld = false;
    }

    public void backspace()
    {
        if (neuesEingabefeld)
        {
            clear();
            return;
        }

        if (!eingabe.isEmpty())
        {
            eingabe.deleteCharAt(eingabe.length() - 1);
        }

        aktualisiereWertAusEingabe();
    }

    public void digitEingeben(String zeichen)
    {
        if (zeichen == null || zeichen.isBlank())
        {
            return;
        }

        if (!istGueltigFuerBasis(zeichen, state.getBasis()))
        {
            return;
        }

        if (neuesEingabefeld)
        {
            eingabe.setLength(0);
            neuesEingabefeld = false;
        }

        if (eingabe.toString().equals("0"))
        {
            eingabe.setLength(0);
        }

        eingabe.append(zeichen.toUpperCase());
        aktualisiereWertAusEingabe();
    }

    public void vorzeichenWechseln()
    {
        if (state.getBasis() != Basis.DEC)
        {
            return;
        }

        if (neuesEingabefeld)
        {
            eingabeSetzenAusWert();
            neuesEingabefeld = false;
        }

        if (eingabe.isEmpty())
        {
            eingabe.append("-");
            state.setWert(0);
            return;
        }

        if (eingabe.charAt(0) == '-')
        {
            eingabe.deleteCharAt(0);
        }
        else
        {
            eingabe.insert(0, '-');
        }

        aktualisiereWertAusEingabe();
    }

    public void not()
    {
        state.setWert(maskiere(~state.getWert()));
        pendingOperation = Operation.NONE;
        neuesEingabefeld = true;
        eingabeSetzenAusWert();
    }

    public void shiftLeft()
    {
        state.setWert(maskiere(state.getWert() << 1));
        pendingOperation = Operation.NONE;
        neuesEingabefeld = true;
        eingabeSetzenAusWert();
    }

    public void shiftRight()
    {
        state.setWert(maskiere(state.getWert() >> 1));
        pendingOperation = Operation.NONE;
        neuesEingabefeld = true;
        eingabeSetzenAusWert();
    }

    public void plus()
    {
        setzeOperation(Operation.ADD);
    }

    public void minus()
    {
        setzeOperation(Operation.SUB);
    }

    public void and()
    {
        setzeOperation(Operation.AND);
    }

    public void or()
    {
        setzeOperation(Operation.OR);
    }

    public void xor()
    {
        setzeOperation(Operation.XOR);
    }

    public void berechne()
    {
        if (pendingOperation == Operation.NONE)
        {
            return;
        }

        long rechterOperand = state.getWert();
        long ergebnis = switch (pendingOperation)
        {
            case ADD -> linkerOperand + rechterOperand;
            case SUB -> linkerOperand - rechterOperand;
            case AND -> linkerOperand & rechterOperand;
            case OR -> linkerOperand | rechterOperand;
            case XOR -> linkerOperand ^ rechterOperand;
            case NONE -> rechterOperand;
        };

        state.setWert(maskiere(ergebnis));
        pendingOperation = Operation.NONE;
        neuesEingabefeld = true;
        eingabeSetzenAusWert();
    }

    public String getAnzeige(Basis basis)
    {
        long wert = maskiere(state.getWert());

        return switch (basis)
        {
            case BIN -> Long.toBinaryString(unsignedDarstellung(wert));
            case OCT -> Long.toOctalString(unsignedDarstellung(wert));
            case DEC -> Long.toString(wert);
            case HEX -> Long.toHexString(unsignedDarstellung(wert)).toUpperCase();
        };
    }

    public String getAktuelleEingabe()
    {
        return eingabe.isEmpty() ? "0" : eingabe.toString();
    }

    public String getPendingOperationText()
    {
        return switch (pendingOperation)
        {
            case ADD -> "+";
            case SUB -> "-";
            case AND -> "AND";
            case OR -> "OR";
            case XOR -> "XOR";
            case NONE -> "";
        };
    }

    private void setzeOperation(Operation operation)
    {
        if (pendingOperation != Operation.NONE && !neuesEingabefeld)
        {
            berechne();
        }

        linkerOperand = state.getWert();
        pendingOperation = operation;
        neuesEingabefeld = true;
    }

    private void aktualisiereWertAusEingabe()
    {
        if (eingabe.length() == 0 || "-".contentEquals(eingabe))
        {
            state.setWert(0);
            return;
        }

        String text = eingabe.toString();

        try
        {
            long wert = (state.getBasis() == Basis.DEC)
                    ? Long.parseLong(text)
                    : Long.parseUnsignedLong(text, state.getBasis().getRadix());

            state.setWert(maskiere(wert));
        }
        catch (NumberFormatException ex)
        {
            state.setWert(0);
        }
    }

    private void eingabeSetzenAusWert()
    {
        eingabe.setLength(0);
        eingabe.append(getAnzeige(state.getBasis()));
    }

    private boolean istGueltigFuerBasis(String zeichen, Basis basis)
    {
        String z = zeichen.toUpperCase();

        return switch (basis)
        {
            case BIN -> z.matches("[01]");
            case OCT -> z.matches("[0-7]");
            case DEC -> z.matches("[0-9]");
            case HEX -> z.matches("[0-9A-F]");
        };
    }

    private long maskiere(long wert)
    {
        int bits = state.getWortbreite().getBits();

        if (bits == 64)
        {
            return wert;
        }

        long maske = (1L << bits) - 1;
        long masked = wert & maske;

        if (state.getBasis() == Basis.DEC)
        {
            long signBit = 1L << (bits - 1);
            if ((masked & signBit) != 0)
            {
                masked |= ~maske;
            }
        }

        return masked;
    }

    private long unsignedDarstellung(long wert)
    {
        int bits = state.getWortbreite().getBits();

        if (bits == 64)
        {
            return wert;
        }

        long maske = (1L << bits) - 1;
        return wert & maske;
    }
}