package com.dev.seongenie.geniecoin.Api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.dev.seongenie.geniecoin.Api.CommonMessage;

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
        db.execSQL("CREATE TABLE FAVORITE_COIN (_id TEXT PRIMARY KEY, exchange TEXT, coin TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertFavor(String exchange, String coin) {
        // DB에 입력한 값으로 행 추가
        try{
            db.execSQL("INSERT INTO FAVORITE_COIN VALUES('" + exchange + "_" + coin + "', '" + exchange + "', '" + coin + "');");
        }
        catch (SQLiteConstraintException e){
            Log.w("seongenie", "exchange : " + exchange + ", coin : " + coin);
        }
    }

    public void deleteAllFavors() {
        // DB에 입력한 값으로 행 추가
        try{
            db.execSQL("DELETE FROM FAVORITE_COIN;");
        }
        catch (SQLiteConstraintException e){
            Log.w("seongenie", e.getMessage());
        }
    }


    public ArrayMap<String, List<String>> getFavors() {
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
            cursor = db.rawQuery("SELECT exchange, coin FROM FAVORITE_COIN WHERE exchange = '" + exchange + "';", null);
            while (cursor.moveToNext()) {
                /** coin filtered by where clause */
                coins.add(cursor.getString(1));
            }
            result.put(exchange, coins);
        }

        return result;
    }

}