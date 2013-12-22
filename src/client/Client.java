package client;

import common.*;
import static common.Global.*;

import java.net.Socket;

/**
 * Start the client that will display the game for a player
 */
class Client
{
  public static void main( String args[] )
  {
    ( new Client() ).start();
  }

  /**
   * Start the Client
   */
  public void start()
  {
    DEBUG.set( true );
    DEBUG.trace( "Pong Client" );
    DEBUG.set( false );
    C_PongModel       model = new C_PongModel();
    C_PongView        view  = new C_PongView();
    C_PongController  cont  = new C_PongController( model, view );
                        
    makeContactWithServer( model, cont );

    model.addObserver( view );       // Add observer to the model
    view.setVisible(true);           // Display Screen
  }

  /**
   * Make contact with the Server who controls the game
   * Players will need to know about the model
   * 
   * @param model Of the game
   * @param cont Controller (MVC) of the Game
   */
  public void makeContactWithServer( C_PongModel model,
                                     C_PongController  cont )
  {
    // Also starts the Player task that get the current state
    //  of the game from the server
  }
}
