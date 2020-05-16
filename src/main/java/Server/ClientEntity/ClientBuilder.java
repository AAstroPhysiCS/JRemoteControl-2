package Server.ClientEntity;

import java.net.InetAddress;
import java.util.Map;

public interface ClientBuilder {

    class ClientBuilderTemplate implements ClientBuilder {

        private String[] info;
        private int id;
        private Map<String, String> env;

        private InetAddress address;
        private int port;

        public ClientBuilderTemplate(){}

        public ClientBuilder setEnv(Map<String, String> env) {
            this.env = env;
            return this;
        }

        public ClientBuilder setInfo(String[] info) {
            this.info = info;
            return this;
        }

        @Override
        public ClientBuilder setId(int id) {
            this.id = id;
            return this;
        }

        @Override
        public ClientBuilder address(InetAddress address) {
            this.address = address;
            return this;
        }

        @Override
        public ClientBuilder port(int port) {
            this.port = port;
            return this;
        }

        @Override
        public ClientEntity build() {
            return new ClientEntity(env, info, id, address, port);
        }
    }

    ClientBuilder setEnv(Map<String, String> env);
    ClientBuilder setInfo(String[] info);
    ClientBuilder setId(int id);
    ClientBuilder address(InetAddress address);
    ClientBuilder port(int port);
    ClientEntity build();
}
