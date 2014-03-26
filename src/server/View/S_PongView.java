package server.View;

import common.DEBUG;
import common.GameObject;
import common.NetObjectWriter;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;


/**
 * Displays a graphical view of the game of pong
 */
public abstract class S_PongView implements Observer {
    /**
     * Called from the model when its state is changed
     *
     * @param aPongModel Model of game
     * @param arg        Arguments - not used
     */
    public abstract void update(Observable aPongModel, Object arg);
}
