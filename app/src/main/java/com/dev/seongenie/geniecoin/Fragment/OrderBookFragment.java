package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.dev.seongenie.geniecoin.Api.CommonMessage;
import com.dev.seongenie.geniecoin.CoinSources.BasicCoin;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.Fragment.OrderBook.TradeHistory;
import com.dev.seongenie.geniecoin.Fragment.OrderBook.TradeHistoryView;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.ServerConnection.RestfulApi;
import com.dev.seongenie.geniecoin.View.OrderBookDataView;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Button;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderBookFragment extends Fragment {
    private TextView message;

    private TextView askTextViews[];
    private TextView bidTextViews[];

    private TextView askValueTextViews[];
    private TextView bidValueTextViews[];

    private TextView totalRightQnty;
    private TextView totalLeftQnty;
    private TextView totalQnty;
    private FavorDialogFragment dialogFrag;

    private TextView startPriceTextView;
    private TextView lowPriceTextView;
    private TextView highPriceTextView;
    private TextView volumeTextView;

    private ImageView orderbookTriangle;
    private ImageView coinIcon;

    private TextView coinNameTextView;
    private TextView exchangeNameTextView;
    private TextView coinPriceTextView;
    private TextView diffRateTextView;

    private Drawable borderUnselected;
    private Drawable borderSelected;

    private TradeAdapter tradeAdapter;
    private ArrayList<TradeHistory> histories;

    private OrderBookFragment thisFragment = this;
    private BasicCoin basicCoin = null;

    private Dialog selectDialog;

    private Timer timer = new Timer();
    private int REFRESH_INTERVAL = 2000;
    private int BLINK_DURATION = 150;
    private PriorityQueue<String> queue = new PriorityQueue<String>();
    AlphaAnimation mAnimation = null;
    Callback<OrderBookDataView> orderbookCallback;
    Callback<TradeHistoryView> tradeHistoryCallback;

    public OrderBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orderbook, container, false);


//        message = (TextView) v.findViewById(R.id.no_select);

        orderbookCallback = new Callback<OrderBookDataView>() {

            @Override
            public void onResponse(Call<OrderBookDataView> call, Response<OrderBookDataView> response) {
                if (response != null && response.code() == 200 ) {
                    Map<String, Double> ask = response.body().getOrderBook().getAsk();
                    Map<String, Double> bid = response.body().getOrderBook().getBid();
                    double lastPrice = response.body().getOrderBook().getLastPrice();
                    double prevPrice = response.body().getOrderBook().getPrevPrice();
                    double minPrice = response.body().getOrderBook().getMinPrice();
                    double maxPrice = response.body().getOrderBook().getMaxPrice();
                    double volume = response.body().getOrderBook().getVolume();

                    Set<String> askKeys = ask.keySet();
                    Set<String> bidKeys = bid.keySet();

                    boolean USD = basicCoin.getExchange().equals("poloniex") ? true : false;

                    queue.clear();
                    for( String askKey : askKeys ) { queue.add(askKey); }

                    String key;
                    int i = 0;
                    double total_ask_qnty = 0;
                    while(!queue.isEmpty()) {
                        key = queue.poll();
                        total_ask_qnty += ask.get(key);
                        if(!USD) { setTextKRW(askTextViews[i], Double.parseDouble(key)); }
                        else { setTextUSD(askTextViews[i], Double.parseDouble(key)); }
                        if(!askValueTextViews[i].getText().equals(String.format("%.6f", ask.get(key)))) {
                            blinkAnimation(askTextViews[i], BLINK_DURATION, 1);
                            blinkAnimation(askValueTextViews[i], BLINK_DURATION, 1);
                        }

                        int textColor = Color.BLACK;
                        if(prevPrice < Double.parseDouble(key))textColor = Color.RED;
                        else if (prevPrice > Double.parseDouble(key))textColor = Color.BLUE;

                        try {
                            if(lastPrice == Double.parseDouble(key)) {
                                askTextViews[i].setBackground(borderSelected);
                            }
                            else {
                                askTextViews[i].setBackground(borderUnselected);
                            }
                        }
                        catch (Exception e) {
                            Log.e("seongenie", "호가 배경선택 오류!");
                            e.printStackTrace();
                        }

                        askTextViews[i].setTextColor(textColor);

                        askValueTextViews[i++].setText(String.format("%.6f", ask.get(key)));
                    }

                    queue.clear();
                    for( String bidKey : bidKeys ) { queue.add(bidKey); }

                    double total_bid_qnty = 0;
                    i = 0;
                    while(!queue.isEmpty()) {
                        key = queue.poll();
                        total_bid_qnty += bid.get(key);
                        if(!USD) { setTextKRW(bidTextViews[i], Double.parseDouble(key)); }
                        else { setTextUSD(bidTextViews[i], Double.parseDouble(key)); }
                        if(!bidValueTextViews[i].getText().equals(String.format("%.6f", bid.get(key)))) {
                            blinkAnimation(bidTextViews[i], BLINK_DURATION, 1);
                            blinkAnimation(bidValueTextViews[i], BLINK_DURATION, 1);
                        }

                        int textColor = Color.BLACK;
                        if(prevPrice < Double.parseDouble(key))textColor = Color.RED;
                        else if (prevPrice > Double.parseDouble(key))textColor = Color.BLUE;

                        bidTextViews[i].setTextColor(textColor);

                        try {
                            if(lastPrice == Double.parseDouble(key)) {
                                bidTextViews[i].setBackground(borderSelected);
                            }
                            else {
                                bidTextViews[i].setBackground(borderUnselected);
                            }
                        } catch (Exception e) {
                            Log.e("seongenie", "호가 배경선택 오류!");
                            e.printStackTrace();
                        }


                        bidValueTextViews[i++].setText(String.format("%.6f", bid.get(key)));
                    }


                    totalLeftQnty.setText(String.format("%.6f", total_ask_qnty));
                    totalRightQnty.setText(String.format("%.6f", total_bid_qnty));

                    volumeTextView.setText(String.format("%,.3f", volume));

                    String changeValue;

                    String changeRate = String.format("%,.2f", getChangeRate(lastPrice, prevPrice));

                    if(!USD) {
                        startPriceTextView.setText(String.format("%,.0f", prevPrice));
                        highPriceTextView.setText(String.format("%,.0f", maxPrice));
                        lowPriceTextView.setText(String.format("%,.0f", minPrice));
                        coinPriceTextView.setText(String.format("%,.0f", lastPrice) + " 원");
                        changeValue = String.format("%,.0f", lastPrice - prevPrice);

                    }
                    else {
                        startPriceTextView.setText(String.format("%,.3f", prevPrice));
                        highPriceTextView.setText(String.format("%,.3f", maxPrice));
                        lowPriceTextView.setText(String.format("%,.3f", minPrice));
                        coinPriceTextView.setText("$ " + String.format("%,.3f", lastPrice));
                        changeValue = String.format("%,.3f", lastPrice - prevPrice);
                    }

                    if(minPrice < prevPrice) {
                        lowPriceTextView.setTextColor(Color.BLUE);
                    } else if (minPrice == prevPrice) {
                        lowPriceTextView.setTextColor(Color.BLACK);
                    } else {
                        lowPriceTextView.setTextColor(Color.RED);
                    }

                    if(maxPrice < prevPrice) {
                        highPriceTextView.setTextColor(Color.BLUE);
                    } else if (maxPrice == prevPrice) {
                        highPriceTextView.setTextColor(Color.BLACK);
                    } else {
                        highPriceTextView.setTextColor(Color.RED);
                    }


                    int triangleSource = 0;
                    if(lastPrice < prevPrice) {
                        coinPriceTextView.setTextColor(Color.BLUE);
                        diffRateTextView.setTextColor(Color.BLUE);
                        triangleSource = R.drawable.blue_triangle;
                    } else if (lastPrice == prevPrice) {
                        coinPriceTextView.setTextColor(Color.BLACK);
                        diffRateTextView.setTextColor(Color.BLACK);
                    } else {
                        coinPriceTextView.setTextColor(Color.RED);
                        diffRateTextView.setTextColor(Color.RED);
                        triangleSource = R.drawable.red_triangle;
                        changeValue = '+' + changeValue;
                        changeRate = '+' + changeRate;
                    }
                    diffRateTextView.setText(changeValue + " (" + changeRate + "%)");
                    orderbookTriangle.setImageResource(triangleSource);

                }
                else {
                    CommonMessage.getInstance().displaySnackbar("서버연결에 실패했습니다.", thisFragment, CommonMessage.ERROR_MESSAGE_TYPE);
                }
            }

            @Override
            public void onFailure(Call<OrderBookDataView> call, Throwable t) {

            }
        };


        borderUnselected = ContextCompat.getDrawable(getActivity(), R.drawable.border_rect);
        borderSelected = ContextCompat.getDrawable(getActivity(), R.drawable.border_selected_rect);
        orderbookTriangle = (ImageView) v.findViewById(R.id.orderbook_triangle);
        coinIcon = (ImageView) v.findViewById(R.id.orderbook_coin_icon);

        askTextViews = new TextView[5];
        askValueTextViews = new TextView[5];
        bidTextViews = new TextView[5];
        bidValueTextViews = new TextView[5];

        askTextViews[0] = (TextView) v.findViewById(R.id.ask1);
        askTextViews[1] = (TextView) v.findViewById(R.id.ask2);
        askTextViews[2] = (TextView) v.findViewById(R.id.ask3);
        askTextViews[3] = (TextView) v.findViewById(R.id.ask4);
        askTextViews[4] = (TextView) v.findViewById(R.id.ask5);

        askValueTextViews[0] = (TextView) v.findViewById(R.id.ask_value1);
        askValueTextViews[1] = (TextView) v.findViewById(R.id.ask_value2);
        askValueTextViews[2] = (TextView) v.findViewById(R.id.ask_value3);
        askValueTextViews[3] = (TextView) v.findViewById(R.id.ask_value4);
        askValueTextViews[4] = (TextView) v.findViewById(R.id.ask_value5);

        bidTextViews[0] = (TextView) v.findViewById(R.id.bid1);
        bidTextViews[1] = (TextView) v.findViewById(R.id.bid2);
        bidTextViews[2] = (TextView) v.findViewById(R.id.bid3);
        bidTextViews[3] = (TextView) v.findViewById(R.id.bid4);
        bidTextViews[4] = (TextView) v.findViewById(R.id.bid5);

        bidValueTextViews[0] = (TextView) v.findViewById(R.id.bid_value1);
        bidValueTextViews[1] = (TextView) v.findViewById(R.id.bid_value2);
        bidValueTextViews[2] = (TextView) v.findViewById(R.id.bid_value3);
        bidValueTextViews[3] = (TextView) v.findViewById(R.id.bid_value4);
        bidValueTextViews[4] = (TextView) v.findViewById(R.id.bid_value5);

        for(int i=0; i<5; i++) {
            askTextViews[i].setTypeface(MainActivity.nanumgothicbold);
            bidTextViews[i].setTypeface(MainActivity.nanumgothicbold);
            askValueTextViews[i].setTypeface(MainActivity.nanumgothic);
            bidValueTextViews[i].setTypeface(MainActivity.nanumgothic);
        }

        totalLeftQnty = (TextView) v.findViewById(R.id.total_left_qnty);
        totalRightQnty = (TextView) v.findViewById(R.id.total_right_qnty);
        totalQnty = (TextView) v.findViewById(R.id.total_qnty);

        totalLeftQnty.setTypeface(MainActivity.nanumgothicbold);
        totalRightQnty.setTypeface(MainActivity.nanumgothicbold);
        totalQnty.setTypeface(MainActivity.nanumgothicbold);

        volumeTextView = (TextView) v.findViewById(R.id.volume);
        startPriceTextView = (TextView) v.findViewById(R.id.start_price);
        lowPriceTextView = (TextView) v.findViewById(R.id.lowedst_price);
        highPriceTextView = (TextView) v.findViewById(R.id.highest_price);

        volumeTextView.setTextColor(Color.BLACK);
        startPriceTextView.setTextColor(Color.BLACK);

        ((TextView)v.findViewById(R.id.volume_label)).setTypeface(MainActivity.nanumgothicbold);
        ((TextView)v.findViewById(R.id.start_price_label)).setTypeface(MainActivity.nanumgothicbold);
        ((TextView)v.findViewById(R.id.highest_price_label)).setTypeface(MainActivity.nanumgothicbold);
        ((TextView)v.findViewById(R.id.lowest_price_label)).setTypeface(MainActivity.nanumgothicbold);

        ((TextView)v.findViewById(R.id.subtitle_contract)).setTypeface(MainActivity.nanumgothicbold);
        ((TextView)v.findViewById(R.id.subtitle_trading)).setTypeface(MainActivity.nanumgothicbold);

        ((TextView)v.findViewById(R.id.textview_buy_qnty)).setTypeface(MainActivity.nanumgothicbold);
        ((TextView)v.findViewById(R.id.textview_order_price)).setTypeface(MainActivity.nanumgothicbold);
        ((TextView)v.findViewById(R.id.textview_sell_qnty)).setTypeface(MainActivity.nanumgothicbold);

        coinNameTextView = (TextView) v.findViewById(R.id.orderbook_coin_name);
        exchangeNameTextView = (TextView) v.findViewById(R.id.orderbook_exchange_name);
        coinPriceTextView = (TextView) v.findViewById(R.id.orderbook_coin_price);

        coinNameTextView.setTypeface(MainActivity.nanumgothicbold);
        exchangeNameTextView.setTypeface(MainActivity.nanumgothic);
        coinPriceTextView.setTypeface(MainActivity.nanumgothicbold);

        diffRateTextView = (TextView) v.findViewById(R.id.orderbook_diff_rate);
        diffRateTextView.setTypeface(MainActivity.nanumgothic);


        View dialogView = inflater.inflate(R.layout.favor_filter_view, container, false);

        selectDialog = new Dialog(this.getContext());
        selectDialog.title("거래소 선택")
                .positiveAction("OK")
                .negativeAction("CANCEL")
                .contentView(dialogView)
                .cancelable(true);

        Button searchButton = (Button) v.findViewById(R.id.orderbook_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDialog.show();
            }
        });


        ListView listview ;

        histories = new ArrayList<TradeHistory>();
        // Adapter 생성
        tradeAdapter = new TradeAdapter(histories);

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) v.findViewById(R.id.listview_tradehistory);
        listview.setAdapter(tradeAdapter);

        tradeHistoryCallback = new Callback<TradeHistoryView>() {

            @Override
            public void onResponse(Call<TradeHistoryView> call, Response<TradeHistoryView> response) {
                try {
                    Map<String, TradeHistory> history = response.body().getHistory();
                    TreeMap<String, TradeHistory> treeMapReverse = new TreeMap<String, TradeHistory>(Collections.reverseOrder());
                    treeMapReverse.putAll(history);
                    Iterator<String> treeMapReverseIter = treeMapReverse.keySet().iterator();
                    while( treeMapReverseIter.hasNext()) {
                        String key = treeMapReverseIter.next();
                        TradeHistory value = treeMapReverse.get( key );
                        tradeAdapter.addItem(value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TradeHistoryView> call, Throwable t) {

            }
        };

        return v;
    }

    public void setTextKRW(TextView textView, double value) {
        textView.setText(String.format("%,.0f", value));
    }

    public void setTextUSD(TextView textView, double value) {
        textView.setText(String.format("%.06f", value));
    }

    private void blinkAnimation(View view, int duration, int repeat) {
        if(mAnimation == null) { mAnimation = new AlphaAnimation(1,0); }
        mAnimation.setDuration(duration);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(repeat);
        mAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(mAnimation);
    }


    public void setOrderBookCoin(BasicCoin basicCoin) {
        this.basicCoin = basicCoin;
        histories.clear();
        if(basicCoin != null) {
            coinNameTextView.setText(basicCoin.getCoinName());
            exchangeNameTextView.setText(exchangeToKorean(basicCoin.getExchange()));
            coinIcon.setImageResource(getCoinIcon(basicCoin.getCoinName()));
            if(timer != null) {
                timer.cancel();
            }

            timer = new Timer();
            doSomethingRepeatedly();
        }
    }

    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                RestfulApi.getInstance().getOrderBook(basicCoin, orderbookCallback);
                RestfulApi.getInstance().getTradeHistory(basicCoin, tradeHistoryCallback);
                Log.d("seongenie", "orderbook network call!!");
                Log.d("seongenie", "tradeHistory network call!!");
            }
        }, 0, REFRESH_INTERVAL);
    }

    @Override
    public void onStart() {
        super.onStart();
        setOrderBookCoin(basicCoin);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer != null)timer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(timer != null)timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null)timer.cancel();
    }

    private double getChangeRate(double lastPrice, double firstPrice) {
        double diffence = lastPrice - firstPrice;
        return firstPrice != 0 ? diffence * 100 / firstPrice : 0;
    }

    private String exchangeToKorean(String eng){
        String result = "";

        switch (eng) {
            case "bithumb" :
                result = "빗썸";
                break;

            case "coinone" :
                result = "코인원";
                break;
            case "poloniex" :
                result = "폴로닉스";
                break;
            case "coinis" :
                result = "코인이즈";
                break;
        }
        return result;
    }

    private int getCoinIcon(String coin){
        int result = 0;
        switch (coin) {
            case "BTC" :
                result = R.drawable.ic_btc;
                break;
            case "BCH" :
                result = R.drawable.ic_bch;
                break;
            case "XRP" :
                result = R.drawable.ic_xrp;
                break;
            case "DASH" :
                result = R.drawable.ic_dash;
                break;
            case "ETH" :
                result = R.drawable.ic_eth;
                break;
            case "ETC" :
                result = R.drawable.ic_etc;
                break;
            case "XMR" :
                result = R.drawable.ic_xmr;
                break;
            case "ZEC" :
                result = R.drawable.ic_zec;
                break;
            case "LTC" :
                result = R.drawable.ic_ltc;
                break;
            case "QTUM" :
                result = R.drawable.ic_qtum;
                break;
            case "STR" :
                result = R.drawable.ic_str;
                break;
            case "REP" :
                result = R.drawable.ic_rep;
                break;
            case "NXT" :
                result = R.drawable.ic_nxt;
                break;
            case "" :
                break;
        }

        return result;
    }


    private class TradeAdapter extends BaseAdapter {
        private ArrayList<TradeHistory> item;
        private TradeHistory temp;

        public TradeAdapter (ArrayList<TradeHistory> item) {
            this.item = item;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_tradehistory, null);
            }

            temp = item.get(position);
            TextView price = (TextView) v.findViewById(R.id.trade_price);
            TextView qnty = (TextView) v.findViewById(R.id.trade_qnty);

            price.setText(String.format("%,.0f", temp.getPrice()));
            qnty.setText(String.format("%.4f", temp.getQnty()));

            if (position % 2 == 0) {
//                v.setBackgroundColor(R.color.message_ok);
            }

            return v;
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public TradeHistory getItem(int position) {
            return item.get(position) ;
        }

        @Override
        public int getCount() {
            return item.size();
        }

        public void addItem(TradeHistory history) {
            if(item.size() == 0 || item.get(0).getIndex() < history.getIndex()) {
                item.add(0, history);
                if (item.size() > 40) {
                    for(int i=40; i<item.size(); i++){
                        item.remove(i);
                    }
                }
                this.notifyDataSetChanged();
            } else return;
        }
    }

}