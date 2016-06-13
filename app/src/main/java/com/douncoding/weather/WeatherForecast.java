package com.douncoding.weather;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherForecast {
    public static final String TAG = WeatherForecast.class.getSimpleName();

    private final String townUrl = "http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=";
    private final String termUrl = "http://web.kma.go.kr/weather/forecast/mid-term-rss3.jsp?stnId=108";
    private final String zone;

    /**
     * 날짜별 {@link Weather} 객체 리스트를 제공
     * '기상청' 에서 제공하는 날씨정보는 일수마다 제공되는 시간별 예보의 차이가 있다.
     * 당일~모레까지는 3시간 단위의 기상정보를 제공하며, 이후는 약6일 정도는 오전/오후로 제공되고, 이후는
     * 하루 단위로 시간을 제공한다. 즉, HashMap 을 이용하여 처리하는 것이 적절하다고 판단된다.
     */
    HashMap<Date, ArrayList<Weather>> mWeatherList;

    /**
     *
     * @param zone 법정도 코드
     */
    public WeatherForecast(String zone) {
        this.zone = zone;
        mWeatherList = new HashMap<>();
    }

    public void execute(){
        new TownWorker().execute();
        new TermWorker().execute();
    }

    /**
     * 동네예보 RSS 파싱
     * 동네예보는 설정된 법정도 코드의 당일 ~ 모레까지의 시간별 기상 예보한다.
     * 동네예보는 '리'단위 까지 구분할 수 있지만, 프로젝트의 특성상 시단위로 제한한다.
     */
    class TownWorker extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(townUrl + zone);
                document.getDocumentElement().normalize();

                Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
                Log.i(TAG, "발생시간: " + publishTime.getTextContent());

                NodeList nodeList = document.getElementsByTagName("data");
                for (int i = 0; i < nodeList.getLength(); i++){
                    Element element = (Element)nodeList.item(i);
                    //mWeatherList.add(WeatherFactory.create(publishTime, element));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*
            for (Weather weather : mWeatherList) {
                Log.w(TAG, weather.toString());
            }
            */
        }
    }

    class TermWorker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(termUrl);
                document.getDocumentElement().normalize();

                Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
                Log.i(TAG, "발생시간: " + publishTime.getTextContent());

                NodeList nodeList = document.getElementsByTagName("location");
                for (int i = 0; i < nodeList.getLength(); i++){
                    Element element = (Element)nodeList.item(i);
                    element.getElementsByTagName("province").item(0).getTextContent();
                    Log.e(TAG, "지역:" + element.getElementsByTagName("city").item(0).getTextContent());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
