package server.Game;

import common.DEBUG;
import common.NetObjectReader;
import common.NetObjectWriter;
import server.*;
import server.View.S_MulticastPongView;
import server.View.S_PongView;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Alex on 26/03/2014.
 */
public class S_MulticastPongGame extends S_PongGame {
    private NetObjectWriter p0, p1 = null;

    public S_MulticastPongGame(int threadNo, ServerSocket socket) {
        super(threadNo, socket);
    }

    @Override
    public void makeContactWithClients(S_PongModel model) {
        try {
            do {
                Socket s = ss.accept();
                Player p = new server.Player(numPlayers, model, s, true);
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
    public void start() {
        DEBUG.set(true);
        DEBUG.trace("Multicast Pong Server " + this.threadNo);

        //DEBUG.set( false );               // Otherwise lots of debug info
        S_PongModel model = new S_PongModel();
        model.setGameNumber(threadNo);

        this.makeContactWithClients(model);

        S_PongView view = null;
        try {
            view = new S_MulticastPongView(this.threadNo);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        S_PongController cont = new S_PongController(model, view);

        model.addObserver(view);       // Add observer to the model
        model.makeActiveObject();      // Start play
    }
}
