package client;

import common.DEBUG;
import common.Global;
import common.NetObjectReader;
import common.NetObjectWriter;

import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
class Client
{
    public static void main(String args[]) {
        (new Client()).start();
    }

    /**
     * Start the Client
     */
    public void start() {
        DEBUG.set(true);
        DEBUG.trace("Pong Client");
        //DEBUG.set( false );
        C_PongModel model = new C_PongModel();
        C_PongView view = new C_PongView();
        C_PongController cont = new C_PongController(model, view);

        makeContactWithServer(model, cont);

        model.addObserver(view);       // Add observer to the model
        view.setVisible(true);           // Display Screen
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
            NetObjectWriter out;
            NetObjectReader in;

            Socket s = new Socket(Global.HOST, Global.PORT);
            out = new NetObjectWriter(s);
            in = new NetObjectReader(s);

            DEBUG.trace("Trying to Connect.");
            out.put("Connect");

            Object obj = in.get();
            if (obj != null) {
                String message = (String) obj;
                DEBUG.trace("RESULT: %s", message);

                if (message.equals("Connected")) {
                    (new Player(model, s)).start();//new Socket(s.getInetAddress(), s.getPort()))).start();
                } else {
                    DEBUG.trace(message);
                }
            }

            //in.close();
            //out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Client.makeContactWithServer()]", ex.getMessage());
        }


        //TODO: makeContactWithServer Do This Method.
        // Also starts the Player task that get the current state
        //  of the game from the server
    }
}
