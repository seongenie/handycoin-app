package com.dev.seongenie.geniecoin.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.dev.seongenie.geniecoin.Api.CommonMessage;
import com.dev.seongenie.geniecoin.Api.SqliteRepository;
import com.dev.seongenie.geniecoin.MainActivity;
import com.dev.seongenie.geniecoin.R;
import com.google.android.flexbox.FlexboxLayout;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

public class FavorDialogFragment extends AAH_FabulousFragment {
    SectionsPagerAdapter mAdapter;
    TabLayout tabs_types;
    List<TextView> textviews = new ArrayList<>();
    ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    private FavorDialogListener favorDialogListener;

    public static FavorDialogFragment newInstance() {
        FavorDialogFragment f = new FavorDialogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("seongenie", "FragmentDialog Start");
        applied_filters = SqliteRepository.getInstance(getContext()).getFavors();
    }

    public void setFavorDialogListener(FavorDialogListener listener){
        this.favorDialogListener = listener;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.favor_filter_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);


        Button cancelButton = (Button)contentView.findViewById(R.id.filter_button_cancel);
        Button okButton = (Button)contentView.findViewById(R.id.filter_button_ok);

        cancelButton.setTypeface(MainActivity.nanumgothic);
        okButton.setTypeface(MainActivity.nanumgothic);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFavorCoins();
                if (favorDialogListener != null) { favorDialogListener.onClickCheck(); }
                closeFilter("check");
            }
        });

        ViewPager vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);
        mAdapter = new SectionsPagerAdapter();
        vp_types.setOffscreenPageLimit(4);
        vp_types.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        tabs_types.setupWithViewPager(vp_types);


        //params to set
        setAnimationDuration(300); //optional; default 500ms
        setPeekHeight(350); // optional; default 400dp
        setCallbacks((Callbacks) getParentFragment()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
//        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

    private void resetFavorCoins() {
        SqliteRepository.getInstance(getContext()).deleteAllFavors();
        Set<String> keys = applied_filters.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String exchange = it.next();
            List<String> coins = applied_filters.get(exchange);
            for(String coin : coins) {
                SqliteRepository.getInstance(getContext()).insertFavor(exchange, coin);
            }
        }
    }


    public class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.favor_filter_sorter, collection, false);
            FlexboxLayout fbl = (FlexboxLayout) layout.findViewById(R.id.fbl);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("bithumb", fbl);
                    break;
                case 1:
                    inflateLayoutWithFilters("coinone", fbl);
                    break;
                case 2:
                    inflateLayoutWithFilters("poloniex", fbl);
                    break;
            }
            collection.addView(layout);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.bithumb);
                case 1:
                    return getString(R.string.coinone);
                case 2:
                    return getString(R.string.poloniex);
            }
            return "";
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
            List<String> keys = new ArrayList<>();
            switch (filter_category) {
                case "bithumb":
                    keys.add("BTC");
                    keys.add("ETH");
                    keys.add("DASH");
                    keys.add("LTC");
                    keys.add("ETC");
                    keys.add("XRP");
                    keys.add("BCH");
                    keys.add("XMR");
                    keys.add("ZEC");
                    break;
                case "coinone":
                    keys.add("BTC");
                    keys.add("BCH");
                    keys.add("ETH");
                    keys.add("ETC");
                    keys.add("XRP");
                    keys.add("QTUM");
                    break;

                case "poloniex":
                    keys.add("BTC");
                    keys.add("BCH");
                    keys.add("ETH");
                    keys.add("LTC");
                    keys.add("XRP");
                    keys.add("ETC");
                    keys.add("ZEC");
                    keys.add("NXT");
                    keys.add("STR");
                    keys.add("DASH");
                    keys.add("XMR");
                    keys.add("REP");
                    break;
            }

            for (int i = 0; i < keys.size(); i++) {
                View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
                final Button tv = ((Button) subchild.findViewById(R.id.coinchip));
                tv.setText(keys.get(i));
                final int finalI = i;
                final List<String> finalKeys = keys;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tv.getTag() != null && tv.getTag().equals("selected")) {
                            tv.setTag("unselected");
                            tv.setBackgroundResource(R.drawable.chip_unselected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_blue));
                            removeFromSelectedMap(filter_category, finalKeys.get(finalI));
                        } else {
                            tv.setTag("selected");
                            tv.setBackgroundResource(R.drawable.chip_selected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            addToSelectedMap(filter_category, finalKeys.get(finalI));
                        }
                    }
                });
//                try {
//                    Log.d("seongenie", "key: " + filter_category + " |val:" + keys.get(finalI));
//                    Log.d("seongenie", "applied_filters != null: " + (applied_filters != null));
//                    Log.d("seongenie", "applied_filters.get(key) != null: " + (applied_filters.get(filter_category) != null));
//                    Log.d("seongenie", "applied_filters.get(key).contains(keys.get(finalI)): " + (applied_filters.get(filter_category).contains(keys.get(finalI))));
//                } catch (Exception e) {
//
//                }
                if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).contains(keys.get(finalI))) {
                    tv.setTag("selected");
                    tv.setBackgroundResource(R.drawable.chip_selected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                } else {
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_blue));
                }
                textviews.add(tv);

                fbl.addView(subchild);
            }
        }

    }


    private void addToSelectedMap(String key, String value) {
        if (applied_filters.get(key) != null && !applied_filters.get(key).contains(value)) {
            applied_filters.get(key).add(value);
        } else {
            List<String> temp = new ArrayList<>();
            temp.add(value);
            applied_filters.put(key, temp);
        }
    }

    private void removeFromSelectedMap(String key, String value) {
        if (applied_filters.get(key).size() == 1) {
            applied_filters.remove(key);
        } else {
            applied_filters.get(key).remove(value);
        }
    }

    public interface FavorDialogListener {
        public void onClickCheck();
    }

}
