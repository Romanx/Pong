package server.Game;

import common.DEBUG;
import common.NetObjectReader;
import common.NetObjectWriter;
import server.Player;
import server.S_PongController;
import server.S_PongModel;
import server.View.S_PongView;
import server.View.S_TCPPongView;

import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Alex on 26/03/2014.
 *
 * This is a subclass of the PongGame defining how a TCP/IP game makes
 * contact with a client and starts a TCP/IP PongView and a player class
 * with multicast set to false.
 *
 */
public class S_TCPPongGame extends S_PongGame {
    private NetObjectWriter p0, p1 = null;

    public S_TCPPongGame(int threadNo, ServerSocket socket) {
        super(threadNo, socket);
    }

    /**
     * A concrete method defining how a TCP Pong Game connects to its client.
     * @param model to update the data on.
     */
    @Override
    public void makeContactWithClients(S_PongModel model) {
        try {
            do {
                Socket s = ss.accept();
                Player p = new server.Player(numPlayers, model, s, false);
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
                        DEBUG.trace("%s : %s", "Server " + threadNo, "Player Two Connected");
                    }
                    numPlayers++;
                    p.start();
                }

            } while (numPlayers < 2);

        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Server.makeContactWithClients()]", ex.getMessage());
        }
    }

    @Override
    /**
     * The run method for a TCP Pong Game which is passed into the servers game threads.
     */
    public void start() {
        DEBUG.set(true);
        DEBUG.trace("Pong Server " + this.threadNo);

        //DEBUG.set( false );               // Otherwise lots of debug info
        S_PongModel model = new S_PongModel();
        model.setGameNumber(threadNo);

        this.makeContactWithClients(model);

        S_PongView view = new S_TCPPongView(p0, p1);
        S_PongController cont = new S_PongController(model, view);

        model.addObserver(view);       // Add observer to the model
        model.makeActiveObject();      // Start play
    }
}
