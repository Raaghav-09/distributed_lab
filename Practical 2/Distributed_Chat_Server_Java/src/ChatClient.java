import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {

    private static final int DEFAULT_PORT = 5050;
    private static final int CONNECTION_TIMEOUT_MS = 5000;

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private volatile boolean connected;
    private final AtomicBoolean disconnectReported = new AtomicBoolean();

    public ChatClient(String serverAddress, int port) {
        createGUI();
        requestUsernameAndConnect(serverAddress, port);
    }

    private void requestUsernameAndConnect(String serverAddress, int port) {
        String username = JOptionPane.showInputDialog(frame, "Enter your name:");

        if (username == null) {
            frame.dispose();
            return;
        }

        if (username.trim().isEmpty()) {
            username = "Anonymous";
        }

        final String selectedUsername = username.trim();
        Thread connectionThread = new Thread(
                () -> connectToServer(serverAddress, port, selectedUsername),
                "chat-server-connector"
        );
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    private void connectToServer(String serverAddress, int port, String username) {
        try {
            Socket connectedSocket = new Socket();
            connectedSocket.connect(
                    new InetSocketAddress(serverAddress, port),
                    CONNECTION_TIMEOUT_MS
            );
            socket = connectedSocket;

            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );

            writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true
            );

            // Send username to server
            writer.println(username);
            if (writer.checkError()) {
                throw new IOException("Could not send username to server.");
            }

            connected = true;
            SwingUtilities.invokeLater(() -> {
                appendMessage("Connected to " + serverAddress + ":" + port + ".");
                messageField.setEnabled(true);
                sendButton.setEnabled(true);
                messageField.requestFocusInWindow();
            });

            // Separate thread continuously receives messages
            Thread receiverThread = new Thread(() -> {
                try {
                    String message;

                    while ((message = reader.readLine()) != null) {
                        final String receivedMessage = message;

                        SwingUtilities.invokeLater(() ->
                            appendMessage(receivedMessage)
                        );
                    }

                } catch (IOException e) {
                    // The cleanup below reports the disconnected state.
                } finally {
                    markDisconnected("Disconnected from server.");
                }
            });

            receiverThread.setDaemon(true);
            receiverThread.start();

        } catch (IOException e) {
            markDisconnected("Unable to connect to " + serverAddress + ":" + port + ".");
        }
    }

    private void createGUI() {
        frame = new JFrame("TCP Chat Application");

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        messageField = new JTextField();
        sendButton = new JButton("Send");
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout(5, 5));
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();

        if (!message.isEmpty() && connected && writer != null) {
            writer.println(message);
            if (writer.checkError()) {
                markDisconnected("Disconnected from server.");
            } else {
                messageField.setText("");
                messageField.requestFocus();
            }
        }
    }

    private void appendMessage(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void markDisconnected(String status) {
        if (!disconnectReported.compareAndSet(false, true)) {
            return;
        }

        connected = false;
        SwingUtilities.invokeLater(() -> {
            appendMessage(status);
            messageField.setEnabled(false);
            sendButton.setEnabled(false);
        });

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public static void main(String[] args) {
        String serverAddress = args.length > 0 ? args[0] : "localhost";
        int port = DEFAULT_PORT;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Port must be a number between 1 and 65535.");
                return;
            }
        }

        final int selectedPort = port;
        SwingUtilities.invokeLater(() -> new ChatClient(serverAddress, selectedPort));
    }
}
