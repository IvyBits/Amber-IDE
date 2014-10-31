package tk.amberide.ide.gui.editor.map;

import com.sun.glass.ui.Window;
import tk.amberide.ide.os.OS;
import tk.amberide.engine.data.map.LevelMap;
import tk.amberide.engine.data.map.codec.Codec;
import tk.amberide.ide.gui.misc.ErrorHandler;
import tk.amberide.engine.input.AbstractKeyboard;
import static tk.amberide.engine.input.AbstractKeyboard.getEventKey;
import static tk.amberide.engine.input.AbstractKeyboard.getEventKeyState;
import static tk.amberide.engine.input.AbstractKeyboard.isKeyDown;
import tk.amberide.engine.input.AbstractMouse;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

import javax.swing.*;

/**
 *
 * @author Tudor
 */
public abstract class AbstractGLMapComponent extends AWTGLCanvas implements IMapComponent {

    protected final MapContext context = new MapContext();
    protected Thread renderingThread;
    protected boolean running;
    protected boolean modified = false;

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
                    if (isShowing()) {
                        repaint();
                        Display.sync(120);
                    } else {
                        OS.sleep(300); // Prevent useless CPU cycles when not showing
                        repaint();
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
                AbstractKeyboard.create(AbstractKeyboard.AWT);
            }
            if (!AbstractMouse.isCreated()) {
                AbstractMouse.create(AbstractMouse.AWT);
            }
        } catch (LWJGLException ex) {
            ex.printStackTrace();
            return;
        }

        if(!isFocusOwner()) {
            return;
        }

        doScroll(AbstractMouse.getDWheel());

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
                            save();
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

    protected void doScroll(int delta) {
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

    public boolean modified() {
        return modified;
    }

    public JComponent getStatusBar() {
        return null;
    }
    private boolean saving;

    public void save() {
        if (!saving) {
            saving = true;
            new Thread() {
                @Override
                public void run() {
                    try {
                        FileOutputStream fos = new FileOutputStream(context.outputFile);
                        Codec.getLatestCodec().compileMap(context.map, new DataOutputStream(fos));
                        fos.close();
                        saving = false;
                    } catch (Exception ex) {
                        ErrorHandler.alert(ex);
                        saving = false;
                    }
                }
            }.start();
            modified = false;
        }
    }

    protected boolean isInBounds(int x, int y) {
        return !(x < 0 || x > context.map.getWidth() - 1 || y < 0 || y > context.map.getLength() - 1);
    }
}
