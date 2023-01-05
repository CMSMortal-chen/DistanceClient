package my.distance.ui.notifications.user;

import my.distance.util.sound.SoundFxPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Notifications {
   private static Notifications instance = new Notifications();
   private List<Notification> notifications = new CopyOnWriteArrayList<>();
   private NotificationRenderer renderer = new NotificationRenderer();

   public List<Notification> getNotifications() {
      return this.notifications;
   }

   private Notifications() {
      instance = this;
   }

   public static Notifications getManager() {
      return instance;
   }

   public void post(String header, String subtext) {
      this.post(header, subtext, 2500L);
   }

   public void post(String header, String subtext, Notifications.Type type) {
      this.post(header, subtext, 2500L, type);
   }

   public void post(String header, String subtext, long displayTime) {
      this.post(header, subtext, displayTime, Notifications.Type.INFO);
   }

   public void post(String header, String subtext, long displayTime, Notifications.Type type) {
      if (type != Type.MUSIC) {
         new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Notification, -5);
      }else {
         new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.NowPlayingIn, -5);
      }
      this.notifications.add(new Notification(header, subtext, displayTime, type));
   }

   public void updateAndRender() {
      if (!this.notifications.isEmpty()) {
         this.renderer.draw(this.notifications);
      }
   }

   public enum Type {
      NOTIFY,
      WARNING,
      INFO,
      MUSIC;
   }
}
