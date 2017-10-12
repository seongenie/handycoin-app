package com.dev.seongenie.geniecoin.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by seongjinlee on 2017. 10. 5..
 */

public class OrderBook {

    @SerializedName("ASK")
    @Expose
    private Map<String, Double> ask;

    @SerializedName("BID")
    @Expose
    private Map<String, Double> bid;

    @SerializedName("exchange")
    @Expose
    private String exchange;

    @SerializedName("coin")
    @Expose
    private String coin;

    @SerializedName("last_price")
    @Expose
    private Double lastPrice;

    @SerializedName("prev_price")
    @Expose
    private Double prevPrice;

    @SerializedName("min_price")
    @Expose
    private Double minPrice;

    @SerializedName("max_price")
    @Expose
    private Double maxPrice;

    @SerializedName("volume")
    @Expose
    private Double volume;

    public OrderBook(Map<String, Double> ask, Map<String, Double> bid) {
        this.ask = ask;
        this.bid = bid;
    }

    public Map<String, Double> getAsk() {
        return ask;
    }

    public void setAsk(Map<String, Double> ask) {
        this.ask = ask;
    }

    public Map<String, Double> getBid() {
        return bid;
    }

    public void setBid(Map<String, Double> bid) {
        this.bid = bid;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Double getPrevPrice() {
        return prevPrice;
    }

    public void setPrevPrice(Double prevPrice) {
        this.prevPrice = prevPrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }
}
