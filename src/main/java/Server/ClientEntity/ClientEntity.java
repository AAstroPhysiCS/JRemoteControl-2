package Server.ClientEntity;

import Events.Listener;
import Server.Overlay.Controller.Controller;

import java.util.Map;

public class ClientEntity {

    private final Map<String, String> env;
    private final String[] info;
    private final int id;

    //every cliententity has an listener
    private final Listener<ClientEntity> event;

    ClientEntity(Map<String, String> env, String[] info, int id) {
        this.env = env;
        this.info = info;
        this.id = id;

        event = new Listener<>(this) {
            @Override
            public boolean onFocus(Controller controller, ClientEntity e) {
                return controller.item == e;
            }
        };
    }

    @Override
    public String toString() {
        return """
                Name: %s
                Id: %s
                OS Name: %s
                OS Version: %s
                OS Vendor: %s
                OS Architecture: %s
                """.formatted(info[info.length - 1], id, info[1], info[2], info[3], info[4]).trim();
    }

    public int getId() {
        return id;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public String[] getInfo() {
        return info;
    }

    public Listener<ClientEntity> getEventListener() {
        return event;
    }
}
