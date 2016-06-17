package com.douncoding.weather;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.internal.Util;

class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.DataHolder> {
    ArrayList<Weather> mDataSet;

    Context context;

    public class DataHolder extends RecyclerView.ViewHolder {
        ImageView mWeatherIcon;

        TextView mMonthText;
        TextView mDateText;
        TextView mDayText;
        TextView mMinTemperature;
        TextView mMaxTemperature;
        TextView mWeatherText;

        public DataHolder(View itemView) {
            super(itemView);

            mWeatherIcon = (ImageView)itemView.findViewById(R.id.weather_icon);
            mMinTemperature = (TextView)itemView.findViewById(R.id.min_temperature_text);
            mMaxTemperature = (TextView)itemView.findViewById(R.id.max_temperature_text);
            mWeatherText = (TextView)itemView.findViewById(R.id.weather_text);

            mMonthText = (TextView)itemView.findViewById(R.id.month_text);
            mDateText = (TextView)itemView.findViewById(R.id.date_text);
            mDayText = (TextView)itemView.findViewById(R.id.day_text);
        }
    }

    public WeekWeatherAdapter() {
        mDataSet = new ArrayList<>();
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.card_week_weather_view, parent, false);

        context = parent.getContext();

        return new DataHolder(rootView);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        Weather weather = mDataSet.get(position);

        holder.mMinTemperature.setText(weather.getMinTemperature());
        holder.mMaxTemperature.setText(weather.getMaxTemperature());

        if (weather.getWeather() != null) {
            holder.mWeatherText.setText(weather.getWeather());
            holder.mWeatherIcon.setImageResource(Utils.weatherStringToIcon(weather.getWeather()));
        }

        // 일자정보 입력
        if (weather.getDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(weather.getDate());

            holder.mMonthText.setText(String.valueOf(c.get(Calendar.MONTH)+1));
            holder.mDateText.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

            int day = c.get(Calendar.DAY_OF_WEEK);
            holder.mDayText.setText("(" + Utils.dayToString(day) + ")");
            if (day == 7 || day == 1) {
                holder.mDayText.setTextColor(Color.RED);
            } else {
                holder.mDayText.setTextColor(
                        ContextCompat.getColor(context, R.color.colorAccent));
            }
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
