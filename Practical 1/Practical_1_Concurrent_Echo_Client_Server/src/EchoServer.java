import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A concurrent, line-based TCP echo server. */
public class EchoServer {
    private static final int DEFAULT_PORT = 1234;
    private static final int MAX_CONCURRENT_CLIENTS = 50;

    public static void main(String[] args) {
        int port = parsePort(args);
        if (port == -1) {
            return;
        }

        System.out.println("Starting Concurrent Echo Server...");

        ExecutorService clients = Executors.newFixedThreadPool(MAX_CONCURRENT_CLIENTS);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            System.out.println("Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                clients.execute(new ClientHandler(clientSocket));
            }
        } catch (BindException e) {
            System.err.println("Cannot start server: port " + port + " is already in use.");
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            clients.shutdown();
        }
    }

    private static int parsePort(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: java EchoServer [port]");
            return -1;
        }
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
            System.err.println("Port must be an integer between 1 and 65535.");
            return -1;
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            Socket socket = clientSocket;
            BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter output = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true
            )
        ) {
            String message;

            while ((message = input.readLine()) != null) {
                System.out.println(
                    "[" + Thread.currentThread().getName() + "] " +
                    socket.getRemoteSocketAddress() + " -> " + message
                );

                // Echo the received message back to the same client.
                output.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client communication error (" +
                clientSocket.getRemoteSocketAddress() + "): " + e.getMessage());
        } finally {
            System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
        }
    }
}
