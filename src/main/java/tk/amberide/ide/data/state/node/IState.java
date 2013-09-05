package tk.amberide.ide.data.state.node;

public interface IState<T> {
    T get();

    String getName();
}