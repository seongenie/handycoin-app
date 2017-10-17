package com.dev.seongenie.geniecoin.CoinSources;

/**
 * Created by seongjinlee on 2017. 10. 5..
 */

public class AlarmCoin extends BasicCoin {
    private double goal;
    private int updown;
    private boolean onOff;

    public AlarmCoin(String exchangeName, String coinName, double goal, int updown, boolean onOff) {
        super(exchangeName, coinName);
        this.goal = goal;
        this.updown = updown;
        this.onOff = onOff;
    }

    public double getGoal() {
        return goal;
    }

    public int getUpdown() {
        return updown;
    }

    public boolean isOnOff() {
        return onOff;
    }
}
