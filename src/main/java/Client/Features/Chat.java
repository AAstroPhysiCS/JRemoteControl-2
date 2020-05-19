package Client.Features;

import Handler.PacketHandler;

public class Chat extends Feature {

    public Chat(PacketHandler packetHandler) {
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
