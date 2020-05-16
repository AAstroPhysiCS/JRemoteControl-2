package Server.ClientEntity;

import Events.Listener;
import Server.Overlay.Controller.Controller;

import java.net.InetAddress;
import java.util.Map;

public class ClientEntity {

    private final Map<String, String> env;
    private final String[] info;
    private final int id;

    private final InetAddress address;
    private final int port;

    //every cliententity has an listener
    private final Listener<ClientEntity> event;

    ClientEntity(Map<String, String> env, String[] info, int id, InetAddress address, int port) {
        this.env = env;
        this.info = info;
        this.id = id;
        this.address = address;
        this.port = port;

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
                """.formatted(info[info.length - 1], id, info[0], info[1], info[2], info[3]).trim();
    }

    public int getId() {
        return id;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String[] getInfo() {
        return info;
    }

    public Listener<ClientEntity> getEventListener() {
        return event;
    }
}
