package amber.data.state;

import amber.data.state.node.IState;

import java.util.Collection;

public interface IStateManager {

    void registerStateOwner(Object owner);

    void unregisterStateOwner(Object owner);

    void registerMacro(String macro, String value);

    void unregisterMacro(String macro);

    void registerStateListener(Class listener);

    void unregisterStateListener(Class listener);

    int clearMacros();

    IState getState(int scope, String name);

    void addState(int scope, IState state);

    void addState(int scope, String key, Object value);

    void removeState(int scope, String key);

    void removeState(int scope, IState state);

    void addStates(int scope, Collection<IState> states);

    void clearStates(int scope);

    void clearStates();

    Collection<IState> getStates(int scope);

    void emitStates(int scope) throws Exception;

    void emitStates() throws Exception;

    void loadStates(int scope) throws Exception;

    void loadStates() throws Exception;
}
