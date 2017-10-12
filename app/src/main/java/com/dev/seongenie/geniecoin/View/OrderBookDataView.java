package com.dev.seongenie.geniecoin.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by seongjinlee on 2017. 10. 5..
 */

public class OrderBookDataView  {
    @SerializedName("data")
    @Expose
    OrderBook orderBook;

    public OrderBookDataView(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }
}
