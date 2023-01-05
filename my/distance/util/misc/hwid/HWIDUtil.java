package my.distance.util.misc.hwid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
 
public class HWIDUtil {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    static int count = 3;

    public static String getHWID() throws Exception {
        StringBuilder s = new StringBuilder();
        String main = generateHWID();
        byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        s.append("DIS_");
        for (byte b : md5) {
            s.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 3);
            i++;
        }
        return s.toString().toUpperCase();
    }

    public static String generateHWID() {
        return System.getProperty("os.name") +
                System.getProperty("os.arch") +
                System.getProperty("os.version") +
                System.getenv("PROCESSOR_IDENTIFIER") +
                System.getenv("NUMBER_OF_PROCESSORS") +
                System.getenv("COMPUTERNAME") +
                System.getenv("USERNAME");
    }
}
