package server;

import common.DEBUG;
import common.Global;
import server.Game.S_MulticastPongGame;
import server.Game.S_PongGame;
import server.Game.S_TCPPongGame;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Start the game server. The call to makeActiveObject() in the model starts the play of the game.
 */
class Server {
    private static Boolean multiplexMode = false;

    public final static AtomicInteger ACTIVE_THREAD_COUNT = new AtomicInteger();
    private static int threadCount = 0;

    /**
     * Returns the correct gameType depending on if the server is a multiplexed server or not.
     *
     * @param threadNo the threadNumber.
     * @param ss       the server socket.
     * @return a typed game depending on if the game is Multiplexed or not.
     */
    public static S_PongGame getTypedGame(int threadNo, ServerSocket ss) {
        if (multiplexMode) {
            return new S_MulticastPongGame(threadNo, ss);
        } else {
            return new S_TCPPongGame(threadNo, ss);
        }
    }

    public static void main(String args[]) {
        try {
            //Determines if it's a Multiplex server or not.
            if (args.length > 0 && args[0].equals(Global.MULTIPLEX)) {
                multiplexMode = true;
            }

            ServerSocket socket = new ServerSocket(Global.PORT);  // Server Socket

            while (true) {
                //TODO: Remove the limit or adjust for a ThreadPool.
                while (ACTIVE_THREAD_COUNT.get() < 3) {
                    ACTIVE_THREAD_COUNT.getAndIncrement();
                    (getTypedGame(threadCount++, socket)).start();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            DEBUG.error("%s : Location[Server.main()]", ex.getMessage());
        }
    }
}

