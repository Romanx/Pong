package server;

import common.DEBUG;
import common.GameObject;

import java.util.Observable;

import static common.Global.*;

/**
 * Model of the game of pong
 * The active object ActiveModel does the work of moving the ball
 */
public class S_PongModel extends Observable {
    private GameObject ball = new GameObject(W / 2, H / 2, BALL_SIZE, BALL_SIZE);
    private GameObject bats[] = new GameObject[2];
    private long requestTime[] = new long[] { 0L, 0L };
    private long averagePingTime[] = new long[] {0L, 0L};

    private Thread activeModel;

    public S_PongModel() {
        bats[0] = new GameObject(60, H / 2, BAT_WIDTH, BAT_HEIGHT);
        bats[1] = new GameObject(W - 60, H / 2, BAT_WIDTH, BAT_HEIGHT);
        activeModel = new Thread(new S_ActiveModel(this));
    }

    /**
     * Start the thread that moves the ball and detects collisions
     */
    public void makeActiveObject() {
        activeModel.start();
    }

    /**
     * Return the Game object representing the ball
     *
     * @return the ball
     */
    public GameObject getBall() {
        return ball;
    }

    /**
     * Set a new Ball object
     *
     * @param aBall - Ball to be set
     */
    public void setBall(GameObject aBall) {
        ball = aBall;
    }

    /**
     * Return the Game object representing the Bat for player
     *
     * @param player 0 or 1
     */
    public GameObject getBat(int player) {
        return bats[player];
    }

    /**
     * Return the Game object representing the Bats
     *
     * @return Array of two bats
     */
    public GameObject[] getBats() {
        return bats;
    }

    /**
     * Set the Bat for a player
     *
     * @param player 0 or 1
     * @param theBat Players Bat
     */
    public void setBat(int player, GameObject theBat) {
        bats[player] = theBat;
    }

    /**
     * Cause update of view of game
     */
    public void modelChanged() {
        //DEBUG.trace("S_PongModel.modelChanged");
        setChanged();
        notifyObservers();
    }

    public long getRequestTime(int playerNumber) {
        if(playerNumber < this.requestTime.length) {
            return this.requestTime[playerNumber];
        } else {
            return 0;
        }
    }

    public void setRequestTime(int playerNumber, long requestTime) {
        if(playerNumber < this.requestTime.length) {
            this.requestTime[playerNumber] = requestTime;
        }
    }

    public long getAveragePingTime(int playerNumber) {
        if(playerNumber < this.averagePingTime.length) {
            return this.averagePingTime[playerNumber];
        } else {
            return 0;
        }
    }

    public void setAveragePingTime(int playerNumber, long averagePingTime) {
        if(playerNumber < this.averagePingTime.length) {
            this.averagePingTime[playerNumber] = averagePingTime;
        }
    }

}
