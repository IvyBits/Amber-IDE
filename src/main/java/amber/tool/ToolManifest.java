package amber.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Tudor
 */
public class ToolManifest {
    private final String name, description, version, main;
    private final String[] authors;

    public ToolManifest(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public ToolManifest(InputStream in) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = (Map<String, Object>) yaml.load(in);
        name = (String) values.get("name");
        description = (String) values.get("description");
        main = (String) values.get("main");
        version = (String) values.get("version");
        if (values.containsKey("author")) {
            authors = new String[]{(String) values.get("author")};
        } else {
            authors = (String[]) ((List) values.get("authors")).toArray(new String[0]);
        }
    }

    /**
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * @return the description
     */
    public String description() {
        return description;
    }

    /**
     * @return the version
     */
    public String version() {
        return version;
    }

    /**
     * @return the main
     */
    public String main() {
        return main;
    }

    /**
     * @return the authors
     */
    public String[] authors() {
        return authors;
    }
}
