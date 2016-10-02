package com.lbriceno.weatherapp.activities.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.lbriceno.weatherapp.R;
import com.lbriceno.weatherapp.adapters.DailyWeatherRecyclerAdapter;
import com.lbriceno.weatherapp.api.Api;
import com.lbriceno.weatherapp.entities.City;
import com.lbriceno.weatherapp.entities.CityWeather;
import com.lbriceno.weatherapp.tools.BSUtils;
import com.lbriceno.weatherapp.tools.Constants;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class WeatherFragment extends Fragment {


    @Bind(R.id.coordinator)
    CoordinatorLayout mCoordinator;

    @Bind(R.id.weather_city_name)
    TextView mCityName;

    @Bind(R.id.weather_average_heat)
    TextView mAverageHeat;

    @Bind(R.id.weather_heat_min_max)
    TextView mMaxMinHeat;

    @Bind(R.id.weather_weather_status)
    TextView mStatus;

    @Bind(R.id.weather_weather_status_desc)
    TextView mStatusDesc;

    @Bind(R.id.weather_icon)
    ImageView mWeatherIcon;

    @Bind(R.id.weather_current_day_recycler)
    RecyclerView mDayRecycler;

    @Bind(R.id.weather_daily_recycler)
    RecyclerView mDailyRecycler;

    private City city;
    private long startQuery;
    private long endQuery;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(City city) {
        WeatherFragment fragment = new WeatherFragment();
        fragment.city = city;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_fragment, container, false);
        ButterKnife.bind(this, v);
        startQuery = BSUtils.getTodayTime(0, 0, 0).getTime();
        endQuery = BSUtils.getTodayTime(0, 23, 59).getTime();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            Long id = savedInstanceState.getLong("city");
            city = Realm.getDefaultInstance().where(City.class).equalTo("id", id).findFirst();
        }

        setData();

        if (Realm.getDefaultInstance().where(CityWeather.class).equalTo("city.id", city.getId()).findAll().size() <= 1) {
            updateWeatherForecast();
        }

        if (Realm.getDefaultInstance().where(CityWeather.class).equalTo("city.id", city.getId()).greaterThan("dt", endQuery).findAll().size() <= 1) {
            updateDaily();
        }

        setCurrentDayRecycler();
        setDailyRecycler();
    }

    private void setDailyRecycler() {

        mDayRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDayRecycler.setAdapter(new DailyWeatherRecyclerAdapter(getActivity(), Realm.getDefaultInstance().where(CityWeather.class).equalTo("city.id", city.getId()).greaterThan("dt", startQuery).lessThan("dt", endQuery).distinct("dt"), true));
        mDayRecycler.setHasFixedSize(true);
    }

    private void setCurrentDayRecycler() {
        RealmResults<CityWeather> weathers = Realm.getDefaultInstance().where(CityWeather.class).equalTo("city.id", city.getId()).greaterThan("dt", endQuery).distinct("dt");
        mDailyRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDailyRecycler.setAdapter(new DailyWeatherRecyclerAdapter(getActivity(), weathers, false));
        mDailyRecycler.setHasFixedSize(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("city", city.getId());
    }

    public void setData() {

        CityWeather todayWeather = Realm.getDefaultInstance().where(CityWeather.class).equalTo("city.id", city.getId()).greaterThan("dt", startQuery).lessThan("dt", endQuery).findFirst();
        double minTemp = todayWeather.getMain().getTempMin().doubleValue();
        double maxTemp = todayWeather.getMain().getTempMax().doubleValue();
        String minMaxTemperature;

        if (minTemp != 0)
            minMaxTemperature = String.format(getString(R.string.degree_format), minTemp) + "/" + (String.format(getString(R.string.degree_format), maxTemp));
        else
            minMaxTemperature = getString(R.string.max_temp) + (String.format(getString(R.string.degree_format), todayWeather.getMain().getTempMax().doubleValue()));

        String averageTemperature = String.format(getString(R.string.degree_format), todayWeather.getMain().getTemp().doubleValue());

        mCityName.setText(city.getName());
        mCityName.invalidate();
        mAverageHeat.setText(averageTemperature);
        mAverageHeat.invalidate();
        mMaxMinHeat.setText(minMaxTemperature);
        mMaxMinHeat.invalidate();

        if (todayWeather.getWeather().get(0) != null) {
            mStatus.setText(todayWeather.getWeather().get(0).getMain());
            mStatusDesc.setText(todayWeather.getWeather().get(0).getDescription());
            Picasso.with(getActivity()).load(Constants.ICONS_ENDPOINT + todayWeather.getWeather().get(0).getIcon()).into(mWeatherIcon);
        }
    }


    public void updateWeatherForecast() {
        Api.getInstance().getForecastForCity(city.getId(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                int code = jsonObject.get("cod").getAsInt();
                if (code == 200) {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setCurrentDayRecycler();

                            }
                        });
                } else {
                    BSUtils.showSnackBar(getActivity(), mCoordinator, jsonObject.get("message").getAsString(), null, null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BSUtils.showSnackBar(getActivity(), mCoordinator, getString(R.string.error_fetching), null, null);
            }
        });
    }

    public void updateDaily() {
        Api.getInstance().getDailyForecast(city.getId(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                int code = jsonObject.get("cod").getAsInt();
                if (code == 200) {

                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDailyRecycler();
                            }
                        });

                } else {
                    BSUtils.showSnackBar(getActivity(), mCoordinator, jsonObject.get("message").getAsString(), null, null);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                BSUtils.showSnackBar(getActivity(), mCoordinator, getString(R.string.error_fetching), null, null);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        city = null;
    }
}
