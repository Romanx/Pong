package common;

/**
 * Major constants used in the game
 */
public class Global
{
  public static final int H = 450;          // Height of window
  public static final int W = 600;          // Width  of window
  
  public static final double B = 6;            // Border offset
  public static final double M = 26;           // Menu offset
  public static final double BALL_SIZE  = 15;  // Ball side
  public static final double BAT_WIDTH  = 10;  // Bat width
  public static final double BAT_HEIGHT = 100; // Bat Height
  
  public static final double BAT_MOVE=5;       // Each move is
  
  // Of course this should not be a constant
  //  but should be user settable
  public static final int    PORT = 50001;       // Port
  public static final int    MULTIPLEX_PORT = 50002;
  public static final String HOST = "localhost"; // M/C Name


  public static final String MULTIPLEX = "multiplex_mode";
}

