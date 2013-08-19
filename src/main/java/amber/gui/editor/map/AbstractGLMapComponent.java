package amber.gui.editor.map;

import amber.data.OS;
import amber.data.map.LevelMap;
import static amber.data.map.Tile3D.Angle._180;
import static amber.data.map.Tile3D.Angle._45;
import static amber.data.map.Tile3D.Angle._90;
import amber.data.map.codec.Codec;
import amber.gui.misc.ErrorHandler;
import amber.input.AbstractKeyboard;
import static amber.input.AbstractKeyboard.getEventKey;
import static amber.input.AbstractKeyboard.getEventKeyState;
import static amber.input.AbstractKeyboard.isKeyDown;
import amber.input.AbstractMouse;
import java.awt.Component;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * @author Tudor
 */
public abstract class AbstractGLMapComponent extends AWTGLCanvas implements IMapComponent {

    protected final MapContext context = new MapContext();
    protected Thread renderingThread;
    protected boolean running;

    static {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
    }

    public AbstractGLMapComponent() throws LWJGLException {
        super(new PixelFormat(8, 16, 0));
    }

    public AbstractGLMapComponent(LevelMap map) throws LWJGLException {
        this();
        context.map = map;
        (renderingThread = new Thread() {
            @Override
            public void run() {
                running = true;
                while (running) {
                    if (isShowing() && isFocusOwner()) {
                        repaint();
                        Display.sync(120);
                    } else {
                        OS.sleep(300); // Prevent useless CPU cycles when not showing
                    }
                }
            }
        }).start();
    }

    @Override
    protected void paintGL() {
        pollInput();
    }

    protected void pollInput() {
        try {
            if (!AbstractKeyboard.isCreated()) {
                System.out.println("new keyboard");
                AbstractKeyboard.create(AbstractKeyboard.AWT);
            }
            if (!AbstractMouse.isCreated()) {
                AbstractMouse.create(AbstractMouse.AWT);
            }
        } catch (LWJGLException ex) {
            ex.printStackTrace();
            return;
        }
        while (AbstractKeyboard.next()) {
            if (getEventKeyState()) {
                int key;
                switch (key = getEventKey()) {
                    case Keyboard.KEY_Z:
                        if (isKeyDown(Keyboard.KEY_RCONTROL) || isKeyDown(Keyboard.KEY_LCONTROL) && !context.undoStack.empty()) {
                            context.redoStack.push(context.map.clone());
                            context.map = context.undoStack.pop();
                        }
                        break;
                    case Keyboard.KEY_Y:
                        if (isKeyDown(Keyboard.KEY_RCONTROL) || isKeyDown(Keyboard.KEY_LCONTROL) && !context.redoStack.empty()) {
                            context.undoStack.push(context.map.clone());
                            context.map = context.redoStack.pop();
                        }
                        break;
                    case Keyboard.KEY_S:
                        if (isKeyDown(Keyboard.KEY_RCONTROL) || isKeyDown(Keyboard.KEY_LCONTROL)) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        FileOutputStream fos = new FileOutputStream(context.outputFile);
                                        Codec.getLatestCodec().compileMap(context.map, new DataOutputStream(fos));
                                        fos.close();
                                    } catch (Exception ex) {
                                        ErrorHandler.alert(ex);
                                    }
                                }
                            }.start();
                        }
                        break;
                    default:
                        doKey(key);
                }
            }
        }
    }

    protected void doKey(int keycode) {
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
