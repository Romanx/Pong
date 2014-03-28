package server.View;

import common.DEBUG;
import common.GameObject;
import common.NetObjectWriter;
import server.S_PongModel;

import java.util.Observable;
import java.util.*;

/**
 * Created by Alex on 26/03/2014.
 */
public class S_TCPPongView extends S_PongView {
    private GameObject ball;
    private GameObject[] bats;
    private NetObjectWriter left, right;
    private Timer timer;

    public S_TCPPongView(NetObjectWriter c1, NetObjectWriter c2) {
        this.left = c1;
        this.right = c2;

        timer = new Timer();
    }

    /**
     * Called from the model when its state is changed
     *
     * @param aPongModel Model of game
     * @param arg        Arguments - not used
     */
    public void update(Observable aPongModel, Object arg) {
        S_PongModel model = (S_PongModel) aPongModel;
        this.ball = model.getBall();
        this.bats = model.getBats();
        long playerOnePing, playerTwoPing;
        playerOnePing = model.getAverageRequestTime(0);
        playerTwoPing = model.getAverageRequestTime(1);

        Object[] result = new Object[] { ball.getX(), ball.getY(), this.bats[0].getY(), this.bats[1].getY()};

        // Now need to send position of game objects to the client as the model on the server has changed
        // If player two's request time is higher than player one.
        if(playerOnePing < playerTwoPing) {
            long timeDiff = playerTwoPing - playerOnePing;
            right.put(new Object[] {result, model.getRequestTime(1)});

            timer.schedule(new PongResponseTask(new Object[]{result, model.getRequestTime(0)}, left), timeDiff);
            DEBUG.trace("Player Ones Ping is Lower than Player Twos.");
        } else {
            long timeDiff = playerOnePing - playerTwoPing;
            left.put(new Object[]{result, model.getRequestTime(0)});

            timer.schedule(new PongResponseTask(new Object[]{result, model.getRequestTime(1)}, right), timeDiff);
            DEBUG.trace("Player Twos Ping is Lower than Player Ones.");
        }

        //Remove the old request since we've told the client about it.
        model.setRequestTime(0, 0);
        model.setRequestTime(1, 0);
    }

    /**
     * A task to send the response to a given output after a
     */
    private class PongResponseTask extends TimerTask {
        private final Object[] data;
        private final NetObjectWriter output;

        public PongResponseTask(final Object[] dataToSend, final NetObjectWriter out) {
            this.data = dataToSend;
            this.output = out;
        }

        @Override
        public void run() {
            output.put(data);
        }
    }
}
