package amber.swing.tree.filesystem;

import amber.swing.tree.filesystem.FileTreeModel.FileTreeNode;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreePath;

class FileTreeCellEditor extends JPanel implements javax.swing.tree.TreeCellEditor {

    private JTextField tf;
    private JLabel lbl;
    JTree tree = null;
    ArrayList listeners = null;

    public FileTreeCellEditor() {
        super(new BorderLayout());
        tf = new JTextField();
        tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    tree.stopEditing();
                }
            }
        });
        lbl = new JLabel();
        lbl.setOpaque(false);
        add(lbl, BorderLayout.WEST);
        add(tf, BorderLayout.CENTER);
        setOpaque(false);
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        setBackground(tree.getBackground());

        FileTreeNode node = (FileTreeNode) value;
        tf.setText(node.getFile().getName());

        lbl.setIcon(((JLabel) tree.getCellRenderer().getTreeCellRendererComponent(
                tree,
                value,
                isSelected,
                expanded,
                leaf,
                row,
                true)).getIcon());
        this.tree = tree;

        return this;
    }

    public Object getCellEditorValue() {
        String newName = tf.getText();
        if ("".equals(newName)) {
            return null;
        }
        FileSystemTree ft = (FileSystemTree) tree;
        ft.renameSelection(newName);

        return null;
    }

    public boolean isCellEditable(EventObject evt) {
        if (evt != null) {
            JTree t = (JTree) evt.getSource();
            if (evt instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) evt;
                TreePath selected = t.getPathForLocation(me.getX(), me.getY());
                if (selected == null) {
                    return false;
                }
                FileTreeNode node = (FileTreeNode) selected.getLastPathComponent();
                if ((node.isRoot()) || (node.root)) {
                    return false;
                }
                if ((me.getClickCount() == 3) && (selected.equals(t.getSelectionPath()))) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        return true;
    }

    public void cancelCellEditing() {
    }

    public void addCellEditorListener(CellEditorListener l) {
    }

    public void removeCellEditorListener(CellEditorListener l) {
    }
}