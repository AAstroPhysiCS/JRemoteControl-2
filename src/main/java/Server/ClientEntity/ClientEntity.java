package Server.ClientEntity;

import Events.EventListener;
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
    private EventListener<ClientEntity> event;

    ClientEntity(Map<String, String> env, String[] info, int id, InetAddress address, int port) {
        this.env = env;
        this.info = info;
        this.id = id;
        this.address = address;
        this.port = port;
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

    public void setController(Controller controller){
        event = new EventListener<>(controller,this) {
            @Override
            public boolean call(ClientEntity e) {
                return controller.item == e;
            }
        };
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

    public EventListener<ClientEntity> getEventListener() {
        return event;
    }
}
