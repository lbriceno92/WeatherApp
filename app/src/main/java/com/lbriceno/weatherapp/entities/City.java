package com.lbriceno.weatherapp.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by luis_ on 10/1/2016.
 */
@RealmClass
public class City extends RealmObject implements Serializable{

    @PrimaryKey
    @SerializedName("id")
    private Long id;

    @SerializedName("coord")
    @Expose
    private Coord coord;

    @SerializedName("name")
    private String name;

    private RealmList<CityWeather> cityWeathers = new RealmList();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<CityWeather> getCityWeathers() {
        return cityWeathers;
    }

    public void setCityWeathers(RealmList<CityWeather> cityWeathers) {
        this.cityWeathers = cityWeathers;
    }
}
