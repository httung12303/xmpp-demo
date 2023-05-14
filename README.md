# **XMPP Demo**

## **Direction**

We will build a simplified version of the XMPP protocol including only what we need for our IOT application. Why? Because XMPP was created for _**Instant messaging**_ while our application involves only with the communication between a servers and multiple clients, many of its functionalities are redundant. We will discuss this further in later sections of this article.

Back to the direction, these 3 main steps will be taken to recreate XMPP:

1. Implement TCP connection between server and clients.
2. Structure the protocol's message.
3. Implement the interactions between server and clients.

Before diving into the implementation, let's first discover what is the XMPP protocol.

## **XMPP Protocol**

As in the protocol's official RFC:

> "**Extensible Messaging and Presence Protocol(XMPP)** is a protocol for streaming Extensible Markup Language (XML) elements in order to exchange structured data in close to real time between any two network endpoints."

In simple words, XMPP can be considered as a protocol for transporting small pieces of data between two places. These pieces of data are in a structured format ( XML ) - Stanza.

## **Stanza**

The structure of XMPP mainly consists of streams. Structured data is sent between the network endpoints using these streams. These streams are established both ways( between the server and the client.). So for a single connection between a server and a client, two streams are established. The structured data that we will be sending are _Stanzas_. There are 3 types of Stanzas:

- Presence
- Message
- IQ

### **Presence Stanza**

The presence stanza controls and reports the availability of an entity(client, server). A simple example of what a presence stanza looks like:

```XML
<presence>
  <show>away</show>
  <status>Coffee Break</status>
</presence>
```

The above presence stanza tells us that, the user is “away” with a message “ Coffee Break”

### **Message Stanza**

As the name implies,this stanza is used to send messages from one entity to another.

Example:

```XML
<message from=’abc@example.com’ to=’xyz@example.com’ type=’chat’>
  <body>We have had a most delightful evening, a most excellent ball</body>
</message>
```

The above Message stanza tells us that, the user _'abc@example.com'_ is sending a message to antoher user, _’xyz@example.com’_ . Here, _’xyz@example.com’_ and _‘abc@example.com‘_ are referred to as the JIDs (Jabber Identifiers). These are the addresses. Each user, registered with a Jabber (XMPP) server will be provided with a unique JID, similar to our email IDs.

### **IQ Stanza**

The IQ stanza stands for Info/Query. It is mostly used for the purpose of querying the server for certain information. For example, the list of users of a particular chat group. It provides a request and response mechanism for XMPP communication.

Example:

```XML
<iq from=’exmaple@gmail.com’ type=’get’ id=’someid’>
  <query xmlns=’jabber:iq:roster’/>
</iq>
```

This IQ stanza here, is asking the server to respond with the example@gmail.com‘s roster.

### **Stanza attributes**

There are many more attributes in Stanzas, but we will cover only the most significant ones.

#### **JID**

JID is used to identify entities across different domains. Similar to an email address, a JID consists of a local identifier and its domain: _identifier@domain_.

In our application, we will use the following structure for JID: _port_number@IP_address_ to keep things simple.

#### **to, from**

As you might have guessed, the 2 attributes tell where the stanzas come from and where they are received.

#### **id**

This attribute is the identifier for stanzas. Normally, this attribute is used in a _request response context_, where server receives a query with a specific id and response with the same id.

In an IQ Stanza, this attribute is **mandatory**.

#### **type**

The type attribute has different values, specific to the kind of stanza, Presence , Message , or IQ stanza. It specifies the purpose of the stanza. For example, in a request response context, the values of this attribute will be _'get'_ and _'result'_ respectively.

This attribute is also **mandatory** in an IQ stanza.

## **Interaction between entities**

Since XMPP is used for Instant messaging, the interaction is generally dependent on the usage. There is no standard where the client connects to server and says _'HELO server '_ and server response with _'200 Hello client'_. The programmer decides this interaction.

## **Application overview**

Our application consists of 2 components: **server** and **client**. Each client will simulate an IoT device that controls the environment of a room, including its temperature, humidity and brightness based on the time of that room.

Periodically, these clients send their status to server. Server receives these information and stores them in a MySQL database.

Also, clients sometimes send queries about the recommended status of the rooms. Upon receiving the query, server immediately response with the recommendation, which we will store in our database. Finally, server checks the last update time stamp of clients in the database and remove them if they have not updated after a certain amount of time.

## **Our XMPP protocol**

Now that you have a decent understanding of the protocol and what the application looks like, let's discuss the version of XMPP that I mentioned at the beginning of this article.

Again, it's an Instant Messaging protocol, so the interaction between server and clients is not concerned here. All that's left for us to discuss is therefore the Stanzas.

Take a look back into the [**Application overview**](#application-overview) section. The only information we are exchanging is the **environment status** of the rooms, the **queries** that clients send and the **responses** from server - which are just more **environment status**. What does that mean? We are ONLY doing info/query messaging. Therefore, the only suitable type of Stanza will of course be **IQ** consisting of 2 types: info (type='result') and query (type='get').

In general, we only need some basic properties in our IQs:

- from
- to
- type

We excluded the **id** property since we will be responding from server right after receiving a query from clients in separate connections, which makes matching the query's and response's id redundant.

That's really all we have to say about the protocol. Now, let's dive into the details of our application.

## **Application details**

Our application can be split into 4 main components:

- Server
- Clients
- Database
- User Interface

Before going into Server and Client, let's look at some of the common sub-components that the 2 share:

## **Threads**

Our application will need a Server and multiple clients exchanging data, which means streams of data! If we were to handle them synchronously, the programming will be much easier and less prone to errors. However, it would be extremely slow. On the other hand, asynchronicity - the option we chose - will provide the **"close to real-time"** speed, which serves the purpose of XMPP - **Instant Messaging**.

While there are many types of thread in this project that provide different functionalities such as database interactions, the 2 most important ones, in my opinion are the I/O threads. Client and Server both have the own extension of these threads, but the general idea is as follow:

### Receive Thread

As the name implies, this thread is made to receive data. A receive thread is attached to a host only while a host can have as many receive threads as it may need. For instance, a Client only need one to receive data from it's Server and a Server should have one for each Client it's communicating with.

But how does it work? It simply **waits** for an incoming message from the other host and process it:

```java
public abstract class ReceiveThread extends Thread {

  private final SocketWrapper socketWrapper;

  public Stanza receiveStanza();

  abstract public void processStanza(Stanza stanza);
  @Override
  public void run() {
    while(true) {
      try {
        // Some more logic.
        Stanza stanza = receiveStanza();
        processStanza(stanza);
      } catch(Exception e) {
        // Handle exceptions.
      }
    }
  }
}
```

### Send Thread

Threads of this type is initiated whenever Server or Clients want to send Stanzas. The hosts attach the Stanza they want to send and the **connection socket** on which they are communicating onto a Send Thread and start it. In other words, Send Thread is similar to a postal service, you provide the "from" and "to" (the connection) on your letter (the Stanza) and let the service provider (our Send Thread) take care of the rest. Here's how it looks:

```java
public abstract class SendThread extends Thread {

  private final SocketWrapper socketWrapper;

  private final Stanza stanza;

  public void sendStanza();

  @Override
  public void run() {
    try {
      // Some more logic.
      sendStanza();
    } catch (Exception e) {
      // Handle exceptions.
    }
  }
}
```

## Socket Wrapper

You might wonder what the SocketWrapper class above is. Well, it's simply wraps a Socket along with DataOutputStream and DataInputStream that encapsulate the Socket's I/O streams. Why? In order to use I/O stream methods with a Socket's streams, we need to wrap it inside I/O streams object provided by the java.io package. In other words, we need to do `new DataInputStream(socket.getInputStream())` or `new DataOutputStream(socket.getOutputStream())` for each of the streams we use. If we simply use the Socket class, every time the hosts exchange data, they need to create new I/O stream objects. This is not only repetitive but also consume a lot of resources.

By creating 2 objects and save them for each Socket's I/O streams, we not only avoid that problem, but also allow us to perform `synchronous(obj)` blocks with these streams. The block basically tells threads that use some common resources to work synchronously. As I mentioned above, a client send 2 types of message, in 2 different send threads but on the same connection. Without the synchronous block, we may encounter problems when the 2 threads write to the same stream at the same time.

## Stanza

It should be easier to understand to look at the code first:
```java
  abstract public class Stanza {
    // Stanza type identifiers
    public static final int QUERY_IQ = 0;
    public static final int RESULT_IQ = 1;

    // The actual XML
    private Document document;

    public int getType();

    public static byte[] getDocumentBytes(Stanza stanza);

    public static Stanza getStanzaFromDocumentBytes(byte[] documentBytes);

    public void addTimeSent();

    // Getters
  }
```
The methods' name should be self-explanatory:
- We use **type** constants and `getType` method to classify different types of Stanza.
- The document object stores our data in XML format:
  - `getDocumentBytes` converts this object into a byte array, which is the actual data in the transmission.
  - `getStanzaFromDocumentBytes` takes a byte array, converts it into a Document object and creates a Stanza from that Document object. The sub-class of the Stanza is determined by the attributes of the Document object (i.e `root` element type, the `type` attribute...)
  - `addTimeSent` is responsible for adding a time stamp `sent_at` in milliseconds indicating the time at which the Stanza was sent. This method is invoked right before `sendThread`s start writing into input streams, more specifically, right before the `DataInputStream.write()` invocation.

And that's the gist of Stanza class. We can now start discussing the most important components: Server and Client.

## **Server**

```java
public class XMPPServer {
  private final ServerSocket serverSocket;
  private final DBManager db;

  public void sendStanza(SocketWrapper wrapper, Stanza stanza);

  public void startReceiveThread(SocketWrapper socketWrapper);

  
}
```