package com.lbriceno.weatherapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.gson.JsonObject;
import com.lbriceno.weatherapp.R;
import com.lbriceno.weatherapp.adapters.WeatherRecyclerAdapter;
import com.lbriceno.weatherapp.api.Api;
import com.lbriceno.weatherapp.entities.City;
import com.lbriceno.weatherapp.tools.BSUtils;
import com.lbriceno.weatherapp.tools.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements WeatherRecyclerAdapter.OnClickListener {

    @Bind(R.id.weather_recycler)
    RecyclerView mWeatherRecycler;
    @Bind(R.id.coordinator)
    CoordinatorLayout mCoordinator;
    private RealmResults<City> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        ButterKnife.bind(this);

        requestWeatherInfo();

        result = Realm.getDefaultInstance().where(City.class).findAll();

        result.addChangeListener(new RealmChangeListener<RealmResults<City>>() {
            @Override
            public void onChange(RealmResults<City> element) {
                mWeatherRecycler.getAdapter().notifyDataSetChanged();
            }
        });

        mWeatherRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mWeatherRecycler.setAdapter(new WeatherRecyclerAdapter(this, result, this));
        mWeatherRecycler.setHasFixedSize(true);
    }

    @Override
    public void onWeatherClick(City city) {
        Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
        intent.putExtra(Constants.cityId, city.getId());
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            requestWeatherInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestWeatherInfo() {
        Api.getInstance().getWeather(new Callback<JsonObject>() {

            @Override
            public void success(JsonObject jsonObject, Response response) {
                int code = jsonObject.get("cod").getAsInt();
                if (code > 200) {
                    BSUtils.showSnackBar(getApplicationContext(), mCoordinator, jsonObject.get("message").getAsString(), null, null);
                }else{
                    BSUtils.showSnackBar(getApplicationContext(), mCoordinator, getString(R.string.data_updated), null, null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                
                BSUtils.showSnackBar(getApplicationContext(), mCoordinator, getString(R.string.error_fetching), null, null);
            }
        });
    }

}
