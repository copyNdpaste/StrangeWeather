package com.douncoding.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // UI
    RecyclerView mWeekWeatherListView;
    TextView mZoneTV;
    TextView mWeatherTV;
    ImageView mWeatherIV;
    TextView mTemperatureTV;
    TextView mHumidityTV;
    TextView mRainfallTV;
    TextView mWindSpeedTV;

    // Controller
    WeekWeatherAdapter mWeekWeatherAdapter;
    WeatherForecast mWeatherForecast;
    SupportedZone mSupportedZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // UI Component Bind
        mZoneTV = (TextView)findViewById(R.id.zone_text);
        mWeatherTV = (TextView)findViewById(R.id.weather_text);
        mWeatherIV = (ImageView)findViewById(R.id.weather_icon);
        mTemperatureTV = (TextView)findViewById(R.id.temperature_text);
        mHumidityTV = (TextView)findViewById(R.id.humidity_text);
        mRainfallTV = (TextView)findViewById(R.id.rainfall_text);
        mWindSpeedTV = (TextView)findViewById(R.id.wind_speed_text);

        // 중기기상 예보 리스트 뷰 설정
        mWeekWeatherListView = (RecyclerView)findViewById(R.id.week_weather_listview);
        setupWeekWeatherForecastView();

        // 지역검색 버튼 바인드
        findViewById(R.id.loc_search_button).setOnClickListener(this);

        // 기상정보 클래스 초기화
        mWeatherForecast = new WeatherForecast();
        mWeatherForecast.setOnListener(weatherForecastListener);

        mSupportedZone = SupportedZone.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 기상청 날씨정보 내려받기
        mWeatherForecast.execute("춘천");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 리스트 뷰(목록) 초기화
     */
    private void setupWeekWeatherForecastView() {
        if (mWeekWeatherAdapter == null) {
            mWeekWeatherAdapter = new WeekWeatherAdapter();
        }

        mWeekWeatherListView.setAdapter(mWeekWeatherAdapter);
        mWeekWeatherListView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Weather> weathers = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weathers.add(new Weather());
        }
        mWeekWeatherAdapter.refresh(weathers);
        mWeekWeatherAdapter.notifyDataSetChanged();
    }

    /**
     * 기상청 날씨정보 내려받은 후 동작
     */
    WeatherForecast.OnListener weatherForecastListener = new WeatherForecast.OnListener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onFinished() {
            updateTodayWeatherForecastUI(mWeatherForecast.getCurrentWeather());
        }
    };

    /**
     * 오늘 날씨정보 갱신
     */
    private void updateTodayWeatherForecastUI(Weather weather) {
        mZoneTV.setText(mSupportedZone.getFullName(mWeatherForecast.getCityName()));

        mWeatherTV.setText(weather.getWeather());
        mRainfallTV.setText(weather.getRainfall() + "mm");
        mHumidityTV.setText(weather.getHumidity() + "%");
        mWindSpeedTV.setText(weather.getWindSpeed() + "m/s");
        mTemperatureTV.setText(weather.getTemperature());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loc_search_button:
                ZonePickDialog.show(this);
                break;
        }
    }
}
