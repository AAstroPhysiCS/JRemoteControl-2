package Handler;

import Client.ClientInfo;

public class ObjectHandler<V extends ClientInfo<?>> {

    public V readObjects(byte[] data) {
        return ObjectSerialization.deseralize(data);
    }

    public byte[] writeObjects(V obj){
        return ObjectSerialization.serialize(obj);
    }
}
