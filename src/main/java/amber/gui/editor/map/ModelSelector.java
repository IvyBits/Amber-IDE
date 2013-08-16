package amber.gui.editor.map;

import amber.Amber;
import amber.data.io.ImagingTools;
import amber.data.res.Resource;
import amber.gl.GLUtil;
import amber.gl.model.ModelScene;
import amber.gl.model.obj.WavefrontObject;
import amber.gui.exc.ErrorHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import org.lwjgl.LWJGLException;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Tudor
 */
public class ModelSelector extends JPanel {

    protected ButtonGroup group = new ButtonGroup();
    protected int index = 0;
    protected JPanel holder = new JPanel();
    protected MapContext context;

    public ModelSelector(MapContext context) {
        this.context = context;
        setLayout(new BorderLayout());
        JScrollPane scroller = new JScrollPane(holder);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        holder.setLayout(new GridLayout(0, 3));
        scroller.getVerticalScrollBar().setUnitIncrement(16);
        add(scroller, BorderLayout.NORTH);
    }

    public void addModel(final WavefrontObject model, final String name) throws LWJGLException, IOException {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JToggleButton button = new JToggleButton();
                button.setSize(85, 85);
                button.setMargin(new Insets(2, 2, 2, 2));
                button.setMaximumSize(new Dimension(85, 85));
                button.setMinimumSize(new Dimension(85, 85));
                button.setPreferredSize(new Dimension(85, 85));
                button.setIcon(new ImageIcon(makeImage(model, 60, 60)));
                button.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        if (context.EXT_modelSelectionSupported) {
                            context.EXT_modelSelection = model;
                        }
                    }
                });
                button.setToolTipText(String.format(
                        "<html>"
                        + "<b>%s</b>"
                        + "<br/>"
                        + "&nbsp;&nbsp;%s vertices"
                        + "<br/>"
                        + "&nbsp;&nbsp;%s normals"
                        + "<br/>"
                        + "<br/>"
                        + "&nbsp;&nbsp;%s textures"
                        + "&nbsp;&nbsp;%s groups"
                        + "<br/>"
                        + "&nbsp;&nbsp;%s materials"
                        + "</html>",
                        name,
                        model.getVertices().size(),
                        model.getNormals().size(),
                        model.getGroups().size(),
                        model.getMaterials().size(),
                        model.getTextures().size()));
                group.add(button);
                holder.add(button);
            }
        });
    }

    public void synchronize() {
        group = new ButtonGroup();
        holder.removeAll();
        for (Resource<WavefrontObject> model : Amber.getResourceManager().getModels()) {
            try {
                addModel(model.get(), model.getName());
            } catch (Exception ex) {
                ErrorHandler.alert(ex);
            }
        }
    }

    public static Image makeImage(final WavefrontObject model, final int twidth, final int theight) {
        final int width = 512;
        final int height = 512;
        BufferedImage img = null;
        try {
            img = GLUtil.renderImage(
                    width,
                    height,
                    new Runnable() {
                @Override
                public void run() {
                    glClearColor(0, 0, 0, 0);
                    ModelScene scene;
                    try {
                        scene = new ModelScene(model);
                    } catch (IOException ex) {
                        ErrorHandler.alert(ex);
                        return;
                    }

                    // Enable transperancy              
                    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();

                    glEnable(GL_BLEND);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glShadeModel(GL_SMOOTH);
                    glEnable(GL_DEPTH_TEST);

                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    float fAspect = (float) width / (float) height;
                    GLU.gluPerspective(45.0f, fAspect, 0.5f, 400.0f);

                    for (int i = 0; i != 2; i++) {
                        glMatrixMode(GL_MODELVIEW);
                        glLoadIdentity();
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                        glLoadIdentity();
                        GLU.gluLookAt((float) Math.sin(0) * 3 * 5, 0, (float) Math.cos(0) * 3 * 5, 0, 0, 0, 0, 1, 0);
                        glTranslatef(0, -3, 0);
                        scene.draw();
                    }
                }
            });

            if (img != null) {
                img = ImagingTools.trimAlpha(img);

                if (img.getHeight() > theight || img.getWidth() > twidth) {
                    return ImagingTools.scaleImage(img, twidth, theight);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return img;
    }
}
