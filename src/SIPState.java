/**
 * Created by Johan Pettersson on 2015-10-09 18:06.
 * Contact: johanp7@kth.se
 */

public interface SIPState {

    public void noResponse();
    public void tryConnect();
    public void sendInvite(String request);
    public void sendAck();
    public void sendBye();
    public void sendOk();
    public void gotOk();
    public void gotAck();
    public void gotBye();
    public void gotBusy();
    public void gotInvite(String response);
    public String getState();
    public void startCall();
    public void receiveCall();
}
