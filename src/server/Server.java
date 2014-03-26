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
import server.Games.*;

/**
 * Start the game server
 * The call to makeActiveObject() in the model
 * starts the play of the game
 */
class Server {
    private static Boolean multiplexMode = false;

    public final static AtomicInteger ACTIVE_THREAD_COUNT = new AtomicInteger(0);
    private static int threadCount = 0;

    public static S_PongGame getTypedGame(int threadNo, ServerSocket ss) {
        if(multiplexMode) {
            return new S_MulticastPongGame(threadNo, ss);
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

