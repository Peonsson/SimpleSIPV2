import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Johan Pettersson on 2015-10-09 18:13.
 * Contact: johanp7@kth.se
 */

public class StateHandler {

    private SIPState notConnected;
    private SIPState connecting;
    private SIPState waitAck;
    private SIPState waitOkConnecting;
    private SIPState connected;
    private SIPState waitOkDisconnecting;

    private SIPState currentState;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private final int SERVER_PORT = 5060;
    private boolean busy = false;

    public StateHandler() {

        notConnected = new NotConnected(this);
        connecting = new Connecting(this);
        waitAck = new WaitAck(this);
        waitOkConnecting = new WaitOkConnecting(this);
        connected = new Connected(this);
        waitOkDisconnecting = new WaitOkDisconnecting(this);

        currentState = notConnected;

        new ClientListener().start();
        new ClientHandler().start();
    }

    public void setCurrentState(SIPState state) {
        currentState = state;
    }

    public void noResponse() {
        currentState.noResponse();
    }

    public void tryConnect() {
        currentState.tryConnect();
    }

    public void sendInvite(String[] parts) {
        currentState.sendInvite(parts);
    }

    public void sendAck() {
        currentState.sendAck();
    }

    public void sendBye() {
        currentState.sendBye();
    }

    public void sendOk() {
        currentState.sendOk();
    }

    public void gotOk() {
        currentState.gotOk();
    }

    public void gotAck() {
        currentState.gotAck();
    }

    public void gotBye() {
        currentState.gotBye();
    }

    public void gotBusy() {
        currentState.gotBusy();
    }

    public void gotInvite(String request) {
        currentState.gotInvite(request);
    }

    public SIPState getNotConnected() {
        return notConnected;
    }

    public SIPState getConnecting() {
        return connecting;
    }

    public SIPState getWaitAck() {
        return waitAck;
    }

    public SIPState getWaitOkConnecting() {
        return waitOkConnecting;
    }

    public SIPState getConnected() {
        return connected;
    }

    public SIPState getWaitOkDisconnecting() {
        return waitOkDisconnecting;
    }

    public SIPState getCurrentState() {
        return currentState;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public PrintWriter setOut(PrintWriter out) {
        this.out = out;
        return this.out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public BufferedReader setIn(BufferedReader in) {
        this.in = in;
        return this.in;
    }

    private class ClientHandler extends Thread {

        @Override
        public void run() {

            Scanner scan = new Scanner(System.in);

            while (true) {

                String input = scan.nextLine();

                if (busy == true && input.toLowerCase().equals("bye")) {

                    currentState.sendBye();

                    if(currentState.getState().toLowerCase().equals("waitokdisconnecting")) {
                        try {
                            input = in.readLine();
                            if(input.toLowerCase().equals("ok")) {
                                currentState.gotOk();
                                continue;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                String[] parts = input.split(" ");

                if (parts[0].startsWith("/quit"))
                    System.exit(0);

                else if (parts.length == 6) {
                    currentState.sendInvite(parts);
                } else {
                    return;
                }

                if (currentState.getState().toLowerCase().equals("waitok")) {
                    System.out.println("got here 1");
                    currentState.sendAck();
                } else {
                    return;
                }

                if (currentState.getState().toLowerCase().equals("connected")) {
                    out.println("We are now connected bro.");
                    busy = true;
                }
            }
        }
    }

    private class ClientListener extends Thread {

        @Override
        public void run() {
            try {

                ServerSocket listenSocket = new ServerSocket(SERVER_PORT);

                while (true) {
                    if (busy == false) {
                        clientSocket = listenSocket.accept();
                        busy = true;

                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String request = in.readLine();

                        currentState.gotInvite(request);
                        if (currentState.getState().toLowerCase().equals("connecting")) {
                            currentState.tryConnect();
                            if (currentState.getState().toLowerCase().equals("waitack")) {
                                currentState.gotAck();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
