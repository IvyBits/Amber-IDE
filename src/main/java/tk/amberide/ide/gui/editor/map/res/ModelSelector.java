package tk.amberide.ide.gui.editor.map.res;

import tk.amberide.Amber;
import tk.amberide.ide.data.res.Resource;
import tk.amberide.engine.gl.model.obj.WavefrontObject;
import tk.amberide.ide.gui.editor.map.MapContext;
import tk.amberide.ide.gui.misc.ErrorHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

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

    public void addModel(WavefrontObject model, String name) {
        addModel(new ImageIcon(ModelThumbnail.getModelImage(model, 60, 60)), model, name);
    }

    private void addModel(final ImageIcon icon, final WavefrontObject model, final String name) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JToggleButton button = new JToggleButton();
                button.setSize(85, 85);
                button.setMargin(new Insets(2, 2, 2, 2));
                button.setMaximumSize(new Dimension(85, 85));
                button.setMinimumSize(new Dimension(85, 85));
                button.setPreferredSize(new Dimension(85, 85));
                button.setIcon(icon);
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
                        + "<br/>"
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
                holder.revalidate();
            }
        });
    }

    public void synchronize() {
        group = new ButtonGroup();
        holder.removeAll();
        new Thread("Model thumbnail generator") {
            @Override
            public void run() {
                for (Resource<WavefrontObject> model : Amber.getResourceManager().getModels()) {
                    try {
                        addModel(model.get(), model.getName());
                    } catch (Exception ex) {
                        ErrorHandler.alert(ex);
                    }
                }
            }
        }.start();
    }
}
