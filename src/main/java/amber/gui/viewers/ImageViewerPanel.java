package amber.gui.viewers;

import amber.gui.editor.FileViewerPanel;
import java.awt.BorderLayout;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;

/**
 *
 * @author Tudor
 */
public class ImageViewerPanel extends FileViewerPanel {

    public ImageViewerPanel(File file) {
        super(file);
        setLayout(new BorderLayout());
        try {
            add(BorderLayout.CENTER, wrap(new JLabel(new ImageIcon(ImageIO.read(file)))));
        } catch (Exception e) {
            add(BorderLayout.CENTER, wrap(new JLabel("Failed to load image.")));
        }
    }

    protected JComponent wrap(JLabel lbl) {
        JScrollPane pane = new JScrollPane(lbl);
        pane.getHorizontalScrollBar().setUnitIncrement(16);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        return pane;
    }

    @Override
    public JMenu[] getContextMenus() {
        return new JMenu[0];
    }
}
