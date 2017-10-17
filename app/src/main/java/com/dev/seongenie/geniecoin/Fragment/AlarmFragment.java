package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.CoinSources.AlarmCoin;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.Service.PriceCheckService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {
    @BindView(R.id.alarm_recycler)
    RecyclerView recyclerView;
    AlarmAdapter alarmAdapter;

    ArrayList<AlarmCoin> alarmCoins;

    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, v);

//        SqliteRepository.getInstance(getContext()).insertAlarm(new AlarmCoin("bithumb", "XRP", 286, 1, true));
//        SqliteRepository.getInstance(getContext()).insertAlarm(new AlarmCoin("bithumb", "BCH", 420000, 1, true));
//        SqliteRepository.getInstance(getContext()).insertAlarm(new AlarmCoin("bithumb", "BTC", 6500000, 1, true));
//        alarmCoins = SqliteRepository.getInstance(getContext()).getAlarmList(false);

        alarmCoins = new ArrayList<AlarmCoin>();
        alarmCoins.add(new AlarmCoin("bithumb", "XRP", 286, 1, true));
        alarmCoins.add(new AlarmCoin("bithumb", "XRP", 289, 1, true));
        alarmCoins.add(new AlarmCoin("bithumb", "XRP", 288, 1, true));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        alarmAdapter = new AlarmAdapter(this.alarmCoins);
        recyclerView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();

//        Start intent ( price check service )
//        Intent intent = new Intent(getActivity(), PriceCheckService.class);
//        startService(intent);
        return v;
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
            public FrameLayout itemLayout;

            public AlarmViewHolder(View v) {
                super(v);
                coinIcon = (ImageView) v.findViewById(R.id.ic_coin);
                coinName = (TextView) v.findViewById(R.id.coin_name);
                exchangeName = (TextView) v.findViewById(R.id.exchange_name);
                itemLayout = (FrameLayout) v.findViewById(R.id.item_layout);

                if (MainActivity.nanumgothic != null) {
                    coinName.setTypeface(MainActivity.nanumgothicbold);
                    exchangeName.setTypeface(MainActivity.nanumgothic);
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

        }

    }
}