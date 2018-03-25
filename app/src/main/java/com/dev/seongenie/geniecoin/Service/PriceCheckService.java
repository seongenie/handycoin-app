package com.dev.seongenie.geniecoin.Service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.CoinSources.AlarmCoin;
import com.dev.seongenie.geniecoin.CoinSources.BasicCoin;
import com.dev.seongenie.geniecoin.CoinSources.Price;
import com.dev.seongenie.geniecoin.CoinSources.ResponseFavor;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.ServerConnection.RestfulApi;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by seongjinlee on 2017. 10. 16..
 */

public class PriceCheckService extends Service{
    private Timer timer = null;
    private int REFRESH_INTERVAL; // default 15 min;
    private Callback<ResponseFavor> callback = null;
    private ArrayList<AlarmCoin> reqCoins = new ArrayList<AlarmCoin>();

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("alarm", Context.MODE_PRIVATE);
        REFRESH_INTERVAL = sharedPreferences.getInt("frequency", 15 * 60 * 1000);
        reqCoins = SqliteRepository.getInstance(getApplicationContext()).getAlarmList(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        callback = new Callback<ResponseFavor>() {
            @Override
            public void onResponse(Call<ResponseFavor> call, Response<ResponseFavor> response) {
                ArrayList<AlarmCoin> removeCoins = new ArrayList<AlarmCoin>();
                Set<String> keySet = response.body().getData().keySet();
                for (AlarmCoin coin : reqCoins) {
                    Map<String, Price> exchange = response.body().getData().get(coin.getExchange());
                    Price price = exchange.get(coin.getCoinName());
                    double goalPrice = coin.getGoal();
                    int upDown = coin.getUpdown();
                    if (upDown > 0 && price.getLastPrice() >= goalPrice) {
                        Log.i("seongenie", "목표가 도달 : " + goalPrice + ", 현재가 : " + price.getLastPrice());
                        notifyAlarm(coin, goalPrice);
                        removeCoins.add(coin);
                    } else if (upDown < 0 && price.getLastPrice() <= goalPrice) {
                        Log.i("seongenie", "목표가 도달 : " + goalPrice + ", 현재가 : " + price.getLastPrice());
                        notifyAlarm(coin, goalPrice);
                        removeCoins.add(coin);
                    }
                }
                
                for (AlarmCoin removeCoin : removeCoins) {
                    reqCoins.remove(removeCoin);
                }
            }

            @Override
            public void onFailure(Call<ResponseFavor> call, Throwable t) {

            }
        };

        if(timer == null) {
            timer = new Timer();
            doSomethingRepeatedly();
        }

        return START_REDELIVER_INTENT;
    }

    private void doSomethingRepeatedly() {
        if (timer != null) {
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    RestfulApi.getInstance().getFavors(reqCoins, callback);
                    if (reqCoins.isEmpty()) {
                        if (timer != null)timer.cancel();
                        stopSelf();
                        return;
                    }
                }
            }, 0, REFRESH_INTERVAL);
        }
    }

    private void notifyAlarm(BasicCoin coin, double price) {

        String textMessage = coin.getCoinName() + "(" + coin.getExchange() + ")이 목표가 " + String.format("%.0f원", price) + " 에 도달했습니다!";

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "")
                        .setSmallIcon(R.drawable.ic_bithumb)
                        .setContentTitle("목표가 도달 알림")
                        .setContentText(textMessage);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify((int)Math.random() * 1000 + 1, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("seongenie", "service 종료");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

}
