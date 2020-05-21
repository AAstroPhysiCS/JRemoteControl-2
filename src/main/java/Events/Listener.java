package Events;

@FunctionalInterface
public interface Listener<T> {
    boolean call(T e);
}
