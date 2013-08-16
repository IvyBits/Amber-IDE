package amber.gui.editor.tool;

import amber.tool.ToolDefinition;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author Tudor
 */
public class ToolPanel extends JPanel {

    private final ToolDefinition def;

    public ToolPanel(ToolDefinition def) {
        super(new BorderLayout());
        this.def = def;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (def.isDecorator()) {
            add(def.getToolPanel());
        }
    }

    @Override
    public void removeNotify() {
        def.disable();
        super.removeNotify();
    }
}
