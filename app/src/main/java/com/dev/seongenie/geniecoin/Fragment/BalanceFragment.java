package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.dev.seongenie.geniecoin.Api.CommonMessage;
import com.dev.seongenie.geniecoin.CoinSources.BasicCoin;
import com.dev.seongenie.geniecoin.CoinSources.Price;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.CoinSources.ResponseFavor;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.ServerConnection.RestfulApi;
import com.rey.material.widget.ImageButton;
import com.rey.material.widget.ImageView;
import com.rey.material.widget.TextView;

import com.dev.seongenie.geniecoin.Fragment.OrderBook.AnimatedExpandableListView;
import com.dev.seongenie.geniecoin.Fragment.OrderBook.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.Api_Client;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.BalanceView;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.BithumbRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {

    private HashMap<String, Double> balanceData;
    private String bithumb_apiKey = ""; // = "24ab10d0a3b68a8ffc68429e19894aaf";
    private String bithumb_secretKey = ""; // = "c3baffe3380a1a5f8263289997ff95c7";
    private Callback<BalanceView> callback;
    private Callback<ResponseFavor> favorCallback;
    private AnimatedExpandableListView listView;
    private BalaceAdapter adapter;
    private Timer timer = null;
    private BalanceFragment thisFragment = this;
    private ArrayList<BasicCoin> basicCoins = new ArrayList<BasicCoin>();
    private List<GroupExchange> items = new ArrayList<GroupExchange>();
    private String addExchange = "";

    private int REFRESH_INTERVAL = 2000;

    private int previousGroup=-1;

    public BalanceFragment() {
        // Required empty public constructor
    }

    private void setBithumbKey() {
        SharedPreferences pref = getActivity().getSharedPreferences("bithumb", Context.MODE_PRIVATE);
        bithumb_apiKey = pref.getString("apikey", "");
        bithumb_secretKey = pref.getString("secretkey", "");
        CommonMessage.getInstance().displaySnackbar(getString(R.string.save_bithumb_apikey_message), this, CommonMessage.OK_MESSAGE_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences pref = getActivity().getSharedPreferences("bithumb", Context.MODE_PRIVATE);
        bithumb_apiKey = pref.getString("apikey", "");
        bithumb_secretKey = pref.getString("secretkey", "");


        ImageButton addButton = (ImageButton)getActivity().findViewById(R.id.balance_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
                    @Override
                    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                        switch (index) {
                            case 0:
                                Log.i("seongenie", "bithumb clicked!! ");
                                SharedPreferences pref = getActivity().getSharedPreferences("bithumb", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("apikey"      , "8ac8ae87e6f0303cfda766329d4c7f51");
                                editor.putString("secretkey"   , "55817900f2e2981129048420e2169f19");
                                editor.commit();
                                setBithumbKey();
                                requestBalance();
                                dialog.dismiss();
                                break;
                            case 1:
                                Log.i("seongenie", "coinone clicked!! ");
                                addExchange = "coinone";
                                dialog.dismiss();
                                break;
                        }
                    }
                });

                adapter.add(new MaterialSimpleListItem.Builder(getContext())
                        .content("빗썸")
                        .icon(R.drawable.ic_bithumb)
                        .backgroundColor(Color.WHITE)
                        .build());

                adapter.add(new MaterialSimpleListItem.Builder(getContext())
                        .content("코인원")
                        .icon(R.drawable.ic_coinone)
                        .backgroundColor(Color.WHITE)
                        .iconPaddingDp(8)
                        .build());

                new MaterialDialog.Builder(getContext())
                        .title("거래소 선택")
                        .adapter(adapter, null)
                        .show();
            }

        });

        callback = new Callback<BalanceView>() {
            @Override
            public void onResponse(Call<BalanceView> call, Response<BalanceView> response) {

                if (response != null) {
                    Map<String, String> data = response.body().getData();
                    HashMap<String, Double> parsingData = new HashMap<String, Double>();
                    Set<String> keys = data.keySet();
                    basicCoins.clear();
                    for(String key : keys) {
                        StringTokenizer token = new StringTokenizer(key, "_");
                        String next = token.nextToken();
                        if(next.equals("total")) {
                            Double value = Double.valueOf(data.get(key));
                            if (value > 0) {
                                String coin = token.nextToken();
                                String exchange = coin.toUpperCase();
                                parsingData.put(exchange, value);
                                basicCoins.add(new BasicCoin("bithumb", exchange));
                            }
                        }
                    }
                    setMyBalance(parsingData);
                }
            }

            @Override
            public void onFailure(Call<BalanceView> call, Throwable t) {
                Log.i("seongenie", "receive failed");
            }
        };


        favorCallback = new Callback<ResponseFavor>() {
            @Override
            public void onResponse(Call<ResponseFavor> call, Response<ResponseFavor> response) {
                try {

                    Map<String, Map<String, Price>> data = response.body().getData();

                    Set<String> exchangeKeys = data.keySet();
                    for (String exchangeKey : exchangeKeys) {
                        Map<String, Price> coinInfo = data.get(exchangeKey);
                        Set<String> coinKeys = coinInfo.keySet();
                        for (String coinKey : coinKeys) {
                            Price price = coinInfo.get(coinKey);
                            double lastPrice = price.getLastPrice();
                            double firstPrice = price.getFirstPrice();
                            int updown = 0;
                            if (lastPrice - firstPrice < 0) updown = -1;
                            else if (lastPrice - firstPrice > 0) updown = 1;
                            populate("bithumb", coinKey, lastPrice, updown);
                        }
                    }

                    adapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    CommonMessage.getInstance().displaySnackbar(getString(R.string.server_connect_failed_message), thisFragment, CommonMessage.ERROR_MESSAGE_TYPE);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ResponseFavor> call, Throwable t) {

            }
        };



        View v = inflater.inflate(R.layout.fragment_balance, null, false);

        adapter = new BalaceAdapter(getContext());
        adapter.setData(items);

        listView = (AnimatedExpandableListView) v.findViewById(R.id.listview_balance);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        if( items.size() == 0 ) requestBalance();

        return v;
    }

    private void setMyBalance(HashMap<String, Double> data) {
        this.balanceData = data;
        items.clear();
        GroupExchange parent = new GroupExchange();
        parent.exchange = "bithumb";
        Set<String> keys = data.keySet();
        BalanceCoin KRW = new BalanceCoin();
        for(String key : keys) {
            BalanceCoin temp = new BalanceCoin();
            temp.coinName = key;
            temp.qnty = data.get(key);
            if (key.equals("KRW")) {
                KRW = temp;
                continue;
            }
            parent.items.add(temp);
        }

        Collections.sort(parent.items, new Comparator<BalanceCoin>() {
            @Override
            public int compare(BalanceCoin o1, BalanceCoin o2) {
                return o1.coinName.compareTo(o2.coinName);
            }
        });

        parent.items.add(KRW);
        items.add(parent);
        adapter.notifyDataSetChanged();
        for(int i=0; i<adapter.getGroupCount(); i++)listView.expandGroup(i);
    }

    private void populate(String exchange, String coin, double lastPrice, int updown) { // UP : 1, Neutral : 0, Down -1
        for (GroupExchange parent : items) {
            if(parent.exchange.equals(exchange)) {
                for (BalanceCoin child : parent.items) {
                    if(child.coinName.equals(coin)) {
                        child.lastPrice = lastPrice;
                        child.updown = updown;
                    }
                }
            }
        }
    }


    private void requestBalance() {
        if (bithumb_apiKey.isEmpty() || bithumb_secretKey.isEmpty()) {
            return;
        }

        String endPoint = "/info/balance";
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", "ALL");

        try {
            HashMap<String, String>[] headerParams = new Api_Client(bithumb_apiKey, bithumb_secretKey).getHeaders(endPoint, rgParams);
            BithumbRequest.getInstance().getBalances(headerParams[0], headerParams[1], callback);
        } catch (IllegalArgumentException e) {
            Log.w("seongenie", "error key");
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (timer == null) {
            timer = new Timer();
            doSomethingRepeatedly();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d("seongenie", "network call!!");
                if(basicCoins.size() > 0){
                    RestfulApi.getInstance().getFavors(basicCoins, favorCallback);
                }
                Log.d("seongenie", "network call finished!!");
            }
        }, 0, REFRESH_INTERVAL);
    }



    private static class GroupExchange {
        String exchange;
        List<BalanceCoin> items = new ArrayList<BalanceCoin>();
    }

    private static class BalanceCoin {
        String coinName;
        double qnty;
        double lastPrice;
        int updown;
    }

    private static class ChildHolder {
        TextView coinName;
        TextView qnty;
        TextView lastPrice;
        TextView evaluatedPrice;
        ImageView icon;

    }

    private static class GroupHolder {
        TextView exchangeName;
        TextView totalEvaluatedPrice;
    }


    /**
     * Adapter for our list of {@link GroupExchange}s.
     */
    private class BalaceAdapter extends AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupExchange> exchanges;

        public BalaceAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupExchange> exchanges) {
            this.exchanges = exchanges;
        }

        @Override
        public BalanceCoin getChild(int groupPosition, int childPosition) {
            return exchanges.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            BalanceCoin item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.balance_list_item, parent, false);
                holder.coinName = (TextView) convertView.findViewById(R.id.balance_coin_name);
                holder.qnty = (TextView) convertView.findViewById(R.id.balance_qnty);
                holder.icon = (ImageView) convertView.findViewById(R.id.balance_coin_image);
                holder.lastPrice = (TextView) convertView.findViewById(R.id.balance_price);
                holder.evaluatedPrice = (TextView) convertView.findViewById(R.id.balance_evaluation);

                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.coinName.setText(item.coinName);
            holder.coinName.setTypeface(MainActivity.nanumgothicbold);
            holder.qnty.setText(String.format("%,.6f 개", item.qnty));
            holder.lastPrice.setText(String.format("%,.0f 원", item.lastPrice));
            holder.evaluatedPrice.setText(String.format("%,.0f 원", item.lastPrice * item.qnty));
            holder.icon.setImageResource(MainActivity.getCoinIcon(item.coinName));

            int updownColor = 0;
            if(item.updown > 0) {
                updownColor = Color.RED;

            } else if(item.updown == 0) {
                updownColor = Color.BLACK;
            } else {
                updownColor = Color.BLUE;
            }
            holder.lastPrice.setTextColor(updownColor);

            if (item.coinName.equals("KRW")) {
                holder.qnty.setVisibility(View.INVISIBLE);
                holder.lastPrice.setVisibility(View.INVISIBLE);
                holder.evaluatedPrice.setText(String.format("%,.0f 원", item.qnty));
            }
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return exchanges.get(groupPosition).items.size();
        }

        @Override
        public GroupExchange getGroup(int groupPosition) {
            return exchanges.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return exchanges.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupExchange item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.header_balance, parent, false);
                holder.exchangeName = (TextView) convertView.findViewById(R.id.balance_exchange_name);
                holder.totalEvaluatedPrice = (TextView) convertView.findViewById(R.id.total_evaluation);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            double total = new Double(0);
            for(BalanceCoin child : item.items) {
                total += child.coinName.equals("KRW") ? child.qnty : (child.qnty * child.lastPrice);
            }

            holder.exchangeName.setText(MainActivity.exchangeToKorean(item.exchange));
            holder.totalEvaluatedPrice.setText(String.format("총 : %,.0f 원", total));

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }
}


