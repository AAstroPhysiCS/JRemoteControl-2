package Handler;

import Handler.Serialization.ObjectSerialization;

import static Tools.Globals.BUFFER_SIZE;

public class ObjectHandler<V extends Message<?>> {

    public V readObjects(byte[] data) {
        return ObjectSerialization.deseralize(data);
    }

    public byte[] writeObjects(V obj) {
        return ObjectSerialization.serialize(obj);
    }

    public V readModifiedObjects(byte[] data) {
        byte[] unModified = new byte[data.length - 1];
        System.arraycopy(data, 1, unModified, 0, unModified.length);
        return ObjectSerialization.deseralize(unModified);
    }

    public V readModifiedObjects(byte[] data, int n) {
        byte[] unModified = new byte[data.length - n];
        System.arraycopy(data, n, unModified, 0, unModified.length);
        return ObjectSerialization.deseralize(unModified);
    }

    public byte[] writeModifiedArray(byte[] arr, byte id) {
        byte[] arrNew = new byte[arr.length + 1];
        System.arraycopy(arr, 0, arrNew, 1, arr.length);
        arrNew[0] = id;
        return arrNew;
    }

    public byte[] writeModifiedArray(byte[] arr, byte... id) {
        byte[] arrNew = new byte[arr.length + id.length];
        System.arraycopy(arr, 0, arrNew, id.length, arr.length);
        System.arraycopy(id, 0, arrNew, 0, id.length);
        return arrNew;
    }

    public byte[][] spliceArray(byte[] arr, int size) {
        int levelOfSplice = arr.length / (size) + 1;
        byte[][] allDataSplitted = new byte[levelOfSplice][size];
        int currLen = 0;
        for (int i = 0; i < allDataSplitted.length; i++) {
            if (i == allDataSplitted.length - 1) {
                allDataSplitted[i] = new byte[arr.length - currLen];
                System.arraycopy(arr, i * (size), allDataSplitted[i], 0, allDataSplitted[i].length);
            } else {
                System.arraycopy(arr, i * (size), allDataSplitted[i], 0, size);
            }
            currLen += size;
        }
        return allDataSplitted;
    }
}
