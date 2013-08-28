package amber.os.colorpicker;

import amber.os.OS;
import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Tudor
 */
public class Test {

    public static void main(String[] args) {
        OS.loadNativeLibraries();
        JFrame parent = new JFrame("Native color chooser test!");
        parent.setSize(500, 500);
        parent.setResizable(false);
        parent.setVisible(true);
        parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while (true) {
            SwingColorDialog wcd = new SwingColorDialog(parent);
            wcd.setInitialColor(Color.BLUE);
            if (wcd.show()) {
                parent.getContentPane().setBackground(wcd.getColor());
            }
        }
    }
}
