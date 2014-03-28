package server;

import common.DEBUG;
import common.GameObject;

import java.util.Observable;

import static common.Global.*;

/**
 * Model of the game of pong
 * The active object ActiveModel does the work of moving the ball
 *
 * This thread also contains the currentRequestTimes, totalRequestTimes
 * and totalRequestAmounts for each players along with get and set methods
 * for them.
 *
 */
public class S_PongModel extends Observable {
    private GameObject ball = new GameObject(W / 2, H / 2, BALL_SIZE, BALL_SIZE);
    private GameObject bats[] = new GameObject[2];
    private long currentRequestTime[] = new long[] { 0L, 0L };
    private long totalRequestTimes[] = new long[] { 0L, 0L };
    private int totalRequestAmount[] = new int[] { 0, 0 };

    private Thread activeModel;

    private boolean shutdown;
    private int gameNumber;

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
        DEBUG.trace("S_PongModel.modelChanged");
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the requestTime for the given playerNumber.
     *
     * @param playerNumber the playerNumber to get the requestTime for.
     * @return the current requestTime or Zero if out of bounds.
     */
    public long getRequestTime(int playerNumber) {
        if(playerNumber < this.currentRequestTime.length) {
            return this.currentRequestTime[playerNumber];
        } else {
            return 0;
        }
    }

    /**
     * Sets the requestTime for the given playerNumber. If it's not a valid
     * player number then does nothing.
     *
     * @param playerNumber the playerNumber to set the requestTime for.
     * @param requestTime the new requestTime to set for the player.
     */
    public void setRequestTime(int playerNumber, long requestTime) {
        if(playerNumber < this.currentRequestTime.length) {
            this.currentRequestTime[playerNumber] = requestTime;
        }
    }

    /**
     * Adds a new requestTime to the totalRequestTime for the given player.
     * If the player is not valid then does nothing.
     *
     * This also increments the totalAmount of requests for the player.
     *
     * @param playerNumber the playerNumber to add the requestTime for.
     * @param requestTime the new requestTime to add for the player.
     */
    public void addToTotalRequestTime(int playerNumber, long requestTime) {
        if(playerNumber < this.totalRequestTimes.length) {
            this.totalRequestTimes[playerNumber] += requestTime;
            this.totalRequestAmount[playerNumber]++;
        }
    }

    /**
     * Gets the averageRequestTime for the given player.
     *
     * @param playerNumber the playerNumber to get the averageRequestTime for.
     * @return the averageRequestTime for the player or Zero if no requests or not a valid player.
     */
    public long getAverageRequestTime(int playerNumber) {

        if(playerNumber < this.totalRequestTimes.length &&
           playerNumber < this.totalRequestAmount.length)
        {
            int totalRequestAmount = this.totalRequestAmount[playerNumber];
            long totalRequestTime = this.totalRequestTimes[playerNumber];

            if(totalRequestAmount > 0) {
                return totalRequestTime / totalRequestAmount;
            }
        }
        return 0;
    }

    /**
     * Returns the state of the game.
     * @return if the game is shutdown.
     */
    public boolean getShutdown() {
        return this.shutdown;
    }

    /**
     * Sets the state of the game.
     * @param shutdown the value to set the state of the game to.
     */
    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    /**
     * Sets the gameNumber for the game.
     * @param gameNumber
     */
    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    /**
     * Returns the game number for the game.
     * @return the gameNumber for the game.
     */
    public int getGameNumber() {
        return gameNumber;
    }
}
