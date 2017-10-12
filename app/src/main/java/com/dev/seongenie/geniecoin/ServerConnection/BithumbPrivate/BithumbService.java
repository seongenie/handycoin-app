package com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate;

import android.util.ArrayMap;

import com.dev.seongenie.geniecoin.View.OrderBookDataView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by seongjinlee on 2017. 10. 9..
 */

public interface BithumbService {
    @FormUrlEncoded
    @POST("/info/balance")
    Call<BalanceView> getBalance(@HeaderMap HashMap<String, String> apiKey, @FieldMap HashMap<String, String> params);

}
