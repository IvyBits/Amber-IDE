package amber.gui.editor.map;

import amber.data.res.Tileset;
import amber.data.map.Layer;
import amber.data.map.LevelMap;
import amber.data.map.Tile;
import amber.data.sparse.SparseMatrix;
import amber.data.sparse.SparseVector;
import amber.gl.FrameTimer;
import amber.gl.GLColor;
import static amber.gl.GLE.*;
import amber.gl.Texture;
import amber.gl.TrueTypeFont;
import amber.gl.tess.ITesselator;
import amber.gl.tess.ImmediateTesselator;
import static amber.gui.editor.map.MapContext.*;
import amber.gui.editor.map.tool._2d.*;
import amber.input.AbstractKeyboard;
import amber.input.AbstractMouse;
import amber.swing.MenuBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Tudor
 */
public class GLMapComponent2D extends AbstractGLMapComponent {

    protected FrameTimer timer = new FrameTimer();
    protected Vector2f cursorPos = new Vector2f();
    protected WeakHashMap<Tileset, Texture> textureCache = new WeakHashMap<Tileset, Texture>();
    protected TrueTypeFont font;
    protected float aspectRatio;
    protected ITesselator tess = new ImmediateTesselator();
    protected ScrollPane display = new ScrollPane();
    protected Tool2D brushTool = new Brush2D(context);
    protected Tool2D eraseTool = new Eraser2D(context);
    protected Tool2D fillTool = new Fill2D(context);

    public GLMapComponent2D(LevelMap map) throws LWJGLException {
        super(map);
        setMinimumSize(new Dimension(0, 0));
        setPreferredSize(new Dimension(map.getWidth() * 32 + 2, map.getLength() * 32 + 2));
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        display.add(this);

        display.setMinimumSize(new Dimension(0, 0));
        display.setPreferredSize(new Dimension(0, 0));
        display.getVAdjustable().setUnitIncrement(16);
        display.getHAdjustable().setUnitIncrement(16);
    }

    @Override
    public void initGL() {
        gleClearColor(Color.WHITE);
        font = new TrueTypeFont(new Font("Courier", Font.PLAIN, 15), true);

        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.1f);

        timer.start();

        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                // Hierarchy change can signify the AWTGLCanvas destroying the original GL context,
                // so we have to clear the now invalid texture cache.
                tess.invalidate();
                AbstractKeyboard.destroy(); // Prevent double events
                AbstractMouse.destroy();
            }
        });
    }

    @Override
    protected void pollInput() {
        super.pollInput();
        int delta = (int) ((float) timer.getDelta() * 2f * 0.1f);

        boolean keyUp = AbstractKeyboard.isKeyDown(Keyboard.KEY_UP) || AbstractKeyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = AbstractKeyboard.isKeyDown(Keyboard.KEY_DOWN) || AbstractKeyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyLeft = AbstractKeyboard.isKeyDown(Keyboard.KEY_LEFT) || AbstractKeyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyRight = AbstractKeyboard.isKeyDown(Keyboard.KEY_RIGHT) || AbstractKeyboard.isKeyDown(Keyboard.KEY_D);

        if (keyUp && !keyDown) {
            display.getVAdjustable().setValue(display.getVAdjustable().getValue() - delta);
        } else if (keyDown && !keyUp) {
            display.getVAdjustable().setValue(display.getVAdjustable().getValue() + delta);
        }

        if (keyLeft && !keyRight) {
            display.getHAdjustable().setValue(display.getHAdjustable().getValue() - delta);
        } else if (keyRight && !keyLeft) {
            display.getHAdjustable().setValue(display.getHAdjustable().getValue() + delta);
        }

        cursorPos.set(AbstractMouse.getX(this) / 32, AbstractMouse.getY(this) / 32);

        if (AbstractMouse.isButtonDown(0)) {
            LevelMap pre = context.map.clone();
            Tool2D tool = currentTool();

            if (tool != null && tool.apply((int) cursorPos.x, (int) cursorPos.y)) {
                context.undoStack.push(pre);
                modified = true;
            }
        }
        AbstractMouse.poll();
    }
    
    @Override
    protected void doKey(int keycode) {
        currentTool().doKey(keycode);
    }

    @Override
    protected void paintGL() {
        if (!GLContext.getCapabilities().GL_ARB_texture_rectangle) {
            running = false;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    display.remove(GLMapComponent2D.this);
                    display.add(new Label("ARB_TEXTURE_RECTANGLE not supported. Try updating your graphics drivers.", Label.CENTER));
                    display.validate();
                }
            });
            return;
        }
        super.paintGL();
        float aspect = (float) getWidth() / (float) getHeight();
        if (aspect != aspectRatio) {
            glViewport(0, 0, getWidth(), getHeight());
            aspectRatio = aspect;
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, getWidth(), 0, getHeight(), -1, 1);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (wireframe) {
            if (glGetInteger(GL_POLYGON_MODE) == GL_FILL) {
                gleToggleWireframe();
            }
        } else if (glGetInteger(GL_POLYGON_MODE) == GL_LINE) {
            gleToggleWireframe();
        }

        glPushMatrix();
        glTranslatef(1, 0, 0);
        List<Layer> layers = context.map.getLayers();
        for (int i = 0; i != layers.size(); i++) {
            drawLayer(layers.get(i));
        }
        drawGrid();
        glPopMatrix();

        if (info) {
            glPushMatrix();
            glLoadIdentity();
            glPushAttrib(GL_CURRENT_BIT | GL_POLYGON_BIT);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Ensure we're not in wireframe mode
            glTranslatef(display.getHAdjustable().getValue(), -display.getVAdjustable().getValue(), 0); // Translate to top-left corner
            GLColor.BLACK.bind();
            font.drawString(0, getHeight() - font.getHeight(), "FPS: " + timer.fps() + "\n"
                    + (!AbstractMouse.isGrabbed() ? "Cursor: (" + (int) cursorPos.x + ", " + (int) cursorPos.y + ")" : ""), 1f, 1f, TrueTypeFont.ALIGN_LEFT);
            glPopAttrib();
            glPopMatrix();
        }

        try {
            swapBuffers();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        timer.updateFPS();
    }

    protected void drawLayer(Layer layer) {
        tess.startTileBatch();
        SparseVector.SparseVectorIterator tileIterator = layer.tileMatrix().iterator();
        while (tileIterator.hasNext()) {
            SparseMatrix.SparseMatrixIterator matrixIterator = ((SparseMatrix<Tile>) tileIterator.next()).iterator();
            while (matrixIterator.hasNext()) {
                Tile t = (Tile) matrixIterator.next();
                if (t != null) {
                    tess.drawTile2D(t, matrixIterator.realX(), matrixIterator.realY());
                }
            }
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        tess.endTileBatch();
    }

    protected void drawGrid() {
        glPushAttrib(GL_CURRENT_BIT | GL_LINE_BIT);
        if (grid) {
            glBegin(GL_LINES);
            {
                GLColor.GRAY.bind();
                for (int x = 0; x <= context.map.getWidth(); x++) {
                    gleLine2d(x * 32, 0, x * 32, context.map.getLength() * 32);
                }
                for (int y = 0; y <= context.map.getLength(); y++) {
                    gleLine2d(0, y * 32, context.map.getWidth() * 32, y * 32);
                }
            }
            glEnd();
        }

        glLineWidth(3);
        GLColor.BLACK.bind();
        glBegin(GL_LINES);
        {
            gleLine2d(0, 0, context.map.getWidth() * 32, 0);
            gleLine2d(0, 0, 0, context.map.getLength() * 32);
            gleLine2d(context.map.getWidth() * 32, 0, context.map.getWidth() * 32, context.map.getLength() * 32);
            gleLine2d(0, context.map.getLength() * 32, context.map.getWidth() * 32, context.map.getWidth() * 32);
        }
        glEnd();
        glLineWidth(2);
        if (cursorPos != null) {
            Dimension size = currentTool().getDrawRectangleSize();
            gleRect2d(cursorPos.x * 32, cursorPos.y * 32, size.width * 32, size.height * 32);
        }
        glPopAttrib();
    }

    private Tool2D currentTool() {
        switch (context.drawMode) {
            case MODE_BRUSH:
                return brushTool;
            case MODE_FILL:
                return fillTool;
            case MODE_ERASE:
                return eraseTool;
        }
        return null;
    }
    
    @Override
    public Component getComponent() {
        return display;
    }
    protected boolean info = true, wireframe = false, grid = true;

    public JMenu[] getContextMenus() {
        return new JMenu[]{new MenuBuilder("View").addCheckbox("Info", true, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    info = !info;
                    repaint();
                }
            }).addCheckbox("Grid", true, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    grid = !grid;
                    repaint();
                }
            }).addCheckbox("Wireframe", false, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    wireframe = !wireframe;
                    repaint();
                }
            }).create()};
    }
}
