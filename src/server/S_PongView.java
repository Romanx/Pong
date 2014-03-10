package server;

import common.DEBUG;
import common.GameObject;
import common.NetObjectWriter;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;


/**
 * Displays a graphical view of the game of pong
 */
class S_PongView implements Observer {
    private S_PongController pongController;
    private GameObject ball;
    private GameObject[] bats;
    private NetObjectWriter left, right;
    private Random r = new Random();

    public S_PongView(NetObjectWriter c1, NetObjectWriter c2) {
        this.left = c1;
        this.right = c2;
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

            delayByMiliseconds(timeDiff);

            left.put(new Object[]{result, model.getRequestTime(0)});
            //DEBUG.trace("Player Ones Ping is Lower than Player Twos.");
        } else {
            long timeDiff = playerOnePing - playerTwoPing;
            left.put(new Object[]{result, model.getRequestTime(0)});

            delayByMiliseconds(timeDiff);

            right.put(new Object[] {result, model.getRequestTime(1)});
            //DEBUG.trace("Player Twos Ping is Lower than Player Ones.");
        }

        //Remove the old request since we've told the client about it.
        model.setRequestTime(0, 0);
        model.setRequestTime(1, 0);
    }

    private void delayByMiliseconds(long timeDiff) {
        // Sleep for the time that the two clients are out of sync.
        try {
            if(timeDiff > 0) Thread.sleep(timeDiff);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
