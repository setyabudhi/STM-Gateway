import java.net.Socket;


public class EBSSim {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int portToHost = 19001;
        int portFromHost = 19002;
        String ipaddress = "localhost";
        int bufsize = 791;

        Socket sockToHost = null;
        Socket sockFromHost = null;

        try {
            if (args.length >= 3) {
                ipaddress = args[0];
                portToHost = Integer.parseInt(args[1]);
                portFromHost = Integer.parseInt(args[2]);
            }

            if (args.length >= 4) {
                try {
                    bufsize = Integer.parseInt(args[3]);
                } catch (Exception e) {
                }
            }

            byte[] buf = new byte[bufsize];
            sockToHost = new Socket(ipaddress, portToHost);
            sockFromHost = new Socket(ipaddress, portFromHost);

            while (true) {
                sockToHost.getInputStream().read(buf);
                sockFromHost.getOutputStream().write(buf);
                sockFromHost.getOutputStream().flush();
                System.out.println(new String(buf));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sockToHost.close();
                sockFromHost.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
