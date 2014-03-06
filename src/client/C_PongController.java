package client;
import common.*;

import java.awt.event.KeyEvent;
import java.util.Date;

import common.GameObject;
/**
 * Pong controller, handles user interactions
 */
public class C_PongController
{
    private C_PongModel model;
    private C_PongView  view;
    private Player player;

    /**
     * Constructor
     * @param aPongModel Model of game on client
     * @param aPongView  View of game on client
     */
    public C_PongController( C_PongModel aPongModel, C_PongView aPongView)
    {
        model  = aPongModel;
        view   = aPongView;
        view.setPongController( this );  // View talks to controller
    }

    /**
     * Decide what to do for each key pressed
     * @param keyCode The keycode of the key pressed
     */
    public void userKeyInteraction(int keyCode )
    {
        // Key typed includes specials, -ve
        // Char is ASCII value

        double batMove = 0;

        switch ( keyCode )              // Character is
        {
            case -KeyEvent.VK_LEFT:        // Left Arrow
                batMove = -Global.BAT_MOVE;
                break;
            case -KeyEvent.VK_RIGHT:       // Right arrow4
                batMove = +Global.BAT_MOVE;
                break;
            case -KeyEvent.VK_UP:          // Up arrow
                // Send to server
                break;
            case -KeyEvent.VK_DOWN:        // Down arrow
                break;
        }

        if(batMove != 0) {
            NetObjectWriter out = this.player.getPlayerOutput();
            out.put(new Object[] { batMove, System.currentTimeMillis() });
        }


    }

    public void setPlayer(Player p) { this.player = p; }


}

