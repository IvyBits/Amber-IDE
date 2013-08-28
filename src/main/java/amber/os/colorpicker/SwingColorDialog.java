package amber.os.colorpicker;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JColorChooser;

/**
 *
 * @author Tudor
 */
public class SwingColorDialog implements IColorDialog {

    private Component parent = null;
    private Color color;
    private Color initial = Color.WHITE;

    public SwingColorDialog(Component parent) {
        setParent(parent);
        setInitialColor(Color.WHITE);
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public Color getInitialColor() {
        return initial;
    }

    public void setInitialColor(Color color) {
        initial = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean show() {
        color = JColorChooser.showDialog(parent, "Pick color...", initial);
        return color != null;
    }
}
