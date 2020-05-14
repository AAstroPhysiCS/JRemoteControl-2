package Server.ClientEntity.Events;

import Server.Overlay.Controller;

public class Listener<T> implements EventListener<T> {

    private final T e;

    public Listener(T e) {
        this.e = e;
    }

    @Override
    public boolean onFocus(Controller controller, T e) {
        return controller.item == e;
    }

    public T get() {
        return e;
    }
}
