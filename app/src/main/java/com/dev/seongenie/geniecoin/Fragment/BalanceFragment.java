package com.dev.seongenie.geniecoin.Fragment;

/**
 * Created by seongjinlee on 2017. 10. 2..
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.seongenie.geniecoin.Fragment.Balance.IconTreeItemHolder;
import com.dev.seongenie.geniecoin.Fragment.Balance.ProfileHolder;
import com.dev.seongenie.geniecoin.Fragment.Balance.SelectableHeaderHolder;
import com.dev.seongenie.geniecoin.Fragment.Balance.SelectableItemHolder;
import com.dev.seongenie.geniecoin.R;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.Api_Client;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.BalanceView;
import com.dev.seongenie.geniecoin.ServerConnection.BithumbPrivate.BithumbRequest;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {

    private AndroidTreeView tView;
    private boolean selectionModeEnabled = false;
    private HashMap<String, Double> balanceData;
    TreeNode root;
    private String apiKey = "24ab10d0a3b68a8ffc68429e19894aaf";
    private String secretKey = "c3baffe3380a1a5f8263289997ff95c7";

    public BalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        String endPoint = "/info/balance";
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", "ALL");

        HashMap<String, String>[] headerParams = new Api_Client(apiKey, secretKey).getHeaders(endPoint, rgParams);
        BithumbRequest.getInstance().getBalances(headerParams[0], headerParams[1], new Callback<BalanceView>() {
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
        });

        View rootView = inflater.inflate(R.layout.fragment_balance, null, false);














        return rootView;
    }

    private void setMyBalance(HashMap<String, Double> data) {
        this.balanceData = data;
    }

}


