package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Wrapper to allow reading of objects from a socket
 */
public class NetObjectReader extends ObjectInputStream
{
  public NetObjectReader( Socket s ) throws IOException
  {
    super( s.getInputStream() );
  }

  // Get object return null on 'error'
  public synchronized Object get()           // Get object from stream
  {
    try                                      //
    {
      return readObject();                   // Return read object
    }
    catch ( Exception err )                  // Reading error
    {
        err.printStackTrace();
      DEBUG.error("NetObjectReader.get %s", 
                   err.getMessage() );
      return null;                           //  On error return null
    }
  }
}


