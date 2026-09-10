# Practical 3 Report

## Aim

Implement a concurrent day-time client-server application using Java TCP sockets.

## Objective

Create a TCP server that accepts multiple client connections concurrently and sends the current server date and time to each connected client. The client also sends its IP address to the server.

## Requirements

- Java Development Kit (JDK) 8 or later
- Two or more terminal windows
- TCP/IP networking support

## Server algorithm

1. Create a `ServerSocket` bound to port 5000 by default (or a port supplied as a command-line argument).
2. Continuously listen for incoming client connections.
3. Accept each client connection.
4. Assign the client to a worker thread from the server's thread pool.
5. Read the IP address sent by the client.
6. Display the client's socket address, reported address, and port on the server console.
7. Obtain the server's current date and time.
8. Send the date and time to the client.
9. Close the client streams and socket.
10. Continue accepting connections until the server is terminated.

## Client algorithm

1. Connect a `Socket` to the server host and port.
2. Read the local IP address used by the connected socket.
3. Send that IP address to the server.
4. Receive the date and time from the server.
5. Display the received response.
6. Close the streams and socket.

## Concurrency

The server dispatches each accepted socket to a worker from a pool of 10 threads. Consequently, multiple client requests can be processed independently without blocking the server's accept loop.

## Result

The concurrent day-time client-server application was implemented successfully. Multiple clients can connect, send their IP addresses, and receive the server's current date and time.
