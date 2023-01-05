package my.distance.api.events.World;

import my.distance.api.Event;
import net.minecraft.client.audio.ISound;

public class EventSound extends Event {
    public ISound sound;
    public EventSound(ISound iSound){
        sound = iSound;
    }
}
