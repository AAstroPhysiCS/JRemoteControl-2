package Server.ClientEntity;

import Events.EventListener;
import Events.Listener;
import Server.Overlay.Controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class ClientEvent {

    private final List<EventListener<ClientEntity>> eventListenerList = new ArrayList<>();
    private final Controller controller;

    public ClientEvent(Controller controller) {
        this.controller = controller;
    }

    public void addListener(Listener<ClientEntity> listener) {
        if(!eventListenerList.contains(listener))
            eventListenerList.add(listener);
    }

    public ClientEntity call() {
        ClientEntity focused = null;
        for (var changeListener : eventListenerList) {
            var c = ((Listener<ClientEntity>) changeListener).get();
            if (changeListener.onFocus(controller, c)) {
                focused = c;
            }
        }
        return focused;
    }
}
