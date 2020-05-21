package Events;

import Server.Overlay.Controller.Controller;

public abstract class EventListener<T> implements Listener<T> {

    private final T e;
    protected Controller controller;

    public EventListener(Controller controller, T e) {
        this.e = e;
        this.controller = controller;
    }

    public T get() {
        return e;
    }
}
