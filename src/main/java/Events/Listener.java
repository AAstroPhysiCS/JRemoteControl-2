package Events;

@FunctionalInterface
public interface Listener<T> {
    boolean listen(T e);
}
