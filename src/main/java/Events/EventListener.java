package Events;

public abstract class EventListener<T> implements Listener<T> {

    private final T e;

    public EventListener(T e) {
        this.e = e;
    }

    public T get() {
        return e;
    }
}
