package amber.data.state;

import amber.data.state.node.FieldState;
import amber.data.state.node.IState;
import amber.data.state.node.LazyMemberState;
import amber.data.state.node.SimpleState;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class AbstractStateManager implements IStateManager {

    protected final ScopedListMap<IState> states = new ScopedListMap<IState>();
    protected final ScopedListMap<Method> listeners = new ScopedListMap<Method>();
    protected final HashMap<String, String> macros = new HashMap<String, String>();

    public void registerStateOwner(Object ownerObject) {
        Object parent = null;
        Class owner = ownerObject instanceof Class ? (Class) ownerObject : (parent = ownerObject).getClass();

        for (Field f : owner.getDeclaredFields()) {
            LazyState lazyState;
            State state;
            if ((lazyState = f.getAnnotation(LazyState.class)) != null) {
                addState(lazyState.scope(), new LazyMemberState(f, lazyState.name(), parent));
            } else if (Modifier.isStatic(f.getModifiers())
                    && parent == null && (state = f.getAnnotation(State.class)) != null) {
                addState(state.value(), new FieldState(f));
            }
        }
        for (Method m : owner.getDeclaredMethods()) {
            LazyState lazyState;
            if ((lazyState = m.getAnnotation(LazyState.class)) != null) {
                addState(lazyState.scope(), new LazyMemberState(m, lazyState.name(), parent));
            }
        }
    }

    @Override
    public void unregisterStateOwner(Object ownerObject) {
        Class owner = ownerObject instanceof Class ? (Class) ownerObject : ownerObject.getClass();

        for (Map.Entry<Integer, List<IState>> scope : states.entrySet()) {
            List<IState> listenerList = scope.getValue();
            for (int i = 0; i != listenerList.size(); i++) {
                IState state = listenerList.get(i);
                Member member = null;
                if (state instanceof FieldState) {
                    member = ((FieldState) state).getField();
                } else if (state instanceof LazyMemberState) {
                    member = ((LazyMemberState) state).getMember();
                }
                // The mother of all if statements.
                //
                // if member is not null:
                //    if member is static and owner is declaring class
                //    OR if owner is not a class but owner's class is declaring class:
                //       remove member from list
                if (member != null
                        && (member.getDeclaringClass() == owner
                        || (!(owner instanceof Class) && owner.getClass() == member.getDeclaringClass()))) {
                    listenerList.remove(i--);
                }
            }
        }
    }

    public void loadStates() throws Exception {
        for (int sc : Scope.scopeIds()) {
            loadStates(sc);
        }
    }

    protected void fireStateLoaded(IState state, int scope) {
        List<Method> listenerList = listeners.get(scope);
        for (Method m : listenerList) {
            try {
                if (m.getParameterTypes().length == 2) {
                    m.invoke(scope, state);
                } else if (m.getParameterTypes().length == 1) {
                    m.invoke(state);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerMacro(String macro, String value) {
        macros.put(macro, value);
    }

    public void unregisterMacro(String macro) {
        macros.remove(macro);
    }

    @Override
    public void registerStateListener(Class listener) {
        for (Method member : listener.getDeclaredMethods()) {
            if (Modifier.isStatic(member.getModifiers()) && member.isAnnotationPresent(StateListener.class)) {
                int[] scopes = member.getAnnotation(StateListener.class).value();
                for (int scope : scopes) {
                    listeners.get(scope).add(member);
                }
            }
        }
    }

    @Override
    public void unregisterStateListener(Class listener) {
        for (Map.Entry<Integer, List<Method>> scope : listeners.entrySet()) {
            List<Method> listenerList = scope.getValue();
            for (int i = 0; i != listenerList.size(); i++) {
                if (listenerList.get(i).getClass() == listener) {
                    listenerList.remove(i);
                }
            }
        }
    }

    @Override
    public int clearMacros() {
        int size = macros.size();
        macros.clear();
        return size;
    }

    protected String resolveMacros(String str) {
        for (Map.Entry<String, String> macro : macros.entrySet()) {
            if (str.contains(macro.getKey())) {
                str = str.replace(macro.getKey(), macro.getValue());
            }
        }
        return str;
    }

    @Override
    public IState getState(int scope, String name) {
        List<IState> sl = states.get(scope);
        for (IState state : sl) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        return null;
    }

    @Override
    public void addState(int scope, IState state) {
        IState pre = getState(scope, state.getName());
        if (pre != null) {
            removeState(scope, pre);
        }
        states.get(scope).add(state);
    }

    @Override
    public void addState(int scope, String key, Object value) {
        addState(scope, new SimpleState(key, value));
    }

    @Override
    public void removeState(int scope, String key) {
        List<IState> stateScope = states.get(scope);
        for (int i = 0; i != stateScope.size(); i++) {
            if (stateScope.get(i).getName().equals(key)) {
                stateScope.remove(i);
            }
        }
    }

    @Override
    public void removeState(int scope, IState state) {
        states.get(scope).remove(state);
    }

    @Override
    public void addStates(int scope, Collection<IState> statesGroup) {
        states.get(scope).addAll(statesGroup);
    }

    @Override
    public void clearStates(int scope) {
        states.get(scope).clear();
    }

    @Override
    public void clearStates() {
        states.clear();
    }

    @Override
    public List<IState> getStates(int scope) {
        return states.get(scope);
    }

    public void emitStates() throws Exception {
        for (int scope : states.keySet()) {
            emitStates(scope);
        }
    }

    protected static class ScopedListMap<V> extends HashMap<Integer, List<V>> {

        @Override
        public List<V> get(Object key) {
            Scope.validateScope((Integer) key);
            if (super.get(key) == null) {
                put((Integer) key, new ArrayList<V>());
            }
            return super.get(key);
        }
    }
}
