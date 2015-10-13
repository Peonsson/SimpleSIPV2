import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Johan Pettersson on 2015-10-09 18:26.
 * Contact: johanp7@kth.se
 */

public class NotConnected implements SIPState {

    private StateHandler currentState;

    public NotConnected(StateHandler newState) {
        this.currentState = newState;
    }

    @Override
    public void noResponse() {
        System.err.println("ERROR");
    }

    @Override
    public void tryConnect() {
        System.err.println("ERROR");
    }

    @Override
    public void sendInvite(String[] parts) {

        try {

            InetAddress host = InetAddress.getByName(parts[3]);
            currentState.setClientSocket(new Socket(host, 5060));

            PrintWriter out = currentState.setOut(new PrintWriter(currentState.getClientSocket().getOutputStream(), true));
            currentState.setIn(new BufferedReader(new InputStreamReader(currentState.getClientSocket().getInputStream())));

            out.println("INVITE");

            //TODO: REMOVE.
            System.out.println("sendInvite");

            currentState.setCurrentState(currentState.getWaitOkConnecting());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAck() {
        System.err.println("ERROR");
    }

    @Override
    public void sendBye() {
        System.err.println("ERROR");
    }

    @Override
    public void sendOk() {
        System.err.println("ERROR");
    }

    @Override
    public void gotOk() {
        System.err.println("ERROR");
    }

    @Override
    public void gotAck() {
        System.err.println("ERROR");
    }

    @Override
    public void gotBye() {
        System.err.println("ERROR");
    }

    @Override
    public void gotBusy() {
        System.err.println("ERROR");
    }

    @Override
    public void gotInvite(String request) {

        if (request.equals("INVITE")) {
            System.out.println("gotInvite");
            currentState.setCurrentState(currentState.getConnecting());
        } else {
            currentState.setCurrentState(currentState.getNotConnected());
        }

    }

    @Override
    public String getState() {
        return "NotConnected";
    }
}
