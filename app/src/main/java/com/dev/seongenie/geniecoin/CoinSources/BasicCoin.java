package com.dev.seongenie.geniecoin.CoinSources;

/**
 * Created by seongjinlee on 2017. 10. 5..
 */

public class BasicCoin {
    protected String exchange;
    protected String coinName;

    public BasicCoin(String exchangeName, String coinName) {
        this.exchange = exchangeName;
        this.coinName = coinName;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchangeName) {
        this.exchange = exchangeName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
}
