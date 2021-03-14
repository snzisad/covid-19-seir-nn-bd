package com.zisad.covid19simulationbd.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfirmedCases {

    @SerializedName("beta")
    @Expose
    private Beta[] BetaList;

    @SerializedName("peak")
    @Expose
    private int peak;

    @SerializedName("peak_day")
    @Expose
    private int peak_day;

    @SerializedName("confirmed_data")
    @Expose
    private int[] confirmed_data;

    @SerializedName("information")
    @Expose
    private String information;

    @SerializedName("r0value")
    @Expose
    private String r0value;

    public Beta[] getBetaList() {
        return BetaList;
    }

    public int getPeak() {
        return peak;
    }

    public int getPeak_day() {
        return peak_day;
    }

    public int[] getConfirmed_data() {
        return confirmed_data;
    }

    public String getInformation() {
        return information;
    }

    public String getR0value() {
        return r0value;
    }
}

