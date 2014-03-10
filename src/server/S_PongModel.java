package server;

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
    private long currentRequestTime[] = new long[] { 0L, 0L };
    private long totalRequestTimes[] = new long[] { 0L, 0L };
    private int totalRequestAmount[] = new int[] { 0, 0 };

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
        if(playerNumber < this.currentRequestTime.length) {
            return this.currentRequestTime[playerNumber];
        } else {
            return 0;
        }
    }

    public void setRequestTime(int playerNumber, long requestTime) {
        if(playerNumber < this.currentRequestTime.length) {
            this.currentRequestTime[playerNumber] = requestTime;
        }
    }

    public void addToTotalRequestTime(int playerNumber, long requestTime) {
        if(playerNumber < this.totalRequestTimes.length) {
            this.totalRequestTimes[playerNumber] += requestTime;
            this.totalRequestAmount[playerNumber]++;
        }
    }

    public long getAverageRequestTime(int playerNumber) {
        int totalRequestAmount = this.totalRequestAmount[playerNumber];
        long totalRequestTime = this.totalRequestTimes[playerNumber];

        if(totalRequestAmount > 0) {
            return totalRequestTime / totalRequestAmount;
        } else {
            return 0;
        }
    }
}
