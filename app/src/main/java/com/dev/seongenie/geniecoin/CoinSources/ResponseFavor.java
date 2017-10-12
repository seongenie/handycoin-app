package com.dev.seongenie.geniecoin.CoinSources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by seongjinlee on 2017. 10. 3..
 */

public class ResponseFavor {
    @SerializedName("data")
    @Expose
    private Map<String, Map<String, Price>> data;

    @SerializedName("USDKRW")
    @Expose
    private double currencyRate;

    public Map<String, Map<String, Price>> getData() {
        return data;
    }

    public double getCurrencyRate() {
        return currencyRate;
    }
}
