package tk.amberide.ide.gui.editor.map.tool._2d;

import tk.amberide.ide.gui.editor.map.MapContext;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    BufferedImage getPreview();

    float getPreviewOpacity();
}
