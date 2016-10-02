
package com.lbriceno.weatherapp.entities;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Rain extends RealmObject {

    @SerializedName("3h")
    private String threeH;

    public String getThreeH() {
        return threeH;
    }

    public void setThreeH(String threeH) {
        this.threeH = threeH;
    }
}
