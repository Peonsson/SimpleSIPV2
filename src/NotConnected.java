import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Johanp7 & robinvet on 2015-10-09 18:26.
 * Contact: johanp7@kth.se, robinvet@kth.se
 */

public class NotConnected implements SIPState {

    private StateHandler currentState;

    public NotConnected(StateHandler newState) {
        this.currentState = newState;
    }

    @Override
    public void noResponse() {
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

        String[] parts = request.split(" ");

        try {

            currentState.setSip_to(parts[1]);
            currentState.setSip_from(parts[2]);
            currentState.setIp_to(parts[3]);
            currentState.setIp_from(parts[4]);
            currentState.setLocalAudioPort(Integer.parseInt(parts[5]));

            // See if handhsake should fail or not according to user
            if (parts.length == 7) {
                if (parts[6].equals("FAIL")) {
                    currentState.setFailHandshake(true);
                }
            }


            InetAddress host = InetAddress.getByName(parts[3]);
            currentState.setClientSocket(new Socket(host, 5060));

            currentState.getClientSocket().setSoTimeout(3000);

            PrintWriter out = currentState.setOut(new PrintWriter(currentState.getClientSocket().getOutputStream(), true));
            currentState.setIn(new BufferedReader(new InputStreamReader(currentState.getClientSocket().getInputStream())));

            if (currentState.getFailHandshake()) {
                out.println("HEJ " + parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5]);
            }
            else {
                out.println(request);
            }

            System.out.println("sendInvite");

            currentState.setCurrentState(currentState.getWaitOkConnecting());

        } catch (ConnectException e) {
            System.err.println("Couldn't connect to user agent.");
            currentState.setBusy(false);
            currentState.noResponse();
        }
        catch (UnknownHostException e) {
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
