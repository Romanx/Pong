package client;

import common.DEBUG;
import common.NetObjectReader;

import java.net.Socket;
/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
class Player extends Thread
{
    private C_PongModel model;
    private Socket socket;

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
            NetObjectReader in = new NetObjectReader(socket);

            while (true) {
                Object obj = in.get();
                if (obj == null) return;
                DEBUG.trace((String) obj);
                //C_PongModel model = (C_PongModel) obj;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("Exception player.run : Client - " + ex.getMessage());
        }

    }
}
