package amber.data.state.node;

public interface IMutableState<T> extends IState<T> {
    void set(T value);
}