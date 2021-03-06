import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;

/**
 * Created by Johanp7 & robinvet on 2015-10-09 18:27.
 * Contact: johanp7@kth.se, robinvet@kth.se
 */

public class WaitOkConnecting implements SIPState {

    private StateHandler currentState;

    public WaitOkConnecting(StateHandler currentState) {
        this.currentState = currentState;
    }

    @Override
    public void noResponse() {
        System.err.println("SocketTimeoutException");
        currentState.setBusy(false);
        currentState.setCurrentState(currentState.getNotConnected());
        System.out.println("getNotConnected");
    }

    @Override
    public void tryConnect() {
        System.err.println("ERROR");
    }

    @Override
    public void sendInvite(String request) {
        System.err.println("ERROR");
    }

    @Override
    public void sendAck() {
        try {
            currentState.getClientSocket().setSoTimeout(3000);
            BufferedReader in = currentState.getIn();
            String request = in.readLine();

            if(request.toLowerCase().equals("ok")) {
                System.out.println("sendAck: gotOk");
                PrintWriter out = currentState.getOut();
                out.println("ACK");
                currentState.getClientSocket().setSoTimeout(0);
                System.out.println("ClientHandler: We are now connected.");
                currentState.setCurrentState(currentState.getConnected());
            } else if (request.toLowerCase().equals("busy")) {
                System.out.println("waitOkConnecting gotBusy");
                gotBusy();
            } else {
                System.err.println("EXPECTED OK BUT GOT: " + request);
                currentState.setCurrentState(currentState.getNotConnected());
                System.out.println("getNotConnected");
            }

        } catch (SocketTimeoutException e) {
            this.noResponse();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        System.out.println("gotBusy");
        currentState.setCurrentState(currentState.getNotConnected());
        System.out.println("getNotConnected");
    }

    @Override
    public void gotInvite() {
        System.err.println("ERROR");
    }

    @Override
    public String getState() {
        return "WaitOkConnecting";
    }

    @Override
    public void startCall() {
        System.err.println("ERROR");
    }

    @Override
    public void receiveCall() {
        System.err.println("ERROR");
    }
}
