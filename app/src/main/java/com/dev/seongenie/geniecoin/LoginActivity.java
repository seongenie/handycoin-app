package com.dev.seongenie.geniecoin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.dev.seongenie.geniecoin.Fragment.AlarmFragment;
import com.dev.seongenie.geniecoin.Fragment.BalanceFragment;
import com.dev.seongenie.geniecoin.Fragment.FavorFragment;
import com.dev.seongenie.geniecoin.Fragment.OrderBookFragment;
import com.dev.seongenie.geniecoin.Fragment.RecyclerOnItemClickListener;
import com.dev.seongenie.geniecoin.Fragment.TransactionFragment;

import java.lang.reflect.Field;

public class LoginActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    //This is font
    public static Typeface nanumgothic;
    public static Typeface nanumgothicbold;

    //ViewPager
    private ViewPager viewPager;

    //Fragments
    FavorFragment favorFragment;
    OrderBookFragment orderBookFragment;
    TransactionFragment transactionFragment;
    BalanceFragment balanceFragment;
    AlarmFragment alarmFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        nanumgothic = Typeface.createFromAsset(getAssets(), "fonts/nanumgothic.otf");
        nanumgothicbold = Typeface.createFromAsset(getAssets(), "fonts/nanumgothicbold.otf");

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

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

                if (position == 1) {
                    Log.i("seongenie", "1 selected!!!");
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageButton settingButton = (ImageButton) findViewById(R.id.button_setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_right, R.anim.none);
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

}