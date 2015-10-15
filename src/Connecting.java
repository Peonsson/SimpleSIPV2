import java.io.PrintWriter;

/**
 * Created by Johan Pettersson on 2015-10-09 18:26.
 * Contact: johanp7@kth.se
 */

public class Connecting implements SIPState {

    private StateHandler currentState;

    public Connecting(StateHandler currentState) {
        this.currentState = currentState;
    }

    @Override
    public void noResponse() {
        System.out.println("noResponse");
        currentState.setBusy(false);
        currentState.setCurrentState(currentState.getNotConnected());
    }

    @Override
    public void tryConnect() {
        System.out.println("tryConnect");

        PrintWriter out = currentState.getOut();
        out.println("OK");

        currentState.setCurrentState(currentState.getWaitAck());
    }

    @Override
    public void sendInvite(String request) {
        System.err.println("ERROR");
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
    public void gotInvite() {
        System.err.println("ERROR");
    }

    @Override
    public String getState() {
        return "Connecting";
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
