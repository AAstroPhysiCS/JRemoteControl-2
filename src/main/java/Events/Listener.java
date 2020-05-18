package Events;

import Server.Overlay.Controller.Controller;

@FunctionalInterface
public interface Listener<T> {
    boolean call(Controller controller, T e);
}
