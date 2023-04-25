package xmpp.client;

import java.time.LocalTime;
import java.util.Random;

public class Environment {
    private LocalTime addedTime;
    private float temperature;
    private int humidity;
    private int brightness;
    public Environment() {
        Random rand = new Random();
        int hour = rand.nextInt(24);
        int min = rand.nextInt(60);
        int sec = rand.nextInt(60);
        addedTime = LocalTime.of(hour, min, sec);
        temperature = rand.nextFloat(-5, 40);
        humidity = rand.nextInt(0, 80);
        brightness = rand.nextInt(0, 800);
    }
    public void changeTemperature(boolean increase) {
        temperature += (increase ? 1 : -1) * 1.5;
    }
    public void changeHumidity(boolean increase) {
        humidity += (increase ? 1 : -1) * 2;
    }
    public void changeBrightness(boolean increase) {
        brightness += (increase ? 1 : -1) * 20;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getBrightness() {
        return brightness;
    }

    public LocalTime getTime() {
        LocalTime now = LocalTime.now();
        LocalTime result = now.plusHours(addedTime.getHour()).plusMinutes(addedTime.getMinute()).plusSeconds(addedTime.getSecond());
        return result;
    }
}
