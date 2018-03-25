package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.seongenie.geniecoin.AlarmAddActivity;
import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.CoinSources.AlarmCoin;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.Service.PriceCheckService;
import com.dev.seongenie.geniecoin.SettingActivity;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {
    @BindView(R.id.alarm_recycler)
    RecyclerView recyclerView;
    AlarmAdapter alarmAdapter;

    @BindView(R.id.add_alarm_message)
    TextView emptyMessageTextView;

    private ArrayList<AlarmCoin> alarmCoins;
    private boolean showCheckBox = false;

    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, v);

//        SqliteRepository.getInstance(getContext()).insertAlarm(new AlarmCoin("bithumb", "XRP", 229, 1, true));
//        SqliteRepository.getInstance(getContext()).insertAlarm(new AlarmCoin("bithumb", "BCH", 420000, 1, true));
//        SqliteRepository.getInstance(getContext()).insertAlarm(new AlarmCoin("bithumb", "BTC", 6500000, 1, true));
        populate();

//        alarmCoins = new ArrayList<AlarmCoin>();
//        alarmCoins.add(new AlarmCoin("bithumb", "XRP", 286, 1, true));
//        alarmCoins.add(new AlarmCoin("bithumb", "XRP", 289, 1, true));
//        alarmCoins.add(new AlarmCoin("bithumb", "XRP", 288, 1, true));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        alarmAdapter = new AlarmAdapter(this.alarmCoins);
        recyclerView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AlarmAddActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_down, R.anim.leave_down);
            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerOnItemClickListener(getActivity(),
                recyclerView,
                new RecyclerOnItemClickListener.OnItemClickListener(){
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.i("seongenie", position + " short clicked!");
                        if(!showCheckBox)return;
                        CheckBox checkBox = (CheckBox)v.findViewById(R.id.remove_checkBox);
                        checkBox.setChecked(!checkBox.isChecked());
                    }

                    @Override
                    public void onItemLongClick(View v, int position) {
                        Log.i("seongenie", position + " long clicked!");
                        showCheckBox = !showCheckBox;
//                        if(!showCheckBox)return;
                        CheckBox checkBox = (CheckBox)v.findViewById(R.id.remove_checkBox);
                        checkBox.setChecked(!checkBox.isChecked());
                        alarmAdapter.notifyDataSetChanged();
                    }
                })
        );






//        Start intent ( price check service )
//        Intent intent = new Intent(getActivity(), PriceCheckService.class);
//        getActivity().startService(intent);
        return v;
    }

    private void populate() {
        alarmCoins = SqliteRepository.getInstance(getContext()).getAlarmList(true);
        if(alarmCoins.size() == 0) {
            emptyMessageTextView.setVisibility(View.VISIBLE);
        } else {
            emptyMessageTextView.setVisibility(View.INVISIBLE);
        }
    }


    private class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<AlarmCoin> items;

        /**
         * References to the views for each data item
         **/
        private class AlarmViewHolder extends RecyclerView.ViewHolder {
            public TextView coinName;
            public TextView exchangeName;
            public ImageView coinIcon;
            public TextView goalPrice;
            public TextView updown;
            public Switch onoffSwitch;
            public FrameLayout itemLayout;
            public CheckBox checkBox;

            public AlarmViewHolder(View v) {
                super(v);
                coinIcon = (ImageView) v.findViewById(R.id.ic_coin);
                coinName = (TextView) v.findViewById(R.id.coin_name);
                exchangeName = (TextView) v.findViewById(R.id.exchange_name);
                itemLayout = (FrameLayout) v.findViewById(R.id.item_layout);
                goalPrice = (TextView) v.findViewById(R.id.goal_textview);
                updown = (TextView) v.findViewById(R.id.updown_textview);;
                onoffSwitch = (Switch) v.findViewById(R.id.onoff_switch);
                checkBox = (CheckBox) v.findViewById(R.id.remove_checkBox);



                if (MainActivity.nanumgothic != null) {
                    coinName.setTypeface(MainActivity.nanumgothicbold);
                    exchangeName.setTypeface(MainActivity.nanumgothic);
                    goalPrice.setTypeface(MainActivity.nanumgothicbold);
                    updown.setTypeface(MainActivity.nanumgothic);
                }
            }
        }

        /**
         * Constructor
         **/
        public AlarmAdapter(List<AlarmCoin> items) {
            this.items = items;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.alarm_item, parent, false);
            return new AlarmViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AlarmCoin item = items.get(position);
            AlarmViewHolder vh = (AlarmViewHolder) holder;
            vh.coinName.setText(item.getCoinName());
            vh.exchangeName.setText(MainActivity.exchangeToKorean(item.getExchange()));
            vh.coinIcon.setImageResource(MainActivity.getCoinIcon(item.getCoinName()));
            vh.goalPrice.setText(String.format("%,.0f 원", item.getGoal()));
            vh.goalPrice.setTextColor(Color.BLACK);
            vh.updown.setText(item.getUpdown() > 0 ? "이상" : "이하");
            vh.updown.setTextColor(item.getUpdown() > 0 ? Color.RED : Color.BLUE);
            vh.onoffSwitch.setChecked(item.isOnOff());


            if (showCheckBox) {
                vh.coinIcon.setVisibility(View.INVISIBLE);
                vh.checkBox.setVisibility(View.VISIBLE);
            } else {
                vh.coinIcon.setVisibility(View.VISIBLE);
                vh.checkBox.setVisibility(View.INVISIBLE);
            }
        }
    }

}