# Concurrent Day-Time Client-Server Application

Java TCP socket implementation for Practical 3. The server accepts multiple client connections concurrently, records the client address, and returns the server's current date and time.

## Project structure

```text
.
├── src/
│   ├── Server_DT.java       # Concurrent TCP server
│   └── Client_DT.java       # TCP client
├── docs/
│   ├── Practical_3_Report.md
│   └── SAMPLE_OUTPUT.md
├── README.md                # Setup and run instructions
└── .gitignore
```

## Requirements

- JDK 8 or later (JDK 11+ recommended)
- Two terminal windows
- TCP port 5000 available on the server machine

Check that Java is installed:

```bash
java -version
javac -version
```

## Compile

From the project root, run:

```bash
javac -d out src/Server_DT.java src/Client_DT.java
```

This creates compiled `.class` files in `out/`. They are intentionally excluded from version control.

## Run locally

1. In the first terminal, start the server:

   ```bash
   java -cp out Server_DT
   ```

   If port 5000 is already in use, select another available port (for example, 5051):

   ```bash
   java -cp out Server_DT 5051
   ```

2. In a second terminal, run a client:

   ```bash
   java -cp out Client_DT
   ```

   If the server uses a different port, provide it to the client too:

   ```bash
   java -cp out Client_DT localhost 5051
   ```

3. To demonstrate concurrency, open more terminals and run the client command again. Each client receives a response independently.

Stop the server with `Ctrl+C`.

## Connect to a server on another machine

Start `Server_DT` on the server machine. On a client machine, use the server IP address and, optionally, a different port argument:

```bash
java -cp out Client_DT 192.168.1.10 5000
```

Allow the selected TCP port (5000 by default) through the server firewall if required. Both programs accept an optional port argument, so no code change is needed to use another available port.

## Notes

- The client sends the local address used by its actual socket connection, which matches the address the server observes for a direct connection.
- The server uses a pool of 10 worker threads, allowing several clients to be handled at the same time while keeping resource use predictable.
- The protocol uses one UTF-8 line in each direction: client IP address, then server date and time.
