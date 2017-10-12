package com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.dev.seongenie.geniecoin.CoinSources.BasicCoin;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.CoinSources.ResponseFavor;
import com.dev.seongenie.geniecoin.ServerConnection.CoinService;
import com.dev.seongenie.geniecoin.View.OrderBookDataView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by seongjinlee on 2017. 10. 10..
 */

public class BithumbRequest {
    public static final BithumbRequest instance = new BithumbRequest();
    private Retrofit retrofit;
    BithumbService bithumbService = null;
    private BithumbRequest(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bithumb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bithumbService = retrofit.create(BithumbService.class);
    }

    public static BithumbRequest getInstance() {
        return instance;
    }

    public void getBalances(HashMap<String, String> header, HashMap<String, String> params, Callback<BalanceView> callBack) {
        Call<BalanceView> call = bithumbService.getBalance(header, params);
        call.enqueue(callBack);
    }

}
