package server;

import common.DEBUG;
import common.Global;
import common.NetObjectReader;
import common.NetObjectWriter;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Start the game server
 *  The call to makeActiveObject() in the model 
 *   starts the play of the game
 */
class Server
{
    private NetObjectWriter p0, p1;

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
        model.makeActiveObject();        // Start play
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
            NetObjectReader in;
            NetObjectWriter out;
            while (true) {
                Socket s = ss.accept();
                in = new NetObjectReader(s);
                out = new NetObjectWriter(s);
                DEBUG.trace("%s", "Connected!");

                Object obj = in.get();
                if (obj == null) break;
                String message = (String) obj;

                if (message.equals("Connect")) {
                    if (p0 == null) {
                        //new Player(0, model, s);
                        p0 = out;
                        p0.put("Connected");
                        (new Player(0, model, s)).start();
                        DEBUG.trace("Player One Connected");
                    } else if (p1 == null) {
                        //new Player(1, model, s);
                        p1 = out;
                        p1.put("Player Two Connected");
                    } else {
                        out.put("No More Room!");
                        //TODO: Spectators
                    }
                }

                in.close();
                out.close();
                //s.close();
            }

        } catch (Exception ex) {
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
    }


    /**
     * Get and update the model with the latest bat movement
     */
    public void run()                             // Execution
    {
        DEBUG.trace("player.run : Server");
        DEBUG.trace("Socket: " + socket.getInetAddress() + ", " + socket.getPort());
        NetObjectWriter in;

        while (true) {
            if (model.hasChanged()) {
                try {
                    in = new NetObjectWriter(socket);
                    in.put(model);

                } catch (Exception ex) {
                    DEBUG.error("Exception player.run : Server - " + ex.getMessage());
                }
            }
        }

    }
}
