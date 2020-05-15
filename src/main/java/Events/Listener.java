package Events;

import Server.Overlay.Controller.Controller;

public abstract class Listener<T> implements EventListener<T> {

    private final T e;

    public Listener(T e) {
        this.e = e;
    }

    @Override
    public abstract boolean onFocus(Controller controller, T e);

    public T get() {
        return e;
    }
}
