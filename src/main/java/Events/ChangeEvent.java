package Events;

import java.util.ArrayList;
import java.util.List;

public class ChangeEvent<V> {

    private final List<EventListener<V>> eventListenerList = new ArrayList<>();

    private V old;

    public void addListener(EventListener<V> eventListener) {
        if (!eventListenerList.contains(eventListener))
            eventListenerList.add(eventListener);
    }

    private V call() {
        V focused = null;
        for (var changeListener : eventListenerList) {
            var c = changeListener.get();
            if (changeListener.listen(c)) {
                focused = c;
            }
        }
        return focused;
    }

    public V onChange() {
        V obj = call();
        if(old == null) {
            old = obj;
            return obj;
        };
        boolean changed = old != obj;
        if (changed) {
            old = obj;
            return obj;
        }
        return null;
    }
}
