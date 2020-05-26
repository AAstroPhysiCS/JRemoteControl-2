package Handler;

import java.io.Serializable;

@FunctionalInterface
public interface Message<V> extends Serializable {
    V get();
}
