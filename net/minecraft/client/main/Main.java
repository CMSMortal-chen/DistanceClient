package net.minecraft.client.main;

import LemonObfAnnotation.ObfuscationClass;
import cms.mortalchen.distance.irc.utils.IRCUtils;
import cms.mortalchen.distance.irc.utils.packets.clientside.ClientDisconnetPacket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import my.distance.api.verify.SHWID;
import my.distance.module.modules.world.IRC;
import my.distance.ui.BackGroundRenderer;
import my.distance.ui.cloudmusic.MusicManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import javax.swing.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

@ObfuscationClass
public class Main {
    public static boolean isbeta = false;
    public static boolean loaded = false;

    public static boolean noverify = false;

    public static void main(String[] p_main_0_) {
//        List<String> BAD_INPUT_FLAGS = new ArrayList<>();
//        byte[] EMPTY_CLASS_BYTES = {
//                -54, -2, -70, -66, 0, 0, 0, 49, 0, 5, 1, 0, 34, 115, 117, 110,
//                47, 105, 110, 115, 116, 114, 117, 109, 101, 110, 116, 47, 73,
//                110, 115, 116, 114, 117, 109, 101, 110, 116, 97, 116, 105, 111,
//                110, 73, 109, 112, 108, 7, 0, 1, 1, 0, 16, 106, 97, 118, 97, 47,
//                108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 7, 0, 3, 0, 1,
//                0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0
//        };
//        BAD_INPUT_FLAGS.add("-javaagent");
//        BAD_INPUT_FLAGS.add("-agentlib");
//
//        Optional<String> inputFlag = ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
//                .filter(input -> BAD_INPUT_FLAGS.stream().anyMatch(input::contains))
//                .findFirst();
//
//        // if there's a bad input flag present in the vm options
//        // then InstrumentationImpl will already have been loaded
//        if (inputFlag.isPresent()) {
//            JOptionPane.showMessageDialog(null, String.format("Bad VM option \"%s\"", inputFlag.get()), "Java", JOptionPane.ERROR_MESSAGE);
//            System.exit(-10);
//            throw new IllegalArgumentException(String.format("Bad VM option \"%s\"", inputFlag.get()));
//        }
//
//        Unsafe unsafe = null;
//        try {
//            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
//            unsafeField.setAccessible(true);
//            unsafe = (Unsafe) unsafeField.get(null);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//            System.exit(-4);
//        }
//        unsafe.defineClass("sun.instrument.InstrumentationImpl", EMPTY_CLASS_BYTES, 0, EMPTY_CLASS_BYTES.length, null, null);
//        try {
//            Class.forName("sun.instrument.InstrumentationImpl");
//        } catch (ClassNotFoundException ignored) {
//
//        }
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

        List<String> arguments = runtimeMxBean.getInputArguments();
        for (String str : arguments) {
            if (str.contains("Xverify:none")) {
                noverify = true;
                break;
            }
        }
        if (!noverify) {
            JOptionPane.showInputDialog(null, "未检测到JVM参数!建议您添加JVM参数", "-noverify");
        }

        BackGroundRenderer.id = 1;
        BackGroundRenderer.id2 = 0;

        SHWID.verify();
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> optionspec19 = optionparser.nonOptions();
        OptionSpec<String> optionspec20 = optionparser.accepts("auto").withRequiredArg();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List<String> list = optionset.valuesOf(optionspec19);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = optionset.valueOf(optionspec5);
        Proxy proxy = Proxy.NO_PROXY;
        if (s != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, optionset.valueOf(optionspec6).intValue()));
            } catch (Exception var46) {
            }
        }

        final String s1 = optionset.valueOf(optionspec7);
        final String s2 = optionset.valueOf(optionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = optionset.valueOf(optionspec13).intValue();
        int j = optionset.valueOf(optionspec14).intValue();
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = optionset.valueOf(optionspec12);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap propertymap = gson.fromJson(optionset.valueOf(optionspec15), PropertyMap.class);
        PropertyMap propertymap1 = gson.fromJson(optionset.valueOf(optionspec16), PropertyMap.class);
        File file1 = optionset.valueOf(optionspec2);
        File file2 = optionset.has(optionspec3) ? optionset.valueOf(optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has(optionspec4) ? optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
        String s4 = optionset.has(optionspec10) ? optionspec10.value(optionset) : optionspec9.value(optionset);
        String s5 = optionset.has(optionspec17) ? optionspec17.value(optionset) : null;
        String s6 = optionset.valueOf(optionspec);
        Integer integer = optionset.valueOf(optionspec1);
        Session session = new Session(optionspec9.value(optionset), s4, optionspec11.value(optionset), optionspec18.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer.intValue()));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                MusicManager.clean();
                Minecraft.stopIntegratedServer();
                IRC.sendRawIRCMessage(IRCUtils.toJson(new ClientDisconnetPacket(System.currentTimeMillis(), "")));
            }
        });
        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameconfiguration)).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
