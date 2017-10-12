package com.dev.seongenie.geniecoin.CoinSources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

public class ReceiveFavorCoin extends BasicCoin{
    @SerializedName("last_price")
    @Expose
    private double lastPrice;

    @SerializedName("first_price")
    @Expose
    private double firstPrice;

    private boolean refresh = false;

    private double krwRate = 0;

    public ReceiveFavorCoin(String exchange, String coinName, double lastPrice, double firstPrice) {
        super(exchange, coinName);
        this.lastPrice = lastPrice;
        this.firstPrice = firstPrice;
    }

    public double getChangeRate() {
        double diffence = this.lastPrice - this.firstPrice;
        return this.firstPrice != 0 ? diffence * 100 / this.firstPrice : 0;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(double firstPrice) {
        this.firstPrice = firstPrice;
    }

    public double getChangePrice() {
        return this.lastPrice - firstPrice;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public double getKrwRate() {
        return krwRate;
    }

    public void setKrwRate(double krwRate) {
        this.krwRate = krwRate;
    }
}
