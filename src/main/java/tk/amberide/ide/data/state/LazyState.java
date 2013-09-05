package tk.amberide.ide.data.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * State serialization annotation.
 * Works like @State, but methods can be annotated as well.
 * Consequently, state fields do not get reloaded automatically.
 * To use, specify desired scope and storage name:
 * <code>
 *    @LazyState(scope = Scope.PROJECT, name = "TheMeaningOfLife")
 *    public static void meaning() {
 *       return 42;
 *    }
 *</code>
 *
 * @author Tudor
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyState {

    int scope();

    String name();
}
