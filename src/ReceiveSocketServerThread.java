import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;


public class ReceiveSocketServerThread extends Thread {
    private static List<byte[]> receiveData = new ArrayList<byte[]>();
    private boolean active;
    private ServerSocket srv_socket;
    private int port;
    private int connectCount = 1;
    //int headersize = 2;
    private List<ReceiveSocketThread> listThread = new ArrayList<ReceiveSocketThread>();

    public ReceiveSocketServerThread(int port) {
        this.port = port;
        active = true;
    }

    public ReceiveSocketServerThread(int port, int connectCount) {
        this.port = port;
        active = true;
        this.connectCount = connectCount;
    }

    public byte[] getReceiveMessage(byte[] wsid, byte[] seqnum) throws IOException {
        if (receiveData.size() > 0) {
            //recv msg mapping
        	int recvoffset = -1;
        	byte[] seqinmsg = new byte[seqnum.length];
        	byte[] wsidinmsg = new byte[wsid.length];
        	for(int i = 0; i < receiveData.size(); i++){
        		byte[] msg = (byte[])receiveData.get(i);
        		System.arraycopy(msg, STMGateWay.seqoffset, seqinmsg, 0, seqnum.length);
        		System.arraycopy(msg, STMGateWay.wsidoffset, wsidinmsg, 0, wsid.length);
        		if(new String(seqnum).equals(new String(seqinmsg))
        				&& new String(wsid).equals(new String(wsidinmsg))){
        			recvoffset = i;
        			break;
        		}
        	}
        	if(recvoffset > -1)
        		return receiveData.remove(recvoffset);
        	else
        		return null;
        } else {
            return null;
        }
    }

    public void run() {
        try {
            srv_socket = new ServerSocket(port, connectCount);
            srv_socket.setSoTimeout(0);

            while (active) {
                Socket clientSocket = srv_socket.accept();
                System.out.println("RecePort[" + port + "] Connected;ClientSocket:" +
                    clientSocket);

                ReceiveSocketThread recthread = new ReceiveSocketThread(clientSocket, receiveData);
                recthread.start();
                listThread.add(recthread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        shutdown();
    }

    public void shutdown() {
        active = false;

        try {
            srv_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
