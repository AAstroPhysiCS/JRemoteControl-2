package Server.ClientEntity;

import java.util.Map;

public class ClientEntity {

    private final Map<String, String> env;
    private final String[] info;
    private final byte id;

    ClientEntity(Map<String, String> env, String[] info, byte id){
        this.env = env;
        this.info = info;
        this.id = id;
    }

    public Map<String, String> getEnv() {
        return env;
    }
    public byte getId() {
        return id;
    }
    public String[] getInfo() {
        return info;
    }
}
