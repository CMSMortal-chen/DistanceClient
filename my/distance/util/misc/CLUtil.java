package my.distance.util.misc;

import LemonObfAnnotation.ObfuscationClass;
import my.distance.Client;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;

import java.net.Socket;

@ObfuscationClass
public class CLUtil {
    public static void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        try {
            Socket sock = new Socket("localhost", 55996);
            sock.getOutputStream().write((serverId + "\u0000").getBytes());
            sock.getOutputStream().flush();
            String res = String.valueOf(sock.getInputStream().read());
            System.out.println( "["+ Client.name +" AutoCL] CL -> " + res);
            sock.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("AutoCl:连接失败");
        }
    }
}
