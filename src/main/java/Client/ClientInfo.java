package Client;

import java.io.Serializable;

@FunctionalInterface
public interface ClientInfo<T> extends Serializable {
    T get();
}
