package tk.amberide.ide.gui.misc;

import java.io.PrintStream;
import java.util.ArrayList;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.demo.TextAreaReadline;
import org.jruby.internal.runtime.ValueAccessor;

public class RubyConsole extends RSyntaxTextArea {

    private TextAreaReadline tar;
    private Ruby runtime;

    public RubyConsole() {
        tar = new TextAreaReadline(this);

        runtime = Ruby.newInstance(new RubyInstanceConfig() {
            {
                setInput(tar.getInputStream());
                setOutput(new PrintStream(tar.getOutputStream()));
                setError(new PrintStream(tar.getOutputStream()));
                setObjectSpaceEnabled(true); // useful for code completion inside the IRB
            }
        });

        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
        runtime.getLoadService().init(new ArrayList());

        tar.hookIntoRuntime(runtime);

        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
        setAnimateBracketMatching(false);
        setAntiAliasingEnabled(true);
        setCodeFoldingEnabled(true);
    }

    public Ruby getRuntime() {
        return runtime;
    }

    public void eval(final String scriptlet) {
        Thread proc = new Thread() {
            @Override
            public void run() {
                runtime.evalScriptlet(scriptlet);
            }
        };
        proc.start();

        try {
            proc.join();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void removeNotify() {
        tar.shutdown();
    }
}
