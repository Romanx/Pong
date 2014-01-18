package server;

import common.DEBUG;
import common.GameObject;
import common.NetObjectWriter;

import java.util.Observable;
import java.util.Observer;


/**
 * Displays a graphical view of the game of pong
 */
class S_PongView implements Observer {
    private S_PongController pongController;
    private GameObject ball;
    private GameObject[] bats;
    private NetObjectWriter left, right;

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

        String s = String.format("%4.2f %4.2f %4.2f %4.2f", ball.getX(), ball.getY(), this.bats[0].getY(), this.bats[1].getY());

        left.put(s);
        right.put(s);

        //TODO: Send the position of games objects to client.

        // Now need to send position of game objects to the client
        //  as the model on the server has changed
    }


}
