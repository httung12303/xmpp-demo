package environment;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        temperature = rand.nextFloat() * 45 -5;
        humidity = rand.nextInt(80);
        brightness = rand.nextInt(5000);
    }
    public void update(float temperature, int humidity, int brightness) {
        changeTemperature(temperature);
        changeHumidity(humidity);
        changeBrightness(humidity);
    }
    public void changeTemperature(float temperature) {
        float changeRate = Math.min(1.5F, Math.abs(this.temperature - temperature));
        this.temperature += (this.temperature > temperature ? -1 : 1) * changeRate;
    }
    public void changeHumidity(int humidity) {
        int changeRate = Math.min(2, Math.abs(this.humidity - humidity));
        this.humidity += (this.humidity > humidity ? -1 : 1) * changeRate;
    }
    public void changeBrightness(int brightness) {
        int changeRate = Math.min(100, Math.abs(this.brightness - brightness));
        this.brightness += (this.brightness > brightness ? -1 : 1) * changeRate;
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

    public String getTime() {
        LocalTime now = LocalTime.now();
        LocalTime result = now.plusHours(addedTime.getHour()).plusMinutes(addedTime.getMinute()).plusSeconds(addedTime.getSecond());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return result.format(formatter);
    }
}
