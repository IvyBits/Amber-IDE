package amber.gui.editor.map;

import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author Tudor
 */
public interface IMapComponent {

    MapContext getMapContext();

    Component getComponent();

    JMenu[] getContextMenus();

    boolean modified();
    void save();
    JComponent getStatusBar();
}
