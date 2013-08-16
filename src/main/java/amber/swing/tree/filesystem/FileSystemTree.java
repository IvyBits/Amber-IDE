package amber.swing.tree.filesystem;

import amber.data.io.FileIO;
import amber.swing.Dialogs;
import amber.swing.tree.Trees;
import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import amber.swing.tree.filesystem.FileTreeModel.FileTreeNode;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.dnd.DnDConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;

public class FileSystemTree extends JTree {

    private FileTreeTransferHandler handler = null;
    private boolean fileMovingEnabled = true;
    FileTreeNode draggedNode = null;
    protected ArrayList<FileTreeAdapter> adapters = new ArrayList<FileTreeAdapter>();
    private final DropTargetListener dragExecutor = new DropTargetListener() {
        public void dragEnter(DropTargetDragEvent dtde) {
        }

        public void dragOver(DropTargetDragEvent dtde) {
            Point e = dtde.getLocation();
            // this.as.autoscroll(e);
            TreePath tp = getPathForLocation(e.x, e.y);
            if (tp != null) {
                draggedNode = ((FileTreeNode) tp.getLastPathComponent());
                File targetDir = draggedNode.getFile();
                if (targetDir.isFile()) {
                    targetDir = targetDir.getParentFile();
                }
                if (!targetDir.canWrite()) {
                    dtde.acceptDrag(DnDConstants.ACTION_NONE);
                } else {
                    File[] fileToDragged = handler.files;
                    for (int i = 0; i < fileToDragged.length; i++) {
                        if (fileToDragged[i].getParentFile() != null) {
                            if (fileToDragged[i].getParentFile().equals(targetDir)) {
                                dtde.acceptDrag(DnDConstants.ACTION_NONE);
                            }
                        } else {
                            return;
                        }
                    }
                    dtde.acceptDrag(DnDConstants.ACTION_MOVE);
                }
            }
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        public void dragExit(DropTargetEvent dte) {
        }

        public void drop(DropTargetDropEvent dtde) {
        }
    };

    public FileSystemTree() {
        setRootVisible(false);
        setCellRenderer(new DefaultTreeCellRenderer() {
            protected Map<String, Icon> iconCache = new HashMap<String, Icon>();
            protected FileSystemView fsv = FileSystemView.getFileSystemView();

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                _outer:
                if ((value instanceof FileTreeNode)) {
                    FileTreeNode node = (FileTreeNode) value;
                    File file = node.getFile();
                    if (file != null) {
                        /*for (FileTreeAdapter ad : adapters) {
                         if (!ad.shouldDisplay(node.file)) {
                         JLabel view = new JLabel();
                         view.setVisible(false);
                         view.setEnabled(false);
                         view.setMaximumSize(new Dimension(0, 0));
                         view.setMinimumSize(new Dimension(0, 0));
                         view.setPreferredSize(new Dimension(0, 0));
                         return Box.createVerticalStrut(1);
                         //break _outer;
                         }
                         }*/
                        String ext = "";
                        String name = file.getName();

                        if (name.contains(".")) {
                            ext = name.substring(name.lastIndexOf("."), name.length());
                        }

                        JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, name, sel, expanded, leaf, row, hasFocus);
                        Icon icon = iconCache.get(name);
                        if (icon == null) {
                            icon = fsv.getSystemIcon(file);
                            for (FileTreeAdapter ad : adapters) {
                                Icon ic = ad.getIcon(file, ext, node, icon);
                                if (ic != icon) {
                                    icon = ic;
                                }
                            }
                            iconCache.put(name, icon);
                        }
                        result.setIcon(icon);
                        return result;
                    }
                }
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
        });
        setCellEditor(new FileTreeCellEditor());
        setDragEnabled(false);
        setEditable(true);
        setTransferHandler(handler = new FileTreeTransferHandler());
        setModel(new FileTreeModel());
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (fileMovingEnabled) {
                    TreePath path = getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        draggedNode = ((FileTreeNode) path.getLastPathComponent());
                        if (draggedNode.getFile() != null && draggedNode.getFile().isFile()) {
                            handler.exportAsDrag(FileSystemTree.this, e, 2);
                        }
                    }
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_F2:
                        startEditingAtPath(getSelectionPath());
                        break;
                }
            }
        });
    }

    public void addFileTreeListener(FileTreeAdapter ftl) {
        adapters.add(ftl);
        addTreeExpansionListener(ftl);
        addTreeSelectionListener(ftl);
        addTreeWillExpandListener(ftl);
        addKeyListener(ftl);
    }

    @Override
    public void processEvent(AWTEvent evt) {
        super.processEvent(evt);
        if (evt instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) evt;
            for (FileTreeAdapter ftl : adapters) {
                ftl.nodeClicked(me, getPathForRow(getClosestRowForLocation(me.getX(), me.getY())));
            }
        }
    }

    void renameSelection(String newName) {
        TreePath tp = getSelectionPath();
        if (tp == null) {
            return;
        }
        FileTreeNode ftn = (FileTreeNode) tp.getLastPathComponent();
        File f = ftn.getFile();
        File nf;
        if (f.renameTo(nf = new File(f.getParentFile(), newName))) {
            ftn.file = nf;
            repaint();
        }
    }

    public void setFileMovingEnabled(boolean enabled) {
        fileMovingEnabled = enabled;
    }

    public boolean isFileMovingEnabled() {
        return fileMovingEnabled;
    }

    public void setFileRenamingEnabled(boolean enabled) {
        setEditable(enabled);
    }

    public boolean isFileTreeEnabled() {
        return isEditable();
    }

    void setSelectedFile(File file) {
        String path = file.getPath();
        StringTokenizer st = new StringTokenizer(path, "/" + File.separator);
        FileTreeNode node = (FileTreeNode) getModel().getRoot();

        TreePath tp = new TreePath(getModel().getRoot());

        if (path.startsWith("/")) {
            for (int i = 0; i < node.getChildCount(); i++) {
                FileTreeNode child = (FileTreeNode) node.getChildAt(i);
                if (child.toString().equals("/")) {
                    node = child;
                    tp = tp.pathByAddingChild(child);
                    break;
                }
            }
        }

        while (st.hasMoreTokens()) {
            String element = st.nextToken();

            boolean found = true;
            for (int i = 0; i < node.getChildCount(); i++) {
                FileTreeNode child = (FileTreeNode) node.getChildAt(i);
                String name = child.getFile().getName();
                if ("".equals(name)) {
                    name = child.getFile().getPath();
                }
                if ((name.equals(element))
                        || (name.equals(element + "/"))
                        || (name.equals(element + "\\"))) {
                    node = child;
                    tp = tp.pathByAddingChild(child);
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
        }
        if (tp.getLastPathComponent() == getModel().getRoot()) {
            FileTreeNode rootNode = (FileTreeNode) getModel().getRoot();
            if (rootNode.getChildCount() > 0) {
                tp = new TreePath(
                        new Object[]{
                    rootNode,
                    rootNode.getChildAt(0)});
            }

        }

        expandPath(tp);
        setSelectionPath(tp);
        scrollPathToVisible(tp);
    }

    File[] getSelectedFiles() {
        TreePath[] tp = getSelectionPaths();
        if (tp == null) {
            return null;
        }
        File[] files = new File[tp.length];
        for (int i = 0; i < tp.length; i++) {
            files[i] = ((FileTreeNode) tp[i].getLastPathComponent()).getFile();
        }
        return files;
    }

    public void addNotify() {
        super.addNotify();
        try {
            getDropTarget().addDropTargetListener(dragExecutor);
        } catch (TooManyListenersException ignored) {
        }
    }

    public void removeNotify() {
        super.removeNotify();
        getDropTarget().removeDropTargetListener(dragExecutor);
    }

    public void setRoot(File root) {
        setModel(new FileTreeModel(root));
        synchronize();
    }

    public File getRoot() {
        return getModel() != null ? ((FileTreeModel) getModel()).getRoot().file : null;
    }

    public void synchronize() {
        String state = Trees.getExpansionState(this, 0);
        setModel(new FileTreeModel(((FileTreeModel) getModel()).getRoot().file));
        if (state != null) {
            Trees.restoreExpanstionState(this, 0, state);
        }
        repaint();
    }
}