# Distributed Chat Server using TCP Sockets in Java

## Aim
Implement a distributed chat server using TCP sockets in Java.

## Description
This project is a multi-client chat application built using Java TCP sockets,
multithreading, and Swing.

- The server listens for incoming TCP client connections on port 5050 by default.
- Each connected client is handled using a separate thread.
- Messages sent by one client are broadcast to all connected clients.
- The client uses Java Swing for the graphical user interface.
- Receiving messages happens continuously in a background thread.
- Sending messages happens when the user presses the Send button or Enter.

## Project Structure

Distributed_Chat_Server_Java/
├── src/
│   ├── ChatServer.java
│   └── ChatClient.java
├── README.md
└── RUN_INSTRUCTIONS.txt

## Requirements
- Java JDK 8 or newer
- No external libraries are required.

## How to Run

### 1. Compile
Open a terminal inside the `src` folder and run:

    javac ChatServer.java ChatClient.java

### 2. Start the server

    java ChatServer

The server starts on port 5050. Port 5000 is avoided because macOS commonly
uses it for AirPlay Receiver.

To use another port, supply it as an argument:

    java ChatServer 5051

### 3. Start clients
Open another terminal inside the same `src` folder and run:

    java ChatClient

Run the client multiple times to simulate multiple users.

### 4. Chat
Each client enters a username. Any message sent by a client is sent to the
server and broadcast to every connected client.

To connect to a server on another host or port, pass the host and port:

    java ChatClient 192.168.1.10 5051

The client and server must always use the same port.

### If the port is already in use

Choose another port for both programs:

    java ChatServer 5051
    java ChatClient localhost 5051

## Concepts Used
- TCP sockets
- ServerSocket
- Socket
- Multithreading
- BufferedReader
- PrintWriter
- Java Swing
- Event handling
- Client-server architecture
- Concurrent collections

## Result
A distributed multi-client chat application was successfully implemented
using TCP sockets in Java. Multiple users can connect to a central server
and exchange messages in real time.
