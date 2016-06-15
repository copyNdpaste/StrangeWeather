package com.douncoding.weather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.DataHolder> {
    ArrayList<Weather> mDataSet;

    public class DataHolder extends RecyclerView.ViewHolder {
        ImageView mWeatherIcon;
        TextView mTemperature;
        TextView mWeatherText;
        public DataHolder(View itemView) {
            super(itemView);
            mWeatherIcon = (ImageView)itemView.findViewById(R.id.weather_icon);
            mTemperature = (TextView)itemView.findViewById(R.id.temperature_text);
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
