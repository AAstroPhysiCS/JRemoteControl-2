package Server.ClientEntity;

import java.util.Map;

public interface ClientBuilder {

    class ClientBuilderTemplate implements ClientBuilder {

        private String id;
        private String[] info;
        private Map<String, String> env;

        public ClientBuilderTemplate(){}

        public ClientBuilder setEnv(Map<String, String> env) {
            this.env = env;
            return this;
        }

        public ClientBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public ClientBuilder setInfo(String[] info) {
            this.info = info;
            return this;
        }

        @Override
        public ClientEntity build() {
            return new ClientEntity(env, info, id);
        }
    }

    ClientBuilder setEnv(Map<String, String> env);
    ClientBuilder setId(String id);
    ClientBuilder setInfo(String[] info);
    ClientEntity build();
}
