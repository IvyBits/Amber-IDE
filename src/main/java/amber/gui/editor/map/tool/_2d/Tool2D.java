package amber.gui.editor.map.tool._2d;

import amber.gui.editor.map.MapContext;
import java.awt.Component;
import java.awt.Dimension;

/**
 *
 * @author Tudor
 */
public interface Tool2D {

    boolean apply(int x, int y);
    
    void setContext(MapContext context);
    
    void doKey(int keycode);
 
    Dimension getDrawRectangleSize();
    
    Component getContextComponent();
}
