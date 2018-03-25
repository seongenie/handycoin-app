package com.dev.seongenie.geniecoin.Fragment;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.R;

import java.util.List;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */


public class FavorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ReceiveFavorCoin> items;
    private Animation mAnimation;


    /** References to the views for each data item **/
    public class FavorViewHolder extends RecyclerView.ViewHolder {
        public TextView coinName;
        public TextView exchangeName;
        public TextView currPrice;
        public TextView changeRate;
        public TextView changePrice;
        public ImageView upDownTriangle;
        public ImageView coinIcon;
        public TextView convertPrice;
        public FrameLayout itemLayout;

        public FavorViewHolder(View v) {
            super(v);
            coinIcon = (ImageView) v.findViewById(R.id.ic_coin);
            upDownTriangle = (ImageView) v.findViewById(R.id.updown_triangle);
            currPrice = (TextView) v.findViewById(R.id.cur_price);
            changeRate = (TextView) v.findViewById(R.id.diff_rate);
            changePrice = (TextView) v.findViewById(R.id.diff_price);
            coinName = (TextView) v.findViewById(R.id.coin_name);
            exchangeName = (TextView) v.findViewById(R.id.exchange_name);
            convertPrice = (TextView) v.findViewById(R.id.convert_price);
            itemLayout = (FrameLayout)v.findViewById(R.id.item_layout);

            if (MainActivity.nanumgothic != null) {
                currPrice.setTypeface(MainActivity.nanumgothic);
                changeRate.setTypeface(MainActivity.nanumgothic);
                changePrice.setTypeface(MainActivity.nanumgothic);
                coinName.setTypeface(MainActivity.nanumgothicbold);
                exchangeName.setTypeface(MainActivity.nanumgothic);
                convertPrice.setTypeface(MainActivity.nanumgothic);
            }
        }
    }

    /** Constructor **/
    public FavorsAdapter(List<ReceiveFavorCoin> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favor_item2, parent, false);
        return new FavorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReceiveFavorCoin item = items.get(position);
        FavorViewHolder vh = (FavorViewHolder) holder;
        vh.coinName.setText(item.getCoinName());
        vh.exchangeName.setText(MainActivity.exchangeToKorean(item.getExchange()));
        vh.coinIcon.setImageResource(MainActivity.getCoinIcon(item.getCoinName()));

        boolean USD = item.getExchange().equals("poloniex") ? true : false;
        boolean TRON = item.getCoinName().equals("TRON") ? true : false;

        /** USD ? KRW */
        if(USD) {
            vh.currPrice.setText("$ " + String.format("%,.3f", item.getLastPrice()));

            String changePrice = String.format("%,.3f", item.getChangePrice());
            changePrice = item.getChangePrice() > 0 ? "+" + changePrice : changePrice;
            vh.changePrice.setText(changePrice);

            vh.convertPrice.setText("≈" + String.format("%,.0f", item.getKrwRate() * item.getLastPrice()) + " 원");
        }
        else {
            String changePrice = "";
            if(TRON) {
                vh.currPrice.setText(String.format("%,.2f", item.getLastPrice()) + " 원");
                changePrice = String.format("%,.2f", item.getChangePrice());
            }
            else {
                vh.currPrice.setText(String.format("%,.0f", item.getLastPrice()) + " 원");
                changePrice = String.format("%,.0f", item.getChangePrice());
            }
            changePrice = item.getChangePrice() > 0 ? "+" + changePrice : changePrice;
            vh.changePrice.setText(changePrice);
            vh.convertPrice.setText("");
        }

        String changeRate = String.format("%.2f", item.getChangeRate()) +"%";
        changeRate = item.getChangeRate() > 0 ? "+" + changeRate : changeRate;
        vh.changeRate.setText(changeRate);

        double changePrice = item.getChangePrice();
        int triangleResource = changePrice == 0 ? 0 :
                changePrice > 0 ? R.drawable.red_triangle : R.drawable.blue_triangle;
        vh.upDownTriangle.setImageResource(triangleResource);

        int color = changePrice == 0 ? Color.BLACK :
                changePrice > 0 ? Color.RED : Color.BLUE;

        vh.currPrice.setTextColor(color);
        vh.changePrice.setTextColor(color);
        vh.changeRate.setTextColor(color);

        if(item.isRefresh()) { blinkAnimation(vh.currPrice, 200, 1); }

        if (position == (getItemCount()-1) && item.getExchange().isEmpty() && item.getCoinName().isEmpty()) {
            vh.itemLayout.setVisibility(View.INVISIBLE);
        }
        else {
            vh.itemLayout.setVisibility(View.VISIBLE);
        }

    }


    private void blinkAnimation(View view, int duration, int repeat) {
        if(mAnimation == null) { mAnimation = new AlphaAnimation(1,0); }
        mAnimation.setDuration(duration);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(repeat);
        mAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(mAnimation);
    }

}