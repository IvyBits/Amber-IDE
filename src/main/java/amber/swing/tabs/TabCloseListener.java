package amber.swing.tabs;

import java.awt.Component;

/**
 *
 * @author Tudor
 */
public interface TabCloseListener {

    public boolean tabClosed(String title, Component comp, CloseableTabbedPane pane);
}
