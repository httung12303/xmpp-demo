package environment;

public class RecEnviroment {
    private float temperature;
    private int humidity;
    private int brightness;

    public RecEnviroment() {
        temperature = Float.MAX_VALUE;
        humidity = Integer.MAX_VALUE;
        brightness = Integer.MAX_VALUE;
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

    public void update(float temperature, int humidity, int brightness) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.brightness = brightness;
    }
}
