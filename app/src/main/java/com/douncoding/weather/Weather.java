package com.douncoding.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * POJO Class
 * RSS 필드에서 제공하는 모든 값을 처리할 수 있도록 설계한다.
 * (단, 12/6시간 예상 강수량과 적성량 값은 제외)
 */
public class Weather {
    Date time;
    String weather;
    String temperature;
    String humidity;

    int weatherIcon;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatTime = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.KOREAN);
        String timeString = formatTime.format(time);

        return String.format(Locale.getDefault(), "-------------------\n" +
                "시간:%s\n" +
                "날씨:%s\n" +
                "온도:%s\n" +
                "습도:%s\n", timeString, weather, temperature, humidity);
    }
}
