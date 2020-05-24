package Server.FeatureListener;

import Events.FeatureListener;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Network.NetworkInterface;
import Tools.Ref;

import javax.sound.sampled.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static Tools.Network.NetworkInterface.Sleep;

public class AudioCaptureListener extends FeatureListener {

    private static final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);

    public AudioCaptureListener(Controller controller, Server server) {
        super(controller, server);
    }

    @Override
    protected Runnable run(Controller controller) {
        return () -> {
            while (runningFeature) {
                byte[] buffer = server.getBuffer();

                Sleep(1000 / 60);

                if (buffer == null || buffer[0] == 0 || buffer[0] != NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE)
                    continue;

                AudioInputStream audioIn = new AudioInputStream(new ByteArrayInputStream(buffer), format, buffer.length);
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                try {
                    if (audioIn.getFormat().matches(format)) {
                        Clip clip = (Clip) AudioSystem.getLine(info);
                        clip.open(audioIn);
                        clip.start();

                        System.out.println("Listen!");

                        Thread.sleep(10000);

                        clip.stop();
                        clip.close();
                        audioIn.close();
                    }
                } catch (InterruptedException | LineUnavailableException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void initComponents(Ref<ClientEntity> selectedClientSup) {

    }

    @Override
    public void disposeAll() {

    }
}
