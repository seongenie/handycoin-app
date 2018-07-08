package com.dev.seongenie.geniecoin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.Fragment.AlarmFragment;
import com.dev.seongenie.geniecoin.Fragment.BalanceFragment;
import com.dev.seongenie.geniecoin.Fragment.OrderBookFragment;
import com.dev.seongenie.geniecoin.Fragment.FavorFragment;
import com.dev.seongenie.geniecoin.Fragment.RecyclerOnItemClickListener;
import com.dev.seongenie.geniecoin.Fragment.TransactionFragment;
import com.rey.material.widget.ImageButton;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    //ViewPager
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    //Title
    @BindView(R.id.main_title)
    TextView mainTitle;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fab_alarm)
    FloatingActionButton alarmFab;

    // favor fragment에서 구현
//    @BindView(R.id.favor_sort)
//    ImageButton favorSortButton;

    //This is font
    public static Typeface nanumgothic;
    public static Typeface nanumgothicbold;
    public static Typeface materialIconFont;

    //Fragments
    FavorFragment favorFragment;
    OrderBookFragment orderBookFragment;
    TransactionFragment transactionFragment;
    BalanceFragment balanceFragment;
    AlarmFragment alarmFragment;
    MenuItem prevMenuItem;

    public static Map<String, List<String>> availableCoins = null;

    // 뒤로가기 버튼
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        availableCoins = SqliteRepository.getInstance(getApplicationContext()).getAvailableCoins();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        alarmFab.hide();

        nanumgothic = Typeface.createFromAsset(getAssets(), "fonts/nanumgothic.otf");
        nanumgothicbold = Typeface.createFromAsset(getAssets(), "fonts/nanumgothicbold.otf");
        materialIconFont = Typeface.createFromAsset(getAssets(), "fonts/material-icon-font.ttf");

//        favorSortButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("seongenie", "popup button clicked!!");
//                PopupMenu popup = new PopupMenu(getApplicationContext(), favorSortButton);
//                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.exchange_order :
//                                break;
//                            case R.id.coin_order :
//                                break;
//                        }
//                        return true;
//                    }
//                });
//                popup.show();
//            }
//        });


        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Main Title
        mainTitle = (TextView) findViewById(R.id.main_title);
        mainTitle.setTypeface(nanumgothicbold);

        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favor:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_orderbook:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_transaction:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.action_balance:
                                viewPager.setCurrentItem(3);
                                break;
                            case R.id.action_alarm:
                                viewPager.setCurrentItem(4);
                                break;
                        }
                        return false;
                    }
                });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                if (position != 0 ) {
                    findViewById(R.id.favor_sort).setVisibility(View.INVISIBLE);
                    fab.hide();

                }
                if (position != 3 )findViewById(R.id.balance_add).setVisibility(View.INVISIBLE);
                if (position != 4 ) alarmFab.hide();

                switch (position) {
                    case 0 :
                        mainTitle.setText(getString(R.string.tab1));
                        findViewById(R.id.favor_sort).setVisibility(View.VISIBLE);
                        fab.show();
                        break;
                    case 1 :
                        mainTitle.setText(getString(R.string.tab2));
                        break;
                    case 2 :
                        mainTitle.setText(getString(R.string.tab3));
                        break;
                    case 3 :
                        mainTitle.setText(getString(R.string.tab4));
                        findViewById(R.id.balance_add).setVisibility(View.VISIBLE);
                        break;
                    case 4 :
                        mainTitle.setText(getString(R.string.tab5));
                        alarmFab.show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // screen loading page count limit
        viewPager.setOffscreenPageLimit(0);

        ImageButton settingButton = (ImageButton) findViewById(R.id.button_setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_right, R.anim.leave_right);
            }
        });

        disableShiftMode(bottomNavigationView);

        setupViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        favorFragment = new FavorFragment();
        orderBookFragment = new OrderBookFragment();
        transactionFragment = new TransactionFragment();
        balanceFragment = new BalanceFragment();
        alarmFragment = new AlarmFragment();

        RecyclerOnItemClickListener.OnItemClickListener onItemClickListener = new RecyclerOnItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View v, int position) {
                Log.i("seongenie", position + " position clicked!!");
                if (FavorFragment.coins != null) {
                    if ((FavorFragment.coins.size()-1) != position) {
                        try {
                            orderBookFragment.setOrderBookCoin(FavorFragment.coins.get(position));
                            viewPager.setCurrentItem(1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            @Override
            public void onItemLongClick(View v, int position) {

            }
        };

        favorFragment.setOnItemClickListener(onItemClickListener);


        adapter.addFragment(favorFragment);
        adapter.addFragment(orderBookFragment);
        adapter.addFragment(transactionFragment);
        adapter.addFragment(balanceFragment);
        adapter.addFragment(alarmFragment);

        viewPager.setAdapter(adapter);
    }

    @SuppressWarnings("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static int getCoinIcon(String coin){
        int result = 0;
        switch (coin) {
            case "BTC" :
                result = R.drawable.ic_btc;
                break;
            case "BCH" :
                result = R.drawable.ic_bch;
                break;
            case "XRP" :
                result = R.drawable.ic_xrp;
                break;
            case "DASH" :
                result = R.drawable.ic_dash;
                break;
            case "ETH" :
                result = R.drawable.ic_eth;
                break;
            case "ETC" :
                result = R.drawable.ic_etc;
                break;
            case "XMR" :
                result = R.drawable.ic_xmr;
                break;
            case "ZEC" :
                result = R.drawable.ic_zec;
                break;
            case "LTC" :
                result = R.drawable.ic_ltc;
                break;
            case "QTUM" :
                result = R.drawable.ic_qtum;
                break;
            case "STR" :
                result = R.drawable.ic_str;
                break;
            case "REP" :
                result = R.drawable.ic_rep;
                break;
            case "NXT" :
                result = R.drawable.ic_nxt;
                break;
            case "TRON" :
                result = R.drawable.tron_black;
                break;
            case "KRW" :
                result = R.drawable.ic_moneybag_blue;
                break;
            case "" :
                break;
        }
        return result;
    }

    public static String exchangeToKorean(String eng){
        String result = "";

        switch (eng) {
            case "bithumb" :
                result = "빗썸";
                break;
            case "coinone" :
                result = "코인원";
                break;
            case "poloniex" :
                result = "폴로닉스";
                break;
            case "coinnest" :
                result = "코인네스트";
                break;
            case "korbit" :
                result = "코빗";
                break;
        }
        return result;
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.back_press_message), Toast.LENGTH_SHORT).show();
        }
    }
}