package tk.amberide.ide.data.state;

import java.util.Collection;
import java.util.HashMap;

/**
 * Constants for State serialization. Scope.PROJECT maps to
 * {PROJECT.DIR/project.xml}, while Scope.GLOBAL maps to
 * {GLOBAL.DIR/global.xml}.
 *
 * @author Tudor
 */
public class Scope {

    public static final int GLOBAL = 0, PROJECT = 1;
    protected static final HashMap<Integer, Scope> scopes = new HashMap<Integer, Scope>();

    static {
        defineScope(0, "${GLOBAL.DIR}", "global");
        defineScope(1, "${PROJECT.DIR}", "project");
    }

    protected final String location, name;

    public Scope(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public static int defineScope(String location, String name) {
        return registerScope(new Scope(location, name));
    }

    public static void defineScope(int id, String location, String name) {
        registerScope(id, new Scope(location, name));
    }

    public static int registerScope(Scope scope) {
        int id = 0;
        while (true) {
            if (scopes.containsKey(id)) {
                id++;
            } else {
                break;
            }
        }
        registerScope(id, scope);
        return id;
    }

    public static void registerScope(int id, Scope scope) {
        if (scopes.containsKey(id)) {
            throw new IllegalStateException("scope id " + id + " already occupied by " + scopes.get(id) + " when adding " + scope);
        }
        scopes.put(id, scope);
    }

    public static boolean isValidScope(int id) {
        return scopes.containsKey(id);
    }

    public static Scope predefinedScope(int id) {
        return scopes.get(id);
    }

    public static void validateScope(int id) {
        if (!isValidScope(id))
            throw new RuntimeException("scope " + id + " is invalid");
    }

    public static Collection<Scope> scopes() {
        return scopes.values();
    }
    
    public static Collection<Integer> scopeIds() {
        return scopes.keySet();
    }

    public String location() {
        return location;
    }

    public String name() {
        return name;
    }
}