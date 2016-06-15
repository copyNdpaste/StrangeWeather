package com.douncoding.weather;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherForecast {
    public static final String TAG = WeatherForecast.class.getSimpleName();

    private final String townUrl = "http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=";
    private final String termUrl = "http://web.kma.go.kr/weather/forecast/mid-term-rss3.jsp?stnId=";
    private String townCode;
    private String termCode;
    private String cityName;

    /**
     * 날짜별 {@link Weather} 객체 리스트를 제공
     * '기상청' 에서 제공하는 날씨정보는 일수마다 제공되는 시간별 예보의 차이가 있다.
     * 당일~모레까지는 3시간 단위의 기상정보를 제공하며, 이후는 약6일 정도는 오전/오후로 제공되고, 이후는
     * 하루 단위로 시간을 제공한다. 즉, HashMap 을 이용하여 처리하는 것이 적절하다고 판단된다.
     */
    private HashMap<Integer, HashMap<Integer, Weather>> mWeatherPerDateMap;

    // 기상청 동네예보 발생 시간
    private Date mTownPublishDate;
    // 기상청 중기예보 발생 시간
    private Date mTermPublishDate;

    // 로딩상태 표현
    private boolean isRunning = false;

    private OnListener onListener;

    public WeatherForecast() {
        mWeatherPerDateMap = new HashMap<>();
    }

    /**
     * @param cityName 지역(시) 이름
     */
    public void execute(String cityName){
        if (!isRunning) {
            SupportedZone supportedZone = SupportedZone.getInstance();
            this.termCode = supportedZone.getDoCodeFromSi(cityName);
            this.townCode = supportedZone.getSiCode(cityName);
            this.cityName = cityName;

            Log.i(TAG, String.format("지역:%s 동네예보:%s 중기예보:%s 기상예보 워커 실행",
                    cityName, townCode, termCode));
            // 예외처리
            if (termCode == null || termCode == null) {
                throw new NullPointerException("잘못된 지역 정보를 입력하였습니다.");
            }
            new Worker().execute();
        } else {
            Log.w(TAG, "이미 로딩 중 입니다.");
        }
    }

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    public interface OnListener {
        void onStarted();
        void onFinished();
    }

    private void errorHandle() {
        if (mWeatherPerDateMap.size() == 0 || isRunning) {
            throw new RuntimeException("정보를 조회할 수 없습니다: 엔트리수:"
                    + mWeatherPerDateMap.size() + " 로딩중:"+isRunning );
        }
    }

    // ===================================================================================
    // 공개 API 시작점
    // ===================================================================================

    public String getCityName() {
        return this.cityName;
    }

    /**
     * 가장 가까운 시간의 오늘 날씨 정보
     */
    public Weather getCurrentWeather() {
        errorHandle();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);

        HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day);
        SortedSet<Integer> keys = new TreeSet<>(map.keySet());
        return map.get(keys.first());
    }

    /**
     * 가장 가까운 시간(0시)의 내일 날씨 정보
     */
    public Weather getTomorrowWeather() {
        errorHandle();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH)+1;

        HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day);
        SortedSet<Integer> keys = new TreeSet<>(map.keySet());
        return map.get(keys.first());
    }

    /**
     * 가장 가까운 시간(0시)의 모레 날씨 정보
     * @return
     */
    public Weather getAfterTomorrowWeather() {
        errorHandle();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH)+2;

        HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day);
        SortedSet<Integer> keys = new TreeSet<>(map.keySet());
        return map.get(keys.first());
    }

    public List<Weather> getWeatherListWithThreeHour() {
        return null;
    }

    public List<Weather> getMidTermWeather() {
        return null;
    }

    // ===================================================================================
    // 공개 API 종료점
    // ===================================================================================

    /**
     * 날씨예보 엔트리 추가
     * @param weather 추가할 객체
     */
    private void addWeather(Weather weather) {
        HashMap<Integer, Weather> hourMap = mWeatherPerDateMap.get(weather.getForecastDay());
        if (hourMap == null) {
            hourMap = new HashMap<>();
        }

        hourMap.put(weather.getForecastHour(), weather);

        mWeatherPerDateMap.put(weather.getForecastDay(), hourMap);

        Log.i(TAG, String.format(Locale.getDefault(), "로컬캐시 - 일:%d 시:%d 기상예보 추가:",
                weather.getForecastDay(), weather.getForecastHour()) + weather.toString());
    }

    class Worker extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isRunning = true;
            Log.i(TAG, "----------------------- 기상예보 데이터 로딩 시작 ---------------------------");

            // 로딩시작 상태 알림
            onListener.onStarted();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            isRunning = false;
            Log.i(TAG, "----------------------- 기상예보 데이터 로딩 완료 ---------------------------");

            // 로딩완료 상태 알림
            onListener.onFinished();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // RSS 파싱을 위한 객체 생성
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                // 동네예보 파싱
                Document townDocument = documentBuilder.parse(townUrl + townCode);
                townDocument.getDocumentElement().normalize();
                parseToCacheFromTownRSS(townDocument);

                // 중기예보 파싱
                Document termDocument = documentBuilder.parse(termUrl + termCode);
                termDocument.getDocumentElement().normalize();
                parseToCacheFromMidTermRSS(termDocument);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 동네예보 RSS 파싱
         * 동네예보는 설정된 법정도 코드의 당일 ~ 모레까지의 시간별 기상 예보한다.
         * 동네예보는 '리'단위 까지 구분할 수 있지만, 프로젝트의 특성상 시단위로 제한한다.
         */
        private void parseToCacheFromTownRSS(Document document) {
            // 'tm' 필드 파싱
            Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
            mTownPublishDate = Utils.convertStringToDateType1(publishTime.getTextContent());

            // 'data' 필드 파싱
            NodeList nodeList = document.getElementsByTagName("data");
            for (int i = 0; i < nodeList.getLength(); i++){
                // 기상예보 객체 생성 및 내부캐시 추가
                Element element = (Element)nodeList.item(i);
                addWeather(WeatherFactory.create(ForecastType.TOWN, mTownPublishDate, element));
            }
        }

        private void parseToCacheFromMidTermRSS(Document document) {
            // 중기예보 - 발행시간 파싱
            Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
            mTermPublishDate = Utils.convertStringToDateType1(publishTime.getTextContent());

            // 중기예보 - 지역별 정보 파싱
            NodeList nodeList = document.getElementsByTagName("location");
            for (int i = 0; i < nodeList.getLength(); i++){
                Element element = (Element)nodeList.item(i);
                //'시' 정보
                String city = element.getElementsByTagName("city").item(0).getTextContent();
                if (cityName.equals(city)) {
                    NodeList dataNodeList = element.getElementsByTagName("data");
                    for (int j = 0; j < dataNodeList.getLength(); j++) {
                        Element dataElmt = (Element)dataNodeList.item(j);
                        addWeather(WeatherFactory.create(
                                ForecastType.MID_TERM, mTermPublishDate, dataElmt));
                    }
                }
            }
        }
    }
}
