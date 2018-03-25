package com.dev.seongenie.geniecoin.Api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.dev.seongenie.geniecoin.Api.CommonMessage;
import com.dev.seongenie.geniecoin.CoinSources.AlarmCoin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

public class SqliteRepository extends SQLiteOpenHelper {
    private static SqliteRepository mInstance = null;
    public static final String DATABASE_NAME = "GENIECOIN.db";
    public static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public static SqliteRepository getInstance(Context context){
        if(mInstance == null) {
            mInstance = new SqliteRepository(context);
        }
        return mInstance;
    }



    private SqliteRepository(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE FAVORITE_COIN (exchange TEXT NOT NULL, coin TEXT NOT NULL, favor_yn BOOLEAN NOT NULL default 'false', PRIMARY KEY(exchange, coin));");
        db.execSQL("create table alarm_coin (exchange text not null, coin text not null," +
                " goal_price double not null, updown int not null, onoff boolean default 'false')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertFavor(String exchange, String coin, boolean yn) {
        // DB에 입력한 값으로 행 추가
        try{
            db.execSQL("INSERT INTO FAVORITE_COIN(exchange, coin, favor_yn) VALUES('" + exchange + "', '" + coin + "', '" + yn + "');");
        }
        catch (SQLiteConstraintException e){
            Log.w("seongenie", "exchange : " + exchange + ", coin : ");
        }
    }

    public void resetAllFavors() {
        // DB에 입력한 값으로 행 추가
        try{
            db.execSQL("UPDATE FAVORITE_COIN SET favor_yn = 'false'");
        }
        catch (SQLiteConstraintException e){
            Log.w("seongenie", e.getMessage());
        }
    }


    public void updateFavor(String exchange, String coin) {
        // DB에 입력한 값으로 행 추가
        try{
            db.execSQL("UPDATE FAVORITE_COIN SET favor_yn = 'true' where exchange = '" + exchange + "' and coin = '" + coin + "'");
        }
        catch (SQLiteConstraintException e){
            Log.w("seongenie", "exchange : " + exchange + ", coin : ");
        }
    }


    public ArrayMap<String, List<String>> getFavors() {
        ArrayMap<String, List<String>> result = new ArrayMap<>();
        String[] exchanges;

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT DISTINCT exchange FROM FAVORITE_COIN where favor_yn = 'true'", null);
        exchanges = new String[cursor.getCount()];

        int idx = 0;
        while (cursor.moveToNext()) {
            exchanges[idx++] = cursor.getString(0);
        }

        for (String exchange: exchanges) {
            List<String> coins = new ArrayList<String>();
            cursor = db.rawQuery("SELECT exchange, coin FROM FAVORITE_COIN WHERE exchange = '" + exchange + "' and favor_yn = 'true';", null);
            while (cursor.moveToNext()) {
                /** coin filtered by where clause */
                coins.add(cursor.getString(1));
            }
            result.put(exchange, coins);
        }

        return result;
    }

    public ArrayMap<String, List<String>> getAvailableCoins() {
        ArrayMap<String, List<String>> result = new ArrayMap<>();
        String[] exchanges;

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT DISTINCT exchange FROM FAVORITE_COIN", null);
        exchanges = new String[cursor.getCount()];

        int idx = 0;
        while (cursor.moveToNext()) {
            exchanges[idx++] = cursor.getString(0);
        }

        for (String exchange: exchanges) {
            List<String> coins = new ArrayList<String>();
            cursor = db.rawQuery("SELECT exchange, coin FROM FAVORITE_COIN WHERE exchange = '" + exchange + "'", null);
            while (cursor.moveToNext()) {
                /** coin filtered by where clause */
                coins.add(cursor.getString(1));
            }
            result.put(exchange, coins);
        }

        return result;
    }


    // false : All, true : only true
    public ArrayList<AlarmCoin> getAlarmList(boolean onoff) {
        ArrayList<AlarmCoin>  alarmCoins = new ArrayList<AlarmCoin>();

        String sql = "select exchange, coin, goal_price, updown, onoff from alarm_coin";
        sql += onoff ? " where onoff = 'true'" : "";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            AlarmCoin temp = new AlarmCoin(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3),
                    (cursor.getInt(4) != 0));
            alarmCoins.add(temp);
        }
        return alarmCoins;
    }


    public boolean insertAlarm(AlarmCoin coin) {
        try {
            db.execSQL("insert into alarm_coin(exchange, coin, goal_price, updown, onoff) VALUES('"
                    + coin.getExchange()
                    + "', '" + coin.getCoinName() + "', "
                    + coin.getGoal() + ", "
                    + coin.getUpdown() + ", '"
                    + coin.isOnOff() + "')");
            return true;
        } catch (Exception e) {
            Log.e("seongenie", "db insert error(AlarmCoin)");
            e.printStackTrace();
            return false;
        }
    }

//    public ArrayList<AlarmCoin> deleteAlarm(boolean onoff) {
//        ArrayList<AlarmCoin>  alarmCoins = new ArrayList<AlarmCoin>();
//
//        String sql = "select exchange, coin, goal_price, updown, onoff from alarm_coin";
//        sql += onoff ? " where onoff = 'true'" : "";
//        Cursor cursor = db.rawQuery(sql, null);
//        while (cursor.moveToNext()) {
//            AlarmCoin temp = new AlarmCoin(
//                    cursor.getString(0),
//                    cursor.getString(1),
//                    cursor.getDouble(2),
//                    cursor.getInt(3),
//                    (cursor.getInt(4) != 0));
//            alarmCoins.add(temp);
//        }
//        return alarmCoins;
//    }
//
//    public ArrayList<AlarmCoin> updateAlarm(boolean onoff) {
//        ArrayList<AlarmCoin>  alarmCoins = new ArrayList<AlarmCoin>();
//
//        String sql = "select exchange, coin, goal_price, updown, onoff from alarm_coin";
//        sql += onoff ? " where onoff = 'true'" : "";
//        Cursor cursor = db.rawQuery(sql, null);
//        while (cursor.moveToNext()) {
//            AlarmCoin temp = new AlarmCoin(
//                    cursor.getString(0),
//                    cursor.getString(1),
//                    cursor.getDouble(2),
//                    cursor.getInt(3),
//                    (cursor.getInt(4) != 0));
//            alarmCoins.add(temp);
//        }
//        return alarmCoins;
//    }




}