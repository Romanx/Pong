package server;

import common.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Start the game server
 * The call to makeActiveObject() in the model
 * starts the play of the game
 */
class Server {
    private NetObjectWriter p0, p1 = null;
    private int NUM_PLAYERS = 0;

    public static void main(String args[]) {
        (new Server()).start();
    }

    /**
     * Start the server
     */
    public void start() {
        DEBUG.set(true);
        DEBUG.trace("Pong Server");
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

            ServerSocket ss = new ServerSocket(Global.PORT);  // Server Socket

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
                        p.start();
                        DEBUG.trace("Player One Connected");
                        NUM_PLAYERS++;
                    } else if (p1 == null) {
                        p1 = p.getPlayerOutput();
                        p.start();
                        DEBUG.trace("Player One Connected");
                        NUM_PLAYERS++;
                    }
                }

            } while (NUM_PLAYERS < 2);


        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Server.makeContactWithClients()]", ex.getMessage());

        }

        //TODO: makeContactWithClients Do This Method.
    }
}

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
//TODO: implement this class.
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

        while (true) {
            Object obj = in.get();
            if (obj == null) return;
            DEBUG.trace(String.format("Player %d: %s", playerNumber, (String) obj));
            Scanner s = new Scanner((String) obj);

            float batY = s.nextFloat();

            GameObject bat = this.model.getBat(playerNumber);
            bat.setY(bat.getY() + batY);

            this.model.modelChanged();
        }
    }

    public NetObjectReader getPlayerInput() { return this.in; }
    public NetObjectWriter getPlayerOutput() {return this.out; }
}
