package com.lbriceno.weatherapp.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lbriceno.weatherapp.entities.City;
import com.lbriceno.weatherapp.entities.CityWeather;
import com.lbriceno.weatherapp.entities.Main;
import com.lbriceno.weatherapp.tools.BSUtils;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by luis_ on 10/1/2016.
 */
public class Api {
    private static Api mInstance = new Api();

    public static Api getInstance() {
        return mInstance;
    }

    private Api() {
    }

    public void getWeather(final Callback<JsonObject> callback) {

        RetrofitService.getInstance().findNearbyPlaces("50.85", "4.35", new Callback<JsonObject>() {
            @Override
            public void success(final JsonObject jsonObject, final Response response) {
                int code = jsonObject.get("cod").getAsInt();

                if (code == 200) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JsonArray list = jsonObject.get("list").getAsJsonArray();
                                Realm.getDefaultInstance().beginTransaction();
                                cleatWeather();
                                for (int i = 0; i < list.size(); i++) {
                                    City city = new Gson().fromJson(list.get(i).getAsJsonObject(), City.class);
                                    CityWeather weather = new Gson().fromJson(list.get(i).getAsJsonObject().toString(), CityWeather.class);
                                    // insert new value
                                    city.getCityWeathers().add(weather);
                                    Realm.getDefaultInstance().copyToRealmOrUpdate(city);
                                    weather.setCity(city);
                                    Realm.getDefaultInstance().copyToRealmOrUpdate(weather);
                                }

                                Realm.getDefaultInstance().commitTransaction();
                                Realm.getDefaultInstance().close();

                                if (callback != null)
                                    callback.success(jsonObject, response);
                            } catch (Exception e) {
                                Log.e(getClass().getName(), e.getMessage(), e);
                            }
                        }
                    }).run();

                } else {
                    if (callback != null)
                        callback.success(jsonObject, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null)
                    callback.failure(error);
            }
        });
    }

    public void cleatWeather() {
        Realm.getDefaultInstance().where(CityWeather.class).findAll().deleteAllFromRealm();
        Realm.getDefaultInstance().where(City.class).findAll().deleteAllFromRealm();
    }

    public void getDailyForecast(final Long cityId, final Callback<JsonObject> callback) {

        RetrofitService.getInstance().dailyForecastForCity(cityId, new Callback<JsonObject>() {

            @Override
            public void success(final JsonObject jsonObject, final Response response) {

                int code = jsonObject.get("cod").getAsInt();

                if (code == 200) {
                    try {
                        final JsonArray list = jsonObject.get("list").getAsJsonArray();

                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {

                            @Override
                            public void execute(Realm realm) {
                                try {
                                    if (Realm.getDefaultInstance().isInTransaction())
                                        Realm.getDefaultInstance().commitTransaction();

                                    Realm.getDefaultInstance().beginTransaction();
                                    City city = Realm.getDefaultInstance().where(City.class).equalTo("id", cityId).findFirst();

                                    for (int i = 0; i < list.size(); i++) {
                                        CityWeather weather = new Gson().fromJson(list.get(i).getAsJsonObject().toString(), CityWeather.class);
                                        weather.setCity(city);
                                        realm.copyToRealm(weather);
                                        weather.setMain(realm.createObject(Main.class));
                                        weather.getMain().setTemp(weather.getTemp().getAverageTemp());
                                        weather.getMain().setTempMax(weather.getTemp().getMax());
                                        weather.getMain().setTempMin(weather.getTemp().getMin());
                                        realm.copyToRealmOrUpdate(weather);
                                        realm.copyToRealmOrUpdate(city);
                                    }

                                    if (callback != null)
                                        callback.success(jsonObject, response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    } finally {

                        Realm.getDefaultInstance().close();
                    }


                } else {
                    if (callback != null)
                        callback.success(jsonObject, response);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null)
                    callback.failure(error);
            }
        });
    }

    public void getForecastForCity(final Long id, final Callback<JsonObject> callback) {

        RetrofitService.getInstance().forecastForCity(id, new Callback<JsonObject>() {

                    @Override
                    public void success(final JsonObject jsonObject, final Response response) {
                        int code = jsonObject.get("cod").getAsInt();

                        if (code == 200) {

                            try {
                                final JsonArray list = jsonObject.get("list").getAsJsonArray();
                                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm) {

                                        if (Realm.getDefaultInstance().isInTransaction())
                                            Realm.getDefaultInstance().commitTransaction();

                                        Realm.getDefaultInstance().beginTransaction();
                                        City city = Realm.getDefaultInstance().where(City.class).equalTo("id", id).findFirst();

                                        Long start = BSUtils.getTodayTime(0, 0, 0).getTime();
                                        Long end = BSUtils.getTodayTime(0, 23, 59).getTime();
                                        for (int i = 0; i < list.size(); i++) {
                                            CityWeather weather = new Gson().fromJson(list.get(i).getAsJsonObject().toString(), CityWeather.class);
                                            weather.setCity(city);

                                            if (start < weather.dt && weather.dt > end)
                                                continue;

                                            realm.copyToRealm(weather);
                                            realm.copyToRealmOrUpdate(city);
                                        }

                                        if (callback != null) {
                                            callback.success(jsonObject, response);
                                        }

                                    }
                                });

                            } catch (Exception e) {
                                Log.e(getClass().getName(), e.getMessage(), e);
                            } finally {

                                Realm.getDefaultInstance().close();
                            }


                        } else {

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (callback != null)
                            callback.failure(error);
                    }
                }

        );
    }

}
