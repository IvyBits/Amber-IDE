package amber.swing.misc;

import amber.Amber;
import amber.os.colorpicker.ColorDialogFactory;
import amber.os.colorpicker.IColorDialog;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 *
 * @author Tudor
 */
public class ColorChooserButton extends JButton {
    private Color selectedColor;

    public ColorChooserButton() {
        this("");
    }
    
    public ColorChooserButton(String text) {
        super(text);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IColorDialog picker = ColorDialogFactory.newFileDialog(Amber.getUI());
                if(picker.show()) {
                    selectedColor = picker.getColor();
                    setColor(selectedColor);
                }              
            }
        });

    }
    
    public Color getColor() {
        return selectedColor;
    }

    public void setColor(Color col) {
        selectedColor = col;
        // Here we create an image with a solid color, and then set it as the icon for our color button.
        BufferedImage icon = new BufferedImage((int) (getWidth() * .40), (int) (getHeight() * 0.40), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = icon.createGraphics();
        graphics.setPaint(col);
        graphics.fillRect(0, 0, icon.getWidth(), icon.getHeight());
        setIcon(new ImageIcon(icon));
    }
}
