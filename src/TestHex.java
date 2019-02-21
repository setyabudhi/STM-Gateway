
public class TestHex {

	
	
	public static String toHex(long value, int length)
    {
        return toHex(value, length, false);
    }

    public static String toHex(long value, int length, boolean isPrefix0x)
    {
        String result = null;
        if(length <= 0)
            return "";
        StringBuffer hexResult = new StringBuffer(2 * length + 2);
        String hexValue = Long.toHexString(value).toUpperCase();
        if(isPrefix0x)
            hexResult.append("0x");
        if(hexValue.length() % 2 == 1)
            hexResult.append("0");
        int missingBytes = length - (hexValue.length() + 1) / 2;
        for(int i = 0; i < missingBytes; i++)
            hexResult.append("00");

        result = hexResult.append(hexValue).toString();
        return result;
    }
    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[(i * 2) + 1]);
        }

        return ret;
    }
    
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
                       .byteValue();
        _b0 = (byte) (_b0 << 4);

        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
                       .byteValue();
        byte ret = (byte) (_b0 ^ _b1);

        return ret;
    }    
    
    
    public static int byte2int(byte b[])
    {
        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
    }
    
    public static int byte2int2(byte b[])
    {
        return b[1] & 0xff | (b[0] & 0xff) << 8;
    }
    
    public static byte[] int2byte(int n)
    {
        byte b[] = new byte[4];
        b[0] = (byte)(n >> 24);
        b[1] = (byte)(n >> 16);
        b[2] = (byte)(n >> 8);
        b[3] = (byte)n;
        return b;
    }
    
    public static byte[] int2byte2(int n)
    {
        byte b[] = new byte[2];
        b[0] = (byte)(n >> 8);
        b[1] = (byte)n;
        return b;
    }    
    
    public static void int2byte(int n, byte buf[], int offset)
    {
        buf[offset] = (byte)(n >> 24);
        buf[offset + 1] = (byte)(n >> 16);
        buf[offset + 2] = (byte)(n >> 8);
        buf[offset + 3] = (byte)n;
    }
    
    
    
    public static String byteToHexString(byte b){
        int n = b;
        if(n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return HexCode[d1]+HexCode[d2];
    }
    private static String HexCode[] = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "a", "b", "c", "d", "e", "f"
    };    
    
    public static String byteArrayToHexString(byte b[]){
        String result = "";
        for(int i = 0; i < b.length; i++)
            result = result+byteToHexString(b[i]);
        return result;
    }
    
    public static String bytes2Hex(byte[] b) {
        String ret = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 255);

            if (hex.length() == 1) {
                hex = (new StringBuilder()).append("0").append(hex).toString();
            }

            ret = (new StringBuilder()).append(ret).append(hex.toUpperCase())
                   .toString();
        }

        return ret;
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int ll = 791;
		/*
		String str = toHex(791,2);
		System.out.println(str);
		System.out.println(new String(HexString2Bytes(str)));
		*/
		byte[] bb = int2byte2(ll);
		System.out.println(bb.length);
		System.out.println(byteArrayToHexString(bb));
		
		System.out.println(byte2int2(bb));

	}

}
