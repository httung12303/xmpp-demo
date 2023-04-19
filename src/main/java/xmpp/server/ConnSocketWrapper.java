package xmpp.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnSocketWrapper {
    private final Socket connSocket;
    private final DataOutputStream outputStream;
    public ConnSocketWrapper(Socket connSocket) throws IOException {
        this.connSocket = connSocket;
        this.outputStream = new DataOutputStream(connSocket.getOutputStream());
    }
    public Socket getConnSocket() {
        return connSocket;
    }
    public DataOutputStream getOutputStream() {
        return outputStream;
    }
}
