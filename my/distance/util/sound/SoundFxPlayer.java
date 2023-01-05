package my.distance.util.sound;


import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class SoundFxPlayer {
    public void playSound(SoundType st, float volume) {
        new Thread(() -> {
            AudioInputStream as;
            try {
                as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("Distance/sound/" + st.getName()))
                .getInputStream())));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void playRandomKeySound(){
        SoundType[] soundTypes = {SoundType.Key1,SoundType.Key2,SoundType.Key3,SoundType.Key4};
        new SoundFxPlayer().playSound(soundTypes[new Random().nextInt(soundTypes.length)],-10);
    }

    public enum SoundType {
        Enable("enable.wav"),
        Disable("disable.wav"),
        Notification("notifcation1.wav"),
        Startup("startup.wav"),
        ClickGuiOpen("clickguiopen.wav"),
        NowPlayingIn("nowplaying-in.wav"),
        NowPlayingOut("nowplaying-out.wav"),

        Key1("Keys/key-press-1.wav"),
        Key2("Keys/key-press-2.wav"),
        Key3("Keys/key-press-3.wav"),
        Key4("Keys/key-press-4.wav"),
        KeyCap("Keys/key-caps.wav"),
        KeyConfirm("Keys/key-confirm.wav"),
        KeyMovement("Keys/key-movement.wav"),
        KeyDelete("Keys/key-delete.wav");


        final String name;

        SoundType(String fileName) {
            this.name = fileName;
        }

        String getName() {
            return name;
        }
    }
}
