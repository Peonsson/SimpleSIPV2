import java.io.IOException;

/**
 * Created by Johan Pettersson on 2015-10-09 18:27.
 * Contact: johanp7@kth.se
 */

public class WaitOkConnecting implements SIPState {

    private StateHandler currentState;

    public WaitOkConnecting(StateHandler currentState) {
        this.currentState = currentState;
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
        System.err.println("ERROR");
    }

    @Override
    public void sendAck() {
        try {
            String request = currentState.getIn().readLine();

            if(request.toLowerCase().equals("ok")) {
                currentState.setCurrentState(currentState.getConnected());
            } else if (request.toLowerCase().equals("busy")) {
                gotBusy();
            } else {
                currentState.setCurrentState(currentState.getNotConnected());
            }

        } catch (IOException e) {
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
    }

    @Override
    public void gotInvite(String request) {
        System.err.println("ERROR");
    }

    @Override
    public String getState() {
        return "WaitOkConnecting";
    }
}