package amber.data.state.node;

public interface IState<T> {
    T get();

    String getName();
}