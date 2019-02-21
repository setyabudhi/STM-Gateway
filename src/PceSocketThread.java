import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.net.SocketException;

import java.util.Date;
import java.util.List;


public class PceSocketThread extends Thread {
	Socket client;
    int bufsize = 791;
    public int seqnumsize = 0;
    public static int seqoffset = 0;
    public int wsidsize = 0;
    public static int wsidoffset = 0;
    SendSocketServerThread toHostThread;
    ReceiveSocketServerThread fromHostThread;

    public PceSocketThread (Socket client, SendSocketServerThread toHostThread, ReceiveSocketServerThread fromHostThread, int bufsize, int seqnumsize, int seqoffset, int wsidsize, int wsidoffset) {
        this.client = client;
        this.bufsize = bufsize;
        this.seqnumsize = seqnumsize;
        this.seqoffset = seqoffset;
        this.wsidsize = wsidsize;
        this.wsidoffset = wsidoffset;
        this.toHostThread = toHostThread;
        this.fromHostThread = fromHostThread;
    }

    public void run() {
        InputStream inbound = null;
        OutputStream outbound = null;
        byte[] bufSend = new byte[bufsize];

        try {
            inbound = client.getInputStream();
            outbound = client.getOutputStream();

            if (client.getInputStream().read(bufSend, 0, bufsize) > 1) {
                if (bufSend.length > 0) {
                    try {
                    	byte[] seqnum = new byte[seqnumsize];
                    	System.arraycopy(bufSend, seqoffset, seqnum, 0, seqnumsize);
                    	
                    	byte[] wsid = new byte[wsidsize];
                    	System.arraycopy(bufSend, wsidoffset, wsid, 0, wsidsize);
                    	
                    	byte[] header = int2byte(bufsize);
                        byte[] tmpbufsend = new byte[header.length + bufsize];
                        System.arraycopy(header, 0, tmpbufsend, 0, header.length);
                        System.arraycopy(bufSend, 0, tmpbufsend, header.length,
                            bufsize);
                        bufSend = tmpbufsend;
                        int sendresult = toHostThread.sendMessage(bufSend);
                        
                        if(sendresult == 0){

	                        byte[] bufReceive = fromHostThread.getReceiveMessage(wsid, seqnum);
	                        int timeout = 1;
	
	                        while ((bufReceive == null) && (timeout < 30)) {
	                            try {
	                                Thread.sleep(1000);
	                                timeout++;
	                                bufReceive = fromHostThread.getReceiveMessage(wsid, seqnum);
	                            } catch (Exception e) {
	                            }
	                        }
	
	                        if (bufReceive == null) {
	                            System.out.println(
	                                "PortFromHost is not received response messages!");
	                        } else {
	                            if (bufReceive.length > bufsize) {
	                            	byte[] buf = new byte[bufsize];
	                                System.arraycopy(bufReceive, 0,
	                                    buf, 0, bufsize);
	                                bufReceive = buf;
	                            }
	                            if(bufReceive.length < bufsize){
	                            	int offset = bufsize - bufReceive.length;
	                            	byte[] offbyte = new byte[offset];
	                            	for(int i = 0 ; i < offset; i++){
	                            		offbyte[i] = 0x30;
	                            	}
	                            	byte[] buf = new byte[bufsize];
	                                System.arraycopy(bufReceive, 0,
	                                    buf, 0, bufReceive.length);
	                                System.arraycopy(offbyte, 0,
	                                        buf, bufReceive.length, offset);
	                                bufReceive = buf;
	                            }
	
	                            if(STMGateWay.isPrintMsg)
	                            	System.out.println("to pce:" + new String(bufReceive));
	                            outbound.write(bufReceive);
	                            outbound.flush();
	                        }
                        }else{
                        	//return host connect error in response code
                        }
                    } catch (Exception se) {
                        se.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outbound.close();
            } catch (Exception e) {
            }

            try {
                inbound.close();
            } catch (Exception e) {
            }

            try {
                client.close();
            } catch (Exception e) {
            }
        }

        shutdown();
    }

    public void shutdown() {
        try {
            System.out.println("Close Receive ClientSocket:" + client);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] int2byte(int n)
    {
        byte b[] = new byte[2];
        b[0] = (byte)(n >> 8);
        b[1] = (byte)n;
        return b;
    }    
}
