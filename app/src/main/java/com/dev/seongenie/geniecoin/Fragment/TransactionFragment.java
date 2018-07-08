package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.seongenie.geniecoin.CoinSources.ColorPrice;
import com.dev.seongenie.geniecoin.Layout.OrderbookTableLayout;
import com.dev.seongenie.geniecoin.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {


    public TransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orderbook_container, container, false);

        return v;
    }

}