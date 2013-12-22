package client;

import common.*;
import static common.Global.*;

import java.net.Socket;
/**
 * Individual player run as a separate thread to allow
 * updates immediately the bat is moved
 */
class Player extends Thread
{

  /**
   * Constructor
   * @param model - model of the game
   * @param s - Socket used to communicate with server
   */
  public Player( C_PongModel model, Socket s  )
  {
    // The player needs to know this to be able to work
  }
  
  
  /**
   * Get and update the model with the latest bat movement
   * sent by the server
   */
  public void run()                             // Execution
  {
    // Listen to network to get the latest state of the
    //  game from the server
    // Update model with this information, Redisplay model
    DEBUG.trace( "Player.run" );
  }
}
