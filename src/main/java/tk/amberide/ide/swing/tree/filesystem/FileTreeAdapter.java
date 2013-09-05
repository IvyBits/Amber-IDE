package tk.amberide.ide.swing.tree.filesystem;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * @author Tudor
 */
public abstract class FileTreeAdapter implements
        TreeSelectionListener,
        TreeExpansionListener,
        TreeWillExpandListener,
        KeyListener {

    public void nodeClicked(MouseEvent e, TreePath path) {
    }

    public boolean shouldDisplay(File file) {
        return true;
    }

    public Icon getIcon(File file, String ext, FileTreeModel.FileTreeNode path, Icon defaultIcon) {
        return defaultIcon;
    }

    public void valueChanged(TreeSelectionEvent e) {
    }

    public void treeExpanded(TreeExpansionEvent event) {
    }

    public void treeCollapsed(TreeExpansionEvent event) {
    }

    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
    }

    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
