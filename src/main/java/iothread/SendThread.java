package iothread;

import socketwrapper.SocketWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import stanza.Stanza;

import javax.xml.transform.TransformerException;

public abstract class SendThread extends Thread {
  private final SocketWrapper socketWrapper;
  private final Stanza stanza;

  public SendThread(SocketWrapper wrapper, Stanza stanza) throws IOException {
    this.socketWrapper = wrapper;
    this.stanza = stanza;
  }
  public void sendStanza() throws IOException, TransformerException {
    final DataOutputStream output = socketWrapper.getOutputStream();
    stanza.addTimeSent();
    synchronized (output) {
      byte[] b = Stanza.getDocumentBytes(stanza);
      output.writeInt(b.length);
      output.write(b);
      output.flush();
    }
  }

  @Override
  public void run() {
    try {
      if (!socketWrapper.connected()) {
        return;
      }
      sendStanza();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      socketWrapper.close();
    }
  }
}
