package server.View;

import common.NetObjectWriter;

import java.util.TimerTask;

/**
 * A task to send the response to a given output after a
 */
public class PongResponseTask extends TimerTask {
    private final Object[] data;
    private final NetObjectWriter output;

    public PongResponseTask(final Object[] dataToSend, final NetObjectWriter out) {
        this.data = dataToSend;
        this.output = out;
    }

    @Override
    public void run() {
        output.put(data);
    }
}
