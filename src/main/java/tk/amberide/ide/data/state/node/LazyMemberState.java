package tk.amberide.ide.data.state.node;

import java.lang.reflect.*;

public class LazyMemberState<T> implements IState<T> {

    protected Member member;
    protected String name;
    protected Object parent;

    public LazyMemberState(Field field, String name, Object parent) {
        this((Member) field, name, parent);
    }

    public LazyMemberState(Method method, String name, Object parent) {
        this((Member) method, name, parent);
    }

    LazyMemberState(Member member, String name, Object parent) {
        if (Modifier.isStatic(member.getModifiers()) && parent != null) {
            throw new IllegalArgumentException("member is static but parent != null");
        }
        this.member = member;
        this.name = name;
        this.parent = parent;
    }

    public Member getMember() {
        return member;
    }

    public T get() {
        ((AccessibleObject) member).setAccessible(true);
        try {
            if (member instanceof Method) {
                return (T) ((Method) member).invoke(parent);
            } else {
                return (T) ((Field) member).get(parent);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "LazyMemberState{" + name + ":"
                + member.getDeclaringClass().getName() + "#"
                + member.getName() + (member instanceof Method ? "()" : "") + "}";
    }
}