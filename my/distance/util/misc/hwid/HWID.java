package my.distance.util.misc.hwid;

import LemonObfAnnotation.ObfuscationClass;
import cms.mortalchen.distance.irc.MyBufferedReader;
import cms.mortalchen.distance.irc.MyPrintWriter;
import cms.mortalchen.distance.irc.utils.IRCUtils;
import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;
import cms.mortalchen.distance.irc.utils.packets.clientside.ClientHandShakePacket;
import cms.mortalchen.distance.irc.utils.packets.clientside.ClientVerifyPacket;
import cms.mortalchen.distance.irc.utils.packets.serverside.ServerHandShakePacket;
import cms.mortalchen.distance.irc.utils.packets.serverside.ServerVerifyResultPacket;
import cms.mortalchen.encryption.RSA;
import my.distance.Client;
import net.minecraft.client.main.Main;

import javax.swing.*;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Socket;

@ObfuscationClass
public class HWID {
    public static void genHWID() throws Exception {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Socket socket = new Socket("disirc.casodo.cc", 44413);
        MyPrintWriter pw = new MyPrintWriter(socket.getOutputStream(), true);
        MyBufferedReader br = new MyBufferedReader(new InputStreamReader(socket.getInputStream()));
        RSA.genKey();
        pw.println(new ClientHandShakePacket(System.currentTimeMillis(),RSA.PUBLIC_KEY).toJson());
        String message = br.readLine();
        IRCPacket packet = IRCUtils.coverToPacket(message);
        if (packet.type.equals(IRCType.HANDSHAKE)){
            ServerHandShakePacket handShakePacket = (ServerHandShakePacket) packet;
            RSA.SERVER_PUBLIC_KEY = handShakePacket.content;
            pw.publicKey = RSA.SERVER_PUBLIC_KEY;
            br.privateKey = RSA.PRIVATE_KEY;
        }else {
            JOptionPane.showMessageDialog(null,"服务器数据包异常","Distance",JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        pw.println(new ClientVerifyPacket(System.currentTimeMillis(),"",HWIDUtil.getHWID(), Client.releaseVersion,Main.isbeta).toJson());

        message = br.readLine();
        packet = IRCUtils.coverToPacket(message);
        if (packet.type.equals(IRCType.VERIFY)) {
            ServerVerifyResultPacket verifyResultPacket = (ServerVerifyResultPacket) IRCUtils.coverToPacket(message);
            if (verifyResultPacket.content.startsWith("false")) {
                String[] str = verifyResultPacket.content.split(":");
                if (str[1].equalsIgnoreCase("version")) {
                    JOptionPane.showInputDialog(null,
                            Main.isbeta ?  "本Beta客户端已过时" : "@您@的@客@户@端@已@过@时@,@请@更@新@客@户@端@".replace("@","")
                            , Client.releaseVersion);
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                    Runtime.getRuntime().exit(0);
                } else if (str[1].equalsIgnoreCase("hwid")) {
                    JOptionPane.showInputDialog(null, Main.isbeta ? "您不是Beta用户" : "您没有注册nacs".replace("n","H").replace("a","W").replace("c","I").replace("s","D")
                            , HWIDUtil.getHWID());
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                    Runtime.getRuntime().exit(0);
                } else {
                    JOptionPane.showMessageDialog(null, "无法判断错误"
                            , "Distance", JOptionPane.ERROR_MESSAGE);
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                    Runtime.getRuntime().exit(0);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "无法判断错误"
                    , "Distance", JOptionPane.ERROR_MESSAGE);
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            Runtime.getRuntime().exit(0);
        }
        socket.close();
    }
}
