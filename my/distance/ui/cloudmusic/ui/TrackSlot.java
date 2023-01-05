package my.distance.ui.cloudmusic.ui;

import my.distance.ui.cloudmusic.MusicManager;
import my.distance.ui.cloudmusic.utils.Track;
import my.distance.util.render.RenderUtil;
import my.distance.fastuni.FontLoader;

import java.awt.*;

public class TrackSlot {

    public Track track;
    public float x;
    public float y;

    public TrackSlot(Track t) {
        this.track = t;
    }

    public void render(float a, float b, int mouseX, int mouseY) {
        this.x = a;
        this.y = b;

        RenderUtil.drawRoundedRect(x, y, x + 137, y + 20, 2, new Color(230,230,230).getRGB());

        FontLoader.msFont16.drawString(track.name, x + 2, y + 3, Color.BLACK.getRGB());
        FontLoader.msFont13.drawString(track.artists, x + 2, y + 12, Color.BLACK.getRGB());

        //RenderUtil.drawRoundedRect(x + 122, y, x + 137, y + 20, 2, new Color(230,230,230).getRGB());
        //RenderUtil.drawGradientSideways(x + 100, y, x + 124, y + 20, 0x00818181, 0xff34373c);

        FontLoader.micon15.drawString("J", x + 125.5f, y + 7f, Color.BLACK.getRGB());

        //RenderUtil.drawOutlinedRect(x + 125, y + 5, x + 135, y + 15, .5f, Color.RED.getRGB());
    }

    public void click(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovering(mouseX, mouseY, x + 125, y + 5, x + 135, y + 15) && mouseButton == 0) {
            MusicManager.INSTANCE.play(track);
        }
    }
}
