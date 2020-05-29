package Tools;

public interface Globals {
    int BUFFER_SIZE = 65535;

    static void Sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
