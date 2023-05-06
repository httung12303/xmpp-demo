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

In simple words, XMPP can be considered as a protocol for transporting small pieces of data between two places. These pieces of data are in a structured format ( XML ).

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

## Application overview

Our application consists of 2 components: server and client. The clients will simulate devices which keep track and control the status of rooms, including their temperature, humidity and brightness based on the time of each room. Periodically, these clients send their status to server. Server receives these information and stores them in a MySQL database. Also, clients sometimes send queries about the recommended status of the rooms. Upon receiving the query, server immediately response with the recommendation, which we will store in our database. Finally, server checks the last update time stamp of clients in the database and remove them if they have not updated after a certain amount of time.

