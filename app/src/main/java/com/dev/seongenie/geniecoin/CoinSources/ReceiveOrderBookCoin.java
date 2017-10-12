package com.dev.seongenie.geniecoin.CoinSources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

public class ReceiveOrderBookCoin extends BasicCoin{

    private List<Map<String, Double>> orderBookInfo;

    public ReceiveOrderBookCoin(String exchangeName, String coinName) {
        super(exchangeName, coinName);
    }

    public List<Map<String, Double>> getOrderBookInfo() {
        return orderBookInfo;
    }

    public void setOrderBookInfo(List<Map<String, Double>> orderBookInfo) {
        this.orderBookInfo = orderBookInfo;
    }
}
