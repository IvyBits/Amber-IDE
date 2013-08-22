package amber.gui.editor.map.tool._3d;

import amber.data.map.Tile3D;
import amber.data.map.Tile3D.Angle;
import amber.gui.editor.map.MapContext;

/**
 *
 * @author Tudor
 */
public abstract class AbstractTool3D implements Tool3D {

    protected MapContext context;

    public AbstractTool3D(MapContext context) {
        setContext(context);
    }

    public void setContext(MapContext context) {
        this.context = context;
    }

    public void doKey(int keycode) {
    }

    public void doScroll(int delta) {
    }

    protected boolean isInBounds(int x, int y) {
        return !(x < 0 || x > context.map.getWidth() - 1 || y < 0 || y > context.map.getLength() - 1);
    }
}
