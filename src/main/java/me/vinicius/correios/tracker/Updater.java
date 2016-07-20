package me.vinicius.correios.tracker;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;
import javafx.util.Duration;
import me.vinicius.correios.api.Event;
import me.vinicius.correios.api.Rastreamento;

@SuppressWarnings("unused")
public class Updater implements Runnable {

    private Map<String, String> eventMap;//Last Event Change Map
    private int updateTime;
    private boolean cancelled;

    public Updater(int updateTime) {
        eventMap = new HashMap<>();
        this.updateTime = updateTime;
        cancelled = false;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isCancelled() {

        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Map<String, String> getEventMap() {
        return eventMap;
    }

    public void setCodesAndUpdate(String codes) {

        if (codes != null) {
            new Thread(() -> { //Run in a Thread so we can match the regex along the Controller
                Matcher m = Pattern.compile("[A-Z]{2}\\d{9}[A-Z]{1,3}").matcher(codes);

                while (m.find()) {
                    Rastreamento res = new Rastreamento(m.group());
                    try {
                        res.track();
                    } catch (IOException e) {
                        //This will only happen if there`s no connection
                    }
                    Event events[] = res.getEvents();
                    if (events != null) {
                        if (events.length > 0) {
                            eventMap.put(m.group(), events[events.length - 1].getData());
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void run() {
        while (!cancelled) {
            try {
                Thread.sleep(updateTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("BEGIN Update");
            for (String s : eventMap.keySet()) {
                Rastreamento res = new Rastreamento(s);
                try {
                    res.track();
                } catch (IOException e) {
                    //
                }
                Event events[] = res.getEvents();
                if (events != null) {
                    if (events.length > 0) {
                        if (!Objects.equals(events[events.length - 1].getData(), eventMap.get(s))) {

                            //Notification
                            String title = "Rastreamento de: " + s + " em " +
                                    events[events.length - 1];
                            String message = events[events.length - 1].getMovement();
                            Notifications notification = Notifications.SUCCESS;
                            TrayNotification tray = new TrayNotification(title,
                                    message, notification);
                            tray.setImage(new Image("http://globalestudio.com.br/loja/im" +
                                    "age/cache/catalog/PRODUTOS/modulosmodulo-de-frete-c" +
                                    "orreios-opencart-54-746x746.jpg"));

                            tray.setAnimation(Animations.FADE);
                            tray.showAndDismiss(Duration.seconds(3));
                        }
                    } else {
                        System.out.println("Update found for: " + s);
                    }
                }
            }
        }
    }
}
