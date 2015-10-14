import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
    AudioStreamUDP stream = null;


    private final int SERVER_PORT = 5060;
    private boolean busy = false;

    private String invite;
    private String sip_to;
    private String sip_from;
    private String ip_to;
    private String ip_from;
    private int audioPort;

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

    public AudioStreamUDP getStream() {
        return stream;
    }

    public void setStream(AudioStreamUDP stream) {
        this.stream = stream;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public String getSip_to() {
        return sip_to;
    }

    public void setSip_to(String sip_to) {
        this.sip_to = sip_to;
    }

    public String getSip_from() {
        return sip_from;
    }

    public void setSip_from(String sip_from) {
        this.sip_from = sip_from;
    }

    public String getIp_to() {
        return ip_to;
    }

    public void setIp_to(String ip_to) {
        this.ip_to = ip_to;
    }

    public String getIp_from() {
        return ip_from;
    }

    public void setIp_from(String ip_from) {
        this.ip_from = ip_from;
    }

    public int getAudioPort() {
        return audioPort;
    }

    public void setAudioPort(int audioPort) {
        this.audioPort = audioPort;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
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

    public void sendInvite(String request) {
        currentState.sendInvite(request);
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

    /**
     * Call initiator. (A)
     */
    private class ClientHandler extends Thread {

        @Override
        public void run() {

            Scanner scan = new Scanner(System.in);

            while (true) {

                String input = scan.nextLine();

                if (busy == true && input.toLowerCase().equals("bye")) {

                    currentState.sendBye();
                    System.out.println("Killing ClientHandler Thread.");
                    continue;
                } else if(busy == true && input.toLowerCase().equals("stop")) {

                    stream.stopStreaming();
                    //TODO: change state.
                }

                busy = true;
                String[] parts = input.split(" ");

                if (parts[0].startsWith("/quit"))
                    System.exit(0);

                else if (parts.length == 6) {

                    currentState.sendInvite(input);

                } else {
                    return;
                }

                if (currentState.getState().toLowerCase().equals("waitokconnecting")) {
                    currentState.sendAck();
                } else {
                    return;
                }

                if (currentState.getState().toLowerCase().equals("connected")) {
                    System.out.println("ClientHandler: We are now connected.");

                    //TODO: implement audio logic.
                    currentState.receiveCall();

                    new ClientHandlerListener().start();
                }
            }
        }
    }

    /**
     * Connecting client's listener. (A)
     */
    private class ClientHandlerListener extends Thread {

        @Override
        public void run() {
            try {

                String request = in.readLine(); //läser från socket

                if (currentState.getState().toLowerCase().equals("connected") && request.toLowerCase().equals("bye")) { //om vi läser bye
                    currentState.gotBye();
                    if (currentState.getState().toLowerCase().equals("notconnected")) {
                        busy = false;
                    } else {
                        System.out.println("ClientHandlerListener: something went wrong 1.");
                    }
                } else if (currentState.getState().toLowerCase().equals("waitokdisconnecting") && request.toLowerCase().equals("ok")) { //om vi läser ok
                    currentState.gotOk();
                    busy = false;
                } else {
                    System.out.println("ClientHandlerListener: something went wrong 2.");
                    return;
                }

            } catch (SocketTimeoutException e) {
                currentState.noResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call receiver. (B)
     */
    private class ClientListener extends Thread {

        @Override
        public void run() {
            try {

                ServerSocket listenSocket = new ServerSocket(SERVER_PORT);

                while (true) {
                    if (busy == false) {

                        System.out.println("Listening..");
                        clientSocket = listenSocket.accept();
                        busy = true;

                        clientSocket.setSoTimeout(3000);

                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        try {
                            String request = in.readLine();

                            currentState.gotInvite(request);
                            if (currentState.getState().toLowerCase().equals("connecting")) {
                                currentState.tryConnect();
                                if (currentState.getState().toLowerCase().equals("waitack")) {
                                    currentState.gotAck();
                                    if (currentState.getState().toLowerCase().equals("connected")) {
                                        System.out.println("ClientListener: We are now connected.");

                                        //TODO: implement audio logic.
                                        currentState.startCall();
                                        new ClientHandlerListener().start();
                                    }
                                }
                            }
                        } catch (SocketTimeoutException e) {
                            currentState.noResponse();
                            busy = false;
                        }
                    } else {
                        System.out.println("busy: " + busy);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}