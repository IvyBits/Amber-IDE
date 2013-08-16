package amber.data.state.node;

public class SimpleState<T> implements IMutableState<T> {
    protected T value;
    protected final String name;

    public SimpleState(String name) {
        this.name = name;
    }

    public SimpleState(String name, T value) {
        this(name);
        set(value);
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    public String getName() {
        return name;
    }
}
