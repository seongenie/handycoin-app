package com.dev.seongenie.geniecoin.Layout;

import android.content.Context;
import android.content.res.TypedArray;
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

    private ColorPrice[] items = new ColorPrice[10];
    private TextView[] itemTextViews = new TextView[10];

    public static final int ASK_COLOR = 1;
    public static final int BID_COLOR = 2;

    public static final int GRAVITY_LEFT = -1;
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_RIGHT = 1;



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
            } else if (color == BID_COLOR) {
                tr.setBackground(getResources().getDrawable(R.drawable.cell_shape_bid));
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

    public void setAllItems(ColorPrice[] items) {
        this.items = items;
        if(items.length == 10) {
            for(int i=0; i<10; i++) {
                setItem(i, items[i]);
            }
        }
    }

    public void setItem(int index, ColorPrice item) {
        items[index] = item;
        itemTextViews[index].setText(item.getContent());
        itemTextViews[index].setTextColor(item.getColor());
    }


}
