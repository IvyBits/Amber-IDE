package tk.amberide.ide.gui.editor.map.tool._2d;

import tk.amberide.ide.gui.editor.map.MapContext;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Select2D extends AbstractTool2D implements Tool2DFeedbackProvider {
    Point selectStart, selectEnd;
    Rectangle selection;

    public Select2D(MapContext context) {
        super(context);
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
    public Dimension getDrawRectangleSize() {
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
        System.out.printf("Selection Start: (%d, %d)\n", x, y);
        selectStart = new Point(x, y);
        selectEnd = new Point(x, y);
    }

    @Override
    public void mouseUp(int x, int y) {
        mouseDown(x, y);
    }

    @Override
    public void mouseDown(int x, int y) {
        selectEnd.x = x;
        selectEnd.y = y;
        int x1 = Math.min(selectStart.x, selectEnd.x);
        int y1 = Math.min(selectStart.y, selectEnd.y);
        int x2 = Math.max(selectStart.x, selectEnd.x);
        int y2 = Math.max(selectStart.y, selectEnd.y);
        selection = new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
        System.out.printf("Selection: (%4d, %4d) (%d, %d)\n", x, y, x2 - x1 + 1, y2 - y1 + 1);
    }

    @Override
    public boolean shouldUndo() {
        return false;
    }
}
