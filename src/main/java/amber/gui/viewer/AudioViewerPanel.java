package amber.gui.viewer;

import amber.gui.FileViewerPanel;
import amber.gui.misc.AudioPlayerPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;

/**
 *
 * @author Tudor
 */
public class AudioViewerPanel extends FileViewerPanel {

    public AudioViewerPanel(File file) {
        super(file);
        setLayout(new GridBagLayout());
        try {
            add(new AudioPlayerPanel(file));
        } catch (Exception e) {
            add(BorderLayout.CENTER, new JScrollPane(new JLabel("Failed to load clip.")));
        }
    }

    @Override
    public JMenu[] getContextMenus() {
        return new JMenu[0];
    }
}
