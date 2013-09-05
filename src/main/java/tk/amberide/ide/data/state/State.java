package tk.amberide.ide.data.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * State serialization annotation.
 * Handles serialization of instance members automatically. Used by annotating
 * a (preferable non-final) static field, optionally specifying the
 * serialization scope.
 * Usage:
 * <code>
 *    @State(Scope.GLOBAL)
 *    protected static int someState = 0;
 * </code>
 * To register a class as a state owner, you must register it with
 * {@link tk.amberide.data.state.StateManager#registerStateOwner(Class)}
 * At startup, a registered state field will be set to it's value prior
 * to shutdown.
 * Complex data types like maps may be serialized via State, but it is not
 * guaranteed that said data type is supported.
 *
 * @author Tudor
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface State {

    int value();
}
