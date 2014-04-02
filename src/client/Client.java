package client;

import common.DEBUG;
import common.Global;

import java.net.ConnectException;
import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
class Client {
    private static boolean isSpectator;
    private static int gameNumber;

    public static void main(String args[]) {
        if (args.length > 0) {
            isSpectator = true;
            gameNumber = Integer.parseInt(args[0]);
        }
        (new Client()).start();
    }

    /**
     * Start the Client
     */
    public void start() {
        DEBUG.trace("Pong Client");
        DEBUG.set(true);
        C_PongModel model = new C_PongModel();
        C_PongView view = new C_PongView();
        C_PongController cont = new C_PongController(model, view);

        if (!isSpectator) {
            makeContactWithServer(model, cont);
        } else {
            startSpectating(model);
        }

        model.addObserver(view);       // Add observer to the model
        view.setVisible(true);           // Display Screen
    }

    /**
     * This will setup the Spectator thread which doesn't need contact
     * with the server but needs to know how to listen to changes.
     *
     * @param model of the game
     */
    public void startSpectating(C_PongModel model) {
        DEBUG.trace("Starting to spectate on game " + gameNumber);
        Spectator s = new Spectator(model, gameNumber);
        s.start();
    }

    /**
     * Make contact with the Server who controls the game
     * Players will need to know about the model
     *
     * @param model Of the game
     * @param cont  Controller (MVC) of the Game
     */
    public void makeContactWithServer(C_PongModel model,
                                      C_PongController cont) {
        try {
            // Also starts the Player task that get the current state of the game from the server
            Socket s = new Socket(Global.HOST, Global.PORT);

            if (s.isConnected()) {

                DEBUG.trace("Trying to Connect.");
                Player p = new Player(model, s);
                cont.setPlayer(p);
                p.start();
            }

        } catch (ConnectException ex) {
            DEBUG.trace("%s  : Location[Client.makeContactWithServer()]", "The Server does not seem to be running.");
        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Client.makeContactWithServer()]", ex.getMessage());
        }


    }
}
