# Practical 4 - Server Socket Options

This Java project demonstrates how to configure and verify five TCP socket options:

- `SO_KEEPALIVE`
- `SO_LINGER`
- `SO_SNDBUF`
- `SO_RCVBUF`
- `TCP_NODELAY`

The application starts a TCP server on the local machine and automatically launches a client in a separate thread. The server configures the options on the accepted connection, prints their effective values, receives a message, and returns an acknowledgement.

## Project contents

```text
23115076_Project_4/
├── SocketOptionsPractical.java  # Main Java source file
├── README.md                    # Project overview and instructions
├── run.sh                       # Compile-and-run helper for macOS/Linux
└── .gitignore                   # Excludes compiled build files
```

## Requirements

- Java Development Kit (JDK) 17 or later
- A terminal (Terminal on macOS/Linux, Command Prompt or PowerShell on Windows)

Check Java is available:

```bash
java -version
javac -version
```

## Run the program

### macOS/Linux - helper script

From the project folder, run:

```bash
chmod +x run.sh
./run.sh
```

### macOS/Linux/Windows - manual commands

From the project folder, run:

```bash
javac SocketOptionsPractical.java
java SocketOptionsPractical
```

On Windows PowerShell or Command Prompt, the same two commands work after changing into the project directory.

## What the program does

1. Creates a `ServerSocket` bound to `127.0.0.1` using an available port.
2. Sets and reports the listening socket receive-buffer request.
3. Starts a local client and accepts its connection at the server.
4. Configures the following options on the accepted, server-side `Socket`:
   - `setKeepAlive(true)` enables TCP keep-alive.
   - `setSoLinger(true, 2)` permits up to two seconds for pending data while closing.
   - `setSendBufferSize(32768)` requests a 32 KiB send buffer.
   - `setReceiveBufferSize(32768)` requests a 32 KiB receive buffer.
   - `setTcpNoDelay(true)` disables Nagle's algorithm for lower-latency small writes.
5. Prints each effective option value using its corresponding getter.
6. Sends `Hello from client`, receives an `ACK` response, and reports that the transfer test passed.

## Expected output

The selected port may differ on every run. Buffer sizes can also be adjusted by the operating system.

```text
Server listening on 127.0.0.1:<port>
ServerSocket SO_RCVBUF requested=32768, effective=32768
Accepted server-side socket options:
  SO_KEEPALIVE = true
  SO_LINGER    = 2 seconds
  SO_SNDBUF    = 32768 bytes (requested 32768)
  SO_RCVBUF    = 32768 bytes (requested 32768)
  TCP_NODELAY  = true
Client verification: ACK: Hello from client
Transfer test: received 'Hello from client' and sent acknowledgement.
All socket-option tests completed successfully.
```

## Notes

- The program uses only the loopback address (`127.0.0.1`), so it does not require another computer or network configuration.
- `SO_KEEPALIVE` enables the feature; its probe interval is controlled by the operating system, so this short program verifies the setting rather than waiting for probes.
- Buffer requests are advisory. Use the printed getter values as the actual values used by the platform.
- Delete compiled `SocketOptionsPractical.class` files if you want a clean folder; they are generated automatically by `javac`.

## Practical result

All required socket options are configured and tested successfully through a localhost client-server message exchange.
