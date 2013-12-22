package server;
import java.net.*;
import static common.Global.*;
import common.*;

/**
 * Start the game server
 *  The call to makeActiveObject() in the model 
 *   starts the play of the game
 */
class Server
{
  private NetObjectWriter p0, p1;
  
  public static void main( String args[] )
  {
   ( new Server() ).start();
  }

  /**
   * Start the server
   */
  public void start()
  {
    DEBUG.set( true );
    DEBUG.trace("Pong Server");
    DEBUG.set( false );               // Otherwise lots of debug info
    S_PongModel model = new S_PongModel();
    
    makeContactWithClients( model );
    
    S_PongView  view  = new S_PongView(p0, p1 );
                        new S_PongController( model, view );

    model.addObserver( view );       // Add observer to the model
    model.makeActiveObject();        // Start play
  }
  
  /**
   * Make contact with the clients who wish to play
   * Players will need to know about the model
   * @param model  Of the game
   */
  public void makeContactWithClients( S_PongModel model )
  {
  }
}

/**
 * Individual player run as a separate thread to allow
 * updates to the model when a player moves there bat
 */
class Player extends Thread
{
  /**
   * Constructor
   * @param player Player 0 or 1
   * @param model Model of the game
   * @param s Socket used to communicate the players bat move
   */
  public Player( int player, S_PongModel model, Socket s  )
  {
  }
  
  
  /**
   * Get and update the model with the latest bat movement
   */
  public void run()                             // Execution
  {
  }
}
