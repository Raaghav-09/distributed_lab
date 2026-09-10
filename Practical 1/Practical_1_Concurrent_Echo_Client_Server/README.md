# Practical 1 - Concurrent Echo Client-Server Application in Java

## Aim
Implement a concurrent echo client-server application in Java using TCP sockets.

## Files
- `src/EchoServer.java` - Concurrent TCP echo server. Each client is handled in a separate thread.
- `src/EchoClient.java` - TCP client that sends user messages and prints the echoed reply.
- `Practical_1_Report.pdf` - Ready-to-submit practical report.
- `sample_output.txt` - Example execution output.

## Requirements
- Java Development Kit (JDK 8 or later)
- Terminal / Command Prompt

## Compile
From the project root, run:

```bash
javac -d out src/EchoServer.java src/EchoClient.java
```

## Run
### Terminal 1 - Start server
```bash
java -cp out EchoServer
```

### Terminal 2 - Start first client
```bash
java -cp out EchoClient
```

### Terminal 3 or more - Start additional clients
```bash
java -cp out EchoClient
```

Each connected client is handled by a different server thread, so multiple clients can communicate with the server concurrently.

Type `exit` in a client to close that client connection.

See [RUN_INSTRUCTIONS.md](RUN_INSTRUCTIONS.md) for configurable host/port usage and troubleshooting.

## Expected Behaviour
If a client sends:

```text
Hello Server
```

the server sends the same message back:

```text
Server echoed: Hello Server
```

## Result
The concurrent echo client-server application is implemented successfully using Java TCP sockets and multithreading.
