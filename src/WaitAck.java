import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Johan Pettersson on 2015-10-09 18:26.
 * Contact: johanp7@kth.se
 */

public class WaitAck implements SIPState {

    private StateHandler currentState;

    public WaitAck(StateHandler currentState) {
        this.currentState = currentState;
    }

    @Override
    public void noResponse() {
        System.out.println("noResponse");
        currentState.setCurrentState(currentState.getNotConnected());
    }

    @Override
    public void tryConnect() {
        System.err.println("ERROR1");
    }

    @Override
    public void sendInvite(String[] parts) {
        System.err.println("ERROR2");
    }

    @Override
    public void sendAck() {
        System.err.println("ERROR3");
    }

    @Override
    public void sendBye() {
        System.err.println("ERROR4");
    }

    @Override
    public void sendOk() {
        System.err.println("ERROR5");
    }

    @Override
    public void gotOk() {
        System.err.println("ERROR6");
    }

    @Override
    public void gotAck() {

        BufferedReader in = currentState.getIn();
        PrintWriter out = currentState.getOut();

        try {
            String response = in.readLine();

            if(response.toLowerCase().equals("ack")) {
                System.out.println("gotAck");
                currentState.getClientSocket().setSoTimeout(0); //TURN OFF TIMEOUT
                currentState.setCurrentState(currentState.getConnected());
            } else {
                out.println("EXPECTED ACK BUT GOT: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gotBye() {
        System.err.println("ERROR7");
    }

    @Override
    public void gotBusy() {
        System.err.println("ERROR8");
    }

    @Override
    public void gotInvite(String request) {
        System.err.println("ERROR9");
    }

    @Override
    public String getState() {
        return "WaitAck";
    }
}
