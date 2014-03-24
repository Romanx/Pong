package server;

import common.*;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Start the game server
 * The call to makeActiveObject() in the model
 * starts the play of the game
 */
class Server {
    private NetObjectWriter p0, p1 = null;
    private int NUM_PLAYERS = 0;
    private int threadNo = 0;
    private ServerSocket ss = null;
    private static Boolean multiplexMode = false;

    public final static AtomicInteger ACTIVE_THREAD_COUNT = new AtomicInteger(0);
    private static int threadCount = 0;

    public static S_PongGame getTypedGame(int threadNo, ServerSocket ss) {
        if(multiplexMode) {
            return new S_TCPPongGame(threadNo, ss);
        } else {
            return new S_TCPPongGame(threadNo, ss);
        }
    }

    public static void main(String args[]) {
        try {

            if(args.length > 0 && args[0].equals(Global.MULTIPLEX)) {
                multiplexMode = true;
            }

            ServerSocket socket = new ServerSocket(Global.PORT);  // Server Socket

            while(true) {
                //TODO: Remove the limit or adjust for a ThreadPool.
                while(ACTIVE_THREAD_COUNT.get() < 3) {
                    ACTIVE_THREAD_COUNT.getAndIncrement();
                    (getTypedGame(threadCount++, socket)).start();
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Server.main()]", ex.getMessage());
        }
    }

    public Boolean getMultiplexMode() {
        return multiplexMode;
    }
}

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
class Player extends Thread {
    private S_PongModel model;
    private int playerNumber;
    private Socket socket;

    private NetObjectReader in;
    private NetObjectWriter out;

    /**
     * Constructor
     *
     * @param player Player 0 or 1
     * @param model  Model of the game
     * @param s      Socket used to communicate the players bat move
     */
    public Player(int player, S_PongModel model, Socket s) {
        this.model = model;
        this.playerNumber = player;
        this.socket = s;

        try {
            in = new NetObjectReader(socket);
            out = new NetObjectWriter(socket);
        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("Exception player.run : Server - " + ex.getMessage());
        }

    }


    /**
     * Get and update the model with the latest bat movement
     */
    public void run()                             // Execution
    {
        DEBUG.trace("player.run : Server");
        DEBUG.trace("Socket: " + socket.getInetAddress() + ", " + socket.getPort());

        out.put("Connected");

        while (!model.getShutdown()) {
            Object obj = in.get();
            if (obj == null) return;
            Object[] result = (Object[])obj;

            String commandType = (String)result[0];

            if(commandType.equals("GameData")) {
                double batY = (Double)result[1];
                long timestamp = (Long)result[2];

                // To avoid just resetting it to the same value.
                GameObject bat = this.model.getBat(playerNumber);

                //Calculate how long the request took to reach the server.
                long timeDelay = System.currentTimeMillis() - timestamp;
                this.model.addToTotalRequestTime(playerNumber, timeDelay);

                this.model.setRequestTime(this.playerNumber, timestamp);
                //this.model.setAveragePingTime(this.playerNumber, pingTime);
                bat.moveY(batY);

                this.model.modelChanged();
            }
            else if(commandType.equals("CloseConnection")) {
                model.setShutdown(true);
                Server.ACTIVE_THREAD_COUNT.getAndDecrement();
            }
        }
    }

    public NetObjectReader getPlayerInput() { return this.in; }
    public NetObjectWriter getPlayerOutput() {return this.out; }
}
