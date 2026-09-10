import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Connects to the day-time server and displays its response. */
public class Client_DT {
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 5005;
    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;

    public static void main(String[] args) {
        String serverHost = args.length > 0 ? args[0] : DEFAULT_SERVER_HOST;
        int serverPort;
        try {
            serverPort = args.length > 1 ? parsePort(args[1]) : DEFAULT_SERVER_PORT;
        } catch (IllegalArgumentException e) {
            System.err.println("Client configuration error: " + e.getMessage());
            return;
        }

        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(serverHost, serverPort), CONNECT_TIMEOUT_MILLIS);

            try (
                PrintWriter toServer = new PrintWriter(
                    new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true
                );
                BufferedReader fromServer = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                )
            ) {
                System.out.printf("Connected to server %s:%d%n", serverHost, serverPort);

                // This is the address actually used for this server connection.
                String clientIp = socket.getLocalAddress().getHostAddress();
                toServer.println(clientIp);
                System.out.println("Client IP sent to server: " + clientIp);

                String response = fromServer.readLine();
                if (response == null) {
                    throw new IOException("Server closed the connection without sending a response.");
                }
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            System.err.println("Make sure Server_DT is running and the host/port are correct.");
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
