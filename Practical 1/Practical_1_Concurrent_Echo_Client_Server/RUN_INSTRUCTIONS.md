# Run Instructions - Concurrent Echo Client-Server

## Prerequisites

- Install JDK 8 or newer.
- Confirm the installation from a terminal:

```bash
java -version
javac -version
```

## Compile

From the project root, run:

```bash
javac -d out src/EchoServer.java src/EchoClient.java
```

This compiles the Java classes into the `out` directory.

## Run the server

In the first terminal, from the project root, run:

```bash
java -cp out EchoServer
```

The server listens on TCP port `1234` by default and stays running until you stop it with `Ctrl+C`.

To use another port, provide it as an argument:

```bash
java -cp out EchoServer 5000
```

## Run one or more clients

Open a separate terminal for every client. With the default local server, run:

```bash
java -cp out EchoClient
```

For a server on another computer or port, provide the host and port:

```bash
java -cp out EchoClient 192.168.1.10 5000
```

Type a message and press Enter. The server returns the same message. Type `exit` to receive the final echo and close that client connection. The server remains available for other clients.

## Quick example

Client terminal:

```text
Enter message: Hello Server
Server echoed: Hello Server
Enter message: exit
Server echoed: exit
```

## Troubleshooting

- **`port ... is already in use`**: stop the program using that port, or start this server on a different port and pass the same port to every client.
- **`Connection refused`**: start the server first, then verify that the client host and port match the server.
- **Remote connection fails**: ensure the server computer's firewall allows incoming TCP traffic on the chosen port and use its reachable IP address rather than `localhost`.
