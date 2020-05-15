package Events;

import Server.Overlay.Controller.Controller;

@FunctionalInterface
public interface EventListener<T> {
    boolean onFocus(Controller controller, T e);
}
