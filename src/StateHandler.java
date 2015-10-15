import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
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
    private int localAudioPort;
    private int remoteAudioPort;

    private boolean talking = false;

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

    public boolean isTalking() {
        return talking;
    }

    public void setTalking(boolean talking) {
        this.talking = talking;
    }

    public int getRemoteAudioPort() {
        return remoteAudioPort;
    }

    public void setRemoteAudioPort(int remoteAudioPort) {
        this.remoteAudioPort = remoteAudioPort;
    }

    public int getLocalAudioPort() {
        return localAudioPort;
    }

    public void setLocalAudioPort(int localAudioPort) {
        this.localAudioPort = localAudioPort;
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

    public void gotInvite() {
        currentState.gotInvite();
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
                    continue;
                } else if(busy == true) {
                    System.err.println("Unexpected command.");
                    continue;
                }

                busy = true;
                String[] parts = input.split(" ");

                if (parts.length == 6 && parts[0].equals("INVITE")) {
                    currentState.sendInvite(input);
                } else {
                    System.err.println("Unexpected command.");
                    continue;
                }

                currentState.sendAck();
                currentState.receiveCall();

                new ClientHandlerListener().start();
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

                String request = in.readLine();

                if (currentState.getState().toLowerCase().equals("connected") && request.toLowerCase().equals("bye")) { //om vi l�ser bye
                    currentState.gotBye();
                    if (currentState.getState().toLowerCase().equals("notconnected")) {
                        busy = false;
                    } else {
                        System.err.println("ClientHandlerListener: something went wrong 1.");
                    }
                } else if (currentState.getState().toLowerCase().equals("waitokdisconnecting") && request.toLowerCase().equals("ok")) { //om vi l�ser ok
                    currentState.gotOk();
                    busy = false;
                } else {
                    System.err.println("ClientHandlerListener: something went wrong 2.");
                    currentState.noResponse();
                }

            } catch (NullPointerException e) {
                System.err.println("Connection terminated unexpectedly.");
                stream.stopStreaming();
                stream.close();
                currentState.noResponse();
            }
            catch (SocketTimeoutException e) {
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

                        System.out.println("Listening at.. " + InetAddress.getLocalHost().getHostAddress() + ":" + listenSocket.getLocalPort());
                        clientSocket = listenSocket.accept();

                        busy = true;

                        clientSocket.setSoTimeout(3000);

                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        currentState.gotInvite();
                        currentState.tryConnect();
                        currentState.gotAck();
                        currentState.startCall();
                        new ClientHandlerListener().start();

                        System.out.println("busy: " + busy);
                    }
                    try {
                        Thread.sleep(200);
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