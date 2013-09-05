package tk.amberide.ide.swing;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Tudor
 */
public class MenuBuilder {

    private JMenu built;

    public MenuBuilder(String title) {
        built = new JMenu(title);
    }

    public MenuBuilder addCheckbox(String text, AbstractAction action) {        
        return addCheckbox(text, false, action);
    }

    public MenuBuilder addCheckbox(String text, boolean def, AbstractAction action) {
        built.add(new JCheckBoxMenuItem(text, def)).addActionListener(action);
        return this;
    }

    public MenuBuilder addRadioButton(String text, AbstractAction action) {
        built.add(new JRadioButtonMenuItem(text)).addActionListener(action);
        return this;
    }

    public MenuBuilder addButton(String text, AbstractAction action) {
        built.add(new JMenuItem(text)).addActionListener(action);
        return this;
    }

    public MenuBuilder addSeparator() {
        built.addSeparator();
        return this;
    }

    public JMenu create() {
        return built;
    }
}
