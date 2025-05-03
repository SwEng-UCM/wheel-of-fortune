package network;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.net.Socket;

public class MessageReceiver {
    private ObjectInputStream in;

    public MessageReceiver(Socket socket) throws Exception {
        InputStream is = socket.getInputStream();
        this.in = new ObjectInputStream(is);
    }

    public synchronized Object receive() throws Exception {
        return in.readObject();
    }
}
