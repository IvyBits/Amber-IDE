package amber.data.state.node;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldState<T> implements IState<T> {
    protected Field field;

    public FieldState(Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            this.field = field;
        } else {
            throw new IllegalArgumentException("field must be static");
        }
    }

    public Field getField() {
        return field;
    }

    public T get() {
        field.setAccessible(true);
        try {
           return (T) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return field.getDeclaringClass().getName() + "/" + field.getName();
    }

    @Override
    public String toString() {
        return "FieldState{" + getName() + "}";
    }
}