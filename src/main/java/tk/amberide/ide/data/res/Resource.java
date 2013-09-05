package tk.amberide.ide.data.res;

import java.io.File;

/**
 *
 * @author Tudor
 */
public class Resource<T> {

    private T resource;
    private String name;
    private File source;
    private int type;
    public static final int TILESET = 0x0001;
    public static final int AUDIO = 0x0010;
    public static final int MODEL = 0x0100;
    public static final int ALL = TILESET | AUDIO | MODEL;

    public Resource(T resource, String name, File source, int type) {
        this.resource = resource;
        this.type = type;
        this.name = name;
        this.source = source;
    }

    public T get() {
        return resource;
    }
    
    public String getName() {
        return name;
    }
    
    public File getSource() {
        return source;
    }

    public int getType() {
        return type;
    }
    

    @ResourceListener(type = Resource.ALL, event = ResourceListener.IMPORT)
    public void resourceLoaded(Resource<T> resource) {
        switch(resource.getType()) {
            case Resource.AUDIO:
            case Resource.MODEL:
            case Resource.TILESET:
                System.out.println("Loaded a resource! It's value is: " + resource.get());
        }
    }
}
