package server;

import common.DEBUG;
import common.GameObject;

import static common.Global.*;

/**
 * A class used by the server model to give it an active part
 * which continuously moves the ball and decides what to
 * do on a collision
 */
class S_ActiveModel implements Runnable {
    S_PongModel pongModel;

    public S_ActiveModel(S_PongModel aPongModel) {
        pongModel = aPongModel;
    }

    /**
     * Code to position the ball after time interval
     * runs as a separate thread
     */
    public void run() {
        final double S = 1;           // Units to move
        try {
            GameObject ball = pongModel.getBall();
            GameObject bats[] = pongModel.getBats();

            while (true) {
                double x = ball.getX();
                double y = ball.getY();
                // Deal with possible edge of board hit
                if (x >= W - B - BALL_SIZE) ball.changeDirectionX();
                if (x <= 0 + B) ball.changeDirectionX();
                if (y >= H - B - BALL_SIZE) ball.changeDirectionY();
                if (y <= 0 + M) ball.changeDirectionY();

                ball.moveX(S);
                ball.moveY(S);

                // As only a hit on the bat is detected it is assumed to be
                // on the front or back of the bat
                // A hit on the top or bottom has an interesting effect
                if (bats[0].collision(ball) == GameObject.Collision.HIT ||
                        bats[1].collision(ball) == GameObject.Collision.HIT) {
                    ball.changeDirectionX();
                }

                pongModel.modelChanged();      // Model changed refresh screen

                Thread.sleep(20);            // About 50 Hz
            }
        } catch (Exception e) {
            e.printStackTrace();
            DEBUG.trace("S_ActiveModel : %s", e.getMessage());
        }
        ;
    }

}

