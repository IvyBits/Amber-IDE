package tk.amberide.ide.swing.tree.filesystem;

import java.io.File;
import java.util.ArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class FileTreeModel implements TreeModel {

    private FileTreeNode root = null;
    private ArrayList listeners = null;

    public FileTreeModel() {
        this.root = new FileTreeNode(null);
    }

    public FileTreeModel(File root) {
        setRoot(root);
    }

    public void setRoot(File root) {
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("root must be a directory");
        }
        this.root = addNodes(null, root);
    }

    protected FileTreeNode addNodes(FileTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        FileTreeNode curDir = new FileTreeNode(dir);
        if (curTop != null) { // should only be null at root
            curTop.add(curDir);
        }

        File f;
        ArrayList<File> files = new ArrayList<File>();
        // Make two passes, one for Dirs and one for Files. This is #1.
        String[] tmp = dir.list();
        if (tmp != null) {
            _outer:
            for (int i = 0; i < tmp.length; i++) {
                String thisObject = tmp[i];
                String newPath;
                if (curPath.equals(".")) {
                    newPath = thisObject;
                } else {
                    newPath = curPath + File.separator + thisObject;
                }
                f = new File(newPath);
                if (f.isDirectory()) {
                    addNodes(curDir, f);
                } else {
                    files.add(f);
                }
            }
        }
        // Pass two: for files.
        for (int fnum = 0; fnum < files.size(); fnum++) {
            curDir.add(new FileTreeNode(files.get(fnum)));
        }
        return curDir;
    }

    public Object getChild(Object parent, int index) {
        FileTreeNode ftn = (FileTreeNode) parent;

        return ftn.getChildAt(index);
    }

    public int getChildCount(Object parent) {
        FileTreeNode ftn = (FileTreeNode) parent;

        return ftn.getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child) {
        FileTreeNode ftn = (FileTreeNode) parent;

        return ftn.getIndex((TreeNode) child);
    }

    public FileTreeNode getRoot() {
        return this.root;
    }

    public boolean isLeaf(Object node) {
        FileTreeNode ftn = (FileTreeNode) node;
        if (ftn == this.root) {
            return false;
        }
        if (ftn.getFile().isDirectory()) {
            return false;
        }
        return true;
    }

    public void reload(TreePath tp) {
        FileTreeNode node = (FileTreeNode) tp.getLastPathComponent();
        node.removeAllChildren();
        if (this.listeners != null) {
            for (int i = 0; i < this.listeners.size(); i++) {
                TreeModelListener listener = (TreeModelListener) this.listeners.get(i);
                listener.treeStructureChanged(new TreeModelEvent(this, tp));
            }
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        if (this.listeners != null) {
            this.listeners.remove(l);
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public class FileTreeNode extends DefaultMutableTreeNode {

        File file;
        boolean root = false;

        public FileTreeNode(File content) {
            this.file = content;
            userObject = this;
        }

        public File getFile() {
            return file;
        }

        public String toString() {
            return file != null ? file.toString() : "null";
        }
    }
}