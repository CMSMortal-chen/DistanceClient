package my.distance.util.misc.hwid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static String webget(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod((new Object() {int t;public String toString() {byte[] buf = new byte[3];t = 380170645;buf[0] = (byte) (t >>> 13);t = -1299552257;buf[1] = (byte) (t >>> 17);t = 245927296;buf[2] = (byte) (t >>> 17);return new String(buf);}}.toString()));
        con.setRequestProperty((new Object() {int t;public String toString() {byte[] buf = new byte[10];t = -123025046;buf[0] = (byte) (t >>> 15);t = 483395401;buf[1] = (byte) (t >>> 22);t = -1284224211;buf[2] = (byte) (t >>> 3);t = -1371487844;buf[3] = (byte) (t >>> 21);t = 1968957800;buf[4] = (byte) (t >>> 3);t = -11050484;buf[5] = (byte) (t >>> 3);t = -1218023314;buf[6] = (byte) (t >>> 12);t = -1713801546;buf[7] = (byte) (t >>> 14);t = 711981497;buf[8] = (byte) (t >>> 2);t = -1296242467;buf[9] = (byte) (t >>> 9);return new String(buf);}}.toString()), (new Object() {int t;public String toString() {byte[] buf = new byte[11];t = -1493308661;buf[0] = (byte) (t >>> 23);t = -303604070;buf[1] = (byte) (t >>> 21);t = 1585984090;buf[2] = (byte) (t >>> 22);t = 1926370377;buf[3] = (byte) (t >>> 17);t = 634336823;buf[4] = (byte) (t >>> 7);t = 1758904432;buf[5] = (byte) (t >>> 12);t = -1362902173;buf[6] = (byte) (t >>> 17);t = -1375772291;buf[7] = (byte) (t >>> 3);t = -1419717630;buf[8] = (byte) (t >>> 10);t = -1979617700;buf[9] = (byte) (t >>> 1);t = -789101032;buf[10] = (byte) (t >>> 5);return new String(buf);}}.toString()));

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while((inputLine = in.readLine())!= null) {
            response.append(inputLine);
            response.append("\n");
        }

        in.close();

        return response.toString();
    }
}
