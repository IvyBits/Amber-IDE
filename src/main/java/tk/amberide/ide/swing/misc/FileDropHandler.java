package tk.amberide.ide.swing.misc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

public abstract class FileDropHandler extends TransferHandler {

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (int i = 0; i < transferFlavors.length; i++) {
            if (transferFlavors[i].equals(DataFlavor.javaFileListFlavor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        try {
            filesDropped(((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor)).toArray(new File[0]));
        } catch (UnsupportedFlavorException e) {
            throw new IllegalStateException("unsupported content dropped", e);
        } catch (IOException e) {
            throw new IllegalStateException("IO exception while importing files", e);
        }
        return true;
    }

    public abstract void filesDropped(File[] files);
}
