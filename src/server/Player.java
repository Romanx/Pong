package server;

import common.*;

import java.net.Socket;

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
public class Player extends Thread {
    private final Boolean isMultiplex;
    private S_PongModel model;
    private int playerNumber;
    private Socket socket;

    private NetObjectReader in;
    private NetObjectWriter out;

    /**
     * Constructor
     *
     * @param player      Player 0 or 1
     * @param model       Model of the game
     * @param s           Socket used to communicate the players bat move
     * @param isMultiplex Defines if the players is connected to a Multiplex game.
     */
    public Player(int player, S_PongModel model, Socket s, Boolean isMultiplex) {
        this.model = model;
        this.playerNumber = player;
        this.socket = s;
        this.isMultiplex = isMultiplex;

        try {
            in = new NetObjectReader(socket);
            out = new NetObjectWriter(socket);
        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("Exception player.run : Server - " + ex.getMessage());
        }

    }


    /**
     * Get and update the model with the latest bat movement.
     * <p/>
     * This sends the game connection information to the client and
     * then listens for new requests. If it's GameData then the data
     * is updated and sent back to the client, if it's a CloseConnection
     * command then the connection is shutdown.
     */
    public void run()                             // Execution
    {
        DEBUG.trace("player.run : Server");
        DEBUG.trace("Socket: " + socket.getInetAddress() + ", " + socket.getPort());

        out.put(new Object[]{"Connected", isMultiplex, model.getGameNumber()});

        while (!model.getShutdown()) {
            Object obj = in.get();
            if (obj == null) return;
            Object[] result = (Object[]) obj;

            String commandType = (String) result[0];

            if (commandType.equals("GameData")) {
                double batY = (Double) result[1];
                long timestamp = (Long) result[2];

                // To avoid just resetting it to the same value.
                GameObject bat = this.model.getBat(playerNumber);

                //Calculate how long the request took to reach the server.
                long timeDelay = System.currentTimeMillis() - timestamp;
                this.model.addToTotalRequestTime(playerNumber, timeDelay);

                this.model.setRequestTime(this.playerNumber, timestamp);

                double newPos = bat.getY() + batY;

                if (newPos < Global.H - Global.B - Global.BAT_HEIGHT &&
                        newPos > 0 + Global.M) {
                    bat.moveY(batY);
                }

                this.model.modelChanged();
            } else if (commandType.equals("CloseConnection")) {
                model.setShutdown(true);
                Server.ACTIVE_THREAD_COUNT.getAndDecrement();
            }
        }
    }

    public NetObjectReader getPlayerInput() {
        return this.in;
    }

    public NetObjectWriter getPlayerOutput() {
        return this.out;
    }
}
