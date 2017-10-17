package com.dev.seongenie.geniecoin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.ServerConnection.RestfulApi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by seongjinlee on 2017. 10. 7..
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestfulApi.getInstance().getExchangeCoins(new Callback<Map<String, List<String>>>() {
            @Override
            public void onResponse(Call<Map<String, List<String>>> call, Response<Map<String, List<String>>> response) {
                Map<String, List<String>> possibleCoinList = response.body();
                Set<String> exchanges = possibleCoinList.keySet();
                for (String exchange : exchanges) {
                    List<String> coins = possibleCoinList.get(exchange);
                    for(String coin : coins) {
                        SqliteRepository.getInstance(getApplicationContext()).insertFavor(exchange, coin, false);
                    }
                }
                changeActivity();
            }

            @Override
            public void onFailure(Call<Map<String, List<String>>> call, Throwable t) {
                changeActivity();
            }
        });
    }

    private void changeActivity() {
        final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setCoinList(Map<String, List<String>> data) {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}