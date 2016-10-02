
package com.lbriceno.weatherapp.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Clouds  extends RealmObject {

    @SerializedName("all")
    @Expose
    private Long all;

    /**
     * 
     * @return
     *     The all
     */
    public Long getAll() {
        return all;
    }

    /**
     * 
     * @param all
     *     The all
     */
    public void setAll(Long all) {
        this.all = all;
    }

}
