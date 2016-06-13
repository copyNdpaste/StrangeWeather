package com.douncoding.weather;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherForecast {
    public static final String TAG = WeatherForecast.class.getSimpleName();

    private final String baseUrl = "http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=";
    private final String zone;

    ArrayList<Weather>  mWeatherList;

    public WeatherForecast(String zone) {
        this.zone = zone;
        mWeatherList = new ArrayList<>();
    }

    public void execute(){
        new Worker().execute();
    }

    class Worker extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "날씨정보 가져오기 시작");
            mWeatherList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(baseUrl + zone);
                document.getDocumentElement().normalize();

                Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
                Log.i(TAG, "발생시간: " + publishTime.getTextContent());

                NodeList nodeList = document.getElementsByTagName("data");
                for (int i = 0; i < nodeList.getLength(); i++){
                    Element element = (Element)nodeList.item(i);
                    mWeatherList.add(WeatherFactory.create(publishTime, element));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "날씨정보 가져오기 완료: " + mWeatherList.size() + "개 정보 로딩");
            for (Weather weather : mWeatherList) {
                Log.w(TAG, weather.toString());
            }
        }
    }
}
