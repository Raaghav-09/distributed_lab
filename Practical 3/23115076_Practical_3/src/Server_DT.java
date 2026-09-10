import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A concurrent TCP server that returns its current date and time to each client. */
public class Server_DT {
    private static final int DEFAULT_PORT = 5005;
    private static final int WORKER_COUNT = 10;
    private static final DateTimeFormatter DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? parsePort(args[0]) : DEFAULT_PORT;
        } catch (IllegalArgumentException e) {
            System.err.println("Server configuration error: " + e.getMessage());
            return;
        }

        ExecutorService clientWorkers = Executors.newFixedThreadPool(WORKER_COUNT);

        System.out.println("Concurrent Day-Time Server");
        System.out.println("Starting server on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started successfully.");
            System.out.println("Waiting for clients... (Press Ctrl+C to stop)\n");

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                clientWorkers.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            clientWorkers.shutdown();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            Socket socket = clientSocket;
            BufferedReader fromClient = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter toClient = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true
            )
        ) {
            String clientReportedIp = fromClient.readLine();
            String remoteIp = socket.getInetAddress().getHostAddress();

            System.out.println("Client connected");
            System.out.println("Client IP (socket): " + remoteIp);
            System.out.println("Client IP (sent by client): " + clientReportedIp);
            System.out.println("Client port: " + socket.getPort());

            String currentDateTime = DATE_TIME_FORMAT.format(LocalDateTime.now());
            String response = "Current Date & Time on Server is: " + currentDateTime;
            toClient.println(response);
            System.out.println("Date and time sent to client.\n");
        } catch (IOException e) {
            System.err.println("Error while handling client: " + e.getMessage());
        }
    }

    private static int parsePort(String value) {
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65_535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535.");
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a number.");
        }
    }
}
