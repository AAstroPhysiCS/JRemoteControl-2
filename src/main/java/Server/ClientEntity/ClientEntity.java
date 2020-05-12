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

    @Override
    public String toString(){
        return """
                Name: %s
                OS Version: %s
                OS Vendor: %s
                OS Architecture %s
                """.formatted(info[info.length - 1], info[0], info[1], info[2]);
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
