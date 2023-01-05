package my.distance.api.verify;

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
import my.distance.manager.Fucker;
import my.distance.util.SuperLib;
import my.distance.util.misc.hwid.HWIDUtil;
import net.minecraft.client.main.Main;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.Socket;
@ObfuscationClass
public class SHWID {
    public static int id = 0;
    public static int id2 = 0;

    public static int hahaha = 1;
    public static int BITCH = 0;

    public static void verify() {
        hahaha = 1;
        BITCH = 0;
        id = 1;
        id2 = 2;
        SuperLib.id = 1;
        SuperLib.id2 = 0;


        switch (hahaha) {
            case 1: {
                switch (BITCH) {
                    case 1: {
                        Fucker.dofuck();
                        break;
                    }
                    case 0: {
                        break;
                    }
                }
                break;
            }
            case 0: {
                RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                } catch (IOException e) {
                }
                try {
                    Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                    shutDownMethod.setAccessible(true);
                    shutDownMethod.invoke(null, 0);
                } catch (Exception ignored) {
                    throw new IllegalStateException();
                }
                break;
            }
        }
    }

    public static boolean HWIDVerify() {
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            Socket socket = new Socket("disirc.casodo.cc", 44413);
            MyPrintWriter pw = new MyPrintWriter(socket.getOutputStream(), true);
            MyBufferedReader br = new MyBufferedReader(new InputStreamReader(socket.getInputStream()));

            RSA.genKey();
            pw.println(new ClientHandShakePacket(System.currentTimeMillis(), RSA.PUBLIC_KEY).toJson());
            String message = br.readLine();
            IRCPacket packet = IRCUtils.coverToPacket(message);
            if (packet.type.equals(IRCType.HANDSHAKE)) {
                ServerHandShakePacket handShakePacket = (ServerHandShakePacket) packet;
                RSA.SERVER_PUBLIC_KEY = handShakePacket.content;
                pw.publicKey = RSA.SERVER_PUBLIC_KEY;
                br.privateKey = RSA.PRIVATE_KEY;
            } else {
                JOptionPane.showMessageDialog(null, "服务器数据包异常", Client.name, JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }

            pw.println(new ClientVerifyPacket(System.currentTimeMillis(), "", HWIDUtil.getHWID(), Client.releaseVersion, Main.isbeta).toJson());

            message = br.readLine();
            packet = IRCUtils.coverToPacket(message);
            if (packet.type.equals(IRCType.VERIFY)) {
                ServerVerifyResultPacket verifyResultPacket = (ServerVerifyResultPacket) packet;
                if (verifyResultPacket.content.startsWith("false")) {
                    String[] str = verifyResultPacket.content.split(":");
                    if (str[1].equalsIgnoreCase("version")) {
                        JOptionPane.showInputDialog(null,
                                Main.isbeta ? "您的客户端已过时" : "本Beta客户端已过时"
                                , Client.releaseVersion);
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        } catch (Exception ignored) {
                            throw new IllegalStateException();
                        }
                    } else if (str[1].equalsIgnoreCase("hwid")) {
                        JOptionPane.showInputDialog(null, Main.isbeta ? "您不是Beta用户" : "您没有注册HWID"
                                , HWIDUtil.getHWID());
                        Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        } catch (Exception ignored) {
                            throw new IllegalStateException();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, (new Object() {
                                    int t;

                                    public String toString() {
                                        byte[] buf = new byte[20];
                                        t = -778060500;
                                        buf[0] = (byte) (t >>> 24);
                                        t = -90555;
                                        buf[1] = (byte) (t >>> 12);
                                        t = -1363204;
                                        buf[2] = (byte) (t >>> 15);
                                        t = -383048380;
                                        buf[3] = (byte) (t >>> 22);
                                        t = -51834987;
                                        buf[4] = (byte) (t >>> 20);
                                        t = -318014657;
                                        buf[5] = (byte) (t >>> 22);
                                        t = -51994;
                                        buf[6] = (byte) (t >>> 10);
                                        t = -700;
                                        buf[7] = (byte) (t >>> 3);
                                        t = -290499;
                                        buf[8] = (byte) (t >>> 12);
                                        t = -9;
                                        buf[9] = (byte) (t >>> 2);
                                        t = -188693;
                                        buf[10] = (byte) (t >>> 11);
                                        t = -1361880;
                                        buf[11] = (byte) (t >>> 14);
                                        t = -44505;
                                        buf[12] = (byte) (t >>> 10);
                                        t = -5252;
                                        buf[13] = (byte) (t >>> 6);
                                        t = -182;
                                        buf[14] = (byte) (t >>> 2);
                                        t = -1724599;
                                        buf[15] = (byte) (t >>> 17);
                                        t = -416705860;
                                        buf[16] = (byte) (t >>> 23);
                                        t = -1268227290;
                                        buf[17] = (byte) (t >>> 24);
                                        t = -2682;
                                        buf[18] = (byte) (t >>> 6);
                                        t = -2787160;
                                        buf[19] = (byte) (t >>> 15);
                                        return new String(buf);
                                    }
                                }.toString())
                                , "Distance", JOptionPane.ERROR_MESSAGE);
                        Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        } catch (Exception ignored) {
                            throw new IllegalStateException();
                        }
                    }
                } else {
                    id = 1;
                    id2 = 2;
                    SuperLib.id = 1;
                    SuperLib.id2 = 0;
                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(null, (new Object() {
                            int t;

                            public String toString() {
                                byte[] buf = new byte[20];
                                t = -778060500;
                                buf[0] = (byte) (t >>> 24);
                                t = -90555;
                                buf[1] = (byte) (t >>> 12);
                                t = -1363204;
                                buf[2] = (byte) (t >>> 15);
                                t = -383048380;
                                buf[3] = (byte) (t >>> 22);
                                t = -51834987;
                                buf[4] = (byte) (t >>> 20);
                                t = -318014657;
                                buf[5] = (byte) (t >>> 22);
                                t = -51994;
                                buf[6] = (byte) (t >>> 10);
                                t = -700;
                                buf[7] = (byte) (t >>> 3);
                                t = -290499;
                                buf[8] = (byte) (t >>> 12);
                                t = -9;
                                buf[9] = (byte) (t >>> 2);
                                t = -188693;
                                buf[10] = (byte) (t >>> 11);
                                t = -1361880;
                                buf[11] = (byte) (t >>> 14);
                                t = -44505;
                                buf[12] = (byte) (t >>> 10);
                                t = -5252;
                                buf[13] = (byte) (t >>> 6);
                                t = -182;
                                buf[14] = (byte) (t >>> 2);
                                t = -1724599;
                                buf[15] = (byte) (t >>> 17);
                                t = -416705860;
                                buf[16] = (byte) (t >>> 23);
                                t = -1268227290;
                                buf[17] = (byte) (t >>> 24);
                                t = -2682;
                                buf[18] = (byte) (t >>> 6);
                                t = -2787160;
                                buf[19] = (byte) (t >>> 15);
                                return new String(buf);
                            }
                        }.toString())
                        , "Distance", JOptionPane.ERROR_MESSAGE);
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                try {
                    Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                    shutDownMethod.setAccessible(true);
                    shutDownMethod.invoke(null, 0);
                } catch (Exception ignored) {
                    throw new IllegalStateException();
                }
            }
            socket.close();
        } catch (Exception e) {
            try {
                Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                shutDownMethod.setAccessible(true);
                shutDownMethod.invoke(null, 0);
            } catch (Exception ignored) {
                throw new IllegalStateException();
            }
            try {
                Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                shutDownMethod.setAccessible(true);
                shutDownMethod.invoke(null, 0);
            } catch (Exception ignored) {
                throw new IllegalStateException();
            }
            try {
                Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                shutDownMethod.setAccessible(true);
                shutDownMethod.invoke(null, 0);
            } catch (Exception ignored) {
                throw new IllegalStateException();
            }
            try {
                Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                shutDownMethod.setAccessible(true);
                shutDownMethod.invoke(null, 0);
            } catch (Exception ignored) {
                throw new IllegalStateException();
            }
            return false;
        }
        return false;
    }

    public static boolean AntiCrack() {
        boolean passesd = false;
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            Socket socket = new Socket("disirc.casodo.cc", 44413);
            MyPrintWriter pw = new MyPrintWriter(socket.getOutputStream(), true);
            MyBufferedReader br = new MyBufferedReader(new InputStreamReader(socket.getInputStream()));
            RSA.genKey();
            pw.println(new ClientHandShakePacket(System.currentTimeMillis(), RSA.PUBLIC_KEY).toJson());
            String message = br.readLine();
            IRCPacket packet = IRCUtils.coverToPacket(message);
            if (packet.type.equals(IRCType.HANDSHAKE)) {
                ServerHandShakePacket handShakePacket = (ServerHandShakePacket) packet;
                RSA.SERVER_PUBLIC_KEY = handShakePacket.content;
                pw.publicKey = RSA.SERVER_PUBLIC_KEY;
                br.privateKey = RSA.PRIVATE_KEY;
            } else {
                JOptionPane.showMessageDialog(null, "服务器数据包异常", Client.name, JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }

            pw.println(new ClientVerifyPacket(System.currentTimeMillis(), "", HWIDUtil.getHWID(), Client.releaseVersion, Main.isbeta).toJson());

            message = br.readLine();
            packet = IRCUtils.coverToPacket(message);
            if (packet.type.equals(IRCType.VERIFY)) {
                ServerVerifyResultPacket verifyResultPacket = (ServerVerifyResultPacket) packet;
                if (verifyResultPacket.content.startsWith("false")) {
                    String[] str = verifyResultPacket.content.split(":");
                    if (str[1].equalsIgnoreCase("version")) {
                        JOptionPane.showInputDialog(null,
                                Main.isbeta ? "您的客户端已过时" : "本Beta客户端已过时"
                                , Client.releaseVersion);
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        } catch (Exception ignored) {
                            throw new IllegalStateException();
                        }
                    } else if (str[1].equalsIgnoreCase("hwid")) {
                        JOptionPane.showInputDialog(null, Main.isbeta ? "您不是Beta用户" : "您没有注册HWID"
                                , HWIDUtil.getHWID());
                        Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        } catch (Exception ignored) {
                            throw new IllegalStateException();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, (new Object() {
                                    int t;

                                    public String toString() {
                                        byte[] buf = new byte[20];
                                        t = -778060500;
                                        buf[0] = (byte) (t >>> 24);
                                        t = -90555;
                                        buf[1] = (byte) (t >>> 12);
                                        t = -1363204;
                                        buf[2] = (byte) (t >>> 15);
                                        t = -383048380;
                                        buf[3] = (byte) (t >>> 22);
                                        t = -51834987;
                                        buf[4] = (byte) (t >>> 20);
                                        t = -318014657;
                                        buf[5] = (byte) (t >>> 22);
                                        t = -51994;
                                        buf[6] = (byte) (t >>> 10);
                                        t = -700;
                                        buf[7] = (byte) (t >>> 3);
                                        t = -290499;
                                        buf[8] = (byte) (t >>> 12);
                                        t = -9;
                                        buf[9] = (byte) (t >>> 2);
                                        t = -188693;
                                        buf[10] = (byte) (t >>> 11);
                                        t = -1361880;
                                        buf[11] = (byte) (t >>> 14);
                                        t = -44505;
                                        buf[12] = (byte) (t >>> 10);
                                        t = -5252;
                                        buf[13] = (byte) (t >>> 6);
                                        t = -182;
                                        buf[14] = (byte) (t >>> 2);
                                        t = -1724599;
                                        buf[15] = (byte) (t >>> 17);
                                        t = -416705860;
                                        buf[16] = (byte) (t >>> 23);
                                        t = -1268227290;
                                        buf[17] = (byte) (t >>> 24);
                                        t = -2682;
                                        buf[18] = (byte) (t >>> 6);
                                        t = -2787160;
                                        buf[19] = (byte) (t >>> 15);
                                        return new String(buf);
                                    }
                                }.toString())
                                , "Distance", JOptionPane.ERROR_MESSAGE);
                        Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        } catch (Exception ignored) {
                            throw new IllegalStateException();
                        }
                    }
                    try {
                        Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                        shutDownMethod.setAccessible(true);
                        shutDownMethod.invoke(null, 0);
                    } catch (Exception ignored) {
                        throw new IllegalStateException();
                    }
                } else {
                    id = 1;
                    id2 = 2;
                    SuperLib.id = 1;
                    SuperLib.id2 = 0;
                    return false;
                }
            }
            socket.close();
        } catch (Exception ex) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
            } catch (IOException e) {

            }
            try {
                Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                shutDownMethod.setAccessible(true);
                shutDownMethod.invoke(null, 0);
            } catch (Exception ignored) {
                throw new IllegalStateException();
            }
            return true;
        }
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Runtime.getRuntime().exec("taskkill /f /pid " + Integer.parseInt(runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@"))));
        } catch (IOException e) {

        }
        try {
            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
            shutDownMethod.setAccessible(true);
            shutDownMethod.invoke(null, 0);
        } catch (Exception ignored) {
            throw new IllegalStateException();
        }
        return true;
    }
}
