package client;

import common.DEBUG;
import common.GameObject;
import common.NetObjectReader;
import common.NetObjectWriter;

import java.net.Socket;
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
        // Listen to network to get the latest state of the
        // game from the server
        // Update model with this information, Redisplay model
        DEBUG.trace("Player.run");
        DEBUG.trace("Socket: " + socket.getInetAddress() + ", " + socket.getPort());
        model.modelChanged();

        try {
            //NetObjectReader in = new NetObjectReader(socket);
            //NetObjectWriter out = new NetObjectWriter(this.socket);

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
                            Scanner s = new Scanner((String)obj);
                            float bX, bY, b0Y, b1Y;
                            bX = s.nextFloat();
                            bY = s.nextFloat();
                            b0Y = s.nextFloat();
                            b1Y = s.nextFloat();

                            GameObject ball = model.getBall();
                            GameObject[] bats = model.getBats();

                            ball.setX(bX);
                            ball.setY(bY);
                            bats[0].setY(b0Y);
                            bats[1].setY(b1Y);

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
