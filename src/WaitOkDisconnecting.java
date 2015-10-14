/**
 * Created by Johan Pettersson on 2015-10-09 18:27.
 * Contact: johanp7@kth.se
 */

public class WaitOkDisconnecting implements SIPState {

    private StateHandler currentState;

    public WaitOkDisconnecting(StateHandler currentState) {
        this.currentState = currentState;
    }

    @Override
    public void noResponse() {
        System.out.println("noResponse");
        currentState.setBusy(false);
        currentState.setCurrentState(currentState.getNotConnected());
        System.out.println("getNotConnected");
    }

    @Override
    public void tryConnect() {
        System.err.println("ERROR");
    }

    @Override
    public void sendInvite(String[] parts) {
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
        System.out.println("gotOk");
        currentState.setCurrentState(currentState.getNotConnected());
        System.out.println("getNotConnected");
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
        System.err.println("ERROR");
    }

    @Override
    public String getState() {
        return "WaitOkDisconnecting";
    }
}
