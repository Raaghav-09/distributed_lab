import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    // Port 5000 is commonly used by AirPlay Receiver on macOS.
    private static final int DEFAULT_PORT = 5050;

    // Thread-safe set of connected clients
    private static final Set<ClientHandler> clients =
            ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        int port = getPort(args);
        if (port == -1) {
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            System.out.println("Chat Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println(
                        "New client connected: " + socket.getInetAddress()
                );

                // One thread per connected client
                Thread clientThread = new Thread(new ClientHandler(socket), "chat-client-handler");
                clientThread.start();
            }

        } catch (BindException e) {
            System.err.println("Cannot start the chat server: port " + port + " is already in use.");
            System.err.println("Try another port, for example: java ChatServer 5051");
            System.err.println("Then connect with: java ChatClient localhost 5051");
        } catch (IOException e) {
            System.err.println("Chat server stopped because of a network error: " + e.getMessage());
        }
    }

    private static int getPort(String[] args) {
        if (args.length == 0) {
            return DEFAULT_PORT;
        }

        try {
            int port = Integer.parseInt(args[0]);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
            return port;
        } catch (NumberFormatException e) {
            System.err.println("Port must be a number between 1 and 65535.");
            return -1;
        }
    }

    // Sends a message to all connected clients
    private static void broadcast(String message) {
        System.out.println(message);

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );

                writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                        true
                );

                // First line received from client is the username
                username = reader.readLine();

                // A client that disconnects before completing the handshake
                // must not appear as an "Anonymous" join/leave event.
                if (username == null) {
                    return;
                }

                username = username.trim();
                if (username.isEmpty()) {
                    username = "Anonymous";
                }

                // Register only after the connection has a working writer.
                // Otherwise an early broadcast can be lost for this client.
                clients.add(this);
                broadcast(username + " joined the chat.");

                String message;

                // Continuously receive messages from this client
                while ((message = reader.readLine()) != null) {
                    if (!message.trim().isEmpty()) {
                        broadcast(username + ": " + message);
                    }
                }

            } catch (IOException e) {
                System.out.println(
                        (username != null ? username : "A client")
                                + " disconnected."
                );
            } finally {
                clients.remove(this);

                if (username != null) {
                    broadcast(username + " left the chat.");
                }

                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        void sendMessage(String message) {
            writer.println(message);

            if (writer.checkError()) {
                clients.remove(this);
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
