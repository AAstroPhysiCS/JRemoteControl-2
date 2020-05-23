package Client.Features;

import Handler.Message;
import Handler.PacketHandler;
import Tools.Network.NetworkInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static Tools.IConstants.BUFFER_SIZE;
import static Tools.Network.NetworkInterface.Sleep;

public class Chat extends Feature {

    private String input, old, oldIncoming;

    private final JTextArea area;
    private final JTextField field;

    private JFrame frame;
    private final JPanel pane;

    public Chat(PacketHandler packetHandler) {
        super(packetHandler);

        area = new JTextArea();
        field = new JTextField();

        field.setBounds(0, 215, 400, 50);
        JScrollPane scroll = new JScrollPane(area, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(0, 0, 390, 190);

        area.setBackground(Color.DARK_GRAY);
        field.setBackground(Color.DARK_GRAY);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        area.setBorder(new EmptyBorder(0, 0, 0, 0));
        field.setBorder(new EmptyBorder(0, 0, 0, 0));

        pane = new JPanel();
        pane.setLayout(null);
        pane.setBounds(0, 0, 400, 300);
        pane.setBackground(Color.BLACK);
        pane.add(scroll);
        pane.add(field);
    }

    @Override
    public void stopFeature() {
        running = false;
        frame.dispose();
        frame = null;
    }

    @Override
    public void startFeature() {
        if (frame == null) {
            initComponents();
        }

        running = true;
        thread.execute(run());
    }

    private void initComponents() {
        area.setText("Dad wants to speak with you!\n");
        field.setText("");

        field.addActionListener(e -> {
            input = field.getText();
            field.setText("");
        });

        frame = new JFrame();
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        frame.setIconImage(icon);
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(400, 300));
        frame.setLayout(null);
        frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (400 / 2), Toolkit.getDefaultToolkit().getScreenSize().height / 2 - (300 / 2));
        frame.setContentPane(pane);
//        frame.setUndecorated(true);
//        frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
//        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
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

                    Message<String> currentInfo = (Message<String>) objectHandler.readModifiedObjects(buffer);
                    if (currentInfo.get() instanceof String s) {
                        if(!s.equals(oldIncoming)){
                            area.append("\nDad: " + s);
                            oldIncoming = s;
                        }
                    }

                    if (!input.isBlank() && !input.equals(old)) {
                        final String i = input;
                        area.append("\nYou: " + i);
                        Message<String> stringMessage = () -> i;
                        byte[] data = objectHandler.writeObjects(stringMessage);
                        byte[] dataModified = objectHandler.writeModifiedArray(data, NetworkInterface.CommandByte.CHAT_BYTE);
                        try {
                            packetHandler.send(dataModified, dataModified.length, packetHandler.getAddress(), packetHandler.getPort());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    old = input;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    @Override
    public void disposeAll() {

    }
}
