package com.dev.seongenie.geniecoin

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dev.seongenie.geniecoin.Fragment.Alarm.ExchangeAdapter
import com.dev.seongenie.geniecoin.Fragment.RecyclerOnItemClickListener
import kotlinx.android.synthetic.main.activity_alarm_add.*


/**
 * Created by seongjinlee on 2018. 6. 7..
 */

class AddAlarmActivity: Activity() {

    lateinit var viewAdapter : RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager
    var selectLevel = 0   // 0 : 거래소선택, 1 : 코인 선택

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_add)
        var coinMap = mapOf<String, List<String>>("bithumb" to listOf("XRP, XMCC"),
                "coinone" to listOf("NEO", "ORA", "MONOECCI"),
                "upbit" to listOf("upbit1", "upbit2"))

        var list : List<String>? = coinMap.keys.toList()

        viewManager = LinearLayoutManager(this)
        viewAdapter = ExchangeAdapter(list)

        alarm_recycler_view.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }


//        var onItemTouchListener = RecyclerView.OnItemTouchListener {
//            }
//        }


        alarm_recycler_view.addOnItemTouchListener(RecyclerOnItemClickListener(this, alarm_recycler_view, object : RecyclerOnItemClickListener.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (selectLevel) {
                    0 ->  {
                        selectLevel++
                        list = coinMap.get(list!![position])
                    }
                    1 -> {

                    }
                }
            }

            override fun onItemLongClick(v: View?, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }))



    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_up, R.anim.leave_up)
    }



}