package amber.os.colorpicker;

import java.awt.Color;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public interface IColorDialog {

    public Component getParent();

    public void setParent(Component parent);

    public Color getInitialColor();

    public void setInitialColor(Color color);

    public Color getColor();
    
    public boolean show();
}
