# Sample Output

Actual IP addresses, client ports, date, and time vary by machine and run.

## Server terminal

```text
Concurrent Day-Time Server
Starting server on port 5000...
Server started successfully.
Waiting for clients... (Press Ctrl+C to stop)

Client connected
Client IP (socket): 127.0.0.1
Client IP (sent by client): 127.0.0.1
Client port: 54001
Date and time sent to client.
```

## Client terminal

```text
Connected to server localhost:5000
Client IP sent to server: 127.0.0.1
Current Date & Time on Server is: 12-08-2026 15:45:00
```

When client and server run on separate machines, the client address is usually a LAN address such as `192.168.x.x`.
