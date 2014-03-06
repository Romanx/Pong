package client;

import common.DEBUG;
import common.GameObject;
import common.NetObjectReader;
import common.NetObjectWriter;

import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
class Player extends Thread
{
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

    public NetObjectWriter getPlayerOutput() {
        return this.out;
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
            } catch(Exception ex) {
                ex.printStackTrace();
                DEBUG.error("Exception player.constructor : Client - " + ex.getMessage());
            }

            DEBUG.trace("Connecting");
            out.put("Connect");

            Object obj = in.get();
            if (obj != null) {
                String message = (String) obj;
                DEBUG.trace("RESULT: %s", message);

                if (message.equals("Connected")) {
                    while (true) {
                        obj = in.get();
                        if (obj != null) {
                            Object[] result = (Object[])obj;
                            double ballX, ballY, batZeroY, batOneY;
                            long timestamp;
                            ballX = (Double)result[0];
                            ballY = (Double)result[1];
                            batZeroY = (Double)result[2];
                            batOneY = (Double)result[3];
                            timestamp = (Long)result[4];
                            if(timestamp > 0) {
                                model.addRequestTimestamp(System.currentTimeMillis() - timestamp);
                            }

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
                } else {
                    DEBUG.trace(message);
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            DEBUG.error("Exception player.constructor : Client - " + ex.getMessage());
        }
    }
}
