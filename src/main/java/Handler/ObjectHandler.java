package Handler;

import Handler.Serialization.ObjectSerialization;

public class ObjectHandler<V extends Message<?>> {

    public V readObjects(byte[] data) {
        return ObjectSerialization.deseralize(data);
    }

    public byte[] writeObjects(V obj){
        return ObjectSerialization.serialize(obj);
    }
}
