package com.douncoding.weather;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeatherFactory {
    /**
     * 기상청 동네예보 RSS 필드 정의
     */
    public static class TownToken {
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
        // 강수확률
        public static String CAHCE_OF_RAIN = "pop";
        // 강수량 6시간
        public static String RAINFALL_6HOUR ="r06";
    }
    /**
     * 기상청 중기예보 RSS 필드 정의
     */
    public static class MidTermToken {
        // 년월시분
        public static String TIME = "tmEf";
        // 날씨
        public static String WEATHER = "wf";
        // 최저온도
        public static String TEMP_MIN = "tmn";
        // 최고온도
        public static String TEMP_MAX = "tmx";
   }

    public static WeatherFactory INSTANCE = null;

    private WeatherFactory() {}

    public static WeatherFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WeatherFactory();
        }
        return INSTANCE;
    }

    public static Weather create(ForecastType type, Date publish, Element data) {
        Weather weather = new Weather();

        if (type == ForecastType.TOWN) {
            Node dayNode = data.getElementsByTagName(TownToken.DAY).item(0);
            Node hourNode = data.getElementsByTagName(TownToken.HOUR).item(0);

            Calendar c = Calendar.getInstance();
            c.setTime(publish);
            c.set(Calendar.DAY_OF_MONTH, publish.getDate() + Integer.valueOf(dayNode.getTextContent()));
            c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourNode.getTextContent()));
            weather.setDate(c.getTime());

            Node tempNode = data.getElementsByTagName(TownToken.TEMP).item(0);
            weather.setTemperature(tempNode.getTextContent());

            Node humNode = data.getElementsByTagName(TownToken.HUMIDITY).item(0);
            weather.setHumidity(humNode.getTextContent());

            Node weatherNode = data.getElementsByTagName(TownToken.WEATHER).item(0);
            weather.setWeather(weatherNode.getTextContent());

            Node windNode = data.getElementsByTagName(TownToken.WIND).item(0);
            weather.setWindSpeed(windNode.getTextContent());

            Node rainNode = data.getElementsByTagName(TownToken.RAINFALL_6HOUR).item(0);
            weather.setRainfall(String.valueOf(Float.valueOf(rainNode.getTextContent())/6.0).substring(0,3));

            Node rainChanceNode = data.getElementsByTagName(TownToken.CAHCE_OF_RAIN).item(0);
            weather.setChanceOfrain(rainChanceNode.getTextContent());
        } else if (type == ForecastType.MID_TERM){
            Node timeNode = data.getElementsByTagName(MidTermToken.TIME).item(0);
            weather.setDate(Utils.convertStringToDateType2(timeNode.getTextContent()));

            Node maxTempNode = data.getElementsByTagName(MidTermToken.TEMP_MAX).item(0);
            weather.setMaxTemperature(maxTempNode.getTextContent());

            Node minTempNode = data.getElementsByTagName(MidTermToken.TEMP_MIN).item(0);
            weather.setMinTemperature(minTempNode.getTextContent());

            Node weatherNode = data.getElementsByTagName(MidTermToken.WEATHER).item(0);
            weather.setWeather(weatherNode.getTextContent());
        }

        return weather;
    }
}
