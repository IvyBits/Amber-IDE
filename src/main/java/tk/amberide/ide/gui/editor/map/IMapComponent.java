package tk.amberide.ide.gui.editor.map;

import java.awt.Component;
import javax.swing.JMenu;

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
}
