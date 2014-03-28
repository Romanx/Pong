package server.Game;

import server.S_PongModel;

import java.net.ServerSocket;

/**
 * Created by Alex on 24/03/2014.
 *
 * An abstract class defining what methods a Pong_Game needs to allow an abstraction
 * between TCP/IP and Multicast.
 */
public abstract class S_PongGame {
    protected ServerSocket ss;
    protected int threadNo = 0;
    protected int numPlayers;

    public S_PongGame(int threadNo, ServerSocket socket) {
        this.threadNo = threadNo;
        this.ss = socket;
    }

    public abstract void makeContactWithClients(S_PongModel model);
    public abstract void start();
}

