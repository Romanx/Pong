package server.View;

import common.GameObject;
import common.Global;
import server.S_PongModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Observable;

/**
 * Created by Alex on 26/03/2014.
 */
public class S_MulticastPongView extends S_PongView {
    private GameObject ball;
    private GameObject[] bats;
    private DatagramSocket socket;
    private int threadNo;

    public S_MulticastPongView(int threadNo) throws SocketException {
        super();
        socket = new DatagramSocket();
        this.threadNo = threadNo;
    }

    @Override
    public void update(Observable aPongModel, Object arg) {
        S_PongModel model = (S_PongModel) aPongModel;

        this.ball = model.getBall();
        this.bats = model.getBats();
        Object[] result = new Object[] {this.threadNo, ball.getX(), ball.getY(), this.bats[0].getY(), this.bats[1].getY()};

        InetAddress group = null;
        DatagramPacket packet;

        try {
            group = InetAddress.getByName(Global.MCA);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            ByteArrayOutputStream b_out = new ByteArrayOutputStream();
            ObjectOutputStream o_out = new ObjectOutputStream(b_out);

            //Write object to ByteStream.
            o_out.writeObject(result);

            //Write ByteStream to array.
            byte[] b = b_out.toByteArray();
            //Create packet.
            packet = new DatagramPacket(b, b.length, group, Global.MULTIPLEX_PORT);

            //Send Data.
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
