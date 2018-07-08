package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.dev.seongenie.geniecoin.Api.CommonMessage;
import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.CoinSources.Price;
import com.dev.seongenie.geniecoin.CoinSources.ReceiveFavorCoin;
import com.dev.seongenie.geniecoin.CoinSources.ResponseFavor;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.ServerConnection.RestfulApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavorFragment extends Fragment implements AAH_FabulousFragment.Callbacks {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    FavorsAdapter adapter;
    View v;
    FavorDialogFragment dialogFrag;

    @BindView(R.id.add_coin_message)
    TextView addCoinMessage;

    private Timer timer;
    private int REFRESH_INTERVAL = 3000;
    private Fragment thisFragment = this;
    private ImageButton favorSortButton;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerOnItemClickListener.OnItemClickListener onItemClickListener = null;
    Callback<ResponseFavor> callback;

    /** Order Rule Initialize */
    private HashMap<String, Comparator<ReceiveFavorCoin>> orderMap = new HashMap<String, Comparator<ReceiveFavorCoin>>(){
        {
            put("exchange", new Comparator<ReceiveFavorCoin>() {
                @Override
                public int compare(ReceiveFavorCoin c1, ReceiveFavorCoin c2) {
                    if(c1.getExchange().isEmpty())return 1;
                    if(c2.getExchange().isEmpty())return -1;
                    return c1.getExchange().compareTo(c2.getExchange());
                }
            });

            put("coin", new Comparator<ReceiveFavorCoin>() {
                @Override
                public int compare(ReceiveFavorCoin c1, ReceiveFavorCoin c2) {
                    if(c1.getCoinName().isEmpty())return 1;
                    if(c2.getCoinName().isEmpty())return -1;
                    return c1.getCoinName().compareTo(c2.getCoinName());
                }
            });

            put("exchangeReverse", new Comparator<ReceiveFavorCoin>() {
                @Override
                public int compare(ReceiveFavorCoin c1, ReceiveFavorCoin c2) {
                    if(c1.getExchange().isEmpty())return 1;
                    if(c2.getExchange().isEmpty())return -1;
                    return c2.getExchange().compareTo(c1.getExchange());
                }
            });

            put("coinReverse", new Comparator<ReceiveFavorCoin>() {
                @Override
                public int compare(ReceiveFavorCoin c1, ReceiveFavorCoin c2) {
                    if(c1.getCoinName().isEmpty())return 1;
                    if(c2.getCoinName().isEmpty())return -1;
                    return c2.getCoinName().compareTo(c1.getCoinName());
                }
            });


        }
    };

    public static List<ReceiveFavorCoin> coins = new ArrayList<>();


    public FavorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_favor, container, false);
        ButterKnife.bind(this, v);

        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        getActivity().openOrCreateDatabase(SqliteRepository.DATABASE_NAME, Context.MODE_PRIVATE, null);

        SqliteRepository repo = SqliteRepository.getInstance(this.getContext());
        addCoinMessage = (TextView) v.findViewById(R.id.add_coin_message);
        addCoinMessage.setTypeface(MainActivity.nanumgothic);
        populate();


        dialogFrag = FavorDialogFragment.newInstance();
        dialogFrag.setParentFab(fab);
        dialogFrag.setCallbacks(this);
        dialogFrag.setFavorDialogListener(new FavorDialogFragment.FavorDialogListener() {
            @Override
            public void onClickCheck() {
                populate();
                CommonMessage.getInstance().displaySnackbar(getString(R.string.save_coin_message), thisFragment, CommonMessage.OK_MESSAGE_TYPE);
                adapter.notifyDataSetChanged();
                RestfulApi.getInstance().getFavors(coins, callback);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFrag.setCallbacks(FavorFragment.this);
                dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        TextView favorTitleCoin = (TextView) v.findViewById(R.id.favor_title_coin);
        TextView favorTitlePrice = (TextView) v.findViewById(R.id.favor_title_price);
        TextView favorTitleChange = (TextView) v.findViewById(R.id.favor_title_change);

        favorTitleCoin.setTypeface(MainActivity.nanumgothic);
        favorTitlePrice.setTypeface(MainActivity.nanumgothic);
        favorTitleChange.setTypeface(MainActivity.nanumgothic);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new FavorsAdapter(this.coins);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerOnItemClickListener(getActivity(), recyclerView, onItemClickListener));

/*        recyclerView.addOnItemTouchListener(new RecyclerOnItemClickListener(getActivity(),
                recyclerView,
                new RecyclerOnItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View v, int position) {
                        Log.i("seongenie", position + " short clicked!");

                    }

                    @Override
                    public void onItemLongClick(View v, int position) {
                        Log.i("seongenie", position + " long clicked!");
                    }
                })
        );



        /** scroll action (scroll reach at end or not */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                /** Scroll reach at last item */
                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount && pastVisiblesItems > 0)
                {
                    Log.d("seongenie", "fab hide!");
                    fab.hide();
                }
                else {
                    Log.d("seongenie", "fab show!");
                    fab.show();
                }
            }
        });


        callback = new Callback<ResponseFavor>() {
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
                            double firstPrice = price.getFirstPrice();
                            double lastPrice = price.getLastPrice();
                            for (ReceiveFavorCoin item : coins) {
                                if (item.getExchange().equals(exchangeKey) && item.getCoinName().equals(coinKey)) {

                                    /** blink effect */
                                    if (item.getLastPrice() != lastPrice) item.setRefresh(true);
                                    else item.setRefresh(false);

                                    item.setKrwRate(response.body().getCurrencyRate());
                                    item.setFirstPrice(firstPrice);
                                    item.setLastPrice(lastPrice);
                                }
                            }
                        }
                    }
                } catch (NullPointerException e) {
//                    CommonMessage.getInstance().displaySnackbar(getString(R.string.server_connect_failed_message), thisFragment, CommonMessage.ERROR_MESSAGE_TYPE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseFavor> call, Throwable t) {
                CommonMessage.getInstance().displaySnackbar(getString(R.string.server_connect_failed_message), thisFragment, CommonMessage.ERROR_MESSAGE_TYPE);
            }
        };

        favorSortButton = (ImageButton) getActivity().findViewById(R.id.favor_sort);
        favorSortButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("seongenie", "popup button clicked!!");
                PopupMenu popup = new PopupMenu(getContext(), favorSortButton);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.exchange_order :
                                setOrderRule("exchange");
                                Collections.sort(coins, getOrderRule());
                                adapter.notifyDataSetChanged();
                                break;
                            case R.id.coin_order :
                                setOrderRule("coin");
                                Collections.sort(coins, getOrderRule());
                                adapter.notifyDataSetChanged();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        return v;
    }

    private Comparator<ReceiveFavorCoin> getOrderRule() {
        SharedPreferences pref = getActivity().getSharedPreferences("favorCoin", Context.MODE_PRIVATE);
        String orderRule = pref.getString("orderRule", "coin");
        return orderMap.get(orderRule);
    }

    private void setOrderRule(String rule) {
        SharedPreferences pref = getActivity().getSharedPreferences("favorCoin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("orderRule", rule);
        editor.commit();
        CommonMessage.getInstance().displaySnackbar(getString(R.string.set_order_message), this, CommonMessage.OK_MESSAGE_TYPE);
    }


    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                RestfulApi.getInstance().getFavors(coins, callback);
                Log.d("seongenie", "coin_size : " + coins.size());
                if(coins.size() <= 1) {
                    timer.cancel();
                    timer = null;
                }
            }
        }, 0, REFRESH_INTERVAL);
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


    private void populate() {
        this.coins.clear();
        ArrayMap<String, List<String>> arrayMap = SqliteRepository.getInstance(getContext()).getFavors();
        Set keys = arrayMap.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String exchange = it.next();
            List<String> coins = arrayMap.get(exchange);
            for(String coin : coins) {
                this.coins.add(new ReceiveFavorCoin(exchange, coin, 0, 0));
            }
        }

        Collections.sort(coins, getOrderRule());

        if(coins.size() == 0) {
            addCoinMessage.setVisibility(View.VISIBLE);
        } else {
            addCoinMessage.setVisibility(View.INVISIBLE);
        }

        this.coins.add(new ReceiveFavorCoin("", "", 0, 0));
        if (callback != null && timer == null) {
            timer = new Timer();
            doSomethingRepeatedly();
        }
    }

    private int removeCoin(ReceiveFavorCoin coin) {
        int pos = coins.indexOf(coin);
        coins.remove(coin);
        adapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addCoin(int pos, ReceiveFavorCoin coin) {
        coins.add(pos, coin);
        adapter.notifyItemInserted(pos);
    }

    @Override
    public void onResult(Object result) {
        Log.i("seongenie", "onResult: " + result.toString());

        if (result.toString().equalsIgnoreCase("swiped_down")) {
            //do something or nothing
        } else {
            //handle result
        }
    }

    public void setOnItemClickListener(RecyclerOnItemClickListener.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}