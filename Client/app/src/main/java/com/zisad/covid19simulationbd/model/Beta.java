package com.zisad.covid19simulationbd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Beta {

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("beta")
    @Expose
    private float beta;

    public String getDate() {
        return date;
    }

    public float getBeta() {
        return beta;
    }
}
