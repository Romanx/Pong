package client;

import common.DEBUG;
import common.GameObject;
import common.Global;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Alex on 27/03/2014.
 * <p/>
 * This clas describes how a spectator operates. They listen for changes
 * from the server in the game that they are spectating and then update
 * the model with the changes.
 */
public class Spectator extends Thread {

    private final C_PongModel model;
    private final int gameNumber;

    public Spectator(C_PongModel model, int gameNumber) {
        this.model = model;
        this.gameNumber = gameNumber;
        this.model.setSpectator(true);
    }

    /**
     * This listens to the network for messages from the multicast server and
     * if the game is the current game the client is spectating then it updates
     * the model and redisplays the game.
     */
    public void run() {
        // Listen to network to get the latest state of the game from the server
        // Update model with this information, Redisplay model
        DEBUG.trace("Spectator.run");

        try {
            MulticastSocket socket = new MulticastSocket(Global.MULTIPLEX_PORT);
            InetAddress group = InetAddress.getByName(Global.MCA);
            socket.joinGroup(group);

            byte[] b = new byte[65535];
            ByteArrayInputStream b_in = new ByteArrayInputStream(b);
            DatagramPacket dgram = new DatagramPacket(b, b.length);
            double ballX, ballY, batZeroY, batOneY;

            while (true) {
                socket.receive(dgram);

                ObjectInputStream o_in = new ObjectInputStream(b_in);
                Object o = o_in.readObject();

                Object[] obj = (Object[]) o;

                Integer recievedGameNo = (Integer) obj[0];

                if (recievedGameNo == gameNumber) {
                    ballX = (Double) obj[1];
                    ballY = (Double) obj[2];
                    batZeroY = (Double) obj[3];
                    batOneY = (Double) obj[4];
                    GameObject ball = model.getBall();
                    GameObject[] bats = model.getBats();

                    ball.setX(ballX);
                    ball.setY(ballY);
                    bats[0].setY(batZeroY);
                    bats[1].setY(batOneY);

                    //Notify Model has Changed.
                    model.modelChanged();
                }

                dgram.setLength(b.length); // must reset length field!
                b_in.reset(); // reset so next read is from start of byte[] again
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
