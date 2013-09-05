package tk.amberide.ide.tool.rb;

import tk.amberide.ide.tool.IDecoratorTool;
import tk.amberide.ide.tool.Tool;
import java.awt.Component;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author Tudor
 */
public final class RubyTool extends Tool implements IDecoratorTool {

    private final ScriptEngine engine;

    public RubyTool(ScriptEngine engine) {
        this.engine = engine;
    }

    @Override
    public void enable() {
        try {
            if ((Boolean) engine.eval("self.respond_to?('enable')")) {
                ((Invocable) engine).invokeFunction("enable", new Object[0]);
            }
        } catch (ScriptException ex) {
            throw new RuntimeException("exception enabling tool", ex);
        } catch (NoSuchMethodException ex) {
        }
    }

    @Override
    public void disable() {
        try {
            if ((Boolean) engine.eval("self.respond_to?('disable')")) {
                ((Invocable) engine).invokeFunction("disable", new Object[0]);
            }
        } catch (ScriptException ex) {
            throw new RuntimeException("exception enabling tool", ex);
        } catch (NoSuchMethodException ex) {
        }
    }

    @Override
    public Component getToolPanel() {
        if (isDecorator()) {
            try {
                return (Component) ((Invocable) engine).invokeFunction("get_tool_panel");
            } catch (ScriptException ex) {
                throw new RuntimeException("exception decorating", ex);
            } catch (NoSuchMethodException ex) {
            }
        } else {
            throw new UnsupportedOperationException("current tool is not a decorator tool");
        }
        return null;
    }

    public boolean isDecorator() {
        try {          
            return (Boolean) engine.eval("self.respond_to?('get_tool_panel')");
        } catch (ScriptException ex) { // Will be thrown because we do not pass Component
            ex.printStackTrace();
            return false;
        }
    }
}
