package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.dev.seongenie.geniecoin.Fragment.OrderBook.AnimatedExpandableListView;
import com.dev.seongenie.geniecoin.Fragment.OrderBook.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.Api_Client;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.BalanceView;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.BithumbRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {

    private HashMap<String, Double> balanceData;
    private String apiKey = "24ab10d0a3b68a8ffc68429e19894aaf";
    private String secretKey = "c3baffe3380a1a5f8263289997ff95c7";
    private Callback<BalanceView> callback;
    private AnimatedExpandableListView listView;
    private BalaceAdapter adapter;

    private int previousGroup=-1;

    public BalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        callback = new Callback<BalanceView>() {
            @Override
            public void onResponse(Call<BalanceView> call, Response<BalanceView> response) {

                if (response != null) {
                    Map<String, String> data = response.body().getData();
                    HashMap<String, Double> parsingData = new HashMap<String, Double>();
                    Set<String> keys = data.keySet();
                    for(String key : keys) {
                        StringTokenizer token = new StringTokenizer(key, "_");
                        String next = token.nextToken();
                        if(next.equals("total")) {
                            Double value = Double.valueOf(data.get(key));
                            if (value > 0) {
                                String coin = token.nextToken();
                                parsingData.put(coin.toUpperCase(), value);
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
        View v = inflater.inflate(R.layout.fragment_balance, null, false);

        List<GroupExchange> items = new ArrayList<GroupExchange>();

        GroupExchange item = new GroupExchange();

        item.exchange = "빗썸";

        BalanceCoin child = new BalanceCoin();
        child.coinName = "리플";
        item.items.add(child);

        child = new BalanceCoin();
        child.coinName = "비트코인 캐시";
        item.items.add(child);

        items.add(item);

        item = new GroupExchange();

        item.exchange = "코인원";

        child = new BalanceCoin();
        child.coinName = "비트코인";
        item.items.add(child);

        child = new BalanceCoin();
        child.coinName = "대시";
        item.items.add(child);

        items.add(item);

        adapter = new BalaceAdapter(getContext());
        adapter.setData(items);

        listView = (AnimatedExpandableListView) v.findViewById(R.id.listview_balance);
        listView.setAdapter(adapter);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        return v;
    }

    private void setMyBalance(HashMap<String, Double> data) {
        this.balanceData = data;
    }


    private void requestBalance() {
        String endPoint = "/info/balance";
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", "ALL");

        HashMap<String, String>[] headerParams = new Api_Client(apiKey, secretKey).getHeaders(endPoint, rgParams);
        BithumbRequest.getInstance().getBalances(headerParams[0], headerParams[1], callback);
    }


    private static class GroupExchange {
        String exchange;
        List<BalanceCoin> items = new ArrayList<BalanceCoin>();
    }

    private static class BalanceCoin {
        String exchangeName;
        String coinName;
        double qnty;
    }

    private static class ChildHolder {
        TextView title;
    }

    private static class GroupHolder {
        TextView title;
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
                holder.title = (TextView) convertView.findViewById(R.id.lblListItem);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.coinName);

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
                holder.title = (TextView) convertView.findViewById(R.id.lblListHeader);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.exchange);

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


