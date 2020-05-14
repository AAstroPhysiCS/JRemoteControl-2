package Server.ClientEntity.Events;

import Server.Overlay.Controller;

@FunctionalInterface
public interface EventListener<T> {
    boolean onFocus(Controller controller, T e);
}
