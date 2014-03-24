package server;

import common.*;

import java.io.IOException;
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

    public final static AtomicInteger ACTIVE_THREAD_COUNT = new AtomicInteger(0);
    private static int threadCount = 0;

    public Server(int threadNo, ServerSocket socket) {
        this.threadNo = threadNo;
        this.ss = socket;
    }

    public static void main(String args[]) {
        try {
            ServerSocket socket = new ServerSocket(Global.PORT);  // Server Socket

            while(true) {
                //TODO: Remove the limit or adjust for a ThreadPool.
                while(ACTIVE_THREAD_COUNT.get() < 3) {
                    ACTIVE_THREAD_COUNT.getAndIncrement();
                    (new Server(threadCount++, socket)).start();
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Server.main()]", ex.getMessage());
        }
    }

    /**
     * Start the server
     */
    public void start() {
        DEBUG.set(true);
        DEBUG.trace("Pong Server " + this.threadNo);
        //DEBUG.set( false );               // Otherwise lots of debug info
        S_PongModel model = new S_PongModel();

        this.makeContactWithClients(model);

        S_PongView view = new S_PongView(p0, p1);
        S_PongController cont = new S_PongController(model, view);

        model.addObserver(view);       // Add observer to the model
        model.makeActiveObject();      // Start play
    }

    /**
     * Make contact with the clients who wish to play
     * Players will need to know about the model
     *
     * @param model Of the game
     */
    public void makeContactWithClients(S_PongModel model) {

        try {
            do {
                Socket s = ss.accept();
                Player p = new server.Player(NUM_PLAYERS, model, s);
                NetObjectReader in = p.getPlayerInput();

                DEBUG.trace("%s", "Connected!");

                Object obj = in.get();
                if (obj == null) break;
                String message = (String) obj;

                if (message.equals("Connect")) {
                    if (p0 == null) {
                        p0 = p.getPlayerOutput();
                        DEBUG.trace("%s : %s", "Server " + threadNo, "Player One Connected");
                    } else if (p1 == null) {
                        p1 = p.getPlayerOutput();
                        DEBUG.trace("Player Two Connected");
                    }
                    NUM_PLAYERS++;
                    p.start();
                }

            } while (NUM_PLAYERS < 2);

        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Server.makeContactWithClients()]", ex.getMessage());
        }
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
