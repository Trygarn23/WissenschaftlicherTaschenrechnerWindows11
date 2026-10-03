package testhilfen;

import javax.swing.AbstractButton;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Sucht Komponenten im Swing-Baum, damit Tests ohne extra Test-Methoden im Produktionscode auskommen. */
public final class SwingSuche
{
    private SwingSuche()
    {
    }

    public static <T extends Component> List<T> alle(Container wurzel, Class<T> typ)
    {
        List<T> treffer = new ArrayList<>();
        for (Component kind : wurzel.getComponents())
        {
            if (typ.isInstance(kind))
            {
                treffer.add(typ.cast(kind));
            }
            if (kind instanceof Container container)
            {
                treffer.addAll(alle(container, typ));
            }
        }
        return treffer;
    }

    public static <T extends Component> T finde(Container wurzel, Class<T> typ, Predicate<T> passt)
    {
        return alle(wurzel, typ).stream()
                .filter(passt)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nicht gefunden: " + typ.getSimpleName()));
    }

    public static <T extends Component> T finde(Container wurzel, Class<T> typ)
    {
        return finde(wurzel, typ, komponente -> true);
    }

    public static AbstractButton button(Container wurzel, String text)
    {
        return finde(wurzel, AbstractButton.class, b -> text.equals(b.getText()));
    }
}
