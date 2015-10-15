import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by Johan Pettersson on 2015-10-09 18:27.
 * Contact: johanp7@kth.se
 */

public class Connected implements SIPState {

    private StateHandler currentState;

    public Connected(StateHandler currentState) {
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
        PrintWriter out = currentState.getOut();
        out.println("BYE");
        System.out.println("sendBye");
        currentState.setCurrentState(currentState.getWaitOkDisconnecting());
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
        PrintWriter out = currentState.getOut();
        out.println("OK");
        System.out.println("gotBye");
        currentState.setCurrentState(currentState.getNotConnected());
        System.out.println("getNotConnected");
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
        return "Connected";
    }

    @Override
    public void startCall() {

        try {
            currentState.setStream(new AudioStreamUDP(currentState.getLocalAudioPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioStreamUDP stream = currentState.getStream();

        try {

            PrintWriter out = currentState.getOut();
            out.println(stream.getLocalPort());

            int remotePort = currentState.getRemoteAudioPort();
            InetAddress host = InetAddress.getByName(currentState.getIp_from());

            System.out.println("startCall: Ip_from: " + currentState.getIp_from() + ", remotePort: " + remotePort);
            stream.connectTo(host, remotePort);

            stream.startStreaming();

            System.out.println("Press ANY key to stop streaming");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }

    @Override
    public void receiveCall() {

        Scanner scan = new Scanner(System.in);

        try {
            currentState.setStream(new AudioStreamUDP(currentState.getLocalAudioPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioStreamUDP stream = currentState.getStream();

        try {

            BufferedReader in = currentState.getIn();
            int remotePort = Integer.parseInt(in.readLine());

            InetAddress address = InetAddress.getByName(currentState.getIp_to());

            System.out.println("receiveCall: getIp_to: " + currentState.getIp_to() + ", remotePort: " +remotePort);
            stream.connectTo(address, remotePort);

            stream.startStreaming();

            System.out.println("Press ANY key to stop streaming");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }
}
