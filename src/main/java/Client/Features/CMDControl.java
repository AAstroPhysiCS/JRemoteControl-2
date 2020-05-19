package Client.Features;

import Handler.PacketHandler;

public class CMDControl extends Feature {

    public CMDControl(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @Override
    public void stopFeature() {

    }

    @Override
    public void startFeature() {

    }

    @Override
    protected Runnable run() {
        return null;
    }

    @Override
    public void disposeAll() {

    }
}
