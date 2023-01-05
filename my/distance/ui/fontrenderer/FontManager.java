package my.distance.ui.fontrenderer;

import my.distance.fastuni.FastUniFontRenderer;
import my.distance.fastuni.FontLoader;
import net.minecraft.client.Minecraft;

public class FontManager {
    public static Minecraft mc = Minecraft.getMinecraft();
    public FastUniFontRenderer Chinese16 = FontLoader.msFont16;
    public FastUniFontRenderer Chinese13 = FontLoader.msFont13;
    public FastUniFontRenderer Chinese14 = FontLoader.msFont14;
    public FastUniFontRenderer Chinese15 = FontLoader.msFont15;
    public FastUniFontRenderer Chinese18 = FontLoader.msFont18;
    public FastUniFontRenderer Chinese20 = FontLoader.msFont20;
    public FastUniFontRenderer Chinese35 = FontLoader.msFont35;


    public FastUniFontRenderer GoogleSans16 = FontLoader.getFont(16, "GoogleSans.ttf",42);
    public FastUniFontRenderer GoogleSans18 = FontLoader.getFont(18, "GoogleSans.ttf",44);
    public FastUniFontRenderer GoogleSans35 = FontLoader.getFont(35, "GoogleSans.ttf",46);
    public FastUniFontRenderer Arial18 = FontLoader.getFont(18, "Arial.ttf",48);

    public FastUniFontRenderer KomikaTitleBold50 = FontLoader.getFont(50, "KomikaTitleBold.ttf",50);


    public FontManager() {

    }

//	public UnicodeFontRenderer getFont(final String s, final float size,  final boolean otf,int count) {
//        UnicodeFontRenderer UnicodeFontRenderer = null;
//        mc.drawSplashScreen(count);
//        try {
//            System.out.println("Start "+ s + " Size:" + size + " Fontload");
//            if (this.fonts.containsKey(s) && this.fonts.get(s).containsKey(size)) {
//                return this.fonts.get(s).get(size);
//            }
//            String s2;
//            if (otf) {
//                s2 = ".otf";
//            }
//            else {
//                s2 = ".ttf";
//            }
//            InputStream is = Minecraft.getMinecraft().getResourceManager()
//                    .getResource(new ResourceLocation("Distance/fonts/"+s+s2)).getInputStream();
//            UnicodeFontRenderer = new UnicodeFontRenderer(Font.createFont(0, is).deriveFont(size), size, -1, -1, false);
//            UnicodeFontRenderer.setUnicodeFlag(true);
//            UnicodeFontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
//            final HashMap<Float, UnicodeFontRenderer> hashMap = new HashMap<Float, UnicodeFontRenderer>();
//            if (this.fonts.containsKey(s)) {
//                hashMap.putAll(this.fonts.get(s));
//            }
//            hashMap.put(size, UnicodeFontRenderer);
//            this.fonts.put(s, hashMap);
//            System.out.println(s + " Size:" + size + " Fontload Done.");
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return UnicodeFontRenderer;
//    }
//    public UnicodeFontRenderer getCFont(final String s, final float size,  final boolean otf) {
//        UnicodeFontRenderer UnicodeFontRenderer = null;
//        try {
//            System.out.println(s + " Fontload");
//
//            if (this.fonts.containsKey(s) && this.fonts.get(s).containsKey(size)) {
//                return this.fonts.get(s).get(size);
//            }
//            final Class<? extends FontManager> class1 = this.getClass();
//            final StringBuilder append = new StringBuilder().append("fonts/" ).append(s);
//            String s2;
//            if (otf) {
//                s2 = ".otf";
//            }
//            else {
//                s2 = ".ttf";
//            }
//            InputStream is = Minecraft.getMinecraft().getResourceManager()
//                    .getResource(new ResourceLocation("Distance/FONT/"+s+s2)).getInputStream();
//            UnicodeFontRenderer = new UnicodeFontRenderer(Font.createFont(0, is).deriveFont(size), size, -1, -1, false);
//            UnicodeFontRenderer.setUnicodeFlag(true);
//            UnicodeFontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
//            final HashMap<Float, UnicodeFontRenderer> hashMap = new HashMap<Float, UnicodeFontRenderer>();
//            if (this.fonts.containsKey(s)) {
//                hashMap.putAll(this.fonts.get(s));
//            }
//            hashMap.put(size, UnicodeFontRenderer);
//            this.fonts.put(s, hashMap);
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        System.out.println(s + " Fontload done.");
//        return UnicodeFontRenderer;
//    }
}
