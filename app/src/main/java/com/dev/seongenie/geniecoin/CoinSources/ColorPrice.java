package com.dev.seongenie.geniecoin.CoinSources;

/**
 * Created by lsj23 on 2018-03-25.
 */

public class ColorPrice {
    private String content;
    private int color;

    public ColorPrice(String content, int color) {
        this.content = content;
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
}
