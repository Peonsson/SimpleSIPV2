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
    public void sendInvite(String request) {

        System.out.println("invite not connected: " + request);

        String[] parts = request.split(" ");

        try {

            currentState.setSip_to(parts[1]);
            currentState.setSip_from(parts[2]);
            currentState.setIp_to(parts[3]);
            currentState.setIp_from(parts[4]);
            currentState.setLocalAudioPort(Integer.parseInt(parts[5]));

            InetAddress host = InetAddress.getByName(parts[3]);
            currentState.setClientSocket(new Socket(host, 5060));

            currentState.getClientSocket().setSoTimeout(3000);

            PrintWriter out = currentState.setOut(new PrintWriter(currentState.getClientSocket().getOutputStream(), true));
            currentState.setIn(new BufferedReader(new InputStreamReader(currentState.getClientSocket().getInputStream())));

            out.println(request);

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
    public void gotInvite() {

        BufferedReader in = currentState.getIn();
        PrintWriter out = currentState.getOut();
        String request = null;

        try {
            request = in.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String[] parts = request.split(" ");

        if (parts[0].toString().equals("INVITE")) {

            currentState.setSip_to(parts[1]);
            currentState.setSip_from(parts[2]);
            currentState.setIp_to(parts[3]);
            currentState.setIp_from(parts[4]);
            currentState.setRemoteAudioPort(Integer.parseInt(parts[5]));

            System.out.println("gotInvite");
//            out.println("OK");
            currentState.setCurrentState(currentState.getConnecting());
        } else {
            System.err.println("EXPECTED INVITE BUT GOT: " + parts[0].toString());
            currentState.setCurrentState(currentState.getNotConnected());
            System.out.println("getNotConnected");
        }
    }

    @Override
    public String getState() {
        return "NotConnected";
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
