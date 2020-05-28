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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static Tools.Globals.RECORD_TIME;
import static Tools.Globals.Sleep;

public class AudioCaptureListener extends FeatureListener {

    private final Map<Byte, byte[]> recordingInBytes = new HashMap<>();
    private int size;

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

                if (buffer[0] == NetworkInterface.CommandByte.AUDIOCAPTURE_INFO_STOP) playAudio();

                if (buffer[0] != NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE) continue;

                System.out.println("SEQUENCE: " + buffer[1] + " LOADED!");

                byte[] audioData = (byte[]) objectHandler.readModifiedObjects(buffer, 2).get();
                if (!recordingInBytes.containsKey(buffer[1])) {
                    recordingInBytes.put(buffer[1], audioData);
                    size += audioData.length;
                }
            }
        };
    }

    private void playAudio() {
        server.resetBuffer();

        final byte[] mainBuffer = new byte[size];
        int ptr = 0;
        for (byte[] b : recordingInBytes.values()) {
            System.out.println(Arrays.toString(b));
            for (byte value : b) {
                mainBuffer[ptr++] = value;
            }
        }

        AudioInputStream audioIn = new AudioInputStream(new ByteArrayInputStream(mainBuffer), format, mainBuffer.length);
        DataLine.Info info = new DataLine.Info(Clip.class, format);

        try {
            if (audioIn.getFormat().matches(format)) {
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(audioIn);
                clip.start();

                System.out.println("Listen!");

                Thread.sleep(RECORD_TIME);

                clip.stop();
                clip.close();
                audioIn.close();
            }
        } catch (InterruptedException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        recordingInBytes.clear();
    }

    @Override
    protected void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.audioCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            controller.audioCapturingConf.setOnAction(actionEvent -> {
                if (newValue && selectedClient != null) {
                    runningFeature = true;
                    thread.execute(run(controller));
                    try {
                        packetHandler.send(new byte[]{NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (oldValue && selectedClient != null) {
                runningFeature = false;
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE_STOP}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void disposeAll() {
        runningFeature = false;
        thread.shutdown();
    }
}
