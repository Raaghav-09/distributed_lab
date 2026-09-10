import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * Practical 4 - configure and test SO_KEEPALIVE, SO_LINGER, SO_SNDBUF,
 * SO_RCVBUF and TCP_NODELAY. Run: javac SocketOptionsPractical.java && java SocketOptionsPractical
 */
public class SocketOptionsPractical {
    private static final int REQUESTED_BUFFER = 32 * 1024;

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            server.setReceiveBufferSize(REQUESTED_BUFFER);
            int port = server.getLocalPort();
            System.out.println("Server listening on 127.0.0.1:" + port);
            System.out.println("ServerSocket SO_RCVBUF requested=" + REQUESTED_BUFFER
                    + ", effective=" + server.getReceiveBufferSize());

            Future<Void> client = executor.submit(() -> runClient(port));
            try (Socket peer = server.accept()) {
                configureAndReport(peer);
                BufferedReader in = new BufferedReader(new InputStreamReader(peer.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(peer.getOutputStream(), StandardCharsets.UTF_8), true);
                String request = in.readLine();
                out.println("ACK: " + request);
                System.out.println("Transfer test: received '" + request + "' and sent acknowledgement.");
            }
            client.get(5, TimeUnit.SECONDS);
            System.out.println("All socket-option tests completed successfully.");
        } finally {
            executor.shutdownNow();
        }
    }

    private static void configureAndReport(Socket socket) throws SocketException {
        socket.setKeepAlive(true);                 // SO_KEEPALIVE
        socket.setSoLinger(true, 2);               // SO_LINGER: wait up to 2 seconds on close
        socket.setSendBufferSize(REQUESTED_BUFFER);// SO_SNDBUF
        socket.setReceiveBufferSize(REQUESTED_BUFFER); // SO_RCVBUF
        socket.setTcpNoDelay(true);                // TCP_NODELAY: disable Nagle's algorithm

        System.out.println("Accepted server-side socket options:");
        System.out.println("  SO_KEEPALIVE = " + socket.getKeepAlive());
        System.out.println("  SO_LINGER    = " + socket.getSoLinger() + " seconds");
        System.out.println("  SO_SNDBUF    = " + socket.getSendBufferSize() + " bytes (requested " + REQUESTED_BUFFER + ")");
        System.out.println("  SO_RCVBUF    = " + socket.getReceiveBufferSize() + " bytes (requested " + REQUESTED_BUFFER + ")");
        System.out.println("  TCP_NODELAY  = " + socket.getTcpNoDelay());
    }

    private static Void runClient(int port) throws IOException {
        try (Socket client = new Socket(InetAddress.getLoopbackAddress(), port);
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {
            out.println("Hello from client");
            String reply = in.readLine();
            if (!"ACK: Hello from client".equals(reply)) throw new IOException("Unexpected response: " + reply);
            System.out.println("Client verification: " + reply);
        }
        return null;
    }
}
