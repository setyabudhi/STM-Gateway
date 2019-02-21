import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class SendSocketServerThread extends Thread {
    private boolean active;
    private ServerSocket srv_socket;
    private int port;
    private int connectCount = 1;
    private int currentWorkingSocket = 0;
    private List<Socket> listSocket = new ArrayList<Socket>();

    public SendSocketServerThread(int port) {
        this.port = port;
        active = true;
    }

    public SendSocketServerThread(int port, int connectCount) {
        this.port = port;
        active = true;
        this.connectCount = connectCount;
    }

    public int sendMessage(byte[] bufSend) throws IOException {
        Socket soc = null;
        boolean flag = true;
        int result = -1;

        while (flag) {
            if (listSocket.size() == 0) {
                flag = false;
                System.out.println("no client connect to Host!");
            } else {
                if ((currentWorkingSocket + 1) > listSocket.size()) {
                    currentWorkingSocket = 0;
                }

                soc = (Socket) listSocket.get(currentWorkingSocket);

                if (isSocketActive(soc)) {
                    try{
	                    soc.getOutputStream().write(bufSend);
	                    soc.getOutputStream().flush();
	                    result = 0;
	                    if(STMGateWay.isPrintMsg)
	                    	System.out.println("send:" + new String(bufSend) + soc);
	                	flag = false;
	                    currentWorkingSocket++;
                    }catch(SocketException se){
                        Socket sendsoc = (Socket) listSocket.remove(currentWorkingSocket);

                        try {
                            System.out.println("Close Send ClientSocket:" +
                                sendsoc);
                            sendsoc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Socket sendsoc = (Socket) listSocket.remove(currentWorkingSocket);

                    try {
                        System.out.println("Close Send ClientSocket:" +
                            sendsoc);
                        sendsoc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /*
        if (soc != null) {
            currentWorkingSocket++;
            System.out.println("send:" + new String(bufSend) + soc);
            soc.getOutputStream().write(bufSend);
            soc.getOutputStream().flush();
        }
        */
        
        return result;
    }

    private boolean isSocketActive(Socket clientSocket)
        throws IOException {
        return (clientSocket != null) && !clientSocket.isClosed() &&
        clientSocket.isConnected() && !clientSocket.isOutputShutdown() &&
        (clientSocket.getOutputStream() != null);
    }

    public void run() {
        try {
            srv_socket = new ServerSocket(port, connectCount);
            srv_socket.setSoTimeout(0);

            while (active) {
                Socket clientSocket = srv_socket.accept();

                if (isSocketActive(clientSocket)) {
		            for (int i = 0; i < listSocket.size();) {
//		                Socket soc = (Socket) listSocket.get(i);
		
		//                    if (!isSocketActive(soc)) {
		                    Socket sendsoc = (Socket) listSocket.remove(i);
		
		                    try {
		                        System.out.println("Close Send ClientSocket:" +
		                            sendsoc);
		                        sendsoc.close();
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    }
		//                    } else {
		//                        i++;
		//                    }
		            }

                    System.out.println("SendPort[" + port + "] Connected;ClientSocket:" +
                        clientSocket);
                    
                    listSocket.add(clientSocket);
                }
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
