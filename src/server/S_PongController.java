package server;
import common.*;


/**
 * Pong controller
 * Not used currently.
 * But there could be a management console for the server.
 */
public class S_PongController
{
  private S_PongModel model;
  private S_PongView  view;

  public S_PongController( S_PongModel aPongModel, S_PongView aPongView)
  {
    model  = aPongModel;
    view   = aPongView;
  }
  
}

