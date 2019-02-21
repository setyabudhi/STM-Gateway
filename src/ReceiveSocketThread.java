import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;

import java.util.Date;
import java.util.List;


public class ReceiveSocketThread extends Thread {
    Socket clientSocket;
    int headersize = 2;
    //int bufsize = 791;
    Date currentTime = new Date();
    List<byte[]> receiveData;

    public ReceiveSocketThread(Socket clientSocket, List<byte[]> receiveData) {
        this.clientSocket = clientSocket;
        this.receiveData = receiveData;
    }

    private boolean isSocketActive() {
        boolean result = false;

        try {
            result = (clientSocket != null) && !clientSocket.isClosed() &&
                clientSocket.isConnected() && !clientSocket.isInputShutdown() &&
                (clientSocket.getInputStream() != null);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public Date getLastReceiveStartTime() {
        return currentTime;
    }

    public void run() {
        while (isSocketActive()) {
            try {
            	int bufsize = 791;
                byte[] bufHeader = new byte[headersize];

            	currentTime = new Date();
                int readHost = clientSocket.getInputStream().read(bufHeader, 0, headersize);
                if (readHost > -1) {
                	bufsize = STMGateWay.byte2int(bufHeader);
                }
                
                byte[] bufReceive = new byte[bufsize];
                readHost = clientSocket.getInputStream().read(bufReceive, 0, bufsize);
                if (readHost > -1) {
                	if(STMGateWay.isPrintMsg)
                		System.out.println("receiv:" + new String(bufReceive) + clientSocket);
                    receiveData.add(bufReceive);
                }
            } catch (SocketException se) {
                System.out.println(se.getMessage() + ".Receive ClientSocket:" +
                    clientSocket);

                break;
            } catch (IOException ioe) {
                ioe.printStackTrace();

                break;
            }
        }

        shutdown();
    }

    public void shutdown() {
        try {
            System.out.println("Close Receive ClientSocket:" + clientSocket);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
