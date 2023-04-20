package socketwrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketWrapper {
    private final Socket connSocket;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;
    public SocketWrapper(Socket connSocket) throws IOException {
        this.connSocket = connSocket;
        this.outputStream = new DataOutputStream(connSocket.getOutputStream());
        this.inputStream = new DataInputStream(connSocket.getInputStream());
    }
    public Socket getConnSocket() {
        return connSocket;
    }
    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }
    public boolean connected() {
        return connSocket.isConnected() && !connSocket.isClosed();
    }
    public void close() {
        try {
            if(connSocket != null) {
                connSocket.close();
            }
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {

        }
    }
}
