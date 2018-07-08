package com.dev.seongenie.geniecoin.CoinSources;

import android.support.annotation.NonNull;

/**
 * Created by lsj23 on 2018-03-25.
 */

public class ColorPrice implements Comparable<ColorPrice>{
    private String content;
    private String amount;
    private int color;

    public ColorPrice(String content, String amount, int color) {
        this.content = content;
        this.amount = amount;
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(@NonNull ColorPrice o) {
        if (Double.parseDouble(this.content) < Double.parseDouble(o.getContent())) return 1;
        else return -1;
    }
}
