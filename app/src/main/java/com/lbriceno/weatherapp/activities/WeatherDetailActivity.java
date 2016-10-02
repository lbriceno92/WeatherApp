package com.lbriceno.weatherapp.activities;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lbriceno.weatherapp.R;
import com.lbriceno.weatherapp.activities.fragments.WeatherFragment;
import com.lbriceno.weatherapp.entities.City;
import com.lbriceno.weatherapp.tools.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class WeatherDetailActivity extends AppCompatActivity {


    @Bind(R.id.custom_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.weather_viewpager)
    ViewPager mPager;

    @Bind(R.id.left_arrow)
    ImageView mLeftScroll;

    @Bind(R.id.right_arrow)
    ImageView mRightScroll;

    public Long id;
    private List<City> mCities;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getLongExtra(Constants.cityId, 0);
        if (id == 0) finish();

        if (Build.VERSION.SDK_INT >= 21) {
            mLeftScroll.setBackground(getDrawable(R.drawable.button_effect_gray_dark_bg));
            mRightScroll.setBackground(getDrawable(R.drawable.button_effect_gray_dark_bg));
        }

        mCities = Realm.getDefaultInstance().where(City.class).findAll();
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mPager.setCurrentItem(mCities.indexOf(Realm.getDefaultInstance().where(City.class).equalTo("id", id).findFirst()));
    }

    @OnClick(R.id.weather_left_scroll)
    public void scrollLeft() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
    }

    @OnClick(R.id.weather_right_scroll)
    public void scrollRight() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        City current;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

        }

        @Override
        public int getCount() {
            return mCities.size();
        }

        @Override
        public Fragment getItem(int position) {
            current = mCities.get(position);
            return WeatherFragment.getInstance(current);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
