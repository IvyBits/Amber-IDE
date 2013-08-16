package amber.tool;

import amber.tool.rb.RubyTool;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public final class ToolDefinition {

    private final Tool tool;
    private final ToolManifest manifest;
    private ToolType type;

    ToolDefinition(Tool tool, ToolManifest manifest, ToolType type) {
        this.tool = tool;
        this.manifest = manifest;
        this.type = type;
    }

    public void enable() {
        tool.enable();
    }

    public void disable() {
        tool.disable();
    }

    public boolean isDecorator() {
        return (type == ToolType.JAVA && tool instanceof IDecoratorTool) || (tool instanceof RubyTool && ((RubyTool) tool).isDecorator());
    }

    public Component getToolPanel() {
        if (isDecorator()) {
            switch (type) {
                case JAVA:
                    if (tool instanceof IDecoratorTool) {
                        return (Component) ((IDecoratorTool) tool).getToolPanel();
                    }
                    break;
                case RUBY:
                    if (tool instanceof RubyTool) {
                        return (Component) ((RubyTool) tool).getToolPanel();
                    }
                    break;
            }
        } else {
            throw new UnsupportedOperationException("current tool is not a decorator tool");
        }
        return null;
    }

    public Tool getTool() {
        return tool;
    }

    public ToolManifest getManifest() {
        return manifest;
    }

    public ToolType getType() {
        return type;
    }
}
