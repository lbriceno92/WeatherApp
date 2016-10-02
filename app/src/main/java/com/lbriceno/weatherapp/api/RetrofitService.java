package com.lbriceno.weatherapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;
import com.lbriceno.weatherapp.base.BaseApplication;
import com.lbriceno.weatherapp.tools.Constants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Query;


public class RetrofitService {

    private final static String TAG = "RetrofitService";

    public static RetrofitEndPoints mInstance;

    public static RetrofitEndPoints getInstance() {
        if (mInstance == null)
            mInstance = createInstance();
        return mInstance;
    }

    private static RetrofitEndPoints createInstance() {
        RetrofitEndPoints api = null;
        String url;

        try {
            url = Constants.ENDPOINT;
            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(5, TimeUnit.MINUTES);
            okHttpClient.setConnectTimeout(5, TimeUnit.MINUTES);

            RestAdapter res = new RestAdapter.Builder()
                    .setEndpoint(url)
                    .setClient(new OkClient(okHttpClient))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(new RequestInterceptor() {

                        @Override
                        public void intercept(RequestFacade request) {
                            //always sending content-type
                            //todo get from shared preferences
                            SharedPreferences preferences = BaseApplication.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
                            request.addQueryParam("lang", preferences.getString(Constants.LANGUAGE,"en"));
                            request.addQueryParam("units", preferences.getString(Constants.SYSTEM,"metric"));
                            request.addQueryParam("APPID", Constants.API_KEY);
                            request.addHeader("Content-Type", "application/json");
                        }
                    }).build();

            api = res.create(RetrofitEndPoints.class);

        } catch (Exception e) {
            Log.e(TAG, "Error log message.", e);
        }

        return api;
    }

//    /**
//     * @param error retrofit error
//     * @return gets the hidden http failed message
//     */
//    public static ErrorMessages getMessageError(RetrofitError error) {
//        try {
//
//
//            if (error.getResponse() != null) {
//                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//
//                if (json != null) {
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    return gson.fromJson(json, ErrorMessages.class);
//                } else {
//                    ErrorMessages messages = new ErrorMessages();
//                    messages.setCode(-1);
//                    messages.setMessage("");
//
//                    return messages;
//                }
//            } else {
//                ErrorMessages errorMessages = new ErrorMessages();
//                errorMessages.setCode(-1);
//                errorMessages.setMessage("Failed to connected to server");
//                return errorMessages;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            ErrorMessages errorMessages = new ErrorMessages();
//            errorMessages.setCode(-1);
//            errorMessages.setMessage("Failed to connected to server");
//            return errorMessages;
//        }
//    }


    public interface RetrofitEndPoints {

        @GET("/find")
        void findNearbyPlaces(@Query("lat") String latitude, @Query("lon") String longitude, Callback<JsonObject> cb);

        @GET("/forecast")
        void forecastForCity(@Query("id") Long id, Callback<JsonObject> callback);

        @GET("/forecast/daily")
        void dailyForecastForCity(@Query("id") Long id, Callback<JsonObject> callback);
    }
}
