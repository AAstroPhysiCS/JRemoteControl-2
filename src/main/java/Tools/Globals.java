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

    static int map(int n, int start1, int stop1, int start2, int stop2) {
        return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
    }
}
