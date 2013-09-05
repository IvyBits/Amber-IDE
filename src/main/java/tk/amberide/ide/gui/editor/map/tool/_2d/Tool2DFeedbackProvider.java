package tk.amberide.ide.gui.editor.map.tool._2d;

import java.awt.*;

public interface Tool2DFeedbackProvider {
    void mouseBegin(int x, int y);
    void mouseUp(int x, int y);
    void mouseDown(int x, int y);
    boolean shouldUndo();
    Point getDrawRectLocation();
}
