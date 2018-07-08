package com.dev.seongenie.geniecoin.Fragment.Alarm

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.seongenie.geniecoin.R
import kotlinx.android.synthetic.main.exchange_item.view.*

/**
 * Created by seongjinlee on 2018. 6. 7..
 */
class ExchangeAdapter(var list : List<String>?) : RecyclerView.Adapter<ExchangeAdapter.ExchangeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ExchangeHolder {
        var view : View = LayoutInflater.from(parent!!.context).inflate(R.layout.exchange_item, parent, false)
        return ExchangeHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeHolder, position: Int) {
        holder.view.exchange_text.text = list!![position]
    }

    override fun getItemCount(): Int {
        return if(list?.size != null) list!!.size else 0
    }

    open fun setItemList(item : List<String>) {
        list = item
    }

    class ExchangeHolder(val view : View) : RecyclerView.ViewHolder(view)

}