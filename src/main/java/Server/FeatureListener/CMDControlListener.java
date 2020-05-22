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

    private String input = "", output = "";
    private ClientEntity selectedClient;

    public CMDControlListener(Controller controller, Server server) {
        super(controller, server);
    }

    @Override
    protected Runnable run(Controller controller) {
        return () -> {
            while (runningFeature) {

                Sleep(1000 / 60);

                String[] splitted = input.split(" ");
                Message<String[]> stringToBytes = () -> splitted;
                byte[] data = objectHandler.writeObjects(stringToBytes);
                byte[] dataModified = objectHandler.writeModifiedArray(data, NetworkInterface.CommandByte.CHAT_BYTE);
                if (selectedClient != null) {
                    try {
                        packetHandler.send(dataModified, dataModified.length, selectedClient.getAddress(), selectedClient.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                byte[] buffer = server.getBuffer();

                if (buffer == null || buffer[0] == 0 || buffer[0] != NetworkInterface.CommandByte.CMDCONTROL_BYTE)
                    continue;

                Message<String> currentInfo = (Message<String>) objectHandler.readModifiedObjects(buffer);
                if (currentInfo.get() instanceof String s) {
                    output = s;
                }
                controller.textAreaControl.appendText("----------RESPONSE----------\n" + output);
                server.resetBuffer();
            }
        };
    }

    @Override
    protected void initComponents(Ref<ClientEntity> selectedClientSup) {
        controller.textAreaControl.setText("");
        controller.textFieldControl.setOnKeyPressed(actionEvent -> {
            if (actionEvent.getCode() == KeyCode.ENTER) {
                controller.textAreaControl.appendText(controller.textFieldControl.getText() + "\n");
                input = controller.textFieldControl.getText();
                controller.textFieldControl.setText("");
            }
        });
        controller.textControlExpandButton.setOnAction(actionEvent -> controller.textAreaControlExpand.appendText("----------RESPONSE----------\n" + output));
        controller.cmdControlButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            selectedClient = selectedClientSup.obj;
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
    }

    @Override
    public void disposeAll() {
        runningFeature = false;
        thread.shutdown();
    }
}
