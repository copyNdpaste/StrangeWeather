package com.douncoding.weather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // UI
    View mCurrentWeatherView;
    RecyclerView mWeekWeatherListView;
    TextView mUpdateTV;
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
        mUpdateTV = (TextView)findViewById(R.id.update_time_text);
        mZoneTV = (TextView)findViewById(R.id.zone_text);
        mWeatherTV = (TextView)findViewById(R.id.weather_text);
        mWeatherIV = (ImageView)findViewById(R.id.weather_icon);
        mTemperatureTV = (TextView)findViewById(R.id.temperature_text);
        mHumidityTV = (TextView)findViewById(R.id.humidity_text);
        mRainfallTV = (TextView)findViewById(R.id.rainfall_text);
        mWindSpeedTV = (TextView)findViewById(R.id.wind_speed_text);
        mCurrentWeatherView = (View)findViewById(R.id.current_weather_panel);

        // 중기기상 예보 리스트 뷰 설정
        mWeekWeatherListView = (RecyclerView)findViewById(R.id.week_weather_listview);
        setupWeekWeatherForecastView();

        // 지역검색 버튼 바인드
        findViewById(R.id.loc_search_button).setOnClickListener(this);
        // 갱신 버튼 바인트
        findViewById(R.id.refresh).setOnClickListener(this);

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
        mWeekWeatherAdapter.notifyDataSetChanged();
    }

    /**
     * 기상청 날씨정보 내려받은 후 동작
     */
    WeatherForecast.OnListener weatherForecastListener = new WeatherForecast.OnListener() {
        ProgressDialog progressDialog;

        @Override
        public void onStarted() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("RSS 읽는중..");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar);
            progressDialog.show();
        }

        @Override
        public void onFinished() {
            mUpdateTV.setText(Utils.currentStringTime());

            updateTodayWeatherForecastUI(mWeatherForecast.getCurrentWeather());

            updateFutureWeatherForecastUI(mWeatherForecast.getFutureWeathersOfZero());

            progressDialog.dismiss();
        }
    };

    /**
     * 오늘 날씨정보 갱신
     */
    private void updateTodayWeatherForecastUI(Weather weather) {
        mZoneTV.setText(mSupportedZone.getFullName(mWeatherForecast.getCityName()));
        mWeatherTV.setText(weather.getWeather());
        mWeatherIV.setImageResource(Utils.weatherStringToIcon(weather.getWeather()));

        mRainfallTV.setText(weather.getRainfall() + "mm");
        mHumidityTV.setText(weather.getHumidity() + "%");
        mWindSpeedTV.setText(weather.getWindSpeed() + "m/s");
        mTemperatureTV.setText(weather.getTemperature());
    }

    /**
     * 기상예보: 오늘을 제외한 날씨정보 갱신
     * @param weatherList
     */
    private void updateFutureWeatherForecastUI(List<Weather> weatherList) {
        mWeekWeatherAdapter.refresh(weatherList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loc_search_button:
                ZonePickDialog.show(this, new ZonePickDialog.OnListener() {
                    @Override
                    public void onClick(String name) {
                        mWeatherForecast.execute(name);
                    }
                });
                break;
            case R.id.refresh:
                try {
                    mWeatherForecast.execute(null);
                } catch (NullPointerException e) {
                    // 기본값이 춘천
                    mWeatherForecast.execute("춘천");
                }
                break;
        }
    }
}
