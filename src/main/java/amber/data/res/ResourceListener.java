package amber.data.res;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Tudor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceListener {
    
    public static final int IMPORT = 0x001;
    public static final int DELETE = 0x01;

    int type();

    int event();
}
