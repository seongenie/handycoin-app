package com.dev.seongenie.geniecoin.Layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.dev.seongenie.geniecoin.CoinSources.ColorPrice;
import com.dev.seongenie.geniecoin.R;
import com.rey.material.widget.TextView;

/**
 * Created by lsj23 on 2018-03-25.
 */

public class OrderbookTableLayout extends TableLayout {
    int current = -1;

    private Drawable borderSelectedRed = ContextCompat.getDrawable(getContext(), R.drawable.border_selected_red);
    private Drawable borderSelectedBlue = ContextCompat.getDrawable(getContext(), R.drawable.border_selected_blue);
    private ColorPrice[] items = new ColorPrice[10];
    private TextView[] itemTextViews = new TextView[10];

    public static final int ASK_COLOR = 1;
    public static final int BID_COLOR = 2;

    public static final int GRAVITY_LEFT = -1;
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_RIGHT = 1;


    private int askBid = 0; // ask = 0; bid = 1;


    public OrderbookTableLayout(Context context) {
        super(context);
    }

    public OrderbookTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        int color;
        int alignment;
        int gravity;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.orderbook,
                0, 0);

        try {
            color = a.getInteger(R.styleable.orderbook_viewColor, 1);
            alignment = a.getInteger(R.styleable.orderbook_viewAlignment, 0);
            gravity = a.getInteger(R.styleable.orderbook_viewGravity, 0);
        } finally {
            a.recycle();
        }
        init(color, alignment, gravity);
    }

    public void init(int color, int alignment, int gravity) {
        for (int i=0; i<10; i++) {
            itemTextViews[i] = new TextView(this.getContext());
            TableRow tr = new TableRow(this.getContext());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            tr.addView(itemTextViews[i]);

            if (color == ASK_COLOR) {
                tr.setBackground(getResources().getDrawable(R.drawable.cell_shape_ask));
                askBid = 0;
            } else if (color == BID_COLOR) {
                tr.setBackground(getResources().getDrawable(R.drawable.cell_shape_bid));
                askBid = 1;
            }

            if (gravity == GRAVITY_LEFT) {
                tr.setGravity(Gravity.LEFT);
            } else if (gravity == GRAVITY_CENTER) {
                tr.setGravity(Gravity.CENTER);
            } else if (gravity == GRAVITY_RIGHT) {
                tr.setGravity(Gravity.RIGHT);
            }

            tr.setPadding(20,20,20,20);
            itemTextViews[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            itemTextViews[i].setTextAlignment(alignment);
            this.addView(tr);
        }
    }

    public void setAllAmount(ColorPrice[] items) {
        this.items = items;
        if(items.length == 10) {
            for(int i=0; i<10; i++) {
                setItemAmount(i, items[i]);
            }
        }
    }

    public void setAllPrice(ColorPrice[] items) {
        this.items = items;
        if(items.length == 10) {
            for(int i=0; i<10; i++) {
                setItemPrice(i, items[i]);
            }
        }
    }

    public void setItemPrice(int index, ColorPrice item) {
        items[index] = item;
        itemTextViews[index].setText(item.getContent());
        itemTextViews[index].setTextColor(item.getColor());
    }

    public void setItemAmount(int index, ColorPrice item) {
        items[index] = item;
        itemTextViews[index].setText(item.getAmount());
        itemTextViews[index].setTextColor(item.getColor());
    }

    public void setBorder(int index) {
        if(current >= 0) {
            if (askBid == 1) this.getChildAt(current).setBackground(getResources().getDrawable(R.drawable.cell_shape_bid));
            else this.getChildAt(current).setBackground(getResources().getDrawable(R.drawable.cell_shape_ask));
        }
        current = index;
        if (current >= 0) {
            if (askBid == 1) this.getChildAt(current).setBackground(borderSelectedRed);
            else this.getChildAt(current).setBackground(borderSelectedBlue);
        }
    }


}
