package com.dev.seongenie.geniecoin.ServerConnection;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.dev.seongenie.geniecoin.CoinSources.BasicCoin;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.CoinSources.ResponseFavor;
import com.dev.seongenie.geniecoin.Fragment.OrderBook.TradeHistoryView;
import com.dev.seongenie.geniecoin.View.OrderBookDataView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

public class RestfulApi {
    public static final RestfulApi instance = new RestfulApi();
    private Retrofit retrofit;
    CoinService coinService = null;
    private RestfulApi(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://13.124.162.39:80")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        coinService = retrofit.create(CoinService.class);
    }


    public static RestfulApi getInstance() {
        return instance;
    }

    public void getExchangeCoins(Callback<Map<String, List<String>>> callback){
        coinService.getExchangeCoins().enqueue(callback);
    }


    public void getFavors(List<? extends BasicCoin> favorList, Callback<ResponseFavor> callback) {
        ArrayMap<String, List<String>> data = new ArrayMap<String, List<String>>();

        for(BasicCoin item : favorList) {
            String exchange = item.getExchange();
            if ( data.get(exchange) == null )data.put(exchange, new ArrayList<String>());
            data.get(exchange).add(item.getCoinName());
        }

        Gson gson = new Gson();
        String params = gson.toJson(data);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), params);
        Log.i("seongenie", "params : " + params);

        coinService.getFavors(requestBody).enqueue(callback);
    }


    public void getOrderBook(BasicCoin coin, Callback<OrderBookDataView> callback){
        String exchange = coin.getExchange();
        String coinName = coin.getCoinName();

        coinService.getOrderBook(exchange, coinName).enqueue(callback);
    }



    public void getTradeHistory(BasicCoin coin, Callback<TradeHistoryView> callback){
        String exchange = coin.getExchange();
        String coinName = coin.getCoinName();

        coinService.getTradeHistory(exchange, coinName).enqueue(callback);
    }

}
