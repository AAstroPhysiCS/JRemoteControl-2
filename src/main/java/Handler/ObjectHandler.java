package Handler;

import Handler.Serialization.ObjectSerialization;

import java.util.AbstractMap;

public class ObjectHandler<V extends Message<?>> {

    public V readObjects(byte[] data) {
        return ObjectSerialization.deseralize(data);
    }

    public AbstractMap.SimpleEntry<Byte, V> readModifiedObjects(byte[] data){
        byte[] unModified = new byte[data.length - 1];
        System.arraycopy(data, 1, unModified, 0, unModified.length);
        return new AbstractMap.SimpleEntry<>(data[0], ObjectSerialization.deseralize(unModified));
    }

    public byte[] writeObjects(V obj){
        return ObjectSerialization.serialize(obj);
    }
}
