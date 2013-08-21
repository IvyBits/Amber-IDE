package amber.gui.editor.map.tool._2d;

import amber.gui.editor.map.MapContext;
import java.awt.Dimension;

/**
 *
 * @author Tudor
 */
public abstract class AbstractTool2D implements Tool2D {

    protected MapContext context;

    public AbstractTool2D(MapContext context) {
        setContext(context);
    }

    public void setContext(MapContext context) {
        this.context = context;
    }

    public Dimension getDrawRectangleSize() {
        return context.tileSelection != null ? new Dimension(context.tileSelection.length, context.tileSelection[0].length) : new Dimension(0, 0);
    }

    public void doKey(int keycode) {
    }

    protected boolean isInBounds(int x, int y) {
        return !(x < 0 || x > context.map.getWidth() - 1 || y < 0 || y > context.map.getLength() - 1);
    }
}
