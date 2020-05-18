package Server.ClientEntity;

import Events.Listener;
import Events.EventListener;
import Server.Overlay.Controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class ClientEvent {

    private final List<Listener<ClientEntity>> eventListenerList = new ArrayList<>();
    private final Controller controller;

    public ClientEvent(Controller controller) {
        this.controller = controller;
    }

    public void addListener(EventListener<ClientEntity> eventListener) {
        if(!eventListenerList.contains(eventListener))
            eventListenerList.add(eventListener);
    }

    public ClientEntity call() {
        ClientEntity focused = null;
        for (var changeListener : eventListenerList) {
            var c = ((EventListener<ClientEntity>) changeListener).get();
            if (changeListener.call(controller, c)) {
                focused = c;
            }
        }
        return focused;
    }
}
