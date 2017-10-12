package com.dev.seongenie.geniecoin.Fragment.OrderBook;

/**
 * Created by seongjinlee on 2017. 10. 9..
 */

import android.util.ArrayMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class TradeHistoryView {
    @SerializedName("data")
    @Expose
    private Map<String, TradeHistory> history;

    @SerializedName("exchange")
    @Expose
    private String exchange;

    @SerializedName("coin")
    @Expose
    private String coinName;

    public TradeHistoryView(Map<String, TradeHistory> history, String exchange, String coinName) {
        this.history = history;
        this.exchange = exchange;
        this.coinName = coinName;
    }

    public Map<String, TradeHistory> getHistory() {
        return history;
    }

    public void setHistory(Map<String, TradeHistory> history) {
        this.history = history;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
}
