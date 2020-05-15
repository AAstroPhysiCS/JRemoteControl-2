package Handler;

import java.io.Serializable;

public interface Message<V> extends Serializable {
    V get();
}
