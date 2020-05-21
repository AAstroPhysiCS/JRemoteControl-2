package Server.ClientEntity;

import Events.EventListener;
import Events.Listener;

import java.util.ArrayList;
import java.util.List;

public class ClientEvent {

    private final List<Listener<ClientEntity>> eventListenerList = new ArrayList<>();

    public void addListener(EventListener<ClientEntity> eventListener) {
        if(!eventListenerList.contains(eventListener))
            eventListenerList.add(eventListener);
    }

    public ClientEntity call() {
        ClientEntity focused = null;
        for (var changeListener : eventListenerList) {
            var c = ((EventListener<ClientEntity>) changeListener).get();
            if (changeListener.call(c)) {
                focused = c;
            }
        }
        return focused;
    }
}
