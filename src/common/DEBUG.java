package common;
/**
 * Print information about the running program
 * @author Mike Smith University of Brighton
 * @version 1.0
 */

public class DEBUG
{
  private static boolean debug = false;

  /**
   * Set true/false to print debugging information
   * @param state Debugging true false
   */
  public static synchronized boolean set( boolean state )
  {
    boolean oldState = debug;
    debug = state;
    return oldState;
  }

  /**
   * Display text for debugging purposes
   * @param fmt  The same as printf etc
   */
  public static void trace(String fmt, Object... params )
  {
    if ( debug )
    {
      synchronized( DEBUG.class )
      {
        System.out.printf( fmt, params );
        System.out.println();
      }
    }
  }

  /**
   * Display a fatal message if the assertion fails
   *  @param fmt The same as printf etc
   */
  public static void assertTrue( boolean ok, String fmt, Object... params )
  {
    if ( ! ok )
    {
      error( "Assert - " + fmt, params );
    }
  }

  /**
   * Display a fatal message
   *  @param fmt The same as printf etc
   */
  public static synchronized void error(String fmt, Object... params )
  {
    System.out.printf( "ERROR: " + fmt, params );
    System.out.println();
    System.exit(-1);
  }

}

