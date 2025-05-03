package network;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MessageSender {
    private ObjectOutputStream out;

    public MessageSender(Socket socket) throws Exception {
        OutputStream os = socket.getOutputStream();
        this.out = new ObjectOutputStream(os);
    }

    public synchronized void send(Object message) throws Exception {
        out.writeObject(message);
        out.flush();
    }
}
