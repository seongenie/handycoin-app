package com.dev.seongenie.geniecoin.ServerConnection;

import com.dev.seongenie.geniecoin.CoinSources.ResponseFavor;
import com.dev.seongenie.geniecoin.Fragment.OrderBook.TradeHistoryView;
import com.dev.seongenie.geniecoin.View.OrderBookDataView;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

public interface CoinService {

    @GET("/coin/posscoin")
    Call<Map<String, List<String>>> getExchangeCoins();

    @POST("/coin/ticker")
    Call<ResponseFavor> getFavors(@Body RequestBody params);

    @GET("/coin/orderbook")
    Call<OrderBookDataView> getOrderBook(@Query("exchange") String exchange, @Query("coin") String coin);

    @GET("/coin/tradeHistory")
    Call<TradeHistoryView> getTradeHistory(@Query("exchange") String exchange, @Query("coin") String coin);

}
