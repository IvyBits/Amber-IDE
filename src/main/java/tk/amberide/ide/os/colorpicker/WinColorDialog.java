package tk.amberide.ide.os.colorpicker;

import tk.amberide.ide.os.OS;
import tk.amberide.ide.os.Win;
import java.awt.Color;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public class WinColorDialog implements IColorDialog {

    private Component parent = null;
    private String title = null;
    private long hwnd = 0;
    private long error = 0;
    private int color;
    private int initial = 0xFFFFFF;

    public WinColorDialog(Component parent) {
        setParent(parent);
        if (!OS.osLibrariesLoaded()) {
            throw new UnsupportedOperationException("AmberOS not loaded");
        }
        setInitialColor(Color.WHITE);
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
        hwnd = Win.getHWND(parent);
    }
    
    public Color getInitialColor() {
        return new Color(initial);
    }
    
    public void setInitialColor(Color color) {
        initial = color.getRGB();
    }
    
    public Color getColor() {
        return new Color(color);
    }

    public boolean show() {
        return showNative();
    }

    private native boolean showNative();
}
