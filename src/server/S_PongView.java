package server;

import common.*;

import java.util.Observable;
import java.util.Observer;


/**
 * Displays a graphical view of the game of pong
 */
class S_PongView implements Observer
{ 
  private S_PongController pongController;
  private GameObject   ball;
  private GameObject[] bats;
  private NetObjectWriter left, right;

  public S_PongView( NetObjectWriter c1, NetObjectWriter c2 )
  {
    left = c1; right = c2;
  }

  /**
   * Called from the model when its state is changed
   * @param aPongModel Model of game
   * @param arg Arguments - not used
   */
  public void update( Observable aPongModel, Object arg )
  {
    S_PongModel model = (S_PongModel) aPongModel;
    ball  = model.getBall();
    bats  = model.getBats();
    // Now need to send position of game objects to the client
    //  as the model on the server has changed
  }

  
}
