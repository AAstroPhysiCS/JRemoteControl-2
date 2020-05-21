package Server.FeatureListener;

import Events.FeatureListener;
import Handler.Message;
import Server.ClientEntity.ClientEntity;
import Server.Overlay.Controller.Controller;
import Server.Server;
import Tools.Network.NetworkInterface;
import Tools.Ref;
import javafx.scene.input.KeyCode;

import java.io.IOException;

import static Tools.Network.NetworkInterface.Sleep;

public class CMDControlListener extends FeatureListener {

    public CMDControlListener(Controller controller, Server server) {
        super(controller, server);
        this.packetHandler = server.getPacketHandler();
    }

    @Override
    protected Runnable run(Controller controller) {
        return () -> {
            while(runningFeature) {
                byte[] buffer = server.getBuffer();

                Sleep(1000 / 60);

                if (buffer == null || buffer[0] == 0) continue;

                try {
                    String[] splitted = controller.textFieldControl.getText().split(" ");
                    Message<String[]> stringToBytes = () -> splitted;
                    byte[] data = objectHandler.writeObjects(stringToBytes);
                    byte[] dataModified = objectHandler.writeModifiedArray(data, NetworkInterface.CommandByte.CHAT_BYTE);
                    packetHandler.send(dataModified, dataModified.length, packetHandler.getPacketAddress(), packetHandler.getPacketPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.textAreaControl.setText("");
        controller.textFieldControl.setOnKeyPressed(actionEvent -> {
            if(actionEvent.getCode() == KeyCode.ENTER){
                controller.textAreaControl.appendText(controller.textFieldControl.getText() + "\n");
                controller.textFieldControl.setText("");
            }
        });
        controller.cmdControlButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            if (newValue && selectedClient != null) {
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CMDCONTROL_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oldValue && selectedClient != null) {
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CMDCONTROL_BYTE_STOP}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        controller.chatControlButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            ClientEntity selectedClient = selectedClientSup.obj;
            if (newValue && selectedClient != null) {
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CHAT_BYTE}, 1, selectedClient.getAddress(), selectedClient.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oldValue && selectedClient != null) {
                try {
                    packetHandler.send(new byte[]{NetworkInterface.CommandByte.CHAT_BYTE_STOP}, 1, selectedClient.getAddress(), selectedClient.getPort());
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
