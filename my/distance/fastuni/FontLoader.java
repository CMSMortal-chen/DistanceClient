package my.distance.fastuni;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class FontLoader {
    public static FastUniFontRenderer msFont10 = getFont(10,"HarmonyOS_Sans_SC_Regular.ttf",14);
    public static FastUniFontRenderer msFont11 = getFont(11,"HarmonyOS_Sans_SC_Regular.ttf",15);
    public static FastUniFontRenderer msFont12 = getFont(12,"HarmonyOS_Sans_SC_Regular.ttf",17);
    public static FastUniFontRenderer msFont13 = getFont(13,"HarmonyOS_Sans_SC_Regular.ttf",18);
    public static FastUniFontRenderer msFont14 = getFont(14,"HarmonyOS_Sans_SC_Regular.ttf",20);
    public static FastUniFontRenderer msFont15 = getFont(15,"HarmonyOS_Sans_SC_Regular.ttf",21);
    public static FastUniFontRenderer msFont16 = getFont(16,"HarmonyOS_Sans_SC_Regular.ttf",23);
    public static FastUniFontRenderer msFont17 = getFont(17,"HarmonyOS_Sans_SC_Regular.ttf",24);
    public static FastUniFontRenderer msFont18 = getFont(18,"HarmonyOS_Sans_SC_Regular.ttf",26);
    public static FastUniFontRenderer msFont19 = getFont(19,"HarmonyOS_Sans_SC_Regular.ttf",27);
    public static FastUniFontRenderer msFont20 = getFont(20,"HarmonyOS_Sans_SC_Regular.ttf",29);
    public static FastUniFontRenderer msFont21 = getFont(21,"HarmonyOS_Sans_SC_Regular.ttf",30);
    public static FastUniFontRenderer msFont22 = getFont(22,"HarmonyOS_Sans_SC_Regular.ttf",32);
    public static FastUniFontRenderer msFont23 = getFont(23,"HarmonyOS_Sans_SC_Regular.ttf",33);
    public static FastUniFontRenderer msFont24 = getFont(24,"HarmonyOS_Sans_SC_Regular.ttf",35);
    public static FastUniFontRenderer msFont25 = getFont(25,"HarmonyOS_Sans_SC_Regular.ttf",37);

    public static FastUniFontRenderer msFont35 = getFont(35,"HarmonyOS_Sans_SC_Regular.ttf",38);
    public static FastUniFontRenderer msFont36 = getFont(36,"HarmonyOS_Sans_SC_Regular.ttf",40);
    public static FastUniFontRenderer msFont72 = getFont(72,"HarmonyOS_Sans_SC_Regular.ttf",41);

    public static FastUniFontRenderer micon15 = getFont(15,"micon.ttf");
    public static FastUniFontRenderer micon30 = getFont(30,"micon.ttf");

    public static void init(){

    }

    public static FastUniFontRenderer getFont(int size, String fontname) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("Distance/fonts/" + fontname)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }

        return new FastUniFontRenderer(font,size,true);
    }
    public static FastUniFontRenderer getFont(int size, String fontname, int prog) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("Distance/fonts/" + fontname)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        Minecraft.getMinecraft().drawSplashScreen(prog);

        return new FastUniFontRenderer(font,size,true);
    }
    public static Font getFonts(int size,String fontname) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("Distance/fonts/" + fontname)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return font;
    }

}

