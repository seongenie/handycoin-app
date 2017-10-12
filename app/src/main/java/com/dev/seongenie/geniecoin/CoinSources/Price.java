package com.dev.seongenie.geniecoin.CoinSources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by seongjinlee on 2017. 10. 3..
 */

public class Price {
    @SerializedName("last_price")
    @Expose
    private double lastPrice;

    @SerializedName("first_price")
    @Expose
    private double firstPrice;

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrie(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(double firstPrice) {
        this.firstPrice = firstPrice;
    }
}
