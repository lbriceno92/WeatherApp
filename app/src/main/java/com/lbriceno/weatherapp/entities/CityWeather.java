
package com.lbriceno.weatherapp.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CityWeather extends RealmObject {

    @PrimaryKey
    private int id = new Random().hashCode();

    @SerializedName("dt")
    @Index
    public Long dt;

    @SerializedName("weather")
    @Expose
    private RealmList<Weather> weather = new RealmList();

    @SerializedName("base")
    @Expose
    private String base;

    @SerializedName("city")
    private City city;

    @SerializedName("main")
    private Main main;

    private Temp temp;

    /**
     * @return The weather
     */
    public RealmList<Weather> getWeather() {
        return weather;
    }

    /**
     * @param weather The weather
     */
    public void setWeather(RealmList<Weather> weather) {
        this.weather = weather;
    }

    /**
     * @return The base
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base The base
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * @return The main
     */
    public Main getMain() {
        return main;
    }

    /**
     * @param main The main
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * @return The dt
     */
    public Long getDt() {
        return dt;
    }

    /**
     * @param dt The dt
     */
    public void setDt(Long dt) {
        this.dt = dt;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }
}
