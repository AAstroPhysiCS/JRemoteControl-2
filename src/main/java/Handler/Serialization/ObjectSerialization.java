package Handler.Serialization;

import java.io.*;

public class ObjectSerialization {

    ObjectSerialization() {}

    public static <T> byte[] serialize(T obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput ous = new ObjectOutputStream(bos);
            ous.writeObject(obj);
            ous.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T deseralize(byte[] objSerialized) {
        Object obj = null;
        try(ByteArrayInputStream bis = new ByteArrayInputStream(objSerialized)){
            ObjectInput ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T) obj;
    }
}
