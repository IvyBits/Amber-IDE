package tk.amberide.ide.gui.editor.map.tool._3d;

import tk.amberide.ide.gui.editor.map.MapContext;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public interface Tool3D {

    boolean apply(int x, int y, int z);

    void setContext(MapContext context);

    void doKey(int keycode);
    
    void doScroll(int delta);

    void draw(int x, int y, int z);

    Component getContextComponent();
}
