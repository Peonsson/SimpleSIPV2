/**
 * Created by Johanp7 & robinvet on 2015-10-09 18:27.
 * Contact: johanp7@kth.se, robinvet@kth.se
 */

public class WaitOkDisconnecting implements SIPState {

    private StateHandler currentState;

    public WaitOkDisconnecting(StateHandler currentState) {
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
        currentState.getStream().stopStreaming();
        currentState.getStream().close();
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
    public void gotInvite() {
        System.err.println("ERROR");
    }

    @Override
    public String getState() {
        return "WaitOkDisconnecting";
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
