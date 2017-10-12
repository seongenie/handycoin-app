package com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by seongjinlee on 2017. 10. 9..
 */

public class BalanceView {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private Map<String, String> data;

    public String getStatus() {
        return status;
    }

    public Map<String, String> getData() {
        return data;
    }
}
