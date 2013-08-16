package amber.swing.tree.filesystem;

import amber.data.io.FileIO;
import amber.swing.Dialogs;
import java.util.Arrays;
import amber.swing.tree.filesystem.FileTreeModel.FileTreeNode;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

class FileTreeTransferHandler extends TransferHandler {

    private static final DataFlavor[] flavors = {DataFlavor.javaFileListFlavor};
    File[] files = null;

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if ((comp instanceof FileSystemTree)) {
            FileTreeNode node = ((FileSystemTree) comp).draggedNode;
            if (!node.file.canWrite()) {
                return false;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].getParentFile().equals(node.file)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        FileSystemTree ft = (FileSystemTree) c;
        files = ft.getSelectedFiles();
        if (files == null) {
            return null;
        }
        final ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(files));
        Transferable transferable = new Transferable() {
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return list;
                }
                return null;
            }

            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(DataFlavor.javaFileListFlavor);
            }
        };
        return transferable;
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        FileSystemTree ft = (FileSystemTree) comp;
        try {
            ArrayList sourcelst = (ArrayList) t.getTransferData(flavors[0]);
            for (File selection : ft.getSelectedFiles()) {
                if (selection == null) {
                    return false;
                }
                int i = 0;
                if (i < sourcelst.size()) {
                    File source = (File) sourcelst.get(i);

                    if ((source.getParentFile() != null)
                            && (source.getParentFile().equals(selection))) {
                        return false;
                    }
                    if (selection.isFile()) {
                        selection = selection.getParentFile();
                        if ((source.getParentFile() != null) && (source.getParentFile().equals(selection))) {
                            return false;
                        }
                    }
                    File destination = new File(selection, source.getName());
                    if (!destination.isDirectory()) {
                        destination = new File(destination.getParentFile(), source.getName());
                    }
                    try {
                        FileIO.move(source, destination);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Dialogs.errorDialog()
                                .setTitle("Failed to move file.")
                                .setMessage("An error occured while moving file (IOException).")
                                .show();
                    }
                    ft.synchronize();
                }
                return true;
            }
            return true;
        } catch (UnsupportedFlavorException exc) {
            return false;
        } catch (IOException exc) {
        }
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return DnDConstants.ACTION_MOVE;
    }
}