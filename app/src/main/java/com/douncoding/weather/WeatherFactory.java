package com.douncoding.weather;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Datacenter on 2016-06-13.
 */
public class WeatherFactory {
    public static class Token {
        // 시간
        public static String HOUR = "hour";
        // 오늘 부터 경과일
        public static String DAY = "day";
        // 온도
        public static String TEMP = "temp";
        // 최고온도
        public static String TEMP_MAX = "tmx";
        // 최저온도
        public static String TEMP_MIN = "tmn";
        // 하늘상태 코드
        public static String SKY_STATE = "sky";
        // 강수코드
        public static String PRECIPITATION_STATE = "pty";
        // 날씨(한국어)
        public static String WEATHER = "wfKor";
        // 풍속
        public static String WIND = "ws";
        // 풍향(한국어)
        public static String WIND_DIRECTION = "wdKor";
        // 습도
        public static String HUMIDITY = "reh";
    }

    public static WeatherFactory INSTANCE = null;

    private WeatherFactory() {}

    public static WeatherFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WeatherFactory();
        }
        return INSTANCE;
    }

    public static Weather create(Element publish, Element data) {
        Weather weather = new Weather();

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            Date date = format.parse(publish.getTextContent());

            Node dayNode = data.getElementsByTagName(Token.DAY).item(0);
            Node hourNode = data.getElementsByTagName(Token.HOUR).item(0);

            date.setDate(date.getDate() + Integer.valueOf(dayNode.getTextContent()));
            date.setHours(Integer.valueOf(hourNode.getTextContent()));
            weather.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Node tempNode = data.getElementsByTagName(Token.TEMP).item(0);
        weather.setTemperature(tempNode.getTextContent());

        Node humNode = data.getElementsByTagName(Token.HUMIDITY).item(0);
        weather.setHumidity(humNode.getTextContent());

        Node weatherNode = data.getElementsByTagName(Token.WEATHER).item(0);
        weather.setWeather(weatherNode.getTextContent());

        return weather;
    }
}
