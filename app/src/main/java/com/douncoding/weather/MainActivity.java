package com.douncoding.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mWeekWeatherListView;
    Button mLocationSearch;

    WeekWeatherAdapter mWeekWeatherAdapter;

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
        mWeekWeatherListView = (RecyclerView)findViewById(R.id.week_weather_listview);
        mLocationSearch = (Button)findViewById(R.id.loc_search_button);

        setupWeekWeatherForecastView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new WeatherForecast("1159068000").execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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


    class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.DataHolder> {
        ArrayList<Weather> mDataSet;

        public class DataHolder extends RecyclerView.ViewHolder {
            ImageView mWeatherIcon;
            TextView mTemperature;
            TextView mWeatherText;
            public DataHolder(View itemView) {
                super(itemView);
                mWeatherIcon = (ImageView)itemView.findViewById(R.id.weather_icon);
                mTemperature = (TextView)itemView.findViewById(R.id.temperature);
                mWeatherText = (TextView)itemView.findViewById(R.id.weather_text);
            }
        }

        public WeekWeatherAdapter() {
            mDataSet = new ArrayList<>();
        }

        @Override
        public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View rootView = inflater.inflate(R.layout.card_week_weather_view, parent, false);

            return new DataHolder(rootView);
        }

        @Override
        public void onBindViewHolder(DataHolder holder, int position) {
            Weather weather = mDataSet.get(position);

            if (weather.getTemperature() != null) {
                holder.mTemperature.setText(weather.getTemperature());
            }

            if (weather.getWeather() != null) {
                holder.mWeatherText.setText(weather.getWeather());
            }
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public void refresh(List<Weather> items) {
            mDataSet.clear();
            mDataSet.addAll(items);
            notifyDataSetChanged();
        }
    }
}
