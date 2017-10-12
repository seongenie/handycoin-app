package com.dev.seongenie.geniecoin.Fragment.OrderBook;

/**
 * Created by seongjinlee on 2017. 10. 9..
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TradeHistory {
    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("qnty")
    @Expose
    private double qnty;

    @SerializedName("transaction_date")
    @Expose
    private String transactionDate;

    @SerializedName("index")
    @Expose
    private int index;

    public TradeHistory(double price, double qnty, String transactionDate, int index) {
        this.price = price;
        this.qnty = qnty;
        this.transactionDate = transactionDate;
        this.index = index;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQnty() {
        return qnty;
    }

    public void setQnty(double qnty) {
        this.qnty = qnty;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
