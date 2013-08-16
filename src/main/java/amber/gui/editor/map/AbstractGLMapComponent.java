package amber.gui.editor.map;

import amber.data.map.LevelMap;
import amber.gui.exc.ErrorHandler;
import java.awt.Component;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * @author Tudor
 */
public abstract class AbstractGLMapComponent extends AWTGLCanvas implements IMapComponent {

    protected final MapContext context = new MapContext();

    public AbstractGLMapComponent() throws LWJGLException {
        super(new PixelFormat(8, 16, 0));
    }

    public AbstractGLMapComponent(LevelMap map) throws LWJGLException {
        this();
        context.map = map;
    }

    @Override
    protected void exceptionOccurred(LWJGLException gle) {
        ErrorHandler.alert(gle);
    }

    public MapContext getMapContext() {
        return context;
    }

    public Component getComponent() {
        return this;
    }

    protected boolean isInBounds(int x, int y) {
        return !(x < 0 || x > context.map.getWidth() - 1 || y < 0 || y > context.map.getLength() - 1);
    }
}
