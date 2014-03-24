package common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Wrapper for reading an object from a socket
 */

public class NetObjectWriter extends ObjectOutputStream {
    Socket s;

    public NetObjectWriter(Socket s) throws IOException {
        super(s.getOutputStream());
        this.s = s;
        s.setTcpNoDelay(true);       // Send data immediately
    }

    // write object to socket returning false on error
    public synchronized boolean put(Object data) {
        try {
            reset();
            writeObject(data);       // Write object
            flush();                   // Flush
            return true;               // Ok
        } catch (IOException err) {
            err.printStackTrace();
            DEBUG.error("NetObjectWriter.get %s",
                    err.getMessage());
            return false;                           // Failed write
        }
    }
}

