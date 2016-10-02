package com.lbriceno.weatherapp.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbriceno.weatherapp.R;
import com.lbriceno.weatherapp.entities.City;
import com.lbriceno.weatherapp.entities.CityWeather;
import com.lbriceno.weatherapp.tools.BSUtils;
import com.lbriceno.weatherapp.tools.Constants;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by luis_ on 10/1/2016.
 */
public class WeatherRecyclerAdapter extends RealmRecyclerViewAdapter<City, WeatherRecyclerAdapter.CityViewHolder> {

    private final Context mContext;
    public OnClickListener mListener;

    public WeatherRecyclerAdapter(Context context, RealmResults<City> data, OnClickListener listener) {
        super(context, data, true);
        mContext = context;
        mListener = listener;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.weather_item, parent, false);
        return new CityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        City obj = getData().get(position);
        CityWeather  weather = Realm.getDefaultInstance().where(CityWeather.class).equalTo("city.id",obj.getId()).greaterThan("dt", BSUtils.getTodayTime(0,0,0).getTime()).lessThan("dt",BSUtils.getTodayTime(0,23,59).getTime()).findFirst();

        holder.data = obj;
        holder.title.setText(obj.getName());
        holder.temp.setText(String.format(mContext.getString(R.string.degree_format),weather.getMain().getTemp()));
        Picasso.with(mContext).load(Constants.ICONS_ENDPOINT + weather.getWeather().get(0).getIcon()).into(holder.icon);
    }

    class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.city_name)
        public TextView title;
        @Bind(R.id.city_temp)
        public TextView temp;
        @Bind(R.id.city_weather_icon)
        public ImageView icon;
        @Bind(R.id.city_card_view_wrapper)
        public LinearLayout wrapper;

        public City data;

        public CityViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            if (mListener != null) {
                wrapper.setOnClickListener(this);
                if (Build.VERSION.SDK_INT >= 21)
                    wrapper.setBackground(mContext.getDrawable(R.drawable.button_effect_gray));
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onWeatherClick(data);
        }
    }

    public interface OnClickListener {
        void onWeatherClick(City weather);
    }
}