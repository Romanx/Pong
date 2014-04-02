package server.View;

import java.util.Observable;
import java.util.Observer;


/**
 * Determines how the server replies with data for the clients to display.
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
