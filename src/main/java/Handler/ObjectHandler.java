package Handler;

import Handler.Serialization.ObjectSerialization;

public class ObjectHandler<V extends Message<?>> {

    public V readObjects(byte[] data) {
        return ObjectSerialization.deseralize(data);
    }

    public V readModifiedObjects(byte[] data) {
        byte[] unModified = new byte[data.length - 1];
        System.arraycopy(data, 1, unModified, 0, unModified.length);
        return ObjectSerialization.deseralize(unModified);
    }

    public byte[] writeModifiedArray(byte[] arr, byte id) {
        byte[] arrNew = new byte[arr.length + 1];
        System.arraycopy(arr, 0, arrNew, 1, arr.length);
        arrNew[0] = id;
        return arrNew;
    }

    public byte[][] spliceArray(byte[] arr, int levelOfSplice){
        byte[][] multArr = new byte[levelOfSplice][arr.length/levelOfSplice];
        int ptr = 0;
        for(int i = 0; i < arr.length/levelOfSplice; i++){
            System.out.println(multArr[ptr].length + " " + arr.length);
            if(multArr[ptr].length == arr.length / levelOfSplice)
                ptr++;
            if(ptr == levelOfSplice) break;
            multArr[ptr][i] = arr[i];
        }
        return multArr;
    }

    public byte[] writeObjects(V obj) {
        return ObjectSerialization.serialize(obj);
    }
}
