package tk.amberide.ide.gui.editor.map.tool._2d;

import org.lwjgl.input.Keyboard;
import tk.amberide.engine.input.AbstractKeyboard;
import tk.amberide.ide.gui.editor.map.MapContext;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Select2D extends AbstractTool2D implements Tool2DFeedbackProvider {
    Point selectStart;
    Rectangle selection;
    Object parent;

    public Select2D(Object parent, MapContext context) {
        super(context);
        this.parent = parent;
    }

    @Override
    public boolean apply(int x, int y) {
        mouseDown(x, y);
        return true;
    }

    @Override
    public Component getContextComponent() {
        return null;
    }

    @Override
    public Point getDrawRectLocation() {
        return selection == null ? new Point() : selection.getLocation();
    }

    @Override
    public Dimension getPreviewBorderSize() {
        return selection == null ? new Dimension() : selection.getSize();
    }

    @Override
    public BufferedImage getPreview() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0x000033CC);
        return image;
    }

    @Override
    public float getPreviewOpacity() {
        return .3f;
    }

    @Override
    public void mouseBegin(int x, int y) {
        selectStart = new Point(x, y);
        updateSelection(x, y);
    }

    @Override
    public void mouseUp(int x, int y) {
        updateSelection(x, y);
        selectStart = null; // if you mess up it NPEs so you catch it
    }

    @Override
    public void mouseDown(int x, int y) {
        updateSelection(x, y);
    }

    @Override
    public void doKey(int keycode) {
        super.doKey(keycode);       
        if (keycode == Keyboard.KEY_D && (AbstractKeyboard.isKeyDown(Keyboard.KEY_LCONTROL) || AbstractKeyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
            context.selection = selection = new Rectangle();
            if (parent instanceof Component)
                ((Component) parent).repaint();
        }
    }

    protected void updateSelection(int x, int y) {
        int x1 = Math.min(selectStart.x, x);
        int y1 = Math.min(selectStart.y, y);
        int x2 = Math.max(selectStart.x, x);
        int y2 = Math.max(selectStart.y, y);
        context.selection = selection = new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    @Override
    public boolean shouldUndo() {
        return false;
    }
}
