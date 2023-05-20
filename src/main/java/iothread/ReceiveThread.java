package iothread;

import stanza.Stanza;
import org.xml.sax.SAXException;
import socketwrapper.SocketWrapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class ReceiveThread extends Thread {
    private final SocketWrapper socketWrapper;

    public ReceiveThread(SocketWrapper wrapper) throws IOException {
        this.socketWrapper = wrapper;
    }

    public SocketWrapper getSocketWrapper() {
        return socketWrapper;
    }

    public Stanza receiveStanza() throws IOException, ParserConfigurationException, SAXException {
        final DataInputStream input = socketWrapper.getInputStream();
        int remainingLen = input.readInt();
        int bytes;
        byte[] buffer = new byte[4 * 1024];
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        while (remainingLen > 0
                && (bytes = input.read(buffer, 0, (int) Math.min(remainingLen, buffer.length))) != -1) {
            remainingLen -= bytes;
            byteStream.write(buffer, 0, bytes);
        }
        Stanza stanza = Stanza.getStanzaFromDocumentBytes(byteStream.toByteArray());
        return stanza;
    }

    abstract public void processStanza(Stanza stanza);

    @Override
    public void run() {
        while (true) {
            try {
                if (!socketWrapper.connected()) {
                    break;
                }
                Stanza stanza = receiveStanza();
                processStanza(stanza);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                socketWrapper.close();
            }
        }
    }
}
