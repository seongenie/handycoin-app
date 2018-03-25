package com.dev.seongenie.geniecoin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;

/**
 * Created by seongjinlee on 2017. 10. 31..
 */

public class AlarmAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_add);

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_up, R.anim.leave_up);
    }
}