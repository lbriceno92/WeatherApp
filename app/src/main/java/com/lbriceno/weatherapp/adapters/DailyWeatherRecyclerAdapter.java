package com.lbriceno.weatherapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbriceno.weatherapp.R;
import com.lbriceno.weatherapp.entities.CityWeather;
import com.lbriceno.weatherapp.tools.BSUtils;
import com.lbriceno.weatherapp.tools.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by luis_ on 10/1/2016.
 */
public class DailyWeatherRecyclerAdapter extends RealmRecyclerViewAdapter<CityWeather, DailyWeatherRecyclerAdapter.WeatherViewHolder> {

    private final Context mContext;
    private final boolean isToday;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

    public DailyWeatherRecyclerAdapter(Context context, RealmResults<CityWeather> data, boolean isToday) {
        super(context, data, true);
        mContext = context;
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.isToday = isToday;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.daily_weather_item, parent, false);
        return new WeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        CityWeather obj = getData().get(position);
        Date date = BSUtils.getDateByUTCTimestamp(obj.dt);
        Double temp;

        if (obj.getMain() == null)
            temp = obj.getTemp().getDay();
        else
            temp = obj.getMain().getTemp();

        if (isToday) {
            holder.date.setText(mContext.getString(R.string.around));
            holder.day.setText(timeFormat.format(date));
        } else {
            holder.date.setText(dateFormat.format(date));
            holder.day.setText(dayFormat.format(date));

        }
        holder.temp.setText(String.format(mContext.getString(R.string.degree_format), temp));
        Picasso.with(mContext).load(Constants.ICONS_ENDPOINT + obj.getWeather().get(0).getIcon()).into(holder.icon);
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.daily_date)
        public TextView date;
        @Bind(R.id.daily_day)
        public TextView day;
        @Bind(R.id.daily_temp)
        public TextView temp;
        @Bind(R.id.daily_icon)
        public ImageView icon;

        public WeatherViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}