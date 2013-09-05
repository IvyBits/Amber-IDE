package tk.amberide.ide.tool;

import tk.amberide.ide.data.io.FileIO;
import tk.amberide.ide.tool.rb.RubyTool;
import tk.amberide.ide.tool.exc.InvalidToolException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Tudor
 */
public class ToolLoader {

    public static ToolDefinition loadTool(File toolFile) throws ZipException, IOException, ScriptException {
        ZipFile zip = new ZipFile(toolFile);
        ToolManifest tdf = new ToolManifest(zip.getInputStream(zip.getEntry("tool.yml")));
        String mainPath = tdf.main();

        if (mainPath.endsWith(".class")) {
            try {
                return loadTool(new URLClassLoader(new URL[]{toolFile.toURI().toURL()}).loadClass(mainPath.replace("/", ".")), tdf);
            } catch (Exception ignored) {
                throw new InvalidToolException("failed to load class " + mainPath);
            }
        } else if (mainPath.endsWith(".rb")) {
            mainPath = mainPath.replace("/", File.separator);
            return loadTool(FileIO.read(zip.getInputStream(zip.getEntry(mainPath))), tdf);
        } else {
            throw new InvalidToolException("unknown tool format");
        }
    }

    public static ToolDefinition loadTool(String script, ToolManifest tdf) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");
        engine.eval(script);
        return new ToolDefinition(new RubyTool(engine), tdf, ToolType.RUBY);
    }

    /*   public static ToolDefinition loadTool(File jar) throws IOException {
     JarFile jf = new JarFile(jar);
     if (jf.getManifest().getEntries().containsKey("Tool-Class")) {
     String toolClassPath = null; //jf.getManifest().getEntries().get("Main-Class");
     try {
     return loadTool(new URLClassLoader(new URL[]{jar.toURI().toURL()}).loadClass(toolClassPath));
     } catch (Exception ignored) {
     throw new InvalidToolException("failed to load class " + toolClassPath);
     }
     } else {
     throw new InvalidToolException("no Tool-Class attribute in manifest of " + jar.getName());
     }
     }*/
    public static ToolDefinition loadTool(Class toolClass, ToolManifest tdf) {
        if (Tool.class.isAssignableFrom(toolClass)) {
            try {
                return new ToolDefinition((Tool) toolClass.newInstance(), tdf, ToolType.JAVA);
            } catch (InstantiationException ex) {
                ex.printStackTrace();
                throw new InvalidToolException("failed to instantiate tool");
            } catch (IllegalAccessException ex) {
                throw new InvalidToolException("failed to access tool");
            }
        } else {
            throw new InvalidToolException(toolClass + " does is not a subclass of " + Tool.class);
        }
    }
}
