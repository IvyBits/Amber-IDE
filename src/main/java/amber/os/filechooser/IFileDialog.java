package amber.os.filechooser;

import java.awt.Component;
import java.io.File;
import java.util.Collection;

/**
 *
 * @author Tudor
 */
public interface IFileDialog {

    boolean show();

    Component getParent();

    void setParent(Component parent);

    String getFilter();

    void setFilter(String filter);

    File getInitial();

    void setInitial(File initial);

    String getTitle();

    void setTitle(String title);

    boolean isMulti();

    void setMulti(boolean multi);

    File getFile();

    File[] getFiles();
}
