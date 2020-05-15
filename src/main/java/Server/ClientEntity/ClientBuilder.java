package Server.ClientEntity;

import java.util.Map;

public interface ClientBuilder {

    class ClientBuilderTemplate implements ClientBuilder {

        private String[] info;
        private int id;
        private Map<String, String> env;

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
        public ClientEntity build() {
            return new ClientEntity(env, info, id);
        }
    }

    ClientBuilder setEnv(Map<String, String> env);
    ClientBuilder setInfo(String[] info);
    ClientBuilder setId(int id);
    ClientEntity build();
}
