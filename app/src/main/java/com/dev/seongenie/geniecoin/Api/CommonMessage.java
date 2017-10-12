package com.dev.seongenie.geniecoin.Api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dev.seongenie.geniecoin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seongjinlee on 2017. 10. 3..
 */

public class CommonMessage {

    public static int OK_MESSAGE_TYPE = 1;
    public static int ERROR_MESSAGE_TYPE = 0;
    private int duration = 1400;
    private static CommonMessage mInstance = null;

    public static CommonMessage getInstance() {
        if (mInstance == null) { mInstance = new CommonMessage(); }
        return mInstance;
    }

    public void displaySnackbar(String text, Fragment fragment, int messageType) {
        Snackbar snack = Snackbar.make(fragment.getView(), text, Snackbar.LENGTH_LONG);
        View v = snack.getView();

        int color = messageType == ERROR_MESSAGE_TYPE ? R.color.message_error : R.color.message_ok;
        v.setBackgroundColor(fragment.getResources().getColor(color));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);
        snack.setDuration(duration);
        snack.show();
    }


}
