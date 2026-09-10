import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/** A command-line client for {@link EchoServer}. */
public class EchoClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1234;

    public static void main(String[] args) {
        ConnectionDetails connection = parseArguments(args);
        if (connection == null) {
            return;
        }

        try (
            Socket socket = new Socket(connection.host, connection.port);
            BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            PrintWriter output = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true
            );
            BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8)
            )
        ) {
            System.out.println("Connected to Echo Server at " + connection.host + ":" + connection.port);
            System.out.println("Type 'exit' to close the client.\n");

            while (true) {
                System.out.print("Enter message: ");
                String message = console.readLine();
                if (message == null) {
                    System.out.println("Input closed. Disconnecting.");
                    break;
                }

                output.println(message);

                String response = input.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }

                System.out.println("Server echoed: " + response);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + connection.host);
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static ConnectionDetails parseArguments(String[] args) {
        if (args.length > 2) {
            System.err.println("Usage: java EchoClient [host] [port]");
            return null;
        }

        String host = args.length >= 1 ? args[0] : DEFAULT_HOST;
        int port = DEFAULT_PORT;
        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Port must be an integer between 1 and 65535.");
                return null;
            }
        }
        return new ConnectionDetails(host, port);
    }

    private static class ConnectionDetails {
        private final String host;
        private final int port;

        ConnectionDetails(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
