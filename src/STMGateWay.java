import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class STMGateWay {
    public int portToHost = 19001;
    public int portFromHost = 19002;
    public int portPCE = 12345;
    public int bufsize = 791;
    public int seqnumsize = 6;
    public static int seqoffset = 65;//66-1
    public int wsidsize = 16;
    public static int wsidoffset = 275;//276-1

    public static boolean isPrintMsg = true;
    public int headersize = 2;
    SendSocketServerThread toHostThread;
    ReceiveSocketServerThread fromHostThread;

    public STMGateWay(String portToHost, String portFromHost, String portPCE) {
        this.portToHost = Integer.parseInt(portToHost);
        this.portFromHost = Integer.parseInt(portFromHost);
        this.portPCE = Integer.parseInt(portPCE);
    }

    public void setBufSize(int bufsize) {
        this.bufsize = bufsize;
    }

    public void execute() {
        if (portToHost == portFromHost) {
            System.out.println(
                "the portToHost and portFromHost can not be same.");
        } else {
            toHostThread = new SendSocketServerThread(portToHost);
            toHostThread.start();
            fromHostThread = new ReceiveSocketServerThread(portFromHost);
            fromHostThread.start();

            try {
                ServerSocket srv_socket = new ServerSocket(portPCE);

                while (true) {
                    Socket pcesock = srv_socket.accept();
                    //ServiceClient(pcesock);
                    PceSocketThread pceThread = new PceSocketThread(pcesock, toHostThread, fromHostThread, bufsize, seqnumsize, seqoffset, wsidsize, wsidoffset);
                    pceThread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    toHostThread.shutdown();
                } catch (Exception e) {
                }

                try {
                    fromHostThread.shutdown();
                } catch (Exception e) {
                }
            }
        }
    }

    public static byte[] int2byte(int n)
    {
        byte b[] = new byte[2];
        b[0] = (byte)(n >> 8);
        b[1] = (byte)n;
        return b;
    }    
    
    public static int byte2int(byte b[])
    {
        return b[1] & 0xff | (b[0] & 0xff) << 8;
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        String portToHost = "";
        String portFromHost = "";
        String portPCE = "";

        if (args.length < 3) {
            System.out.println("params: portToHost portFromHost portPCE bufsize[option] notPrintMessageFlag[option:1]");
        } else {
            portToHost = args[0];
            portFromHost = args[1];
            portPCE = args[2];

            System.out.println("ToHost Port:" + portToHost);
            System.out.println("FromHost Port:" + portFromHost);
            System.out.println("PCEEIS Port:" + portPCE);

            STMGateWay client = new STMGateWay(portToHost, portFromHost, portPCE);

            if (args.length > 3) {
                try {
                    int bufsize = Integer.parseInt(args[3]);
                    client.setBufSize(bufsize);
                } catch (Exception e) {
                }
            }
            
            if (args.length > 4) {
                try {
                    if("1".equals(args[4])){
                    	isPrintMsg = false;
                    }
                } catch (Exception e) {
                }
            }

            //client.setHeader(new byte[] { 0x03, 0x17 });

            client.execute();
        }
    }
}
