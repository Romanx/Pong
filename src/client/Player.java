package client;

import common.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
public class Player extends Thread {
    private C_PongModel model;
    private Socket socket;
    private NetObjectReader in;
    private NetObjectWriter out;

    /**
     * Constructor
     *
     * @param model - model of the game
     * @param s     - Socket used to communicate with server
     */
    public Player(C_PongModel model, Socket s) {
        this.model = model;
        this.socket = s;
        // The player needs to know this to be able to work
    }

    /**
     * Returns the players NetObjectWriter to avoid the issue with creating more than one.
     *
     * @return the players NetObjectWriter
     */
    public NetObjectWriter getPlayerOutput() {
        return this.out;
    }

    /**
     * Sends the close connection command to the server.
     */
    public void closeConnection() {
        this.out.put(new Object[]{"CloseConnection"});
    }

    /**
     * Get and update the model with the latest bat movement
     * sent by the server
     */
    public void run()                             // Execution
    {
        // Listen to network to get the latest state of the game from the server
        // Update model with this information, Redisplay model
        DEBUG.trace("Player.run");
        DEBUG.trace("Socket: " + socket.getInetAddress() + ", " + socket.getPort());

        try {
            try {
                out = new NetObjectWriter(this.socket);
                in = new NetObjectReader(this.socket);
            } catch (Exception ex) {
                ex.printStackTrace();
                DEBUG.error("Exception player.constructor : Client - " + ex.getMessage());
            }

            DEBUG.trace("Connecting");
            out.put("Connect");

            Object obj = in.get();
            if (obj != null) {
                Object[] messages = (Object[]) obj;
                DEBUG.trace("RESULT: %s", messages[0]);

                String response = (String) messages[0];
                Boolean isMultiplex = (Boolean) messages[1];
                int gameNumber = (Integer) messages[2];

                if (response.equals("Connected")) {
                    if (isMultiplex) {
                        this.processMultiplexResponses(gameNumber);
                    } else {
                        this.processTCPResponses();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("Exception player.constructor : Client - " + ex.getMessage());
        }
    }

    /**
     * Defines how to process TCP Responses from the server.
     * Contains the main loop of how to deal with them.
     */
    public void processTCPResponses() {
        Object obj;
        while (true) {
            obj = in.get();
            if (obj != null) {
                double ballX, ballY, batZeroY, batOneY;
                long timestamp;

                // Pull the data from the serialised message. Currently in two sections,
                // Object array of GameObject data and the last request time.
                Object[] result = (Object[]) obj;
                Object[] gameObjects = (Object[]) result[0];

                //Define variable here rather than pulling out of the array twice.
                timestamp = (Long) result[1];

                // Only update the timestamp if we've sent a request since last update.
                if (timestamp > 0) {
                    model.addRequestTimestamp(System.currentTimeMillis() - timestamp);
                }

                // Assignments with casting to the correct types. Since they're stored as Objects i have to cast
                // them to Boxed primative types.
                ballX = (Double) gameObjects[0];
                ballY = (Double) gameObjects[1];
                batZeroY = (Double) gameObjects[2];
                batOneY = (Double) gameObjects[3];
                GameObject ball = model.getBall();
                GameObject[] bats = model.getBats();

                ball.setX(ballX);
                ball.setY(ballY);
                bats[0].setY(batZeroY);
                bats[1].setY(batOneY);

                //Notify Model has Changed.
                model.modelChanged();
            }
        }
    }

    /**
     * Defines how to deal with Multiplex responses from the server.
     * Contains the main loop to wait for them.
     *
     * @param gameNumber the game number to listen for.
     */
    public void processMultiplexResponses(int gameNumber) {
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
            DEBUG.error("Exception Player.processMultiplexReponse : Client - " + e.getMessage());

        }
    }
}
