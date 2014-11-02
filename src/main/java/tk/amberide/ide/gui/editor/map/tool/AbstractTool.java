package tk.amberide.ide.gui.editor.map.tool;

import tk.amberide.ide.gui.editor.map.MapContext;

/**
 *
 * @author Tudor
 */
public abstract class AbstractTool implements Tool {

    protected MapContext context;

    public AbstractTool(MapContext context) {
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
